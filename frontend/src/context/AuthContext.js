"use client";

import { createContext, useContext, useState, useEffect } from 'react';
import { fetchApi } from '../lib/api';
import { useRouter } from 'next/navigation';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const router = useRouter();

  useEffect(() => {
    const storedUser = localStorage.getItem('user');
    if (storedUser) {
      setUser(JSON.parse(storedUser));
    }
    setLoading(false);
  }, []);

  const login = async (username, password) => {
    const data = await fetchApi('/auth/signin', {
      method: 'POST',
      body: JSON.stringify({ username, password })
    });
    localStorage.setItem('user', JSON.stringify(data));
    setUser(data);
    router.push('/dashboard');
  };

  const register = async (username, password) => {
    await fetchApi('/auth/signup', {
      method: 'POST',
      body: JSON.stringify({ username, password })
    });
    router.push('/login');
  };

  const logout = () => {
    localStorage.removeItem('user');
    setUser(null);
    router.push('/login');
  };

  return (
    <AuthContext.Provider value={{ user, loading, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
