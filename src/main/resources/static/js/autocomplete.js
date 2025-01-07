let isFetching = false; // 요청 상태를 관리

async function fetchAutocomplete(word) {
    if (isFetching || word.trim() === '') return; // 검색어가 없으면 요청하지 않음

    isFetching = true;
    const listElement = document.getElementById('autocomplete-list');
    listElement.innerHTML = ''; // 기존 목록 초기화
    listElement.style.display = 'none';

    try {
        const response = await fetch(`/tour/autocomplete?word=${encodeURIComponent(word)}`);
        if (!response.ok) throw new Error('Failed to fetch data');

        const suggestions = await response.json();
        if (suggestions.length > 0) {
            listElement.style.display = 'block';
            suggestions.forEach((item) => {
                const listItem = document.createElement('li');

                // 검색어와 일치하는 부분을 하이라이트
                const regex = new RegExp(`(${word})`, 'gi'); // 대소문자 무시
                const highlightedText = item.replace(regex, '<span style="background-color: lightblue;">$1</span>');

                listItem.innerHTML = highlightedText;
                listItem.style.padding = '8px';
                listItem.style.cursor = 'pointer';
                listItem.onmouseover = () => (listItem.style.backgroundColor = '#f0f0f0');
                listItem.onmouseout = () => (listItem.style.backgroundColor = 'white');
                listItem.onclick = () => {
                    document.getElementById('keyword').value = item; // 선택한 항목으로 입력창 채움
                    listElement.style.display = 'none'; // 자동완성 목록 숨김
                };
                listElement.appendChild(listItem);
            });
        }
    } catch (error) {
        console.error('Autocomplete error:', error);
    } finally {
        isFetching = false;
    }
}

const inputElement = document.getElementById('keyword');

let debounceTimeout;
inputElement.addEventListener('input', (event) => {
    const word = event.target.value;

    // 디바운싱으로 요청 빈도를 제한
    clearTimeout(debounceTimeout);
    debounceTimeout = setTimeout(() => {
        fetchAutocomplete(word); // 검색어 입력 시 자동완성 요청
    }, 300); // 300ms 딜레이
});

// 검색창 외부를 클릭하면 목록 숨김
document.addEventListener('click', (event) => {
    const listElement = document.getElementById('autocomplete-list');
    const isClickInside = inputElement.contains(event.target) || listElement.contains(event.target);

    if (!isClickInside) {
        listElement.style.display = 'none';
    }
});
