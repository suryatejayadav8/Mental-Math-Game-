import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Sparkles, User, AlertCircle, ArrowRight, Loader2 } from 'lucide-react';

export default function RegisterPage() {
  const [playerName, setPlayerName] = useState('');
  const [error, setError] = useState('');
  const { register, loading } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    const trimmed = playerName.trim();
    if (!trimmed) {
      setError('Please choose a player name.');
      return;
    }

    if (trimmed.length < 2 || trimmed.length > 50) {
      setError('Player name must be between 2 and 50 characters.');
      return;
    }

    if (!/^[a-zA-Z0-9_ ]+$/.test(trimmed)) {
      setError('Player name can only contain letters, numbers, spaces, and underscores.');
      return;
    }

    try {
      await register(trimmed);
      navigate('/dashboard');
    } catch (err) {
      setError(err.message || 'Registration failed. That name might already be taken.');
    }
  };

  return (
    <div style={{ maxWidth: '440px', margin: '60px auto', padding: '0 20px' }}>
      <div className="glass-card" style={{ padding: '36px' }}>
        <div style={{ textAlign: 'center', marginBottom: '28px' }}>
          <div style={{
            background: 'linear-gradient(135deg, rgba(16, 185, 129, 0.2), rgba(56, 189, 248, 0.2))',
            width: '56px',
            height: '56px',
            borderRadius: '16px',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            margin: '0 auto 16px',
            border: '1px solid rgba(16, 185, 129, 0.3)',
          }}>
            <Sparkles size={26} color="#34D399" />
          </div>
          <h2 style={{ fontSize: '24px', fontWeight: 800, marginBottom: '6px' }}>Create Account</h2>
          <p style={{ fontSize: '14px', color: 'var(--text-secondary)' }}>
            Choose a unique username to track your scores in MySQL
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
              Choose Player Name (2-50 characters)
            </label>
            <div style={{ position: 'relative' }}>
              <input
                type="text"
                className="form-input"
                placeholder="e.g. QuickCalc, Surya99"
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
            className="btn btn-emerald"
            disabled={loading}
            style={{ width: '100%', padding: '14px', marginTop: '6px' }}
          >
            {loading ? (
              <>
                <Loader2 size={18} className="animate-spin" /> Registering...
              </>
            ) : (
              <>
                Create Account & Play <ArrowRight size={18} />
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
          Already have an account?{' '}
          <Link to="/login" style={{ color: '#38BDF8', fontWeight: 600, textDecoration: 'none' }}>
            Login here
          </Link>
        </div>
      </div>
    </div>
  );
}
