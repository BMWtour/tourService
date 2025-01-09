document.getElementById('adjustButton').addEventListener('click', function() {
    let researchCount = document.querySelector('input[name="count"]:checked')?.value || 10;  // 선택된 값이 없으면 기본값 10
    let category = document.getElementById('category').value; // category 값을 가져오기
    let address = document.getElementById('address').value; // region 값을 가져오기
    let keyword = document.getElementById('keyword').value; // keyword 값을 가져오기
    let sortField = document.getElementById('sortBy').value;  // sortBy select의 값
    let sortDirection = sortField === "title.title_keyword"
        ? (document.querySelector('.sort-button.button-active')?.getAttribute('data-value') || "asc" )
        : "";

    const params = new URLSearchParams({
        p: 1,
        rc: researchCount,
        c: category,
        a: address,
        k: keyword,
        sf: sortField,
        sd: sortDirection
    });

    // 서버로 직접 이동
    window.location.href = '/tour/list?' + params.toString();
});




document.getElementById('resetButton').addEventListener('click', function() {
    let category = document.getElementById('category').value; // category 값을 가져오기
    let address = document.getElementById('address').value; // region 값을 가져오기
    let keyword = document.getElementById('keyword').value; // keyword 값을 가져오기
    const params = new URLSearchParams({
        p: 1,
        rc: 10,
        c: category,
        a: address,
        k: keyword,
        sf: "",
        sd: ""
    });
    // 서버로 직접 이동
    window.location.href = '/tour/list?' + params.toString();
});








// 정렬 선택 변화 감지
document.getElementById('sortBy').addEventListener('change', () => {
    const sortBy = document.getElementById('sortBy').value;
    const sortAsc = document.getElementById('sortAsc');
    const sortDesc = document.getElementById('sortDesc');

    // "위치" 선택 시 내림차순, 오름차순 버튼 비활성화
    if (sortBy !== 'title.title_keyword' ) {
        sortAsc.classList.remove("button-active");
        sortDesc.classList.remove("button-active");
        // document.querySelectorAll('.sort-button').forEach(btn => btn.classList.remove("button-active"));
        sortAsc.disabled = true;
        sortDesc.disabled = true;
    } else {
        sortAsc.disabled = false;
        sortDesc.disabled = false;
    }
});


// Sorting Buttons
document.getElementById('sortAsc').addEventListener('click', () => {
    const sortBy = document.getElementById('sortBy').value;
    const order = 'asc';
});

document.getElementById('sortDesc').addEventListener('click', () => {
    const sortBy = document.getElementById('sortBy').value;
    const order = 'desc';
});

// Reset Button
document.getElementById('resetButton').addEventListener('click', () => {
    document.getElementById('sortBy').value = "location";
    document.querySelectorAll('.count-options input[type="radio"]').forEach(radio => radio.checked = false);
    document.querySelectorAll('.sort-button').forEach(btn => btn.classList.remove("button-active"));
    document.getElementById('sortAsc').disabled = true;
    document.getElementById('sortDesc').disabled = true;
});

// Sorting Button Click (one active at a time)
document.querySelectorAll('.sort-button').forEach(button => {
    button.addEventListener('click', () => {
        document.querySelectorAll('.sort-button').forEach(btn => btn.classList.remove("button-active"));
        button.classList.add("button-active");
    });
});







