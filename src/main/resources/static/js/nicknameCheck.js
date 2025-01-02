function initializeNicknameCheck(inputId, messageId, endpoint, callback) {
    const inputField = document.getElementById(inputId);
    const messageField = document.getElementById(messageId);

    inputField.addEventListener("keyup", () => {
        const nickname = inputField.value.trim();
        if (!nickname) {
            messageField.textContent = "";
            callback(false);
            return;
        }

        fetch(`${endpoint}?user_nickname=${encodeURIComponent(nickname)}`)
            .then((response) => response.json())
            .then((data) => {
                if (data.isDuplicateNickName) {
                    messageField.textContent = "이미 사용 중인 닉네임입니다.";
                    messageField.style.color = "red";
                    callback(false);
                } else {
                    messageField.textContent = "사용 가능한 닉네임입니다.";
                    messageField.style.color = "green";
                    callback(true);
                }
            });
    });
}
