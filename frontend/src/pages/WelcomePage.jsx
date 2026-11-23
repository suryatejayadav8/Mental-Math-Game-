import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Brain, Zap, Clock, Trophy, Target, ArrowRight, Sparkles, ShieldCheck } from 'lucide-react';

export default function WelcomePage() {
  const { isAuthenticated } = useAuth();
  const navigate = useNavigate();

  return (
    <div className="container" style={{ width: '100%', maxWidth: '1300px', margin: '0 auto', padding: '50px 20px' }}>
      {/* Hero Section */}
      <div style={{ textAlign: 'center', marginBottom: '60px' }}>
        <div style={{
          display: 'inline-flex',
          alignItems: 'center',
          gap: '8px',
          background: 'rgba(139, 92, 246, 0.12)',
          border: '1px solid rgba(139, 92, 246, 0.3)',
          padding: '6px 16px',
          borderRadius: '30px',
          color: '#A78BFA',
          fontSize: '13px',
          fontWeight: 600,
          marginBottom: '20px',
        }}>
          <Sparkles size={15} /> Real-Time Arithmetic Agility Challenge
        </div>

        <h1 style={{
          fontSize: '48px',
          fontWeight: 800,
          lineHeight: 1.15,
          letterSpacing: '-1px',
          marginBottom: '16px',
        }}>
          Improve Your <span className="gradient-text">Calculation Speed</span>
        </h1>

        <p style={{
          fontSize: '18px',
          color: 'var(--text-secondary)',
          maxWidth: '650px',
          margin: '0 auto 36px',
          lineHeight: 1.6,
        }}>
          Challenge your brain with rapid 10-question math rounds. Boost speed, test accuracy,
          and climb the global leaderboard powered by MySQL.
        </p>

        {/* Primary Action Buttons */}
        <div style={{ display: 'flex', justifyContent: 'center', gap: '16px', flexWrap: 'wrap' }}>
          {isAuthenticated ? (
            <Link to="/dashboard" className="btn btn-emerald" style={{ padding: '14px 32px', fontSize: '16px' }}>
              <Zap size={18} /> Enter Dashboard <ArrowRight size={18} />
            </Link>
          ) : (
            <>
              <Link to="/login" className="btn btn-emerald" style={{ padding: '14px 32px', fontSize: '16px' }}>
                <Zap size={18} /> Play Now
              </Link>
              <Link to="/register" className="btn btn-primary" style={{ padding: '14px 30px', fontSize: '16px' }}>
                <Sparkles size={18} /> Create Account
              </Link>
              <Link to="/leaderboard" className="btn btn-secondary" style={{ padding: '14px 26px', fontSize: '16px' }}>
                <Trophy size={18} color="#F59E0B" /> Leaderboard
              </Link>
            </>
          )}
        </div>
      </div>

      {/* Highlights Grid */}
      <div style={{
        display: 'grid',
        gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))',
        gap: '20px',
        marginBottom: '60px',
      }}>
        <div className="glass-card" style={{ padding: '26px' }}>
          <div style={{
            background: 'rgba(56, 189, 248, 0.15)',
            width: '46px',
            height: '46px',
            borderRadius: '12px',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            marginBottom: '16px',
          }}>
            <Clock size={24} color="#38BDF8" />
          </div>
          <h3 style={{ fontSize: '18px', fontWeight: 700, marginBottom: '8px' }}>10 Questions Round</h3>
          <p style={{ fontSize: '14px', color: 'var(--text-secondary)', lineHeight: 1.5 }}>
            Fast-paced arithmetic rounds that measure your reaction time and calculation agility under pressure.
          </p>
        </div>

        <div className="glass-card" style={{ padding: '26px' }}>
          <div style={{
            background: 'rgba(139, 92, 246, 0.15)',
            width: '46px',
            height: '46px',
            borderRadius: '12px',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            marginBottom: '16px',
          }}>
            <Target size={24} color="#A78BFA" />
          </div>
          <h3 style={{ fontSize: '18px', fontWeight: 700, marginBottom: '8px' }}>3 Difficulty Levels</h3>
          <p style={{ fontSize: '14px', color: 'var(--text-secondary)', lineHeight: 1.5 }}>
            Choose from Easy (single digits), Medium (double digits), or Hard (triple digits & speed multipliers).
          </p>
        </div>

        <div className="glass-card" style={{ padding: '26px' }}>
          <div style={{
            background: 'rgba(16, 185, 129, 0.15)',
            width: '46px',
            height: '46px',
            borderRadius: '12px',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            marginBottom: '16px',
          }}>
            <Zap size={24} color="#34D399" />
          </div>
          <h3 style={{ fontSize: '18px', fontWeight: 700, marginBottom: '8px' }}>Speed & Accuracy Scoring</h3>
          <p style={{ fontSize: '14px', color: 'var(--text-secondary)', lineHeight: 1.5 }}>
            Earn base points plus speed bonuses for quick thinking. The faster you solve, the higher your score.
          </p>
        </div>

        <div className="glass-card" style={{ padding: '26px' }}>
          <div style={{
            background: 'rgba(245, 158, 11, 0.15)',
            width: '46px',
            height: '46px',
            borderRadius: '12px',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            marginBottom: '16px',
          }}>
            <Trophy size={24} color="#FBBF24" />
          </div>
          <h3 style={{ fontSize: '18px', fontWeight: 700, marginBottom: '8px' }}>Global Leaderboard</h3>
          <p style={{ fontSize: '14px', color: 'var(--text-secondary)', lineHeight: 1.5 }}>
            All session records are stored directly in MySQL. Compete against others for the Top 10 rankings!
          </p>
        </div>
      </div>
    </div>
  );
}
