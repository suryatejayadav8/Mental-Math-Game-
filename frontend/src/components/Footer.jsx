import React from 'react';
import { Brain, Heart } from 'lucide-react';

export default function Footer() {
  return (
    <footer style={{
      borderTop: '1px solid rgba(139, 92, 246, 0.15)',
      background: 'rgba(8, 12, 21, 0.9)',
      padding: '24px 20px',
      marginTop: 'auto',
      textAlign: 'center',
    }}>
      <div style={{
        maxWidth: '1200px',
        margin: '0 auto',
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        gap: '8px',
      }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '8px', color: '#94A3B8', fontSize: '13px' }}>
          <Brain size={16} color="#8B5CF6" />
          <span>Mental Math Game – Full-Stack React & Spring Boot Application</span>
        </div>
        <div style={{ fontSize: '12px', color: '#64748B' }}>
          Powered by React 18, Vite, Java Spring Boot, JDBC, and MySQL. Ready for Vercel deployment.
        </div>
      </div>
    </footer>
  );
}
