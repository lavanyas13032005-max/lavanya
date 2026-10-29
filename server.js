const express = require('express');
const cors = require('cors');
const path = require('path');

const app = express();
const PORT = process.env.PORT || 3000;

// Middleware
app.use(cors());
app.use(express.json());
app.use(express.static(path.join(__dirname, '.')));

// Mock database of users (for demonstration)
const users = [
    { id: 1, email: 'demo@example.com', username: 'demo', password: 'demo123' },
    { id: 2, email: 'user@example.com', username: 'user', password: 'user123' },
    { id: 3, email: 'admin@example.com', username: 'admin', password: 'admin123' }
];

// Validation helper functions
function isValidEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}

function isValidUsername(username) {
    return username && username.length >= 3 && /^[a-zA-Z0-9_]+$/.test(username);
}

function validateLoginInput(email, password) {
    const errors = [];

    // Validate email/username
    if (!email || email.trim() === '') {
        errors.push('Email or username is required');
    } else {
        const isEmail = isValidEmail(email);
        const isUsername = isValidUsername(email);

        if (!isEmail && !isUsername) {
            errors.push('Invalid email format or username (username must be at least 3 characters)');
        }
    }

    // Validate password
    if (!password) {
        errors.push('Password is required');
    } else if (password.length < 6) {
        errors.push('Password must be at least 6 characters');
    }

    return errors;
}

// Routes
app.get('/', (req, res) => {
    res.sendFile(path.join(__dirname, 'index.html'));
});

// Login endpoint
app.post('/api/login', (req, res) => {
    const { email, password, rememberMe } = req.body;

    // Validate input
    const validationErrors = validateLoginInput(email, password);
    if (validationErrors.length > 0) {
        return res.status(400).json({
            success: false,
            message: validationErrors[0],
            errors: validationErrors
        });
    }

    // Find user in mock database
    const user = users.find(u => 
        (u.email === email || u.username === email) && u.password === password
    );

    if (!user) {
        // Don't reveal whether email exists or password is wrong
        return res.status(401).json({
            success: false,
            message: 'Invalid email/username or password'
        });
    }

    // Successful login
    res.json({
        success: true,
        message: `Welcome back, ${user.username}!`,
        user: {
            id: user.id,
            email: user.email,
            username: user.username
        },
        rememberMe: rememberMe
    });
});

// Health check endpoint
app.get('/api/health', (req, res) => {
    res.json({
        status: 'ok',
        timestamp: new Date().toISOString()
    });
});

// 404 handler for API routes
app.use('/api', (req, res) => {
    res.status(404).json({
        success: false,
        message: 'API endpoint not found'
    });
});

// Error handler
app.use((err, req, res, next) => {
    console.error('Error:', err);
    res.status(500).json({
        success: false,
        message: 'Internal server error',
        error: process.env.NODE_ENV === 'development' ? err.message : undefined
    });
});

// Start server
app.listen(PORT, () => {
    console.log(`
╔════════════════════════════════════════════╗
║       Login Page Server Started            ║
╠════════════════════════════════════════════╣
║  Server is running on port ${PORT}          
║  Open your browser and visit:              
║  http://localhost:${PORT}                   
║                                            ║
║  Test Credentials:                         ║
║  - Email: demo@example.com / Pass: demo123 ║
║  - Username: user / Pass: user123          ║
║  - Username: admin / Pass: admin123        ║
╚════════════════════════════════════════════╝
    `);
});

// Graceful shutdown
process.on('SIGTERM', () => {
    console.log('SIGTERM signal received: closing HTTP server');
    app.close(() => {
        console.log('HTTP server closed');
        process.exit(0);
    });
});
