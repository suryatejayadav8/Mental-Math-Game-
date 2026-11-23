import React from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Brain, Trophy, History, LogOut, User, Play, LogIn, Sparkles } from 'lucide-react';

export default function Navbar() {
  const { player, isAuthenticated, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  const isActive = (path) => location.pathname === path;

  return (
    <nav
      style={{
        width: '100%',
        borderBottom: '1px solid rgba(139, 92, 246, 0.2)',
        background: 'rgba(8, 12, 21, 0.90)',
        backdropFilter: 'blur(14px)',
        WebkitBackdropFilter: 'blur(14px)',
        position: 'sticky',
        top: 0,
        zIndex: 50,
        padding: '12px 24px',
      }}
    >
      <div
        style={{
          width: '100%',
          maxWidth: '1400px',
          margin: '0 auto',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          flexWrap: 'wrap',
          gap: '12px',
        }}
      >
        {/* Brand Logo - Left */}
        <Link
          to="/"
          style={{
            display: 'flex',
            alignItems: 'center',
            gap: '10px',
            textDecoration: 'none',
            color: 'white',
          }}
        >
          <div
            style={{
              background: 'linear-gradient(135deg, #8B5CF6, #3B82F6)',
              width: '38px',
              height: '38px',
              borderRadius: '10px',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              boxShadow: '0 4px 12px rgba(139, 92, 246, 0.4)',
              flexShrink: 0,
            }}
          >
            <Brain size={22} color="white" />
          </div>
          <div>
            <span style={{ fontWeight: 800, fontSize: '19px', letterSpacing: '-0.5px' }}>
              MENTAL<span style={{ color: '#A78BFA' }}>MATH</span>
            </span>
          </div>
        </Link>

        {/* Navigation Links - Right */}
        <div
          style={{
            display: 'flex',
            alignItems: 'center',
            gap: '8px',
            flexWrap: 'wrap',
          }}
        >
          {isAuthenticated ? (
            <>
              <Link
                to="/dashboard"
                className="btn btn-secondary"
                style={{
                  padding: '7px 13px',
                  fontSize: '13px',
                  background: isActive('/dashboard') ? 'rgba(139, 92, 246, 0.25)' : undefined,
                  borderColor: isActive('/dashboard') ? '#8B5CF6' : undefined,
                }}
              >
                <Play size={14} /> Dashboard
              </Link>
              <Link
                to="/leaderboard"
                className="btn btn-secondary"
                style={{
                  padding: '7px 13px',
                  fontSize: '13px',
                  background: isActive('/leaderboard') ? 'rgba(139, 92, 246, 0.25)' : undefined,
                  borderColor: isActive('/leaderboard') ? '#8B5CF6' : undefined,
                }}
              >
                <Trophy size={14} color="#F59E0B" /> Leaderboard
              </Link>
              <Link
                to="/history"
                className="btn btn-secondary"
                style={{
                  padding: '7px 13px',
                  fontSize: '13px',
                  background: isActive('/history') ? 'rgba(139, 92, 246, 0.25)' : undefined,
                  borderColor: isActive('/history') ? '#8B5CF6' : undefined,
                }}
              >
                <History size={14} color="#38BDF8" /> History
              </Link>
              <Link
                to="/profile"
                className="btn btn-secondary"
                style={{
                  padding: '7px 13px',
                  fontSize: '13px',
                  background: isActive('/profile') ? 'rgba(139, 92, 246, 0.25)' : undefined,
                  borderColor: isActive('/profile') ? '#8B5CF6' : undefined,
                }}
              >
                <User size={14} color="#A78BFA" /> Profile
              </Link>

              {/* Player Tag & Logout */}
              <div
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  gap: '8px',
                  marginLeft: '4px',
                  paddingLeft: '10px',
                  borderLeft: '1px solid rgba(148, 163, 184, 0.2)',
                }}
              >
                <Link
                  to="/profile"
                  style={{
                    display: 'flex',
                    alignItems: 'center',
                    gap: '6px',
                    background: isActive('/profile') ? 'rgba(139, 92, 246, 0.3)' : 'rgba(30, 41, 59, 0.7)',
                    padding: '5px 12px',
                    borderRadius: '20px',
                    border: '1px solid rgba(139, 92, 246, 0.35)',
                    textDecoration: 'none',
                    cursor: 'pointer',
                    transition: 'all 0.2s ease',
                  }}
                  title="View Profile"
                >
                  <User size={13} color="#A78BFA" />
                  <span style={{ fontSize: '13px', fontWeight: 600, color: '#F8FAFC' }}>
                    {player?.playerName}
                  </span>
                </Link>
                <button
                  onClick={handleLogout}
                  className="btn btn-secondary"
                  title="Logout"
                  style={{ padding: '7px 11px' }}
                >
                  <LogOut size={14} color="#EF4444" />
                </button>
              </div>
            </>
          ) : (
            <>
              <Link
                to="/leaderboard"
                className="btn btn-secondary"
                style={{ padding: '8px 16px', fontSize: '13px' }}
              >
                <Trophy size={14} color="#F59E0B" /> Leaderboard
              </Link>
              <Link
                to="/login"
                className="btn btn-secondary"
                style={{ padding: '8px 16px', fontSize: '13px' }}
              >
                <LogIn size={14} /> Login
              </Link>
              <Link
                to="/register"
                className="btn btn-primary"
                style={{ padding: '8px 18px', fontSize: '13px' }}
              >
                <Sparkles size={14} /> Register
              </Link>
            </>
          )}
        </div>
      </div>
    </nav>
  );
}
