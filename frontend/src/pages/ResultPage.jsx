import React from 'react';
import { useLocation, useNavigate, Link } from 'react-router-dom';

export default function ResultPage() {
  const location = useLocation();
  const navigate = useNavigate();

  const state = location.state;

  if (!state) {
    return (
      <div className="container" style={{ maxWidth: '520px', padding: '5rem 1rem', textAlign: 'center' }}>
        <div className="card">
          <h2 style={{ marginBottom: '1rem' }}>No Result Found</h2>
          <p style={{ color: 'var(--color-text-secondary)', marginBottom: '1.5rem' }}>
            It looks like no recent game session data is available.
          </p>
          <Link to="/dashboard" className="btn btn-primary">
            Go to Dashboard
          </Link>
        </div>
      </div>
    );
  }

  const {
    difficulty = 'EASY',
    totalQuestions = 10,
    correctAnswers = 0,
    timeTaken = 0,
    savedScore = 0,
    history = [],
    submitError = null,
  } = state;

  const incorrectAnswers = totalQuestions - correctAnswers;
  const accuracy = totalQuestions > 0 ? ((correctAnswers / totalQuestions) * 100).toFixed(1) : '0.0';
  const avgTimePerQuestion = totalQuestions > 0 ? (timeTaken / totalQuestions).toFixed(1) : '0.0';

  const formatTimer = (seconds) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}m ${secs}s`;
  };

  const getPerformanceMessage = (acc) => {
    const num = parseFloat(acc);
    if (num === 100) return { title: '🏆 Flawless Victory!', subtitle: 'Incredible speed and 100% precision! You mastered this set.' };
    if (num >= 80) return { title: '🌟 Outstanding Performance!', subtitle: 'Great mental reflexes and sharp arithmetic accuracy.' };
    if (num >= 60) return { title: '👍 Good Job!', subtitle: 'Solid effort! Consistent practice will boost your speed further.' };
    return { title: '💪 Keep Practicing!', subtitle: 'Speed comes with repetition. Give it another shot!' };
  };

  const perf = getPerformanceMessage(accuracy);

  return (
    <div className="container" style={{ width: '100%', maxWidth: '1080px', margin: '0 auto', padding: '2rem 1.5rem' }}>
      {/* Submit Error alert if any */}
      {submitError && (
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
          <strong>Notice:</strong> {submitError} (Your results are still shown below).
        </div>
      )}

      {/* Main Result Card */}
      <div
        className="card card-glow"
        style={{
          textAlign: 'center',
          padding: '2.5rem 1.5rem',
          marginBottom: '2rem',
          background: 'linear-gradient(135deg, rgba(30, 41, 75, 0.9) 0%, rgba(18, 24, 43, 0.98) 100%)',
        }}
      >
        <span className={`badge badge-${difficulty.toLowerCase()}`} style={{ marginBottom: '1rem' }}>
          {difficulty} MODE
        </span>

        <h1 style={{ fontSize: '2.2rem', fontWeight: 800, margin: '0.5rem 0' }}>{perf.title}</h1>
        <p style={{ color: 'var(--color-text-secondary)', fontSize: '1rem', maxWidth: '520px', margin: '0 auto 2rem' }}>
          {perf.subtitle}
        </p>

        {/* Score Showcase */}
        <div
          style={{
            display: 'inline-block',
            padding: '1.25rem 2.5rem',
            borderRadius: '16px',
            background: 'rgba(0, 0, 0, 0.35)',
            border: '1px solid var(--color-border)',
            marginBottom: '2.5rem',
          }}
        >
          <div style={{ fontSize: '0.85rem', color: 'var(--color-text-muted)', textTransform: 'uppercase', letterSpacing: '0.1em' }}>
            Final Game Score
          </div>
          <div
            className="gradient-text"
            style={{ fontSize: '3.5rem', fontWeight: 800, fontFamily: 'monospace', lineHeight: 1.1 }}
          >
            {savedScore}
          </div>
          <div style={{ fontSize: '0.85rem', color: 'var(--color-purple)', fontWeight: 600 }}>Points Earned</div>
        </div>

        {/* 4 Core Stat Cards */}
        <div
          style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fit, minmax(160px, 1fr))',
            gap: '1rem',
            textAlign: 'center',
          }}
        >
          <div className="card" style={{ padding: '1.25rem 1rem', background: 'rgba(16, 185, 129, 0.08)' }}>
            <div style={{ fontSize: '1.8rem', fontWeight: 800, color: 'var(--color-success)' }}>{correctAnswers}</div>
            <div style={{ fontSize: '0.82rem', color: 'var(--color-text-muted)', marginTop: '0.25rem' }}>Correct Answers</div>
          </div>

          <div className="card" style={{ padding: '1.25rem 1rem', background: 'rgba(239, 68, 68, 0.08)' }}>
            <div style={{ fontSize: '1.8rem', fontWeight: 800, color: 'var(--color-danger)' }}>{incorrectAnswers}</div>
            <div style={{ fontSize: '0.82rem', color: 'var(--color-text-muted)', marginTop: '0.25rem' }}>Incorrect Answers</div>
          </div>

          <div className="card" style={{ padding: '1.25rem 1rem', background: 'rgba(59, 130, 246, 0.08)' }}>
            <div style={{ fontSize: '1.8rem', fontWeight: 800, color: 'var(--color-blue)' }}>{accuracy}%</div>
            <div style={{ fontSize: '0.82rem', color: 'var(--color-text-muted)', marginTop: '0.25rem' }}>Accuracy Rate</div>
          </div>

          <div className="card" style={{ padding: '1.25rem 1rem', background: 'rgba(139, 92, 246, 0.08)' }}>
            <div style={{ fontSize: '1.8rem', fontWeight: 800, color: 'var(--color-purple)' }}>{formatTimer(timeTaken)}</div>
            <div style={{ fontSize: '0.82rem', color: 'var(--color-text-muted)', marginTop: '0.25rem' }}>
              Time Taken (~{avgTimePerQuestion}s/q)
            </div>
          </div>
        </div>

        {/* Navigation Action Buttons */}
        <div
          style={{
            display: 'flex',
            justifyContent: 'center',
            flexWrap: 'wrap',
            gap: '1rem',
            marginTop: '2.5rem',
          }}
        >
          <button
            onClick={() => navigate('/game', { state: { difficulty } })}
            className="btn btn-primary"
            style={{ padding: '0.85rem 1.75rem', fontSize: '1rem' }}
          >
            🎮 Play Again ({difficulty})
          </button>
          <button
            onClick={() => navigate('/leaderboard')}
            className="btn btn-secondary"
            style={{ padding: '0.85rem 1.5rem', fontSize: '1rem' }}
          >
            🏆 Leaderboard
          </button>
          <button
            onClick={() => navigate('/history')}
            className="btn btn-secondary"
            style={{ padding: '0.85rem 1.5rem', fontSize: '1rem' }}
          >
            📊 My Scores
          </button>
          <button
            onClick={() => navigate('/profile')}
            className="btn btn-secondary"
            style={{ padding: '0.85rem 1.5rem', fontSize: '1rem' }}
          >
            👤 Profile
          </button>
          <button
            onClick={() => navigate('/dashboard')}
            className="btn btn-secondary"
            style={{ padding: '0.85rem 1.5rem', fontSize: '1rem' }}
          >
            Dashboard
          </button>
        </div>
      </div>

      {/* Detailed Question Review Breakdown */}
      {history && history.length > 0 && (
        <div className="card">
          <h2 style={{ fontSize: '1.25rem', fontWeight: 700, marginBottom: '1.25rem' }}>
            Question-by-Question Review
          </h2>
          <div className="table-container">
            <table>
              <thead>
                <tr>
                  <th style={{ width: '60px' }}>#</th>
                  <th>Question</th>
                  <th>Your Answer</th>
                  <th>Correct Answer</th>
                  <th style={{ textAlign: 'center', width: '100px' }}>Status</th>
                </tr>
              </thead>
              <tbody>
                {history.map((item, idx) => (
                  <tr key={idx}>
                    <td style={{ color: 'var(--color-text-muted)' }}>{item.questionNumber || idx + 1}</td>
                    <td style={{ fontFamily: 'monospace', fontWeight: 600 }}>
                      {item.operand1} {item.operator} {item.operand2}
                    </td>
                    <td
                      style={{
                        fontFamily: 'monospace',
                        fontWeight: 700,
                        color: item.isCorrect ? 'var(--color-success)' : 'var(--color-danger)',
                      }}
                    >
                      {item.userAnswer}
                    </td>
                    <td style={{ fontFamily: 'monospace', fontWeight: 600 }}>{item.correctAnswer}</td>
                    <td style={{ textAlign: 'center' }}>
                      {item.isCorrect ? (
                        <span style={{ color: 'var(--color-success)', fontWeight: 700 }}>✓ Correct</span>
                      ) : (
                        <span style={{ color: 'var(--color-danger)', fontWeight: 700 }}>✗ Missed</span>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  );
}
