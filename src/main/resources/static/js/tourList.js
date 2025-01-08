// 초기화 함수
function initializeSearch() {
    const regionInput = document.getElementById("address");
    const categoryInput = document.getElementById("category");
    const keywordInput = document.getElementById("keyword");
    const searchButton = document.getElementById("search-Button");

    // 검색 요청 처리 함수
    function performSearch() {
        const region = regionInput.value ?? "";
        const category = categoryInput.value ?? "";
        const keyword = keywordInput.value ?? "";

        const params = {
            p: 1,
            c: category,
            a: region,
            k: keyword,
            sf: "",
            sd: ""
        };

        const baseUrl = "http://localhost:8090/tour/list";
        const queryString = new URLSearchParams(params).toString();
        const fullUrl = `${baseUrl}?${queryString}`;

        // 페이지 이동
        window.location.href = fullUrl;
    }

    // Enter 키 이벤트 처리
    keywordInput.addEventListener("keypress", (event) => {
        if (event.key === "Enter") {
            event.preventDefault(); // 기본 동작 방지
            performSearch(); // 검색 실행
        }
    });

    // 버튼 클릭 이벤트 처리
    searchButton.addEventListener("click", performSearch);
}

// 초기화 함수 호출
initializeSearch();









