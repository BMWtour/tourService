function handleFormSubmit(formSelector, successUrl, successMessageBase) {
    const form = document.querySelector(formSelector);

    if (!form) {
        console.error(`Form not found for selector: ${formSelector}`);
        return;
    }

    const showModal = (message) => {
        const modal = document.getElementById("successModal");
        const messageElement = document.getElementById("successMessage");
        messageElement.textContent = message;
        modal.style.display = "block";

        // 3초 후 리다이렉션
        setTimeout(() => {
            window.location.href = successUrl;
        }, 1500);
    };

    form.addEventListener("submit", (event) => {
        event.preventDefault(); // 기본 폼 전송 방지

        const formData = new FormData(form);
        fetch(form.action, {
            method: form.method,
            body: formData,
        })
            .then((response) => {
                if (response.ok) {
                    return response.json();
                }
                throw new Error(`${successMessageBase} 실패`);
            })
            .then((data) => {
                if (data.success) {
                    showModal(`${successMessageBase}이 완료되었습니다!`);
                } else {
                    alert(`${successMessageBase}에 실패했습니다. 입력값을 확인해주세요.`);
                }
            })
            .catch((error) => {
                alert(`${successMessageBase} 중 오류가 발생했습니다.`);
                console.error(error);
            });
    });
}
