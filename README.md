# 🧠 Mental Math Game – Full-Stack Web (React + Spring Boot) & Desktop (Swing)

A fun and interactive Mental Math Game that helps users improve their calculation speed, logical thinking, and mathematical skills through timed arithmetic challenges. Test your skills, solve random math questions, and track your performance!

### Key Features
1. **React Web Frontend (Vite + Modern UI)** – Ready for local development and **Vercel** deployment.
2. **Java Spring Boot REST API** – Secure backend with CORS, input validation, and JDBC database access.
3. **Java Swing Desktop GUI** – Fully preserved, dark-navy themed desktop interface.
4. **Terminal Console CLI** – Fallback command-line mode.
5. **MySQL Database** – Reuses existing `players` and `scores` tables with zero schema breaking changes.

---

## 🏗️ Architecture Overview

```
mental-math-game/
├── pom.xml                                      # Maven config: Spring Boot + Swing + JDBC + Test
├── README.md                                    # Root documentation
│
├── frontend/                                    # ⚛️ React Web Application (Vite)
│   ├── package.json                             # Dependencies: React 18, React Router 6, Lucide
│   ├── vite.config.js                           # Vite configuration with proxy to port 8081
│   ├── vercel.json                              # Vercel SPA routing rewrite rules
│   ├── .env.example                             # Environment template (VITE_API_BASE_URL)
│   ├── index.html                               # HTML entry point with modern fonts
│   └── src/
│       ├── api/api.js                           # Centralized REST client (VITE_API_BASE_URL)
│       ├── context/AuthContext.jsx              # Auth state & localStorage session management
│       ├── styles/index.css                     # Dark navy (#080C15) & purple (#8B5CF6) theme
│       ├── components/
│       │   ├── Navbar.jsx                       # Responsive navigation with player profile & links
│       │   └── Footer.jsx                       # Global footer
│       ├── pages/
│       │   ├── WelcomePage.jsx                  # Hero, feature highlights, difficulty breakdown
│       │   ├── LoginPage.jsx                    # Player authentication with validation
│       │   ├── RegisterPage.jsx                 # New player registration
│       │   ├── DashboardPage.jsx                # Player hub, difficulty launcher & stats
│       │   ├── GamePage.jsx                     # 10-question math engine, live timer, instant feedback
│       │   ├── ResultPage.jsx                   # Final score, accuracy %, review table, play again
│       │   ├── LeaderboardPage.jsx              # Global Top 10 rankings with rank medals
│       │   ├── ScoreHistoryPage.jsx             # Player's past games, personal bests, KPI cards
│       │   └── ProfilePage.jsx                  # Player profile, quick stats & account details
│       ├── App.jsx                              # Route definitions & layout wrappers
│       └── main.jsx                             # React DOM bootstrap
│
└── src/                                         # ☕ Java Backend & Desktop GUI
    ├── main/
    │   ├── resources/
    │   │   └── application.properties           # Spring Boot server config (port 8081, CORS)
    │   └── java/com/mentalmath/
    │       ├── MentalMathApiApplication.java    # 🚀 Spring Boot REST API entry point
    │       ├── Main.java                        # 🖥️ Desktop Swing GUI / CLI entry point
    │       ├── controller/
    │       │   ├── AuthController.java          # POST /api/auth/login, POST /api/auth/register, GET /api/players/{id}
    │       │   ├── GameController.java          # GET /api/game/question, GET /api/game/questions, POST /api/game/submit
    │       │   ├── ScoreController.java         # GET /api/leaderboard, GET /api/scores/{playerId}
    │       │   └── HealthController.java        # GET /api/health
    │       ├── dto/
    │       │   ├── ApiResponse.java             # Standard JSON response envelope
    │       │   ├── AuthRequest.java             # Player credentials request payload
    │       │   ├── AuthResponse.java            # Player authentication response payload
    │       │   └── GameSubmitRequest.java       # Session completion payload
    │       ├── db/
    │       │   └── DatabaseConnection.java      # JDBC manager & env credential reader
    │       ├── model/
    │       │   ├── Player.java                  # Maps to MySQL 'players' table
    │       │   ├── Score.java                   # Maps to MySQL 'scores' table
    │       │   └── Question.java                # Math question, answer & operand logic
    │       ├── manager/
    │       │   ├── PlayerManager.java           # Player SQL queries using PreparedStatement
    │       │   └── ScoreManager.java            # Score persistence, leaderboard join & history
    │       ├── game/
    │       │   ├── QuestionGenerator.java       # Generates Easy, Medium, Hard arithmetic
    │       │   └── Game.java                    # Scoring algorithms & speed bonuses
    │       └── gui/                             # 🖥️ Preserved Java Swing Desktop GUI screens
    │           ├── MainFrame.java
    │           ├── UITheme.java
    │           ├── components/
    │           └── screens/
    └── test/java/com/mentalmath/
        ├── DatabaseIntegrationTest.java         # JDBC integration test
        ├── QuestionGeneratorTest.java           # Math question logic tests
        └── ScoreModelTest.java                  # Accuracy & model tests
```

---

## 🗄️ MySQL Database Setup

The application connects to your existing MySQL database:
- **Database Name**: `mental_math_game`
- **Tables**: `players` and `scores` (unchanged schema)

### Environment Variables
Configure your database credentials in your environment. They are never committed or hardcoded in source code:

**PowerShell (Windows):**
```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/mental_math_game?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
$env:DB_USER="root"
$env:DB_PASSWORD="your_mysql_password_here"
```

**Linux / macOS:**
```bash
export DB_URL="jdbc:mysql://localhost:3306/mental_math_game?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
export DB_USER="root"
export DB_PASSWORD="your_mysql_password_here"
```

---

## 🚀 Running the Web Stack

### 1. Start the Java Spring Boot REST API
In the project root folder:
```powershell
mvn spring-boot:run
```
- API will start on: `http://localhost:8081` (dynamic via `${PORT:8081}`)
- CORS is pre-configured to accept requests from `http://localhost:5173`, `http://localhost:3000`, and `*.vercel.app`.

#### REST API Endpoints:
| Method | Endpoint | Description |
|---|---|---|
| `GET`  | `/api/health` | Health and database connectivity check |
| `POST` | `/api/auth/register` | Register a new player name |
| `POST` | `/api/auth/login` | Authenticate an existing player |
| `GET`  | `/api/players/{playerId}` | Get player profile by ID |
| `GET`  | `/api/game/question?difficulty=EASY` | Generate a single question |
| `GET`  | `/api/game/questions?difficulty=MEDIUM&count=10` | Generate a 10-question round |
| `POST` | `/api/game/submit` | Validate & save a completed game session |
| `GET`  | `/api/leaderboard?limit=10` | Global top 10 players leaderboard |
| `GET`  | `/api/scores/{playerId}` | Complete game history for a player |

---

### 2. Start the React Frontend (Local Development)
Open a new terminal, navigate to the `frontend` folder:
```powershell
cd frontend
npm install
npm run dev
```
- Open `http://localhost:5173` in your browser.
- The React application communicates with `http://localhost:8081` by default.

---

## 🌐 Deploying the React Frontend to Vercel

The frontend is ready for zero-configuration deployment to [Vercel](https://vercel.com):

1. **Push your code to GitHub**.
2. **Log into Vercel** and click **"Add New Project"** ➔ **Import Git Repository**.
3. **Configure Project Settings**:
   - **Framework Preset**: `Vite`
   - **Root Directory**: `frontend`
   - **Build Command**: `npm run build`
   - **Output Directory**: `dist`
4. **Environment Variables**:
   In Vercel's project dashboard, add an Environment Variable:
   - **Key**: `VITE_API_BASE_URL`
   - **Value**: Your deployed backend URL (e.g. `https://your-backend.railway.app` or `https://your-api.onrender.com`)
   *(Note: For local testing, it defaults to `http://localhost:8081`)*.
5. **Deploy**:
   Click **Deploy**. Vercel will build and host your site.
   - The included `frontend/vercel.json` ensures that deep client routes (`/dashboard`, `/game`, `/leaderboard`, `/profile`, etc.) correctly rewrite to `index.html` without 404 errors on page reload.

---

## 🖥️ Running the Java Swing Desktop Application

The original desktop application remains completely intact and fully functional:

### Launch Desktop GUI:
```powershell
mvn compile exec:java
```

### Launch Console Mode:
```powershell
mvn compile exec:java -Dexec.args="--console"
```

---

## 🧪 Running Automated Tests
```powershell
mvn test
```
Runs the full test suite including arithmetic generator tests, division cleanly without decimals, non-negative subtraction, and model validation.
