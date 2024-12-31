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

        // 1.5초 후 리다이렉션
        setTimeout(() => {
            modal.style.display = "none";
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
                if (!response.ok) {
                    console.error("HTTP Error:", response.status);
                    throw new Error(`${successMessageBase} 요청 실패. 서버 응답 상태: ${response.status}`);
                }
                return response.json();
            })
            .then((data) => {
                if (data.success) {
                    showModal(`${successMessageBase}이 완료되었습니다!`);
                } else {
                    alert(`${successMessageBase}에 실패했습니다. 입력값을 확인해주세요.`);
                    console.error(`${successMessageBase} 실패:`, data);
                }
            })
            .catch((error) => {
                alert(`${successMessageBase} 중 오류가 발생했습니다.`);
                console.error(`${successMessageBase} 오류:`, error);
            });
    });
}
