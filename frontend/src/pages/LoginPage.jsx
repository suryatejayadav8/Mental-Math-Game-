import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { LogIn, User, AlertCircle, ArrowRight, Loader2 } from 'lucide-react';

export default function LoginPage() {
  const [playerName, setPlayerName] = useState('');
  const [error, setError] = useState('');
  const { login, loading } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    const trimmed = playerName.trim();
    if (!trimmed) {
      setError('Please enter your player name.');
      return;
    }

    try {
      await login(trimmed);
      navigate('/dashboard');
    } catch (err) {
      setError(err.message || 'Login failed. Please verify your player name.');
    }
  };

  return (
    <div style={{ maxWidth: '440px', margin: '60px auto', padding: '0 20px' }}>
      <div className="glass-card" style={{ padding: '36px' }}>
        <div style={{ textAlign: 'center', marginBottom: '28px' }}>
          <div style={{
            background: 'linear-gradient(135deg, rgba(139, 92, 246, 0.2), rgba(59, 130, 246, 0.2))',
            width: '56px',
            height: '56px',
            borderRadius: '16px',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            margin: '0 auto 16px',
            border: '1px solid rgba(139, 92, 246, 0.3)',
          }}>
            <LogIn size={26} color="#A78BFA" />
          </div>
          <h2 style={{ fontSize: '24px', fontWeight: 800, marginBottom: '6px' }}>Player Login</h2>
          <p style={{ fontSize: '14px', color: 'var(--text-secondary)' }}>
            Enter your registered player name to continue
          </p>
        </div>

        {error && (
          <div style={{
            background: 'rgba(239, 68, 68, 0.15)',
            border: '1px solid rgba(239, 68, 68, 0.4)',
            color: '#FCA5A5',
            padding: '12px 16px',
            borderRadius: '10px',
            fontSize: '13px',
            display: 'flex',
            alignItems: 'center',
            gap: '10px',
            marginBottom: '20px',
          }}>
            <AlertCircle size={18} color="#EF4444" style={{ flexShrink: 0 }} />
            <span>{error}</span>
          </div>
        )}

        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '18px' }}>
          <div>
            <label style={{
              display: 'block',
              fontSize: '13px',
              fontWeight: 600,
              color: 'var(--text-secondary)',
              marginBottom: '8px',
            }}>
              Player Name
            </label>
            <div style={{ position: 'relative' }}>
              <input
                type="text"
                className="form-input"
                placeholder="e.g. BrainMaster, Surya"
                value={playerName}
                onChange={(e) => setPlayerName(e.target.value)}
                disabled={loading}
                autoFocus
                style={{ paddingLeft: '40px' }}
              />
              <User size={18} color="#64748B" style={{ position: 'absolute', left: '14px', top: '15px' }} />
            </div>
          </div>

          <button
            type="submit"
            className="btn btn-primary"
            disabled={loading}
            style={{ width: '100%', padding: '14px', marginTop: '6px' }}
          >
            {loading ? (
              <>
                <Loader2 size={18} className="animate-spin" /> Verifying...
              </>
            ) : (
              <>
                Login to Play <ArrowRight size={18} />
              </>
            )}
          </button>
        </form>

        <div style={{
          marginTop: '24px',
          paddingTop: '20px',
          borderTop: '1px solid rgba(148, 163, 184, 0.15)',
          textAlign: 'center',
          fontSize: '13px',
          color: 'var(--text-secondary)',
        }}>
          Don't have an account?{' '}
          <Link to="/register" style={{ color: '#A78BFA', fontWeight: 600, textDecoration: 'none' }}>
            Register here
          </Link>
        </div>
      </div>
    </div>
  );
}
