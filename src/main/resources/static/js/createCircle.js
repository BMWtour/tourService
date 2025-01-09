// JavaScript에서 data-point-dto-list 값을 읽고 JSON 파싱
const pointDtoList = JSON.parse(document.getElementById('map').getAttribute('data-point-dto-list'));
console.log(pointDtoList);  // 배열이 잘 출력되는지 확인

    // map 객체를 전역으로 선언하여 다른 함수에서도 접근할 수 있도록 함
    let map;

// 원 생성 함수
function createCircle(center, radiusInMeters) {
    const circle = new naver.maps.Circle({
        center: new naver.maps.LatLng(center.lat, center.lng),
        radius: radiusInMeters,
        strokeColor: '#4169E1',
        strokeWeight: 2
    });

    // 원을 지도에 추가
    circle.setMap(map);
    return circle;
}

// 지도 초기화 함수
function initMap() {
    const mapOptions = {
        zoom: 10,
        scaleControl: false,
        logoControl: false,
        mapDataControl: false,
        zoomControl: true,
        minZoom: 6,
    };

    map = new naver.maps.Map('map', mapOptions);

    const centerPoint = calculateCenterPoint(pointDtoList);
    const maxDistance = calculateMaxDistance(centerPoint, pointDtoList);
    const radiusInMeters = maxDistance * 1000 + 500;



    map.setCenter(new naver.maps.LatLng(centerPoint.lat, centerPoint.lng));
    map.setZoom(calculateZoomLevelFromRadius(maxDistance));

    // 지도에 마커 추가
    pointDtoList.forEach(createMarker);

    // 원 생성
    createCircle(centerPoint, radiusInMeters);
}

    // 중앙점을 계산하는 함수
    function calculateCenterPoint(locations) {
        let sumLat = 0;
        let sumLng = 0;

        locations.forEach(location => {
            sumLat += location.lat;
            sumLng += location.lng;
        });

        const centerLat = sumLat / locations.length;
        const centerLng = sumLng / locations.length;

        return { lat: centerLat, lng: centerLng };
    }

    // 중심점에서 가장 먼 위치까지의 거리 계산 함수
    function calculateMaxDistance(center, locations) {
        let maxDistance = 0;

        locations.forEach(location => {
            const distance = calculateDistance(center.lat, center.lng, location.lat, location.lng);
            maxDistance = Math.max(maxDistance, distance);
        });

        return maxDistance; // 킬로미터 단위
    }

    // 두 지점 사이의 거리 계산 함수 (Haversine 공식을 사용)
    function calculateDistance(lat1, lng1, lat2, lng2) {
        const R = 6371; // 지구의 반지름 (킬로미터)
        const dLat = toRadians(lat2 - lat1);
        const dLng = toRadians(lng2 - lng1);
        const a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                  Math.cos(toRadians(lat1)) * Math.cos(toRadians(lat2)) *
                  Math.sin(dLng / 2) * Math.sin(dLng / 2);
        const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // 킬로미터 단위
    }

    // 도 단위로 변환하는 함수
    function toRadians(degrees) {
        return degrees * (Math.PI / 180);
    }

    function calculateZoomLevelFromRadius(radiusInKm) {
    // 반지름 (킬로미터)을 미터로 변환
    const radiusInMeters = radiusInKm * 1000;

    // 지도 크기 가져오기
    const mapSize = map.getSize(); // { width, height }
    const viewportWidth = mapSize.width; // 픽셀 단위 너비
    const viewportHeight = mapSize.height; // 픽셀 단위 높이

    // 화면 대각선 길이를 계산 (픽셀 단위)
    const viewportDiagonal = Math.sqrt(viewportWidth ** 2 + viewportHeight ** 2);

    // 대각선 길이에 따른 줌 레벨 계산
    // 대략적으로 1줌 레벨 차이는 2배 크기의 범위를 표시함
    const zoomLevel = Math.floor(16 - Math.log2((radiusInMeters * 2) / viewportDiagonal));

    // 최소 및 최대 줌 레벨 제한
    return Math.min(Math.max(zoomLevel, 6), 16); // 6~16 사이의 값으로 제한
}

    // 마커 생성 함수
    function createMarker(point) {
        const marker = new naver.maps.Marker({
            position: new naver.maps.LatLng(point.lat, point.lng),
            map: map,
        });

        // 마커 클릭 시 이벤트 처리
        naver.maps.Event.addListener(marker, 'click', () => {
            const spanTextList = document.querySelectorAll(".Text");
            const scrollContainer = document.querySelector('.scroll-container');
            const matchingSpanText = Array.from(spanTextList).find(Text => {
                return Text.textContent.trim() === point.title;
            });

            if (matchingSpanText) {
                const activeRow = document.querySelector('.search-item.clicked');
                if (activeRow) activeRow.classList.remove('clicked');

                const matchingRow = matchingSpanText.closest('.search-item');
                if (scrollContainer && scrollContainer.scrollHeight > scrollContainer.clientHeight) {
                    if (activeRow !== matchingRow && matchingRow) {
                        // matchingRow의 위치를 계산하여 scroll-container 안에서만 스크롤
                        const rowPosition = matchingRow.offsetTop; // matchingRow의 offsetTop 위치
                        const containerHeight = scrollContainer.clientHeight; // scroll-container의 높이
                        const containerScrollTop = scrollContainer.scrollTop; // scroll-container의 현재 스크롤 위치

                        // 스크롤을 matchingRow가 중앙에 오도록 설정
                        scrollContainer.scrollTop = rowPosition - containerHeight / 2 + matchingRow.offsetHeight / 2;
                        // 스크롤을 scroll-container 안에서만 이동하도록 처리
                        // matchingRow.scrollIntoView({ behavior: 'smooth', block: 'center' });
                        matchingRow.classList.add("clicked");
                    }
                } else { matchingRow.classList.add("clicked"); }

            }
            else {
                console.log("No matching row found.");
            }
        });

        // 마커 정보창 표시 이벤트
        naver.maps.Event.addListener(marker, 'mouseover', () => showInfoWindow(marker, point));
        naver.maps.Event.addListener(marker, 'mouseout', () => map.closeInfoWindow());
    }

    // 인포윈도우 표시 함수
    function showInfoWindow(marker, point) {
        const infoWindow = new naver.maps.InfoWindow({
            content: `
                <div style="
                    width: 200px;
                    text-align: center;
                    padding: 15px;
                    border-radius: 8px;
                    background-color: #fff;
                    border: 1px solid #ddd;
                    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
                    font-family: 'Arial', sans-serif;
                    font-size: 14px;
                    color: #333;
                    line-height: 1.5;
                ">
                    <strong style="font-size: 16px; color: #333;">${point.title || 'Untitled'}</strong>
                </div>
            `,
        });
        infoWindow.open(map, marker);
    }

    // 지도 로드 후 initMap 함수 실행
    window.onload = initMap;








    // 지도 마커 클릭 시 해당 항목 강조
    function onMarkerClick(markerId) {
        // 모든 항목에서 'highlighted' 클래스 제거
        const allItems = document.querySelectorAll('.search-item');
        allItems.forEach(item => item.classList.remove('highlighted'));

        // 해당 항목에 'highlighted' 클래스 추가
        const itemToHighlight = document.querySelector(`.search-item[="${markerId}"]`);
        if (itemToHighlight) {
            itemToHighlight.classList.add('highlighted');
            // 스크롤 위치 조정 (해당 항목이 보이도록)
            itemToHighlight.scrollIntoView({ behavior: 'smooth', block: 'center' });
        }
    }

    // 예시: 마커 클릭 시 호출
    onMarkerClick(1);  // '1'은 마커 ID로, 해당하는 div에 'highlighted' 클래스가 추가됨