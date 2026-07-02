(function() {
    'use strict';

    const contextPath = document.body.getAttribute('data-context-path') || '';

    // ----- DOM Elements -----
    const form = document.getElementById('adminLoginForm');
    const emailInput = document.getElementById('email');
    const passwordInput = document.getElementById('password');
    const togglePasswordBtn = document.getElementById('togglePassword');
    const submitBtn = form ? form.querySelector('.admin-login-btn') : null;

    if (togglePasswordBtn && passwordInput) {
        togglePasswordBtn.addEventListener('click', function() {
            const isPasswordHidden = passwordInput.type === 'password';
            passwordInput.type = isPasswordHidden ? 'text' : 'password';
            this.textContent = isPasswordHidden ? '🙈' : '👁️';

            // Update aria-label
            this.setAttribute('aria-label',
                isPasswordHidden ? 'Hide password' : 'Show password'
            );
        });
    }

    if (form) {
        form.addEventListener('submit', function(e) {
            const email = emailInput ? emailInput.value.trim() : '';
            const password = passwordInput ? passwordInput.value.trim() : '';

            // Clear previous error states
            clearErrors();

            // Validate email
            if (!email) {
                e.preventDefault();
                showFieldError(emailInput, 'Email is required');
                return false;
            }

            if (!isValidEmail(email)) {
                e.preventDefault();
                showFieldError(emailInput, 'Please enter a valid email address');
                return false;
            }

            // Validate password
            if (!password) {
                e.preventDefault();
                showFieldError(passwordInput, 'Password is required');
                return false;
            }

            if (password.length < 6) {
                e.preventDefault();
                showFieldError(passwordInput, 'Password must be at least 6 characters');
                return false;
            }

            // Show loading state
            if (submitBtn) {
                submitBtn.classList.add('loading');
                submitBtn.disabled = true;
            }

            // The form will submit normally if validation passes
        });
    }

    function isValidEmail(email) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    }

    function showFieldError(input, message) {
        if (!input) return;

        // Add error class to input group
        const inputGroup = input.closest('.input-group');
        if (inputGroup) {
            inputGroup.style.borderColor = '#ff3333';
            inputGroup.style.boxShadow = '0 0 20px rgba(255, 0, 0, 0.2)';
        }

        // Create or update error message
        let errorMsg = input.parentElement.querySelector('.field-error');
        if (!errorMsg) {
            errorMsg = document.createElement('span');
            errorMsg.className = 'field-error';
            errorMsg.style.cssText = `
                display: block;
                color: #ff6666;
                font-size: 12px;
                margin-top: 4px;
            `;
            input.parentElement.appendChild(errorMsg);
        }
        errorMsg.textContent = '✗ ' + message;

        // Shake animation
        input.style.animation = 'shake 0.4s ease';
        setTimeout(() => {
            input.style.animation = '';
        }, 400);
    }

    function clearErrors() {
        // Remove all field errors
        document.querySelectorAll('.field-error').forEach(el => el.remove());

        // Reset input groups
        document.querySelectorAll('.input-group').forEach(group => {
            group.style.borderColor = '';
            group.style.boxShadow = '';
        });

        // Reset input animations
        document.querySelectorAll('.input-group input').forEach(input => {
            input.style.animation = '';
        });
    }

    if (emailInput) {
        emailInput.addEventListener('blur', function() {
            const email = this.value.trim();
            if (email && !isValidEmail(email)) {
                showFieldError(this, 'Please enter a valid email address');
            } else {
                // Remove error if valid
                const errorMsg = this.parentElement.querySelector('.field-error');
                if (errorMsg) errorMsg.remove();
                const inputGroup = this.closest('.input-group');
                if (inputGroup) {
                    inputGroup.style.borderColor = '';
                    inputGroup.style.boxShadow = '';
                }
            }
        });

        emailInput.addEventListener('input', function() {
            // Remove error state while typing
            const errorMsg = this.parentElement.querySelector('.field-error');
            if (errorMsg) errorMsg.remove();
            const inputGroup = this.closest('.input-group');
            if (inputGroup) {
                inputGroup.style.borderColor = '';
                inputGroup.style.boxShadow = '';
            }
        });
    }

    if (passwordInput) {
        passwordInput.addEventListener('input', function() {
            // Remove error state while typing
            const errorMsg = this.parentElement.querySelector('.field-error');
            if (errorMsg) errorMsg.remove();
            const inputGroup = this.closest('.input-group');
            if (inputGroup) {
                inputGroup.style.borderColor = '';
                inputGroup.style.boxShadow = '';
            }
        });
    }

    const styleSheet = document.createElement('style');
    styleSheet.textContent = `
        @keyframes shake {
            0%, 100% { transform: translateX(0); }
            25% { transform: translateX(-8px); }
            75% { transform: translateX(8px); }
        }
    `;
    document.head.appendChild(styleSheet);

    console.log('🛡️ Admin Login Panel Loaded');
    console.log('📌 Shortcut: Ctrl+Shift+A to focus email field');

    // ============================================
    // 8. REMOVE LOADING STATE ON BACK NAVIGATION
    // ============================================
    window.addEventListener('pageshow', function() {
        if (submitBtn) {
            submitBtn.classList.remove('loading');
            submitBtn.disabled = false;
        }
    });

})();