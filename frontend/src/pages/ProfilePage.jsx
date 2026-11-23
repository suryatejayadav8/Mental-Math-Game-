import React, { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { api } from '../api/api';
import { User, Trophy, Zap, Target, Clock, Calendar, Play, History, LogOut } from 'lucide-react';

export default function ProfilePage() {
  const { player, isAuthenticated, logout } = useAuth();
  const navigate = useNavigate();

  const [playerDetails, setPlayerDetails] = useState(null);
  const [scores, setScores] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (!isAuthenticated || !player) {
      navigate('/login', { state: { returnTo: '/profile' } });
      return;
    }

    let isMounted = true;
    async function fetchProfileData() {
      try {
        setLoading(true);
        setError(null);

        // Fetch latest player profile and score history in parallel
        const [profileRes, scoresRes] = await Promise.allSettled([
          api.auth.getPlayer(player.playerId),
          api.scores.getPlayerScores(player.playerId),
        ]);

        if (!isMounted) return;

        if (profileRes.status === 'fulfilled' && profileRes.value) {
          setPlayerDetails(profileRes.value);
        } else {
          setPlayerDetails(player);
        }

        if (scoresRes.status === 'fulfilled' && Array.isArray(scoresRes.value)) {
          setScores(scoresRes.value);
        } else {
          setScores([]);
        }
      } catch (err) {
        if (isMounted) {
          setError(err.message || 'Failed to load profile data.');
        }
      } finally {
        if (isMounted) {
          setLoading(false);
        }
      }
    }

    fetchProfileData();

    return () => {
      isMounted = false;
    };
  }, [player, isAuthenticated, navigate]);

  const formatDate = (dateStr) => {
    if (!dateStr) return 'N/A';
    try {
      const d = new Date(dateStr);
      return d.toLocaleDateString(undefined, {
        month: 'long',
        day: 'numeric',
        year: 'numeric',
      });
    } catch {
      return dateStr;
    }
  };

  // Aggregate stats
  const totalGames = scores.length;
  const bestScore = totalGames > 0 ? Math.max(...scores.map((s) => s.score || 0)) : 0;
  const totalQuestions = scores.reduce((sum, s) => sum + (s.totalQuestions || 0), 0);
  const totalCorrect = scores.reduce((sum, s) => sum + (s.correctAnswers || 0), 0);
  const overallAccuracy =
    totalQuestions > 0 ? ((totalCorrect / totalQuestions) * 100).toFixed(1) : '0.0';
  const fastestTime =
    totalGames > 0 ? Math.min(...scores.map((s) => s.timeTaken || 999999)) : 0;

  // Breakdown by difficulty
  const getStatsByDifficulty = (diff) => {
    const diffScores = scores.filter((s) => (s.difficulty || '').toUpperCase() === diff);
    const count = diffScores.length;
    const maxScore = count > 0 ? Math.max(...diffScores.map((s) => s.score || 0)) : 0;
    const diffCorrect = diffScores.reduce((sum, s) => sum + (s.correctAnswers || 0), 0);
    const diffTotal = diffScores.reduce((sum, s) => sum + (s.totalQuestions || 0), 0);
    const acc = diffTotal > 0 ? ((diffCorrect / diffTotal) * 100).toFixed(0) : '—';
    return { count, maxScore, acc };
  };

  const easyStats = getStatsByDifficulty('EASY');
  const mediumStats = getStatsByDifficulty('MEDIUM');
  const hardStats = getStatsByDifficulty('HARD');

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  if (loading) {
    return (
      <div className="container" style={{ textAlign: 'center', padding: '6rem 1rem' }}>
        <div className="spinner" style={{ margin: '0 auto 1.5rem' }}></div>
        <h2 style={{ fontSize: '1.35rem', fontWeight: 600 }}>Loading Profile...</h2>
        <p style={{ color: 'var(--color-text-secondary)', fontSize: '0.95rem' }}>
          Retrieving player records and calculation achievements.
        </p>
      </div>
    );
  }

  const activePlayer = playerDetails || player;

  return (
    <div className="container" style={{ width: '100%', maxWidth: '1200px', margin: '0 auto', padding: '2rem 1.5rem' }}>
      {/* Header Profile Identity Card */}
      <div
        className="card card-glow"
        style={{
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          flexWrap: 'wrap',
          gap: '1.75rem',
          padding: '2.25rem',
          marginBottom: '2rem',
          background: 'linear-gradient(135deg, rgba(30, 41, 75, 0.85) 0%, rgba(18, 24, 43, 0.98) 100%)',
        }}
      >
        <div style={{ display: 'flex', alignItems: 'center', gap: '1.5rem', flexWrap: 'wrap' }}>
          {/* Avatar Icon */}
          <div
            style={{
              width: '80px',
              height: '80px',
              borderRadius: '20px',
              background: 'linear-gradient(135deg, #8B5CF6, #3B82F6)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              fontSize: '2rem',
              fontWeight: 800,
              color: 'white',
              boxShadow: '0 8px 24px rgba(139, 92, 246, 0.35)',
            }}
          >
            {activePlayer?.playerName ? activePlayer.playerName.charAt(0).toUpperCase() : 'P'}
          </div>

          <div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', flexWrap: 'wrap', marginBottom: '0.4rem' }}>
              <h1 style={{ fontSize: '2rem', fontWeight: 800, margin: 0 }}>
                {activePlayer?.playerName}
              </h1>
              <span className="badge badge-purple" style={{ fontSize: '0.78rem' }}>
                Mental Athlete
              </span>
            </div>

            <div style={{ display: 'flex', alignItems: 'center', gap: '1.25rem', color: 'var(--color-text-secondary)', fontSize: '0.88rem', flexWrap: 'wrap' }}>
              <span>
                Player ID: <strong style={{ color: 'var(--color-text-primary)' }}>#{activePlayer?.playerId}</strong>
              </span>
              <span>•</span>
              <span style={{ display: 'flex', alignItems: 'center', gap: '0.35rem' }}>
                <Calendar size={14} color="#A78BFA" />
                Joined: {formatDate(activePlayer?.createdAt)}
              </span>
            </div>
          </div>
        </div>

        {/* Action Buttons */}
        <div style={{ display: 'flex', gap: '0.75rem', flexWrap: 'wrap' }}>
          <button
            onClick={() => navigate('/game')}
            className="btn btn-primary"
            style={{ padding: '0.75rem 1.4rem', fontSize: '0.95rem' }}
          >
            <Play size={16} /> Play Game
          </button>
          <button
            onClick={handleLogout}
            className="btn btn-danger"
            style={{ padding: '0.75rem 1.2rem', fontSize: '0.95rem' }}
          >
            <LogOut size={16} /> Logout
          </button>
        </div>
      </div>

      {error && (
        <div
          style={{
            marginBottom: '1.5rem',
            padding: '1rem',
            borderRadius: '8px',
            background: 'rgba(239, 68, 68, 0.15)',
            border: '1px solid rgba(239, 68, 68, 0.4)',
            color: 'var(--color-danger)',
          }}
        >
          {error}
        </div>
      )}

      {/* Career KPI Statistics Grid */}
      <div style={{ marginBottom: '2rem' }}>
        <h2 style={{ fontSize: '1.25rem', fontWeight: 700, marginBottom: '1rem' }}>
          Career Statistics
        </h2>
        <div
          style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
            gap: '1rem',
          }}
        >
          <div className="card text-center" style={{ padding: '1.5rem 1rem' }}>
            <div style={{ display: 'flex', justifyContent: 'center', marginBottom: '0.5rem' }}>
              <Zap size={24} color="#3B82F6" />
            </div>
            <div style={{ fontSize: '0.82rem', color: 'var(--color-text-muted)', textTransform: 'uppercase' }}>
              Total Sessions
            </div>
            <div style={{ fontSize: '2.1rem', fontWeight: 800, color: 'var(--color-text-primary)', marginTop: '0.2rem' }}>
              {totalGames}
            </div>
          </div>

          <div className="card text-center" style={{ padding: '1.5rem 1rem' }}>
            <div style={{ display: 'flex', justifyContent: 'center', marginBottom: '0.5rem' }}>
              <Trophy size={24} color="#F59E0B" />
            </div>
            <div style={{ fontSize: '0.82rem', color: 'var(--color-text-muted)', textTransform: 'uppercase' }}>
              High Score
            </div>
            <div style={{ fontSize: '2.1rem', fontWeight: 800, color: '#F59E0B', marginTop: '0.2rem' }}>
              {bestScore}
            </div>
          </div>

          <div className="card text-center" style={{ padding: '1.5rem 1rem' }}>
            <div style={{ display: 'flex', justifyContent: 'center', marginBottom: '0.5rem' }}>
              <Target size={24} color="#10B981" />
            </div>
            <div style={{ fontSize: '0.82rem', color: 'var(--color-text-muted)', textTransform: 'uppercase' }}>
              Accuracy Rate
            </div>
            <div style={{ fontSize: '2.1rem', fontWeight: 800, color: 'var(--color-success)', marginTop: '0.2rem' }}>
              {overallAccuracy}%
            </div>
          </div>

          <div className="card text-center" style={{ padding: '1.5rem 1rem' }}>
            <div style={{ display: 'flex', justifyContent: 'center', marginBottom: '0.5rem' }}>
              <Clock size={24} color="#8B5CF6" />
            </div>
            <div style={{ fontSize: '0.82rem', color: 'var(--color-text-muted)', textTransform: 'uppercase' }}>
              Fastest Round
            </div>
            <div style={{ fontSize: '2.1rem', fontWeight: 800, color: 'var(--color-purple)', marginTop: '0.2rem' }}>
              {totalGames > 0 && fastestTime < 999999 ? `${fastestTime}s` : '—'}
            </div>
          </div>
        </div>
      </div>

      {/* Performance by Difficulty Level */}
      <div style={{ marginBottom: '2.5rem' }}>
        <h2 style={{ fontSize: '1.25rem', fontWeight: 700, marginBottom: '1rem' }}>
          Performance by Difficulty
        </h2>
        <div
          style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))',
            gap: '1.25rem',
          }}
        >
          {/* Easy Card */}
          <div className="card" style={{ borderLeft: '4px solid #10B981' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
              <h3 style={{ fontSize: '1.15rem', fontWeight: 700, margin: 0 }}>Easy Mode</h3>
              <span className="badge badge-easy">Beginner</span>
            </div>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem', fontSize: '0.9rem' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                <span style={{ color: 'var(--color-text-secondary)' }}>Rounds Played:</span>
                <strong>{easyStats.count}</strong>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                <span style={{ color: 'var(--color-text-secondary)' }}>Best Score:</span>
                <strong style={{ color: '#10B981' }}>{easyStats.maxScore} pts</strong>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                <span style={{ color: 'var(--color-text-secondary)' }}>Avg Accuracy:</span>
                <strong>{easyStats.acc}%</strong>
              </div>
            </div>
          </div>

          {/* Medium Card */}
          <div className="card" style={{ borderLeft: '4px solid #3B82F6' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
              <h3 style={{ fontSize: '1.15rem', fontWeight: 700, margin: 0 }}>Medium Mode</h3>
              <span className="badge badge-medium">Intermediate</span>
            </div>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem', fontSize: '0.9rem' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                <span style={{ color: 'var(--color-text-secondary)' }}>Rounds Played:</span>
                <strong>{mediumStats.count}</strong>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                <span style={{ color: 'var(--color-text-secondary)' }}>Best Score:</span>
                <strong style={{ color: '#3B82F6' }}>{mediumStats.maxScore} pts</strong>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                <span style={{ color: 'var(--color-text-secondary)' }}>Avg Accuracy:</span>
                <strong>{mediumStats.acc}%</strong>
              </div>
            </div>
          </div>

          {/* Hard Card */}
          <div className="card" style={{ borderLeft: '4px solid #8B5CF6' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
              <h3 style={{ fontSize: '1.15rem', fontWeight: 700, margin: 0 }}>Hard Mode</h3>
              <span className="badge badge-hard">Expert</span>
            </div>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem', fontSize: '0.9rem' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                <span style={{ color: 'var(--color-text-secondary)' }}>Rounds Played:</span>
                <strong>{hardStats.count}</strong>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                <span style={{ color: 'var(--color-text-secondary)' }}>Best Score:</span>
                <strong style={{ color: '#8B5CF6' }}>{hardStats.maxScore} pts</strong>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                <span style={{ color: 'var(--color-text-secondary)' }}>Avg Accuracy:</span>
                <strong>{hardStats.acc}%</strong>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Quick Navigation Hub */}
      <div
        className="card"
        style={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          flexWrap: 'wrap',
          gap: '1rem',
          background: 'rgba(18, 24, 43, 0.6)',
        }}
      >
        <div>
          <h3 style={{ fontSize: '1.1rem', fontWeight: 700, margin: '0 0 0.35rem 0' }}>
            Ready to break your personal best?
          </h3>
          <p style={{ color: 'var(--color-text-secondary)', fontSize: '0.9rem', margin: 0 }}>
            Every session tracks your accuracy and speed. Climb up the global leaderboard!
          </p>
        </div>
        <div style={{ display: 'flex', gap: '0.75rem', flexWrap: 'wrap' }}>
          <Link to="/history" className="btn btn-secondary">
            <History size={15} /> Full History
          </Link>
          <Link to="/leaderboard" className="btn btn-secondary">
            <Trophy size={15} color="#F59E0B" /> Leaderboard
          </Link>
          <Link to="/game" className="btn btn-primary">
            <Play size={15} /> Play Now
          </Link>
        </div>
      </div>
    </div>
  );
}
