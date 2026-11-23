import React, { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { api } from '../api/api';

export default function ScoreHistoryPage() {
  const { player, isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const [scores, setScores] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (!isAuthenticated || !player) {
      navigate('/login', { state: { returnTo: '/history' } });
      return;
    }

    let isMounted = true;
    async function fetchScores() {
      try {
        setLoading(true);
        setError(null);
        const data = await api.scores.getPlayerScores(player.playerId);
        if (isMounted) {
          setScores(Array.isArray(data) ? data : []);
        }
      } catch (err) {
        if (isMounted) {
          setError(err.message || 'Failed to load score history.');
        }
      } finally {
        if (isMounted) {
          setLoading(false);
        }
      }
    }

    fetchScores();

    return () => {
      isMounted = false;
    };
  }, [player, isAuthenticated, navigate]);

  const formatDate = (dateStr) => {
    if (!dateStr) return '—';
    try {
      const d = new Date(dateStr);
      return d.toLocaleDateString(undefined, {
        month: 'short',
        day: 'numeric',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
      });
    } catch {
      return dateStr;
    }
  };

  // Compute summary metrics
  const totalGames = scores.length;
  const bestScore = totalGames > 0 ? Math.max(...scores.map((s) => s.score || 0)) : 0;
  const avgAccuracy =
    totalGames > 0
      ? (
          scores.reduce((acc, s) => {
            const pct = s.totalQuestions > 0 ? (s.correctAnswers / s.totalQuestions) * 100 : 0;
            return acc + pct;
          }, 0) / totalGames
        ).toFixed(1)
      : '0.0';
  const fastestTime =
    totalGames > 0 ? Math.min(...scores.map((s) => s.timeTaken || 999999)) : 0;

  return (
    <div className="container" style={{ width: '100%', maxWidth: '1200px', margin: '0 auto', padding: '2rem 1.5rem' }}>
      {/* Header Banner */}
      <div
        className="card card-glow"
        style={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          flexWrap: 'wrap',
          gap: '1rem',
          marginBottom: '2rem',
          padding: '2rem',
          background: 'linear-gradient(135deg, rgba(30, 41, 75, 0.8) 0%, rgba(18, 24, 43, 0.95) 100%)',
        }}
      >
        <div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '0.4rem' }}>
            <span style={{ fontSize: '2rem' }}>📊</span>
            <h1 style={{ fontSize: '1.85rem', fontWeight: 800, margin: 0 }}>My Score History</h1>
          </div>
          <p style={{ color: 'var(--color-text-secondary)', margin: 0, fontSize: '0.95rem' }}>
            Track your arithmetic calculation progression and personal bests.
          </p>
        </div>

        <div style={{ display: 'flex', gap: '0.75rem' }}>
          <button onClick={() => navigate('/dashboard')} className="btn btn-secondary">
            Dashboard
          </button>
          <button onClick={() => navigate('/game')} className="btn btn-primary">
            🎮 Play Game
          </button>
        </div>
      </div>

      {/* Summary KPI Cards */}
      <div
        style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
          gap: '1rem',
          marginBottom: '2rem',
        }}
      >
        <div className="card text-center" style={{ padding: '1.5rem 1rem' }}>
          <div style={{ fontSize: '0.82rem', color: 'var(--color-text-muted)', textTransform: 'uppercase' }}>
            Games Played
          </div>
          <div style={{ fontSize: '2.2rem', fontWeight: 800, color: 'var(--color-text-primary)', marginTop: '0.25rem' }}>
            {totalGames}
          </div>
        </div>

        <div className="card text-center" style={{ padding: '1.5rem 1rem' }}>
          <div style={{ fontSize: '0.82rem', color: 'var(--color-text-muted)', textTransform: 'uppercase' }}>
            Personal Best Score
          </div>
          <div style={{ fontSize: '2.2rem', fontWeight: 800, color: 'var(--color-purple)', marginTop: '0.25rem' }}>
            {bestScore}
          </div>
        </div>

        <div className="card text-center" style={{ padding: '1.5rem 1rem' }}>
          <div style={{ fontSize: '0.82rem', color: 'var(--color-text-muted)', textTransform: 'uppercase' }}>
            Average Accuracy
          </div>
          <div style={{ fontSize: '2.2rem', fontWeight: 800, color: 'var(--color-blue)', marginTop: '0.25rem' }}>
            {avgAccuracy}%
          </div>
        </div>

        <div className="card text-center" style={{ padding: '1.5rem 1rem' }}>
          <div style={{ fontSize: '0.82rem', color: 'var(--color-text-muted)', textTransform: 'uppercase' }}>
            Fastest Session
          </div>
          <div style={{ fontSize: '2.2rem', fontWeight: 800, color: 'var(--color-success)', marginTop: '0.25rem' }}>
            {totalGames > 0 && fastestTime < 999999 ? `${fastestTime}s` : '—'}
          </div>
        </div>
      </div>

      {/* Historical Games Table Card */}
      <div className="card">
        <h2 style={{ fontSize: '1.25rem', fontWeight: 700, marginBottom: '1.25rem' }}>
          All Game Sessions
        </h2>

        {loading ? (
          <div style={{ textAlign: 'center', padding: '3rem 1rem' }}>
            <div className="spinner" style={{ margin: '0 auto 1rem' }}></div>
            <p style={{ color: 'var(--color-text-secondary)' }}>Loading your records...</p>
          </div>
        ) : error ? (
          <div style={{ textAlign: 'center', padding: '2.5rem 1rem' }}>
            <div style={{ fontSize: '2rem', marginBottom: '0.5rem' }}>⚠️</div>
            <p style={{ color: 'var(--color-danger)' }}>{error}</p>
          </div>
        ) : scores.length === 0 ? (
          <div style={{ textAlign: 'center', padding: '4rem 1rem' }}>
            <div style={{ fontSize: '3rem', marginBottom: '1rem' }}>🎯</div>
            <h3 style={{ fontSize: '1.2rem', marginBottom: '0.5rem' }}>No Games Played Yet</h3>
            <p style={{ color: 'var(--color-text-secondary)', marginBottom: '1.5rem' }}>
              You haven't played any mental math rounds yet. Start a session now to record your first score!
            </p>
            <Link to="/game" className="btn btn-primary">
              Play Your First Game
            </Link>
          </div>
        ) : (
          <div className="table-container">
            <table>
              <thead>
                <tr>
                  <th>Date & Time</th>
                  <th>Difficulty</th>
                  <th style={{ textAlign: 'center' }}>Correct / Total</th>
                  <th style={{ textAlign: 'center' }}>Accuracy</th>
                  <th style={{ textAlign: 'right' }}>Score</th>
                  <th style={{ textAlign: 'right' }}>Time</th>
                </tr>
              </thead>
              <tbody>
                {scores.map((s, idx) => {
                  const acc =
                    s.totalQuestions > 0
                      ? ((s.correctAnswers / s.totalQuestions) * 100).toFixed(0)
                      : '0';

                  return (
                    <tr key={s.scoreId || idx}>
                      <td style={{ color: 'var(--color-text-secondary)', fontSize: '0.9rem' }}>
                        {formatDate(s.playedAt)}
                      </td>
                      <td>
                        <span className={`badge badge-${(s.difficulty || 'EASY').toLowerCase()}`}>
                          {s.difficulty}
                        </span>
                      </td>
                      <td style={{ textAlign: 'center', fontFamily: 'monospace' }}>
                        <span style={{ color: 'var(--color-success)', fontWeight: 700 }}>
                          {s.correctAnswers}
                        </span>{' '}
                        / {s.totalQuestions}
                      </td>
                      <td style={{ textAlign: 'center', fontWeight: 600 }}>{acc}%</td>
                      <td
                        style={{
                          textAlign: 'right',
                          fontFamily: 'monospace',
                          fontSize: '1.05rem',
                          fontWeight: 800,
                          color: 'var(--color-purple)',
                        }}
                      >
                        {s.score}
                      </td>
                      <td style={{ textAlign: 'right', fontFamily: 'monospace', color: 'var(--color-text-muted)' }}>
                        {s.timeTaken}s
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}
