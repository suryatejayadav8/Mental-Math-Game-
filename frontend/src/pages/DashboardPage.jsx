import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Play, Trophy, History, User, LogOut, Sparkles, CheckCircle2 } from 'lucide-react';

export default function DashboardPage() {
  const { player, logout } = useAuth();
  const navigate = useNavigate();
  const [selectedDifficulty, setSelectedDifficulty] = useState('EASY');

  const difficulties = [
    {
      id: 'EASY',
      name: 'Easy Mode',
      badge: 'Beginner',
      badgeClass: 'badge-easy',
      description: 'Addition & subtraction with numbers 1 to 20. Ideal for warm-ups and calculation speed training.',
      points: '10 pts / question',
      numbers: '1 – 20',
      ops: '+ , −',
      accentColor: '#10B981',
    },
    {
      id: 'MEDIUM',
      name: 'Medium Mode',
      badge: 'Intermediate',
      badgeClass: 'badge-medium',
      description: 'Adds multiplication & clean division with numbers up to 50. Tests your reflexes and mental arithmetic.',
      points: '20 pts / question',
      numbers: '1 – 50',
      ops: '+ , − , × , ÷',
      accentColor: '#3B82F6',
    },
    {
      id: 'HARD',
      name: 'Hard Mode',
      badge: 'Expert',
      badgeClass: 'badge-hard',
      description: 'Large numbers up to 100 with all 4 operations. High speed multipliers rewarded for fast athletes.',
      points: '30 pts / question',
      numbers: '1 – 100',
      ops: '+ , − , × , ÷',
      accentColor: '#8B5CF6',
    },
  ];

  const handleStartGame = (difficulty) => {
    navigate('/game', { state: { difficulty: difficulty || selectedDifficulty } });
  };

  return (
    <div className="container" style={{ width: '100%', maxWidth: '1400px', margin: '0 auto', padding: '2rem' }}>
      {/* Welcome Banner Card - Full Width */}
      <div
        className="card card-glow"
        style={{
          width: '100%',
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          flexWrap: 'wrap',
          gap: '1.5rem',
          marginBottom: '2.5rem',
          padding: '2.25rem 2.5rem',
          background: 'linear-gradient(135deg, rgba(30, 41, 75, 0.85) 0%, rgba(18, 24, 43, 0.98) 100%)',
        }}
      >
        <div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '0.5rem' }}>
            <span style={{ fontSize: '2rem' }}>👋</span>
            <h1 style={{ fontSize: '2rem', fontWeight: 800, margin: 0 }}>
              Welcome back, <span className="gradient-text">{player?.playerName || 'Player'}</span>!
            </h1>
          </div>
          <p style={{ color: 'var(--text-secondary)', margin: 0, fontSize: '1rem' }}>
            Player ID: <strong style={{ color: 'var(--text-main)' }}>#{player?.playerId}</strong> • Ready to sharpen your calculation agility today?
          </p>
        </div>

        {/* Quick Action Navigation Buttons */}
        <div style={{ display: 'flex', gap: '0.75rem', flexWrap: 'wrap' }}>
          <button
            onClick={() => navigate('/profile')}
            className="btn btn-secondary"
            style={{ fontSize: '0.9rem', padding: '0.75rem 1.25rem' }}
          >
            <User size={16} color="#A78BFA" /> My Profile
          </button>
          <button
            onClick={() => navigate('/history')}
            className="btn btn-secondary"
            style={{ fontSize: '0.9rem', padding: '0.75rem 1.25rem' }}
          >
            <History size={16} color="#38BDF8" /> Score History
          </button>
          <button
            onClick={() => navigate('/leaderboard')}
            className="btn btn-secondary"
            style={{ fontSize: '0.9rem', padding: '0.75rem 1.25rem' }}
          >
            <Trophy size={16} color="#F59E0B" /> Leaderboard
          </button>
          <button
            onClick={logout}
            className="btn btn-danger"
            style={{ fontSize: '0.9rem', padding: '0.75rem 1.25rem' }}
          >
            <LogOut size={16} /> Logout
          </button>
        </div>
      </div>

      {/* Select Difficulty Section Header */}
      <div style={{ marginBottom: '1.5rem' }}>
        <h2 style={{ fontSize: '1.5rem', fontWeight: 800, marginBottom: '0.4rem' }}>
          Select Difficulty Level
        </h2>
        <p style={{ color: 'var(--text-secondary)', fontSize: '0.95rem', margin: 0 }}>
          Each game consists of 10 rapid arithmetic questions. Speed bonuses are awarded for fast, accurate rounds.
        </p>
      </div>

      {/* 3 Difficulty Cards - Desktop 3 in a row, Tablet 2, Mobile 1 */}
      <div className="difficulty-grid">
        {difficulties.map((d) => {
          const isSelected = selectedDifficulty === d.id;
          return (
            <div
              key={d.id}
              onClick={() => setSelectedDifficulty(d.id)}
              className="card"
              style={{
                cursor: 'pointer',
                transition: 'all 0.25s ease',
                border: isSelected
                  ? `2px solid ${d.accentColor}`
                  : '1px solid var(--border-card)',
                boxShadow: isSelected
                  ? `0 0 30px rgba(139, 92, 246, 0.35)`
                  : 'none',
                transform: isSelected ? 'translateY(-4px)' : 'none',
                display: 'flex',
                flexDirection: 'column',
                justifyContent: 'space-between',
                padding: '2rem',
                minHeight: '340px',
              }}
            >
              <div>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.25rem' }}>
                  <h3 style={{ fontSize: '1.4rem', fontWeight: 800, margin: 0 }}>{d.name}</h3>
                  <span className={`badge ${d.badgeClass}`}>{d.badge}</span>
                </div>

                <p style={{ color: 'var(--text-secondary)', fontSize: '0.92rem', lineHeight: 1.6, marginBottom: '1.5rem' }}>
                  {d.description}
                </p>

                <div
                  style={{
                    background: 'rgba(0, 0, 0, 0.35)',
                    padding: '1rem',
                    borderRadius: '12px',
                    fontSize: '0.88rem',
                    marginBottom: '1.5rem',
                    border: '1px solid rgba(255, 255, 255, 0.05)',
                  }}
                >
                  <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.5rem' }}>
                    <span style={{ color: 'var(--text-muted)' }}>Numbers:</span>
                    <strong style={{ color: 'var(--text-main)' }}>{d.numbers}</strong>
                  </div>
                  <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.5rem' }}>
                    <span style={{ color: 'var(--text-muted)' }}>Operations:</span>
                    <strong style={{ color: 'var(--text-main)' }}>{d.ops}</strong>
                  </div>
                  <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                    <span style={{ color: 'var(--text-muted)' }}>Scoring Base:</span>
                    <strong style={{ color: d.accentColor }}>{d.points}</strong>
                  </div>
                </div>
              </div>

              <button
                onClick={(e) => {
                  e.stopPropagation();
                  handleStartGame(d.id);
                }}
                className={isSelected ? 'btn btn-primary' : 'btn btn-secondary'}
                style={{
                  width: '100%',
                  padding: '0.95rem',
                  fontSize: '1rem',
                  fontWeight: 700,
                  gap: '8px',
                }}
              >
                <Play size={16} /> Play {d.name}
              </button>
            </div>
          );
        })}
      </div>

      {/* Quick Launch & Performance Tips Banner */}
      <div
        className="card"
        style={{
          width: '100%',
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))',
          gap: '2.5rem',
          padding: '2.25rem',
          background: 'rgba(18, 24, 43, 0.65)',
        }}
      >
        <div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '1rem' }}>
            <Sparkles size={20} color="#A78BFA" />
            <h3 style={{ fontSize: '1.2rem', fontWeight: 800, margin: 0 }}>
              Quick Tips for Maximum Score
            </h3>
          </div>
          <ul style={{ color: 'var(--text-secondary)', fontSize: '0.92rem', lineHeight: 1.8, paddingLeft: '1.25rem' }}>
            <li>Accuracy is king — 100% precision yields the highest multiplier bonus!</li>
            <li>Answer quickly: every second saved adds directly to your final score.</li>
            <li>Press <kbd style={{ background: '#1E293B', padding: '2px 8px', borderRadius: '4px', border: '1px solid #475569', color: '#F8FAFC' }}>Enter</kbd> to submit answers rapidly without reaching for the mouse.</li>
          </ul>
        </div>

        <div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '1rem' }}>
            <Trophy size={20} color="#F59E0B" />
            <h3 style={{ fontSize: '1.2rem', fontWeight: 800, margin: 0 }}>
              Global Competition
            </h3>
          </div>
          <p style={{ color: 'var(--text-secondary)', fontSize: '0.92rem', lineHeight: 1.6, marginBottom: '1.25rem' }}>
            Scores are recorded directly in the MySQL database. Compare your calculation speed with fellow mental math athletes on the leaderboard.
          </p>
          <button
            onClick={() => handleStartGame(selectedDifficulty)}
            className="btn btn-primary"
            style={{ width: '100%', padding: '0.95rem', fontSize: '1rem' }}
          >
            ⚡ Start 10-Question Session ({selectedDifficulty})
          </button>
        </div>
      </div>
    </div>
  );
}
