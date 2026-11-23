import React, { useState, useEffect, useRef } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { api } from '../api/api';
import { Clock, CheckCircle2, XCircle, ArrowRight, AlertTriangle } from 'lucide-react';

export default function GamePage() {
  const { player, isAuthenticated } = useAuth();
  const location = useLocation();
  const navigate = useNavigate();

  const difficulty = (location.state?.difficulty || 'EASY').toUpperCase();

  const [questions, setQuestions] = useState([]);
  const [currentIndex, setCurrentIndex] = useState(0);
  const [userAnswer, setUserAnswer] = useState('');
  const [feedback, setFeedback] = useState(null); // { isCorrect: boolean, correctAnswer: number }
  const [resultsHistory, setResultsHistory] = useState([]);
  const [correctCount, setCorrectCount] = useState(0);
  const [timeTaken, setTimeTaken] = useState(0);
  const [status, setStatus] = useState('loading'); // 'loading' | 'playing' | 'submitting' | 'error'
  const [errorMessage, setErrorMessage] = useState('');

  const inputRef = useRef(null);
  const timerRef = useRef(null);

  // Redirect if not logged in
  useEffect(() => {
    if (!isAuthenticated) {
      navigate('/login', { state: { returnTo: '/game' } });
    }
  }, [isAuthenticated, navigate]);

  // Load questions on mount
  useEffect(() => {
    let isMounted = true;
    async function loadQuestions() {
      try {
        setStatus('loading');
        setErrorMessage('');
        const data = await api.game.getQuestions(difficulty, 10);
        if (isMounted) {
          if (Array.isArray(data) && data.length > 0) {
            setQuestions(data);
            setStatus('playing');
          } else {
            throw new Error('Failed to load questions from game server.');
          }
        }
      } catch (err) {
        if (isMounted) {
          setStatus('error');
          setErrorMessage(err.message || 'Unable to connect to game service.');
        }
      }
    }

    if (isAuthenticated) {
      loadQuestions();
    }

    return () => {
      isMounted = false;
    };
  }, [difficulty, isAuthenticated]);

  // Timer logic
  useEffect(() => {
    if (status === 'playing') {
      timerRef.current = setInterval(() => {
        setTimeTaken((prev) => prev + 1);
      }, 1000);
    } else {
      if (timerRef.current) clearInterval(timerRef.current);
    }

    return () => {
      if (timerRef.current) clearInterval(timerRef.current);
    };
  }, [status]);

  // Autofocus input on each question
  useEffect(() => {
    if (status === 'playing' && inputRef.current && !feedback) {
      inputRef.current.focus();
    }
  }, [currentIndex, status, feedback]);

  const formatTimer = (seconds) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
  };

  const handleAnswerSubmit = (e) => {
    if (e) e.preventDefault();
    if (feedback !== null || status !== 'playing') return;

    const trimmed = userAnswer.trim();
    if (trimmed === '' || isNaN(trimmed)) {
      return;
    }

    const currentQuestion = questions[currentIndex];
    const userVal = parseInt(trimmed, 10);
    const isCorrect = userVal === currentQuestion.correctAnswer;

    const newCorrectCount = isCorrect ? correctCount + 1 : correctCount;
    if (isCorrect) {
      setCorrectCount(newCorrectCount);
    }

    const answerRecord = {
      questionNumber: currentIndex + 1,
      questionText: currentQuestion.questionText,
      operand1: currentQuestion.operand1,
      operand2: currentQuestion.operand2,
      operator: currentQuestion.operator,
      correctAnswer: currentQuestion.correctAnswer,
      userAnswer: userVal,
      isCorrect,
    };

    const updatedHistory = [...resultsHistory, answerRecord];
    setResultsHistory(updatedHistory);

    setFeedback({
      isCorrect,
      correctAnswer: currentQuestion.correctAnswer,
      userAnswer: userVal,
    });

    // Automatically transition to next question after 1.1s
    setTimeout(() => {
      handleNext(updatedHistory, newCorrectCount);
    }, 1100);
  };

  const handleNext = async (currentHistory = resultsHistory, currentCorrect = correctCount) => {
    setFeedback(null);
    setUserAnswer('');

    if (currentIndex + 1 < questions.length) {
      setCurrentIndex((prev) => prev + 1);
    } else {
      // Completed all 10 questions!
      finishGame(currentHistory, currentCorrect);
    }
  };

  const finishGame = async (history, finalCorrect) => {
    setStatus('submitting');
    if (timerRef.current) clearInterval(timerRef.current);

    const totalQuestions = questions.length || 10;
    const finalTime = Math.max(1, timeTaken);

    let savedScore = null;
    let submitError = null;

    try {
      const submitPayload = {
        playerId: player.playerId,
        difficulty,
        totalQuestions,
        correctAnswers: finalCorrect,
        timeTaken: finalTime,
      };

      const res = await api.game.submitScore(submitPayload);
      if (res && res.data) {
        savedScore = res.data.score;
      }
    } catch (err) {
      console.error('Failed to save score:', err);
      submitError = err.message || 'Could not save score to database.';
    }

    navigate('/result', {
      replace: true,
      state: {
        playerId: player.playerId,
        playerName: player.playerName,
        difficulty,
        totalQuestions,
        correctAnswers: finalCorrect,
        timeTaken: finalTime,
        savedScore,
        history,
        submitError,
      },
    });
  };

  if (status === 'loading') {
    return (
      <div className="game-viewport">
        <div className="spinner" style={{ margin: '0 auto 1.5rem' }}></div>
        <h2 style={{ fontSize: '1.5rem', fontWeight: 700 }}>Generating Questions...</h2>
        <p style={{ color: 'var(--text-secondary)', fontSize: '0.95rem', marginTop: '0.5rem' }}>
          Preparing your {difficulty.toLowerCase()} mental arithmetic round.
        </p>
      </div>
    );
  }

  if (status === 'error') {
    return (
      <div className="game-viewport">
        <div className="card text-center" style={{ maxWidth: '520px', border: '1px solid rgba(239, 68, 68, 0.4)', padding: '2.5rem' }}>
          <div style={{ fontSize: '3rem', marginBottom: '1rem' }}>⚠️</div>
          <h2 style={{ color: 'var(--danger)', marginBottom: '0.5rem' }}>Game Error</h2>
          <p style={{ color: 'var(--text-secondary)', marginBottom: '1.5rem' }}>{errorMessage}</p>
          <div style={{ display: 'flex', gap: '1rem', justifyContent: 'center' }}>
            <button onClick={() => window.location.reload()} className="btn btn-primary">
              Retry
            </button>
            <button onClick={() => navigate('/dashboard')} className="btn btn-secondary">
              Back to Dashboard
            </button>
          </div>
        </div>
      </div>
    );
  }

  const currentQ = questions[currentIndex] || {};
  const progressPercent = ((currentIndex + 1) / questions.length) * 100;

  return (
    <div className="game-viewport">
      <div className="game-content-box">
        {/* Top HUD: Difficulty, Question Tracker, Score, Timer */}
        <div
          style={{
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            flexWrap: 'wrap',
            gap: '1rem',
            marginBottom: '1.5rem',
            width: '100%',
          }}
        >
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.85rem' }}>
            <span className={`badge badge-${difficulty.toLowerCase()}`} style={{ fontSize: '13px', padding: '6px 14px' }}>
              {difficulty}
            </span>
            <span style={{ fontSize: '1rem', fontWeight: 700, color: 'var(--text-secondary)' }}>
              Question <strong style={{ color: 'var(--text-main)' }}>{currentIndex + 1}</strong> of {questions.length}
            </span>
          </div>

          <div style={{ display: 'flex', alignItems: 'center', gap: '1.5rem' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', fontSize: '1rem' }}>
              <span style={{ color: 'var(--emerald)', fontWeight: 800 }}>✓ {correctCount}</span>
              <span style={{ color: 'var(--text-muted)' }}>/</span>
              <span style={{ color: 'var(--text-secondary)' }}>{currentIndex}</span>
            </div>

            <div
              style={{
                display: 'flex',
                alignItems: 'center',
                gap: '0.6rem',
                background: 'rgba(30, 41, 75, 0.8)',
                padding: '0.45rem 1.1rem',
                borderRadius: '24px',
                border: '1px solid var(--border-card)',
                fontFamily: 'monospace',
                fontSize: '1.15rem',
                fontWeight: 800,
                color: 'var(--primary-hover)',
                boxShadow: '0 4px 12px rgba(0, 0, 0, 0.3)',
              }}
            >
              <Clock size={16} />
              <span>{formatTimer(timeTaken)}</span>
            </div>
          </div>
        </div>

        {/* Full-width Progress Bar */}
        <div
          style={{
            width: '100%',
            height: '10px',
            background: 'rgba(255, 255, 255, 0.08)',
            borderRadius: '999px',
            overflow: 'hidden',
            marginBottom: '2.5rem',
          }}
        >
          <div
            style={{
              height: '100%',
              width: `${progressPercent}%`,
              background: 'linear-gradient(90deg, #3B82F6 0%, #8B5CF6 100%)',
              transition: 'width 0.3s ease',
            }}
          />
        </div>

        {/* Main Central Game Card */}
        <div
          className="card card-glow"
          style={{
            textAlign: 'center',
            padding: '3.5rem 2rem',
            position: 'relative',
            width: '100%',
            background: 'linear-gradient(135deg, rgba(22, 28, 52, 0.9) 0%, rgba(14, 18, 36, 0.98) 100%)',
          }}
        >
          {/* Question Text */}
          <div style={{ marginBottom: '2.5rem' }}>
            <span
              style={{
                fontSize: '0.9rem',
                textTransform: 'uppercase',
                letterSpacing: '0.15em',
                color: 'var(--text-muted)',
                fontWeight: 700,
              }}
            >
              Calculate the result
            </span>

            <div
              style={{
                fontSize: 'clamp(2.5rem, 6vw, 4.2rem)',
                fontWeight: 800,
                fontFamily: "'JetBrains Mono', 'Courier New', monospace",
                letterSpacing: '3px',
                margin: '0.75rem 0',
                color: 'var(--text-main)',
                lineHeight: 1.1,
              }}
            >
              {currentQ.operand1} {currentQ.operator} {currentQ.operand2}
            </div>

            <div style={{ fontSize: '1.75rem', color: 'var(--primary-hover)', fontWeight: 800 }}>
              = ?
            </div>
          </div>

          {/* Answer Input & Submit Form */}
          <form
            onSubmit={handleAnswerSubmit}
            style={{
              width: '100%',
              maxWidth: '380px',
              margin: '0 auto',
            }}
          >
            <div style={{ position: 'relative', marginBottom: '1.25rem' }}>
              <input
                ref={inputRef}
                type="number"
                value={userAnswer}
                onChange={(e) => setUserAnswer(e.target.value)}
                placeholder="Enter answer"
                disabled={feedback !== null || status === 'submitting'}
                style={{
                  width: '100%',
                  padding: '1.1rem',
                  fontSize: '2rem',
                  textAlign: 'center',
                  fontWeight: 800,
                  fontFamily: 'monospace',
                  borderRadius: '16px',
                  border: feedback
                    ? feedback.isCorrect
                      ? '2px solid var(--emerald)'
                      : '2px solid var(--danger)'
                    : '2px solid var(--border-card)',
                  background: feedback
                    ? feedback.isCorrect
                      ? 'rgba(16, 185, 129, 0.15)'
                      : 'rgba(239, 68, 68, 0.15)'
                    : 'var(--input-bg)',
                  color: 'var(--text-main)',
                  transition: 'all 0.2s ease',
                  outline: 'none',
                  boxShadow: 'inset 0 2px 4px rgba(0,0,0,0.4)',
                }}
              />
            </div>

            {/* Instant Feedback Banner */}
            {feedback && (
              <div
                style={{
                  marginBottom: '1.25rem',
                  padding: '0.9rem 1.25rem',
                  borderRadius: '12px',
                  fontSize: '1rem',
                  fontWeight: 700,
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  gap: '0.6rem',
                  background: feedback.isCorrect ? 'rgba(16, 185, 129, 0.2)' : 'rgba(239, 68, 68, 0.2)',
                  color: feedback.isCorrect ? '#34D399' : '#FCA5A5',
                  border: `1px solid ${feedback.isCorrect ? 'rgba(16, 185, 129, 0.4)' : 'rgba(239, 68, 68, 0.4)'}`,
                  animation: 'pulseGlow 1.5s ease',
                }}
              >
                {feedback.isCorrect ? (
                  <>
                    <CheckCircle2 size={20} color="#34D399" /> Correct! Perfect calculation!
                  </>
                ) : (
                  <>
                    <XCircle size={20} color="#EF4444" /> Incorrect! Correct answer was {feedback.correctAnswer}
                  </>
                )}
              </div>
            )}

            <button
              type="submit"
              disabled={userAnswer.trim() === '' || feedback !== null || status === 'submitting'}
              className="btn btn-primary"
              style={{
                width: '100%',
                padding: '1.05rem',
                fontSize: '1.15rem',
                fontWeight: 800,
                gap: '8px',
              }}
            >
              {feedback !== null ? 'Next Question...' : 'Submit Answer'} <ArrowRight size={18} />
            </button>
          </form>

          <p style={{ marginTop: '1.75rem', color: 'var(--text-muted)', fontSize: '0.88rem' }}>
            Tip: Press <kbd style={{ background: '#1E293B', padding: '2px 8px', borderRadius: '4px', border: '1px solid #475569', color: '#F8FAFC' }}>Enter</kbd> to submit instantaneously!
          </p>
        </div>

        {/* Exit Round Link */}
        <div style={{ textAlign: 'center', marginTop: '1.5rem' }}>
          <button
            onClick={() => {
              if (window.confirm('Are you sure you want to exit? Your game progress will be lost.')) {
                navigate('/dashboard');
              }
            }}
            style={{
              background: 'none',
              border: 'none',
              color: 'var(--text-muted)',
              cursor: 'pointer',
              fontSize: '0.9rem',
              textDecoration: 'underline',
              padding: '6px 12px',
            }}
          >
            Exit Game to Dashboard
          </button>
        </div>
      </div>
    </div>
  );
}
