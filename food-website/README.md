# 🍽️ FoodDelight - Premium Food Website

A modern, responsive, and fully functional food website built with HTML, CSS, and vanilla JavaScript. Perfect for restaurants, food delivery services, or culinary businesses.

## 📋 Project Overview

FoodDelight is a complete food website showcasing:
- **Modern UI Design** with smooth animations and transitions
- **Fully Responsive Layout** optimized for mobile, tablet, and desktop devices
- **Interactive Components** including navigation, forms, and shopping cart functionality
- **Professional Styling** using CSS Grid, Flexbox, and CSS Variables
- **Vanilla JavaScript** with no external dependencies

## 📁 Project Structure

```
food-website/
├── index.html          # Main HTML file with all sections
├── styles.css          # Complete styling and responsive design
├── script.js           # JavaScript functionality and interactivity
└── README.md          # Project documentation
```

## 🎨 Features

### 1. **Navigation Bar**
- Fixed sticky navigation with smooth scrolling
- Mobile-responsive hamburger menu
- Active link highlighting based on scroll position
- Professional gradient background

### 2. **Hero Section**
- Eye-catching gradient background
- Engaging hero title and subtitle
- Call-to-action button with smooth scroll
- Animated floating elements

### 3. **Featured Food Items**
- 6 premium food items displayed in a responsive grid
- Food cards with:
  - Emoji-based food icons (customizable with real images)
  - Item name and description
  - Pricing information
  - Category badges (Popular, Bestseller, Spicy, etc.)
  - "Add to Cart" functionality
- Hover effects with smooth transformations
- Responsive grid (1-3 columns based on screen size)

### 4. **About Section**
- Two-column layout with text and visual content
- Company information and values
- Feature highlights (Award Winning, Farm Fresh, Expert Chefs)
- Smooth hover animations on features

### 5. **Contact Section**
- Contact information display (Location, Phone, Email, Hours)
- Fully validated contact form with:
  - Name field validation
  - Email validation with regex pattern
  - Subject field
  - Message textarea
  - Real-time field validation
  - Success/error messages
  - Form reset on successful submission
- Professional form styling

### 6. **Footer**
- Multi-column layout
- Company branding and description
- Quick navigation links
- Social media links with hover effects
- Newsletter subscription
- Copyright information

### 7. **Additional Features**
- **Shopping Cart System**: Track items, prices, and quantities
- **Smooth Scrolling**: Enhanced navigation with smooth scroll behavior
- **Form Validation**: Comprehensive client-side validation
- **Notifications**: Toast-style notifications for cart items and form submission
- **Scroll Animations**: Intersection Observer for fade-in animations
- **Mobile Menu**: Fully functional hamburger menu for mobile devices
- **Responsive Design**: Mobile-first approach with breakpoints at 768px and 480px

## 🚀 Getting Started

### Requirements
- Modern web browser (Chrome, Firefox, Safari, Edge)
- No server required - can be opened directly in browser

### Installation & Usage

1. **Save the Files**
   - Place all three files (`index.html`, `styles.css`, `script.js`) in the same directory

2. **Open in Browser**
   - Double-click `index.html` or right-click and select "Open with Browser"
   - Or open via browser menu: File → Open File

3. **Explore the Website**
   - Navigate using the sticky navbar
   - Add items to cart (see console for cart logs)
   - Fill out the contact form
   - Test responsive design by resizing browser window

## 🎨 Customization Guide

### Color Scheme
Edit CSS variables in `styles.css`:
```css
:root {
    --primary-color: #ff6b35;      /* Orange */
    --secondary-color: #f7931e;    /* Dark Orange */
    --accent-color: #fdb833;       /* Gold */
    --dark-color: #2d3436;         /* Dark Gray */
    --light-color: #f5f3ff;        /* Light Purple */
}
```

### Food Items
To add or modify food items in `index.html`:
```html
<article class="food-card">
    <div class="food-image">
        <div class="image-placeholder">🍕</div>
        <span class="badge">Popular</span>
    </div>
    <div class="food-content">
        <h3>Your Item Name</h3>
        <p class="description">Your description</p>
        <div class="food-footer">
            <span class="price">$X.XX</span>
            <button class="add-btn" onclick="addToCart('Item Name', X.XX)">+ Add</button>
        </div>
    </div>
</article>
```

### Replace Emoji Icons with Real Images
Replace emoji placeholders with image tags:
```html
<!-- Change from: -->
<div class="image-placeholder">🍕</div>

<!-- To: -->
<img src="path/to/image.jpg" alt="Item description" class="food-image-img">
```

### Update Contact Information
Edit the contact section in `index.html`:
```html
<div class="info-item">
    <h4>📍 Location</h4>
    <p>Your Address Here<br>City, State ZIP</p>
</div>
```

### Update Business Details
- Company name: Change "FoodDelight" throughout files
- Branding: Update navbar brand text and footer
- Social media links: Update footer social links

## 📱 Responsive Breakpoints

- **Desktop**: Full multi-column layouts (1024px and up)
- **Tablet**: 2-column layouts (768px - 1023px)
- **Mobile**: Single-column layouts (480px - 767px)
- **Small Mobile**: Optimized single-column (below 480px)

## 🔧 JavaScript Functions

### Cart Management
```javascript
addToCart(itemName, price)     // Add item to cart
getCartTotal()                  // Get total cart price
getCartItemCount()             // Get number of items
```

### Form Validation
```javascript
validateForm()                 // Validate entire form
validateField(field)           // Validate single field
isValidEmail(email)            // Check email format
```

### Utilities
```javascript
showNotification(message)      // Display toast notification
```

## 📊 Browser Compatibility

- ✅ Chrome (latest)
- ✅ Firefox (latest)
- ✅ Safari (latest)
- ✅ Edge (latest)
- ✅ Mobile browsers (iOS Safari, Chrome Mobile)

## ♿ Accessibility Features

- Semantic HTML markup
- Proper heading hierarchy (H1, H2, H3, etc.)
- Alt text for all images
- ARIA labels on interactive elements
- Keyboard navigation support
- Color contrast compliance
- Form labels properly associated with inputs

## 🎯 Key Highlights

1. **No Dependencies**: Pure HTML, CSS, and JavaScript - no frameworks or libraries needed
2. **Modern CSS**: Uses CSS Grid, Flexbox, CSS Variables, and modern layouts
3. **Smooth Animations**: CSS transitions and keyframe animations throughout
4. **Form Validation**: Real-time validation with helpful error messages
5. **Mobile-First Design**: Optimized for all device sizes
6. **Professional Look**: Modern UI with proper spacing and typography
7. **Easy to Customize**: Well-commented code with clear structure

## 📝 File Descriptions

### index.html (291 lines)
- Complete semantic HTML structure
- All sections: Navigation, Hero, Featured, About, Contact, Footer
- Form with proper labels and validation hooks
- Meta tags for responsiveness and SEO

### styles.css (850+ lines)
- CSS Variables for theming
- Flexbox and Grid layouts
- Responsive design with mobile breakpoints
- Smooth animations and transitions
- Professional color scheme and typography

### script.js (400+ lines)
- Mobile menu toggle functionality
- Shopping cart system
- Form validation with real-time feedback
- Smooth scroll behavior
- Scroll-based animations with Intersection Observer
- Newsletter subscription handling
- Console utilities for testing

## 🐛 Troubleshooting

| Issue | Solution |
|-------|----------|
| Styles not loading | Ensure `styles.css` is in the same directory as `index.html` |
| JavaScript not working | Check browser console for errors, ensure `script.js` is in same directory |
| Mobile menu not working | Verify hamburger element IDs match in HTML and JavaScript |
| Images not showing | Replace emoji placeholders with actual image file paths |
| Form not validating | Check browser console, ensure all input IDs match in HTML |

## 🔐 Security Notes

- Form submissions are currently console-logged only
- To actually send emails, integrate with a backend service (e.g., Formspree, SendGrid)
- Never include sensitive credentials in frontend code
- Use HTTPS in production
- Implement CORS headers if calling external APIs

## 📈 Future Enhancement Ideas

1. Backend integration for form submission
2. Real image uploads for food items
3. Shopping cart with checkout functionality
4. User authentication and profiles
5. Payment gateway integration
6. Admin panel for menu management
7. Customer reviews and ratings
8. Online ordering and delivery tracking
9. Restaurant table reservation system
10. Multi-language support

## 📄 License

This project is open source and available for personal and commercial use.

## 👨‍💻 Created By

Bold Create Agent - A modern food website template

---

## 💡 Tips for Best Results

1. **Test Responsiveness**: Use browser DevTools to test on different screen sizes
2. **Performance**: All assets are local, making it very fast
3. **Deployment**: Upload all three files to any web hosting service
4. **Customization**: Start by changing colors and content to match your brand
5. **Testing**: Open browser console (F12) to see cart logs and any errors

---

**Enjoy your modern food website! 🍽️✨**
