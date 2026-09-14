"use client";

import { useState, useEffect } from 'react';
import { fetchApi, breakdownTaskWithAI } from '../lib/api';

const COLUMNS = ['TODO', 'DOING', 'DONE'];

const COLUMN_NAMES = {
  TODO: 'To Do',
  DOING: 'Doing',
  DONE: 'Done'
};

export default function TaskBoard({ user }) {
  const [tasks, setTasks] = useState([]);
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showCreateModal, setShowCreateModal] = useState(false);
  
  // New task form state
  const [newTaskTitle, setNewTaskTitle] = useState('');
  const [newTaskDesc, setNewTaskDesc] = useState('');

  // Edit task form state
  const [showEditModal, setShowEditModal] = useState(false);
  const [editingTask, setEditingTask] = useState(null);
  const [editTaskTitle, setEditTaskTitle] = useState('');
  const [editTaskDesc, setEditTaskDesc] = useState('');

  const [isBreakingDown, setIsBreakingDown] = useState(false);

  const handleBreakdownNewTask = async () => {
    if (!newTaskTitle) {
      alert('Please enter a title first to break it down.');
      return;
    }
    setIsBreakingDown(true);
    try {
      const response = await breakdownTaskWithAI(newTaskTitle, newTaskDesc);
      if (response && response.subtasks) {
        setNewTaskDesc(prev => prev ? prev + '\n\n' + response.subtasks : response.subtasks);
      }
    } catch (e) {
      alert('Error breaking down task: ' + e.message);
    } finally {
      setIsBreakingDown(false);
    }
  };

  const handleBreakdownEditTask = async () => {
    if (!editTaskTitle) {
      alert('Please enter a title first to break it down.');
      return;
    }
    setIsBreakingDown(true);
    try {
      const response = await breakdownTaskWithAI(editTaskTitle, editTaskDesc);
      if (response && response.subtasks) {
        setEditTaskDesc(prev => prev ? prev + '\n\n' + response.subtasks : response.subtasks);
      }
    } catch (e) {
      alert('Error breaking down task: ' + e.message);
    } finally {
      setIsBreakingDown(false);
    }
  };

  const loadTasks = async () => {
    try {
      const data = await fetchApi('/tasks');
      setTasks(data || []);
    } catch (e) {
      console.error(e);
    }
    setLoading(false);
  };

  const loadUsers = async () => {
    if (user.role === 'ROLE_ADMIN') {
      try {
        const data = await fetchApi('/users');
        setUsers(data || []);
      } catch (e) {
        console.error(e);
      }
    }
  };

  useEffect(() => {
    loadTasks();
    loadUsers();
  }, []);

  const handleDragStart = (e, taskId) => {
    e.dataTransfer.setData('taskId', taskId);
  };

  const handleDrop = async (e, status) => {
    e.preventDefault();
    const taskId = e.dataTransfer.getData('taskId');
    if (!taskId) return;
    
    // Optimistic update
    setTasks(prev => prev.map(t => t.id === taskId ? { ...t, status } : t));
    
    try {
      await fetchApi(`/tasks/${taskId}/status`, {
        method: 'PUT',
        body: JSON.stringify({ status })
      });
      loadTasks(); // reload to get exact state
    } catch (err) {
      console.error(err);
      loadTasks(); // revert
    }
  };

  const handleDragOver = (e) => {
    e.preventDefault();
  };

  const handleCreateTask = async (e) => {
    e.preventDefault();
    try {
      await fetchApi('/tasks', {
        method: 'POST',
        body: JSON.stringify({ title: newTaskTitle, description: newTaskDesc })
      });
      setNewTaskTitle('');
      setNewTaskDesc('');
      setShowCreateModal(false);
      loadTasks();
    } catch (err) {
      console.error(err);
      alert(err.message || 'Error creating task');
    }
  };

  const handleEditTask = async (e) => {
    e.preventDefault();
    try {
      await fetchApi(`/tasks/${editingTask.id}`, {
        method: 'PUT',
        body: JSON.stringify({ title: editTaskTitle, description: editTaskDesc })
      });
      setEditingTask(null);
      setShowEditModal(false);
      loadTasks();
    } catch (err) {
      console.error(err);
      alert(err.message || 'Error editing task');
    }
  };

  const handleDeleteTask = async (taskId) => {
    if (!confirm('Are you sure you want to delete this task?')) return;
    try {
      await fetchApi(`/tasks/${taskId}`, {
        method: 'DELETE'
      });
      loadTasks();
    } catch (err) {
      console.error(err);
      alert(err.message || 'Error deleting task');
    }
  };

  const handleAssignTask = async (taskId, assigneeId) => {
    try {
      await fetchApi(`/tasks/${taskId}/assign`, {
        method: 'PUT',
        body: JSON.stringify({ assigneeId })
      });
      loadTasks();
    } catch (err) {
      console.error(err);
      alert('Error assigning task: ' + err.message);
    }
  };

  if (loading) return <div>Loading tasks...</div>;

  return (
    <div>
      <div style={{ marginBottom: '20px' }}>
        <button onClick={() => setShowCreateModal(true)} className="btn-primary">+ Create New Task</button>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))', gap: '20px' }}>
        {COLUMNS.map(col => (
          <div 
            key={col} 
            className="glass-panel" 
            style={{ padding: '20px', minHeight: '500px' }}
            onDrop={(e) => handleDrop(e, col)}
            onDragOver={handleDragOver}
          >
            <h3 style={{ marginBottom: '20px', paddingBottom: '10px', borderBottom: '1px solid rgba(255,255,255,0.1)' }}>
              {COLUMN_NAMES[col]} ({tasks.filter(t => t.status === col).length})
            </h3>
            
            <div style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
              {tasks.filter(t => t.status === col).map(task => (
                <div 
                  key={task.id} 
                  className="glass-card" 
                  style={{ padding: '15px', cursor: 'grab' }}
                  draggable
                  onDragStart={(e) => handleDragStart(e, task.id)}
                >
                  <h4 style={{ margin: '0 0 10px 0', color: 'var(--text-primary)' }}>{task.title}</h4>
                  <p style={{ margin: '0 0 15px 0', color: 'var(--text-secondary)', fontSize: '14px', whiteSpace: 'pre-wrap' }}>{task.description}</p>
                  
                  <div style={{ fontSize: '11px', color: 'var(--text-secondary)', marginBottom: '15px' }}>
                    <div>Created: {new Date(task.createdAt).toLocaleString()}</div>
                    {task.updatedAt && task.updatedAt !== task.createdAt && (
                      <div>Updated: {new Date(task.updatedAt).toLocaleString()}</div>
                    )}
                  </div>

                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', fontSize: '12px', color: 'var(--text-secondary)' }}>
                    <div>
                      {task.assignedUserId ? 
                        <span style={{ background: 'rgba(59, 130, 246, 0.2)', color: 'var(--primary-color)', padding: '4px 8px', borderRadius: '4px' }}>Assigned</span> 
                        : 
                        <span style={{ background: 'rgba(239, 68, 68, 0.2)', color: 'var(--danger-color)', padding: '4px 8px', borderRadius: '4px' }}>Unassigned</span>
                      }
                    </div>
                    
                    {/* Assignment Controls */}
                    {user.role === 'ROLE_ADMIN' ? (
                      <select 
                        style={{ background: 'rgba(0,0,0,0.3)', color: 'white', border: '1px solid rgba(255,255,255,0.1)', padding: '4px', borderRadius: '4px' }}
                        value={task.assignedUserId || ''}
                        onChange={(e) => handleAssignTask(task.id, e.target.value)}
                      >
                        <option value="">Unassigned</option>
                        {users.map(u => <option key={u.id} value={u.id}>{u.username}</option>)}
                      </select>
                    ) : (
                      (!task.assignedUserId) && (
                        <button 
                          onClick={() => handleAssignTask(task.id, user.id)}
                          style={{ background: 'transparent', border: '1px solid var(--primary-color)', color: 'var(--primary-color)', padding: '2px 8px', borderRadius: '4px', cursor: 'pointer' }}
                        >
                          Assign to me
                        </button>
                      )
                    )}
                  </div>
                  
                  {/* Task Management Controls (Edit/Delete) */}
                  {(user.role === 'ROLE_ADMIN' || task.creatorId === user.id) && (
                    <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '8px', marginTop: '10px' }}>
                      <button 
                        onClick={() => {
                          setEditingTask(task);
                          setEditTaskTitle(task.title);
                          setEditTaskDesc(task.description);
                          setShowEditModal(true);
                        }}
                        style={{ background: 'transparent', border: 'none', color: '#10B981', cursor: 'pointer', fontSize: '12px', textDecoration: 'underline' }}
                      >
                        Edit
                      </button>
                      <button 
                        onClick={() => handleDeleteTask(task.id)}
                        style={{ background: 'transparent', border: 'none', color: '#EF4444', cursor: 'pointer', fontSize: '12px', textDecoration: 'underline' }}
                      >
                        Delete
                      </button>
                    </div>
                  )}
                </div>
              ))}
            </div>
          </div>
        ))}
      </div>

      {showCreateModal && (
        <div style={{ position: 'fixed', top: 0, left: 0, right: 0, bottom: 0, background: 'rgba(0,0,0,0.7)', display: 'flex', justifyContent: 'center', alignItems: 'center', zIndex: 1000 }}>
          <div className="glass-panel" style={{ padding: '30px', width: '100%', maxWidth: '500px' }}>
            <h2 style={{ marginBottom: '20px' }}>Create Task</h2>
            <form onSubmit={handleCreateTask} style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
              <div>
                <label style={{ display: 'block', marginBottom: '5px' }}>Title</label>
                <input required className="input-field" value={newTaskTitle} onChange={e => setNewTaskTitle(e.target.value)} />
              </div>
              <div>
                <label style={{ display: 'block', marginBottom: '5px' }}>Description</label>
                <textarea required className="input-field" value={newTaskDesc} onChange={e => setNewTaskDesc(e.target.value)} style={{ minHeight: '100px', resize: 'vertical' }} />
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: '10px' }}>
                <button type="button" onClick={handleBreakdownNewTask} disabled={isBreakingDown} className="btn-secondary" style={{ background: 'var(--primary-color)', color: 'white', border: 'none' }}>
                  {isBreakingDown ? '✨ Thinking...' : '✨ Break Down with AI'}
                </button>
                <div style={{ display: 'flex', gap: '10px' }}>
                  <button type="button" onClick={() => setShowCreateModal(false)} className="btn-secondary">Cancel</button>
                  <button type="submit" className="btn-primary">Create</button>
                </div>
              </div>
            </form>
          </div>
        </div>
      )}

      {showEditModal && editingTask && (
        <div style={{ position: 'fixed', top: 0, left: 0, right: 0, bottom: 0, background: 'rgba(0,0,0,0.7)', display: 'flex', justifyContent: 'center', alignItems: 'center', zIndex: 1000 }}>
          <div className="glass-panel" style={{ padding: '30px', width: '100%', maxWidth: '500px' }}>
            <h2 style={{ marginBottom: '20px' }}>Edit Task</h2>
            <form onSubmit={handleEditTask} style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
              <div>
                <label style={{ display: 'block', marginBottom: '5px' }}>Title</label>
                <input required className="input-field" value={editTaskTitle} onChange={e => setEditTaskTitle(e.target.value)} />
              </div>
              <div>
                <label style={{ display: 'block', marginBottom: '5px' }}>Description</label>
                <textarea required className="input-field" value={editTaskDesc} onChange={e => setEditTaskDesc(e.target.value)} style={{ minHeight: '100px', resize: 'vertical' }} />
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: '10px' }}>
                <button type="button" onClick={handleBreakdownEditTask} disabled={isBreakingDown} className="btn-secondary" style={{ background: 'var(--primary-color)', color: 'white', border: 'none' }}>
                  {isBreakingDown ? '✨ Thinking...' : '✨ Break Down with AI'}
                </button>
                <div style={{ display: 'flex', gap: '10px' }}>
                  <button type="button" onClick={() => { setShowEditModal(false); setEditingTask(null); }} className="btn-secondary">Cancel</button>
                  <button type="submit" className="btn-primary">Save Changes</button>
                </div>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
