(function () {
    function validatePassword(password) {
        return password.length >= 8;
    }

    function checkPasswordMatch(password, confirmPassword) {
        return password === confirmPassword;
    }

    function updatePasswordStrength(password) {
        const strengthBars = document.querySelectorAll('.strength-bar');
        if (!strengthBars.length) return;

        let strength = 0;
        if (password.length >= 8) strength++;
        if (password.match(/[a-z]/) && password.match(/[A-Z]/)) strength++;
        if (password.match(/\d/)) strength++;
        if (password.match(/[^a-zA-Z\d]/)) strength++;

        strengthBars.forEach(bar => {
            bar.className = 'strength-bar';
        });

        if (strength <= 1) {
            strengthBars[0].classList.add('weak');
        } else if (strength === 2) {
            strengthBars[0].classList.add('medium');
            strengthBars[1].classList.add('medium');
        } else if (strength >= 3) {
            strengthBars[0].classList.add('strong');
            strengthBars[1].classList.add('strong');
            strengthBars[2].classList.add('strong');
        }
    }

    document.addEventListener('DOMContentLoaded', function () {
        const signupForm = document.querySelector('form[action*="signup"]');
        if (!signupForm) return;

        const passwordInput = document.getElementById('password');
        const confirmPasswordInput = document.getElementById('confirmPassword');
        const passwordHint = document.querySelector('.password-hint');

        // Create password strength indicator
        const strengthIndicator = document.createElement('div');
        strengthIndicator.className = 'password-strength';
        strengthIndicator.innerHTML = `
            <div class="strength-bar"></div>
            <div class="strength-bar"></div>
            <div class="strength-bar"></div>
        `;
        if (passwordInput) {
            passwordInput.parentNode.appendChild(strengthIndicator);
        }

        // Validate password on input
        if (passwordInput) {
            passwordInput.addEventListener('input', function() {
                const isValid = validatePassword(this.value);
                if (passwordHint) {
                    if (this.value.length === 0) {
                        passwordHint.textContent = 'Must be at least 8 characters';
                        passwordHint.className = 'password-hint';
                    } else if (isValid) {
                        passwordHint.textContent = '✓ Password is valid';
                        passwordHint.className = 'password-hint valid';
                    } else {
                        passwordHint.textContent = '✗ Password must be at least 8 characters';
                        passwordHint.className = 'password-hint invalid';
                    }
                }
                updatePasswordStrength(this.value);
            });
        }

        // Validate password match
        if (confirmPasswordInput) {
            confirmPasswordInput.addEventListener('input', function() {
                if (passwordInput && passwordInput.value) {
                    const matches = checkPasswordMatch(passwordInput.value, this.value);
                    if (this.value.length > 0 && !matches) {
                        this.style.borderColor = '#ff3333';
                    } else {
                        this.style.borderColor = '';
                    }
                }
            });
        }

        // Form submission validation
        signupForm.addEventListener('submit', function(e) {
            const password = passwordInput ? passwordInput.value : '';
            const confirmPassword = confirmPasswordInput ? confirmPasswordInput.value : '';

            if (!validatePassword(password)) {
                e.preventDefault();
                if (passwordHint) {
                    passwordHint.textContent = '✗ Password must be at least 8 characters';
                    passwordHint.className = 'password-hint invalid';
                }
                if (passwordInput) {
                    passwordInput.focus();
                }
                return false;
            }

            if (!checkPasswordMatch(password, confirmPassword)) {
                e.preventDefault();
                if (confirmPasswordInput) {
                    confirmPasswordInput.style.borderColor = '#ff3333';
                    confirmPasswordInput.focus();
                }
                alert('Passwords do not match!');
                return false;
            }
        });
    });
})();