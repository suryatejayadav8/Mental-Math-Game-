# Mental Math Game – React Frontend (Vite)

Modern web interface for the Mental Math Game, built with React 18, Vite, and React Router 6.

## 🚀 Getting Started

### 1. Install Dependencies
```bash
npm install
```

### 2. Configure Environment Variables
Copy `.env.example` to `.env`:
```bash
cp .env.example .env
```
Edit `.env` if your backend is hosted somewhere other than `http://localhost:8080`:
```env
VITE_API_BASE_URL=http://localhost:8080
```

### 3. Start Development Server
```bash
npm run dev
```
The site runs at `http://localhost:5173`.

### 4. Build for Production
```bash
npm run build
```
Production assets are output into the `dist/` directory.

---

## ☁️ Deploying to Vercel

1. Import this repository into Vercel.
2. Set **Root Directory** to `frontend`.
3. Set **Framework Preset** to `Vite`.
4. Add the Environment Variable:
   - `VITE_API_BASE_URL` = `<your-spring-boot-backend-url>`
5. Click **Deploy**.

The repository includes `vercel.json` configured for Single Page Application client routing rewrites.
