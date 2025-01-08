function initializeCategories(containerId, warningId, maxSelection, minSelection, callback) {
    const container = document.getElementById(containerId);
    const rawCategories = container.getAttribute("value"); // "[자연관광지, 역사관광지, 체험관광지]"
    const userCategories = rawCategories
    const warning = document.getElementById(warningId);

    const updateCategoryStatus = () => {
        const selectedCount = document.querySelectorAll('input[name="category"]:checked').length;
        const isValid = selectedCount >= minSelection && selectedCount <= maxSelection;

        warning.style.display = isValid ? "none" : "block";
        warning.textContent = isValid ? "" : `최대 ${maxSelection}개, 최소 ${minSelection}개만 선택할 수 있습니다.`;
        callback(isValid);
    };
    fetch("/api/proxy/categories")
        .then((response) => response.json())
        .then((data) => {
            const categories = data.hits.hits.map((hit) => hit._source);

            categories.forEach((category) => {
                const details = document.createElement("details");
                const summary = document.createElement("summary");
                summary.textContent = category.name;
                details.appendChild(summary);

                // 카테고리를 열어둘지 결정하는 플래그
                let hasCheckedSubcategory = false;

                category.subcategories.forEach((subcategory) => {
                    const checkbox = document.createElement("input");
                    checkbox.type = "checkbox";
                    checkbox.name = "category";
                    checkbox.value = subcategory;

                    const label = document.createElement("label");
                    label.textContent = subcategory;

                    // 체크박스 생성 후 userCategories와 비교
                    if (userCategories.includes(subcategory)) {
                        checkbox.checked = true; // 해당 체크박스를 체크 상태로 설정
                        hasCheckedSubcategory = true; // 체크된 항목이 있으면 플래그를 true로 설정
                    }

                    details.appendChild(checkbox);
                    details.appendChild(label);
                    details.appendChild(document.createElement("br"));

                    checkbox.addEventListener("change", updateCategoryStatus);
                });

                // 체크된 항목이 있으면 해당 카테고리를 펼침
                if (hasCheckedSubcategory) {
                    details.setAttribute("open", "true");
                }

                container.appendChild(details);
            });

            updateCategoryStatus(); // 초기 상태 확인
        })
        .catch((error) => {
            console.error("Failed to fetch categories:", error);
        });
}