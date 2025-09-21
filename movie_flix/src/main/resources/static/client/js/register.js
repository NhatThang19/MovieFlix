document.addEventListener('DOMContentLoaded', function () {
    const password = document.getElementById('password');
    const confirmPassword = document.getElementById('confirmPassword');
    const strengthMeter = document.getElementById('strengthMeter');
    const passwordMatch = document.getElementById('passwordMatch');
    const registerForm = document.getElementById('registerForm');

    // Toggle password visibility
    function togglePasswordVisibility(toggleBtnId, passwordInputId) {
        const toggleButton = document.getElementById(toggleBtnId);
        const passwordInput = document.getElementById(passwordInputId);

        if (toggleButton && passwordInput) {
            toggleButton.addEventListener('click', function () {
                const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
                passwordInput.setAttribute('type', type);
                this.innerHTML = type === 'password' ? '<i class="fas fa-eye"></i>' : '<i class="fas fa-eye-slash"></i>';
            });
        }
    }

    togglePasswordVisibility('togglePassword', 'password');
    togglePasswordVisibility('toggleConfirmPassword', 'confirmPassword');


    // Simple password strength checker function
    function checkPasswordStrength(pwd) {
        if (pwd.length === 0) return '';
        if (pwd.length < 6) return 'weak';
        if (pwd.length < 10) return 'medium';
        return 'strong';
    }

    // Password strength checker event
    if (password && strengthMeter) {
        password.addEventListener('input', function () {
            const strength = checkPasswordStrength(this.value);
            strengthMeter.className = 'strength-meter';
            if (strength === 'weak') {
                strengthMeter.classList.add('strength-weak');
            } else if (strength === 'medium') {
                strengthMeter.classList.add('strength-medium');
            } else if (strength === 'strong') {
                strengthMeter.classList.add('strength-strong');
            }
        });
    }

    // Password match checker event
    if (confirmPassword && passwordMatch && password) {
        confirmPassword.addEventListener('input', function () {
            if (this.value === '') {
                passwordMatch.textContent = '';
                passwordMatch.className = 'password-match';
            } else if (this.value === password.value) {
                passwordMatch.textContent = 'Mật khẩu khớp';
                passwordMatch.className = 'password-match match-valid';
            } else {
                passwordMatch.textContent = 'Mật khẩu không khớp';
                passwordMatch.className = 'password-match match-invalid';
            }
        });
    }
});