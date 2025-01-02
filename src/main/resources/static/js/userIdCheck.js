function initializeUserIdCheck(inputId, messageId, endpoint, callback) {
    const inputField = document.getElementById(inputId);
    const messageField = document.getElementById(messageId);

    inputField.addEventListener("keyup", () => {
        const userId = inputField.value.trim();
        if (!userId) {
            messageField.textContent = "";
            callback(false);
            return;
        }

        fetch(`${endpoint}?uid=${encodeURIComponent(userId)}`)
            .then((response) => response.json())
            .then((data) => {
                if (data.isDuplicate) {
                    messageField.textContent = "이미 사용 중인 아이디입니다.";
                    messageField.style.color = "red";
                    callback(false);
                } else {
                    messageField.textContent = "사용 가능한 아이디입니다.";
                    messageField.style.color = "green";
                    callback(true);
                }
            });
    });
}
