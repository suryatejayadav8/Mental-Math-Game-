import React, { createContext, useContext, useState, useEffect } from 'react';
import { api } from '../api/api';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [player, setPlayer] = useState(() => {
    try {
      const saved = localStorage.getItem('mental_math_player');
      return saved ? JSON.parse(saved) : null;
    } catch {
      return null;
    }
  });

  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (player) {
      localStorage.setItem('mental_math_player', JSON.stringify(player));
    } else {
      localStorage.removeItem('mental_math_player');
    }
  }, [player]);

  const login = async (playerName) => {
    setLoading(true);
    try {
      const response = await api.auth.login(playerName);
      if (response && response.player) {
        setPlayer(response.player);
        return { success: true, player: response.player };
      }
      throw new Error(response.message || 'Login failed');
    } finally {
      setLoading(false);
    }
  };

  const register = async (playerName) => {
    setLoading(true);
    try {
      const response = await api.auth.register(playerName);
      if (response && response.player) {
        setPlayer(response.player);
        return { success: true, player: response.player };
      }
      throw new Error(response.message || 'Registration failed');
    } finally {
      setLoading(false);
    }
  };

  const logout = () => {
    setPlayer(null);
    localStorage.removeItem('mental_math_player');
  };

  return (
    <AuthContext.Provider
      value={{
        player,
        isAuthenticated: !!player,
        login,
        register,
        logout,
        loading,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}
