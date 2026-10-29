// Form validation and interactivity
class LoginForm {
    constructor() {
        this.form = document.getElementById('loginForm');
        this.emailInput = document.getElementById('email');
        this.passwordInput = document.getElementById('password');
        this.rememberMeCheckbox = document.getElementById('rememberMe');
        this.togglePasswordBtn = document.getElementById('togglePassword');
        this.submitBtn = document.getElementById('submitBtn');
        this.successMessage = document.getElementById('successMessage');
        this.errorMessage = document.getElementById('errorMessage');
        this.successText = document.getElementById('successText');
        this.errorText = document.getElementById('errorText');

        this.init();
    }

    init() {
        // Form submission
        this.form.addEventListener('submit', (e) => this.handleSubmit(e));

        // Real-time validation
        this.emailInput.addEventListener('blur', () => this.validateEmail());
        this.emailInput.addEventListener('input', () => this.clearEmailError());
        
        this.passwordInput.addEventListener('blur', () => this.validatePassword());
        this.passwordInput.addEventListener('input', () => this.clearPasswordError());

        // Password visibility toggle
        this.togglePasswordBtn.addEventListener('click', () => this.togglePasswordVisibility());

        // Load saved email if "Remember me" was checked
        this.loadRememberedEmail();

        // Social login buttons
        this.setupSocialButtons();
    }

    validateEmail() {
        const email = this.emailInput.value.trim();
        const errorElement = document.getElementById('emailError');

        if (!email) {
            errorElement.textContent = 'Email or username is required';
            this.emailInput.classList.add('error');
            return false;
        }

        // Check if it's a valid email format or just a username
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$|^[a-zA-Z0-9_]{3,}$/;
        if (!emailRegex.test(email)) {
            errorElement.textContent = 'Please enter a valid email or username (at least 3 characters)';
            this.emailInput.classList.add('error');
            return false;
        }

        errorElement.textContent = '';
        this.emailInput.classList.remove('error');
        return true;
    }

    clearEmailError() {
        const errorElement = document.getElementById('emailError');
        errorElement.textContent = '';
        this.emailInput.classList.remove('error');
    }

    validatePassword() {
        const password = this.passwordInput.value;
        const errorElement = document.getElementById('passwordError');

        if (!password) {
            errorElement.textContent = 'Password is required';
            this.passwordInput.classList.add('error');
            return false;
        }

        if (password.length < 6) {
            errorElement.textContent = 'Password must be at least 6 characters';
            this.passwordInput.classList.add('error');
            return false;
        }

        errorElement.textContent = '';
        this.passwordInput.classList.remove('error');
        return true;
    }

    clearPasswordError() {
        const errorElement = document.getElementById('passwordError');
        errorElement.textContent = '';
        this.passwordInput.classList.remove('error');
    }

    togglePasswordVisibility() {
        const isPassword = this.passwordInput.type === 'password';
        this.passwordInput.type = isPassword ? 'text' : 'password';
        
        const eyeIcon = this.togglePasswordBtn.querySelector('.eye-icon');
        eyeIcon.textContent = isPassword ? '🙈' : '👁️';
    }

    async handleSubmit(e) {
        e.preventDefault();

        // Clear previous messages
        this.hideMessages();

        // Validate form
        const isEmailValid = this.validateEmail();
        const isPasswordValid = this.validatePassword();

        if (!isEmailValid || !isPasswordValid) {
            return;
        }

        // Prepare form data
        const formData = {
            email: this.emailInput.value.trim(),
            password: this.passwordInput.value,
            rememberMe: this.rememberMeCheckbox.checked
        };

        // Show loading state
        this.setLoadingState(true);

        try {
            // Send login request to backend
            const response = await fetch('/api/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(formData)
            });

            const data = await response.json();

            if (response.ok) {
                // Success
                this.showSuccessMessage(data.message || 'Login successful! Redirecting...');
                
                // Save email if remember me is checked
                if (formData.rememberMe) {
                    this.saveRememberedEmail(formData.email);
                } else {
                    this.clearRememberedEmail();
                }

                // Simulate redirect after 1.5 seconds
                setTimeout(() => {
                    // In a real application, you would redirect here
                    console.log('Redirecting to dashboard...');
                    // window.location.href = '/dashboard';
                }, 1500);
            } else {
                // Error
                this.showErrorMessage(data.message || 'Login failed. Please try again.');
            }
        } catch (error) {
            console.error('Error:', error);
            this.showErrorMessage('Network error. Please check your connection and try again.');
        } finally {
            this.setLoadingState(false);
        }
    }

    setLoadingState(isLoading) {
        this.submitBtn.disabled = isLoading;
        const buttonText = this.submitBtn.querySelector('.button-text');
        const buttonLoader = this.submitBtn.querySelector('.button-loader');

        if (isLoading) {
            buttonText.style.display = 'none';
            buttonLoader.style.display = 'inline-flex';
        } else {
            buttonText.style.display = 'flex';
            buttonLoader.style.display = 'none';
        }
    }

    showSuccessMessage(message) {
        this.successText.textContent = message;
        this.successMessage.style.display = 'block';
        this.errorMessage.style.display = 'none';
    }

    showErrorMessage(message) {
        this.errorText.textContent = message;
        this.errorMessage.style.display = 'block';
        this.successMessage.style.display = 'none';
    }

    hideMessages() {
        this.successMessage.style.display = 'none';
        this.errorMessage.style.display = 'none';
    }

    saveRememberedEmail(email) {
        localStorage.setItem('rememberedEmail', email);
    }

    clearRememberedEmail() {
        localStorage.removeItem('rememberedEmail');
    }

    loadRememberedEmail() {
        const rememberedEmail = localStorage.getItem('rememberedEmail');
        if (rememberedEmail) {
            this.emailInput.value = rememberedEmail;
            this.rememberMeCheckbox.checked = true;
        }
    }

    setupSocialButtons() {
        const socialButtons = document.querySelectorAll('.social-button');
        socialButtons.forEach(button => {
            button.addEventListener('click', (e) => {
                e.preventDefault();
                const provider = button.classList.contains('google-btn') ? 'Google' : 'GitHub';
                this.showErrorMessage(`${provider} login is not yet configured.`);
            });
        });
    }
}

// Initialize when DOM is ready
document.addEventListener('DOMContentLoaded', () => {
    new LoginForm();
});
