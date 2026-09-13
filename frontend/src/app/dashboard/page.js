"use client";

import { useAuth } from '../../context/AuthContext';
import { useRouter } from 'next/navigation';
import { useEffect, useState } from 'react';
import TaskBoard from '../../components/TaskBoard';

export default function Dashboard() {
  const { user, loading, logout } = useAuth();
  const router = useRouter();

  useEffect(() => {
    if (!loading && !user) {
      router.push('/login');
    }
  }, [user, loading, router]);

  if (loading || !user) {
    return <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>Loading...</div>;
  }

  return (
    <div style={{ padding: '30px', maxWidth: '1400px', margin: '0 auto' }}>
      <header style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '40px' }} className="glass-panel animate-fade-in">
        <div style={{ padding: '20px' }}>
          <h1 className="text-gradient" style={{ margin: 0 }}>Task Management</h1>
          <p style={{ color: 'var(--text-secondary)', margin: '5px 0 0 0' }}>Welcome, {user.username} {user.role === 'ROLE_ADMIN' ? '(Admin)' : ''}</p>
        </div>
        <div style={{ padding: '20px' }}>
          <button onClick={logout} className="btn-secondary">Log Out</button>
        </div>
      </header>
      
      <main className="animate-fade-in" style={{ animationDelay: '0.1s' }}>
        <TaskBoard user={user} />
      </main>
    </div>
  );
}
