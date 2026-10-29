/* ========================================
   FOOD WEBSITE - JAVASCRIPT FUNCTIONALITY
   ======================================== */

// Initialize cart
let cart = [];

// ===== Mobile Menu Toggle =====
document.addEventListener('DOMContentLoaded', function() {
    const hamburger = document.getElementById('hamburger');
    const navMenu = document.getElementById('navMenu');

    if (hamburger) {
        hamburger.addEventListener('click', function() {
            hamburger.classList.toggle('active');
            navMenu.classList.toggle('active');
        });
    }

    // Close menu when a link is clicked
    const navLinks = document.querySelectorAll('.nav-link');
    navLinks.forEach(link => {
        link.addEventListener('click', function() {
            hamburger.classList.remove('active');
            navMenu.classList.remove('active');
        });
    });

    // Initialize form validation
    initializeForm();
});

// ===== Add to Cart Function =====
function addToCart(itemName, price) {
    const item = {
        id: Date.now(),
        name: itemName,
        price: price,
        quantity: 1
    };

    // Check if item already in cart
    const existingItem = cart.find(cartItem => cartItem.name === itemName);
    if (existingItem) {
        existingItem.quantity++;
    } else {
        cart.push(item);
    }

    showNotification(`${itemName} added to cart!`);
    console.log('Current cart:', cart);
}

// ===== Notification System =====
function showNotification(message) {
    const notification = document.getElementById('cartNotification');
    const notificationText = document.getElementById('notificationText');

    notificationText.textContent = message;
    notification.classList.add('show');

    // Auto-hide after 3 seconds
    setTimeout(() => {
        notification.classList.remove('show');
    }, 3000);
}

// ===== Form Validation =====
function initializeForm() {
    const contactForm = document.getElementById('contactForm');

    if (contactForm) {
        contactForm.addEventListener('submit', function(e) {
            e.preventDefault();

            // Clear previous errors
            clearErrors();

            // Validate all fields
            const isValid = validateForm();

            if (isValid) {
                submitForm();
            }
        });

        // Real-time validation on blur
        const inputs = contactForm.querySelectorAll('input, textarea');
        inputs.forEach(input => {
            input.addEventListener('blur', function() {
                validateField(this);
            });
        });
    }
}

function validateForm() {
    const name = document.getElementById('name').value.trim();
    const email = document.getElementById('email').value.trim();
    const subject = document.getElementById('subject').value.trim();
    const message = document.getElementById('message').value.trim();

    let isValid = true;

    // Validate name
    if (name === '') {
        showFieldError('name', 'Name is required');
        isValid = false;
    } else if (name.length < 2) {
        showFieldError('name', 'Name must be at least 2 characters');
        isValid = false;
    }

    // Validate email
    if (email === '') {
        showFieldError('email', 'Email is required');
        isValid = false;
    } else if (!isValidEmail(email)) {
        showFieldError('email', 'Please enter a valid email');
        isValid = false;
    }

    // Validate subject
    if (subject === '') {
        showFieldError('subject', 'Subject is required');
        isValid = false;
    } else if (subject.length < 3) {
        showFieldError('subject', 'Subject must be at least 3 characters');
        isValid = false;
    }

    // Validate message
    if (message === '') {
        showFieldError('message', 'Message is required');
        isValid = false;
    } else if (message.length < 10) {
        showFieldError('message', 'Message must be at least 10 characters');
        isValid = false;
    }

    return isValid;
}

function validateField(field) {
    const fieldName = field.name;
    const fieldValue = field.value.trim();

    // Clear previous error
    document.getElementById(fieldName + 'Error').textContent = '';
    field.classList.remove('error');

    let isValid = true;
    let errorMessage = '';

    switch (fieldName) {
        case 'name':
            if (fieldValue === '') {
                errorMessage = 'Name is required';
                isValid = false;
            } else if (fieldValue.length < 2) {
                errorMessage = 'Name must be at least 2 characters';
                isValid = false;
            }
            break;

        case 'email':
            if (fieldValue === '') {
                errorMessage = 'Email is required';
                isValid = false;
            } else if (!isValidEmail(fieldValue)) {
                errorMessage = 'Please enter a valid email';
                isValid = false;
            }
            break;

        case 'subject':
            if (fieldValue === '') {
                errorMessage = 'Subject is required';
                isValid = false;
            } else if (fieldValue.length < 3) {
                errorMessage = 'Subject must be at least 3 characters';
                isValid = false;
            }
            break;

        case 'message':
            if (fieldValue === '') {
                errorMessage = 'Message is required';
                isValid = false;
            } else if (fieldValue.length < 10) {
                errorMessage = 'Message must be at least 10 characters';
                isValid = false;
            }
            break;
    }

    if (!isValid) {
        showFieldError(fieldName, errorMessage);
    }

    return isValid;
}

function showFieldError(fieldName, message) {
    const errorElement = document.getElementById(fieldName + 'Error');
    const fieldElement = document.getElementById(fieldName);

    if (errorElement) {
        errorElement.textContent = message;
        errorElement.classList.add('show');
    }

    if (fieldElement) {
        fieldElement.classList.add('error');
    }
}

function clearErrors() {
    const errorElements = document.querySelectorAll('.error-message');
    errorElements.forEach(error => {
        error.textContent = '';
        error.classList.remove('show');
    });

    const inputElements = document.querySelectorAll('input, textarea');
    inputElements.forEach(input => {
        input.classList.remove('error');
    });
}

function isValidEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}

function submitForm() {
    const name = document.getElementById('name').value.trim();
    const email = document.getElementById('email').value.trim();
    const subject = document.getElementById('subject').value.trim();
    const message = document.getElementById('message').value.trim();

    // Here you would typically send the form data to a server
    console.log('Form submitted:', {
        name,
        email,
        subject,
        message
    });

    // Show success message
    const successMessage = document.getElementById('successMessage');
    successMessage.textContent = 'Thank you! Your message has been sent successfully. We\'ll get back to you soon!';
    successMessage.classList.add('show');

    // Reset form
    document.getElementById('contactForm').reset();

    // Hide success message after 5 seconds
    setTimeout(() => {
        successMessage.classList.remove('show');
    }, 5000);
}

// ===== Smooth Scroll Enhancement =====
document.addEventListener('DOMContentLoaded', function() {
    // Add smooth scroll to all internal links
    const scrollLinks = document.querySelectorAll('a[href^="#"]');

    scrollLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            const href = this.getAttribute('href');

            if (href !== '#' && document.querySelector(href)) {
                e.preventDefault();

                const targetElement = document.querySelector(href);
                const offsetTop = targetElement.offsetTop;
                const navHeight = document.querySelector('.navbar').offsetHeight;

                window.scrollTo({
                    top: offsetTop - navHeight,
                    behavior: 'smooth'
                });
            }
        });
    });
});

// ===== Scroll Animations =====
const observerOptions = {
    threshold: 0.1,
    rootMargin: '0px 0px -100px 0px'
};

const observer = new IntersectionObserver(function(entries) {
    entries.forEach(entry => {
        if (entry.isIntersecting) {
            entry.target.style.opacity = '1';
            entry.target.style.transform = 'translateY(0)';
        }
    });
}, observerOptions);

document.addEventListener('DOMContentLoaded', function() {
    // Observe food cards for animation
    const foodCards = document.querySelectorAll('.food-card');
    foodCards.forEach((card, index) => {
        card.style.opacity = '0';
        card.style.transform = 'translateY(30px)';
        card.style.transition = `opacity 0.6s ease ${index * 0.1}s, transform 0.6s ease ${index * 0.1}s`;
        observer.observe(card);
    });

    // Observe info items
    const infoItems = document.querySelectorAll('.info-item');
    infoItems.forEach((item, index) => {
        item.style.opacity = '0';
        item.style.transform = 'translateY(30px)';
        item.style.transition = `opacity 0.6s ease ${index * 0.1}s, transform 0.6s ease ${index * 0.1}s`;
        observer.observe(item);
    });

    // Observe features
    const features = document.querySelectorAll('.feature');
    features.forEach((feature, index) => {
        feature.style.opacity = '0';
        feature.style.transform = 'translateY(30px)';
        feature.style.transition = `opacity 0.6s ease ${index * 0.1}s, transform 0.6s ease ${index * 0.1}s`;
        observer.observe(feature);
    });
});

// ===== Newsletter Subscription =====
document.addEventListener('DOMContentLoaded', function() {
    const newsletterButtons = document.querySelectorAll('.newsletter button');

    newsletterButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault();

            const input = this.previousElementSibling;
            const email = input.value.trim();

            if (email === '') {
                showNotification('Please enter your email');
                return;
            }

            if (!isValidEmail(email)) {
                showNotification('Please enter a valid email');
                return;
            }

            showNotification('Thank you for subscribing!');
            input.value = '';
        });
    });
});

// ===== Active Navigation Link Highlight =====
window.addEventListener('scroll', function() {
    const navLinks = document.querySelectorAll('.nav-link');
    let current = '';

    const sections = document.querySelectorAll('section');
    sections.forEach(section => {
        const sectionTop = section.offsetTop;
        const sectionHeight = section.clientHeight;

        if (pageYOffset >= sectionTop - 200) {
            current = section.getAttribute('id');
        }
    });

    navLinks.forEach(link => {
        link.classList.remove('active');
        if (link.getAttribute('href').slice(1) === current) {
            link.classList.add('active');
        }
    });
});

// ===== Utility: Get Cart Total =====
function getCartTotal() {
    return cart.reduce((total, item) => total + (item.price * item.quantity), 0);
}

// ===== Utility: Get Cart Item Count =====
function getCartItemCount() {
    return cart.reduce((count, item) => count + item.quantity, 0);
}

// ===== Developer Console Functions =====
console.log('%c🍽️ FoodDelight Website Loaded', 'font-size: 18px; color: #ff6b35; font-weight: bold;');
console.log('Use addToCart(itemName, price) to test cart functionality');
console.log('Cart:', cart);
