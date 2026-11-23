/**
 * Centralized API service connecting the React frontend to the Spring Boot REST API.
 * Uses the VITE_API_BASE_URL environment variable (defaults to http://localhost:8081).
 */
const BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081';

async function handleResponse(response) {
  const contentType = response.headers.get('content-type');
  const isJson = contentType && contentType.includes('application/json');
  const data = isJson ? await response.json() : await response.text();

  if (!response.ok) {
    const errorMsg = (data && data.message) || response.statusText || 'Request failed';
    throw new Error(errorMsg);
  }
  return data;
}

export const api = {
  // Authentication
  auth: {
    async register(playerName) {
      const res = await fetch(`${BASE_URL}/api/auth/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ playerName }),
      });
      return handleResponse(res);
    },

    async login(playerName) {
      const res = await fetch(`${BASE_URL}/api/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ playerName }),
      });
      return handleResponse(res);
    },

    async getPlayer(playerId) {
      const res = await fetch(`${BASE_URL}/api/players/${playerId}`);
      return handleResponse(res);
    },
  },

  // Game Engine & Scoring
  game: {
    async getQuestion(difficulty = 'EASY') {
      const res = await fetch(`${BASE_URL}/api/game/question?difficulty=${encodeURIComponent(difficulty)}`);
      return handleResponse(res);
    },

    async getQuestions(difficulty = 'EASY', count = 10) {
      const res = await fetch(
        `${BASE_URL}/api/game/questions?difficulty=${encodeURIComponent(difficulty)}&count=${count}`
      );
      return handleResponse(res);
    },

    async submitScore(payload) {
      const res = await fetch(`${BASE_URL}/api/game/submit`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
      });
      return handleResponse(res);
    },
  },

  // Leaderboard & History
  scores: {
    async getLeaderboard(limit = 10) {
      const res = await fetch(`${BASE_URL}/api/leaderboard?limit=${limit}`);
      return handleResponse(res);
    },

    async getPlayerScores(playerId) {
      const res = await fetch(`${BASE_URL}/api/scores/${playerId}`);
      return handleResponse(res);
    },
  },
};
