function initializePasswordCheck(passwordId, confirmPasswordId, messageId, callback) {
    const passwordField = document.getElementById(passwordId);
    const confirmPasswordField = document.getElementById(confirmPasswordId);
    const messageField = document.getElementById(messageId);

    function validatePasswords() {
        const password = passwordField.value.trim();
        const confirmPassword = confirmPasswordField.value.trim();

        if (!password || !confirmPassword) {
            messageField.textContent = "";
            callback(false);
        } else if (password === confirmPassword) {
            messageField.textContent = "패스워드가 일치합니다.";
            messageField.style.color = "green";
            callback(true);
        } else {
            messageField.textContent = "패스워드가 일치하지 않습니다.";
            messageField.style.color = "red";
            callback(false);
        }
    }

    passwordField.addEventListener("keyup", validatePasswords);
    confirmPasswordField.addEventListener("keyup", validatePasswords);
}
