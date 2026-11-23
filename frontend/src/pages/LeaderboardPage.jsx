import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { api } from '../api/api';
import { useAuth } from '../context/AuthContext';

export default function LeaderboardPage() {
  const [leaderboard, setLeaderboard] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const { player } = useAuth();
  const navigate = useNavigate();

  const fetchLeaderboard = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await api.scores.getLeaderboard(10);
      setLeaderboard(Array.isArray(data) ? data : []);
    } catch (err) {
      setError(err.message || 'Unable to fetch leaderboard.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchLeaderboard();
  }, []);

  const formatDate = (dateStr) => {
    if (!dateStr) return '—';
    try {
      const d = new Date(dateStr);
      return d.toLocaleDateString(undefined, { month: 'short', day: 'numeric', year: 'numeric' });
    } catch {
      return dateStr;
    }
  };

  const getRankBadge = (rank) => {
    if (rank === 1) return <span style={{ fontSize: '1.25rem' }}>🥇</span>;
    if (rank === 2) return <span style={{ fontSize: '1.25rem' }}>🥈</span>;
    if (rank === 3) return <span style={{ fontSize: '1.25rem' }}>🥉</span>;
    return <span style={{ fontWeight: 700, color: 'var(--color-text-muted)' }}>#{rank}</span>;
  };

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
            <span style={{ fontSize: '2rem' }}>🏆</span>
            <h1 style={{ fontSize: '1.85rem', fontWeight: 800, margin: 0 }}>Global Leaderboard</h1>
          </div>
          <p style={{ color: 'var(--color-text-secondary)', margin: 0, fontSize: '0.95rem' }}>
            Top 10 highest-scoring mental math athletes around the world.
          </p>
        </div>

        <div style={{ display: 'flex', gap: '0.75rem' }}>
          <button onClick={fetchLeaderboard} className="btn btn-secondary" disabled={loading}>
            🔄 {loading ? 'Refreshing...' : 'Refresh'}
          </button>
          <button onClick={() => navigate('/dashboard')} className="btn btn-primary">
            🎮 Play Now
          </button>
        </div>
      </div>

      {/* Leaderboard Table Card */}
      <div className="card">
        {loading ? (
          <div style={{ textAlign: 'center', padding: '4rem 1rem' }}>
            <div className="spinner" style={{ margin: '0 auto 1rem' }}></div>
            <p style={{ color: 'var(--color-text-secondary)' }}>Loading leaderboard records...</p>
          </div>
        ) : error ? (
          <div style={{ textAlign: 'center', padding: '3rem 1rem' }}>
            <div style={{ fontSize: '2.5rem', marginBottom: '0.75rem' }}>⚠️</div>
            <p style={{ color: 'var(--color-danger)', marginBottom: '1rem' }}>{error}</p>
            <button onClick={fetchLeaderboard} className="btn btn-secondary">
              Retry
            </button>
          </div>
        ) : leaderboard.length === 0 ? (
          <div style={{ textAlign: 'center', padding: '4rem 1rem' }}>
            <div style={{ fontSize: '3rem', marginBottom: '1rem' }}>🎯</div>
            <h3 style={{ fontSize: '1.25rem', marginBottom: '0.5rem' }}>No Scores Recorded Yet</h3>
            <p style={{ color: 'var(--color-text-secondary)', marginBottom: '1.5rem' }}>
              Be the first mental math player to set a high score on the board!
            </p>
            <button onClick={() => navigate('/dashboard')} className="btn btn-primary">
              Play First Game
            </button>
          </div>
        ) : (
          <div className="table-container">
            <table>
              <thead>
                <tr>
                  <th style={{ width: '70px', textAlign: 'center' }}>Rank</th>
                  <th>Player</th>
                  <th>Difficulty</th>
                  <th style={{ textAlign: 'right' }}>Score</th>
                  <th style={{ textAlign: 'center' }}>Correct</th>
                  <th style={{ textAlign: 'center' }}>Accuracy</th>
                  <th style={{ textAlign: 'center' }}>Time</th>
                  <th style={{ textAlign: 'right' }}>Date</th>
                </tr>
              </thead>
              <tbody>
                {leaderboard.map((item, idx) => {
                  const rank = idx + 1;
                  const isCurrentPlayer = player && item.playerId === player.playerId;
                  const acc =
                    item.totalQuestions > 0
                      ? ((item.correctAnswers / item.totalQuestions) * 100).toFixed(0)
                      : '0';

                  return (
                    <tr
                      key={item.scoreId || idx}
                      style={{
                        background: isCurrentPlayer ? 'rgba(139, 92, 246, 0.12)' : undefined,
                        borderLeft: isCurrentPlayer ? '3px solid var(--color-purple)' : undefined,
                      }}
                    >
                      <td style={{ textAlign: 'center' }}>{getRankBadge(rank)}</td>
                      <td>
                        <span style={{ fontWeight: 700, color: 'var(--color-text-primary)' }}>
                          {item.playerName || `Player #${item.playerId}`}
                        </span>
                        {isCurrentPlayer && (
                          <span
                            style={{
                              marginLeft: '0.5rem',
                              fontSize: '0.72rem',
                              padding: '2px 6px',
                              borderRadius: '4px',
                              background: 'var(--color-purple)',
                              color: 'white',
                              fontWeight: 700,
                            }}
                          >
                            YOU
                          </span>
                        )}
                      </td>
                      <td>
                        <span className={`badge badge-${(item.difficulty || 'EASY').toLowerCase()}`}>
                          {item.difficulty}
                        </span>
                      </td>
                      <td
                        style={{
                          textAlign: 'right',
                          fontFamily: 'monospace',
                          fontSize: '1.1rem',
                          fontWeight: 800,
                          color: rank === 1 ? '#F59E0B' : 'var(--color-purple)',
                        }}
                      >
                        {item.score}
                      </td>
                      <td style={{ textAlign: 'center', fontFamily: 'monospace' }}>
                        {item.correctAnswers} / {item.totalQuestions}
                      </td>
                      <td style={{ textAlign: 'center', fontWeight: 600 }}>{acc}%</td>
                      <td style={{ textAlign: 'center', fontFamily: 'monospace', color: 'var(--color-text-secondary)' }}>
                        {item.timeTaken}s
                      </td>
                      <td style={{ textAlign: 'right', color: 'var(--color-text-muted)', fontSize: '0.85rem' }}>
                        {formatDate(item.playedAt)}
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
