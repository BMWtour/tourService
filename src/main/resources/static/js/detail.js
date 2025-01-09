//-----------html이 다 뜨고나서, 개요 줄바꿈 처리 부분-------------
window.onload = function() {
    // HTML에서 'summaryText' 클래스를 가진 요소를 찾음
    let textElement = document.querySelector('.summaryText');

    // 요소의 텍스트를 가져옴
    let text = textElement.innerText;

    // 텍스트를 '. ' 기준으로 나누고 '<br>' 태그로 합침
    let formattedText = text.split('. ').join('.<br>');

    // 변환된 텍스트를 HTML에 삽입
    textElement.innerHTML = formattedText;
};


//내 위치 찾기 좌표 받는 변수
let myLatitude = "";
let myLongitude = "";

//주소 입력창의 문자열을 받아 좌표로 받는 배열 변수
let startXY = [];

//관광지 좌표
let tourAttrLatitude = document.getElementById("tourInfoLat").textContent;
let tourAttrLongitude = document.getElementById("tourInfoLng").textContent;

//맵 디폴트 위치. 추후 DB에서 관광지 좌표 가져오기
let position = new naver.maps.LatLng(tourAttrLatitude, tourAttrLongitude)

//루트
let polyline = null;

//지도의 길찾기 루트에 가이드 좌표들을 나타내기 위한 마커 배열
let guideMarkers = [];
let guideInfo = [];

//맵 옵션
let mapOptions = {
    center     : position,
    zoom       : 18,
    maxZoom    : 20,
    minZoom    : 1,
    zoomControl: true,
};

//-----------관광지 위치 맵(페이지 상단)-----------
// 관광지 위치 맵
// var tourMap = new naver.maps.Map('tourMap', mapOptions);
// 관광지 위치 마커
// var marker = new naver.maps.Marker({
//     position: new naver.maps.LatLng(tourAttrLatitude, tourAttrLongitude),
//     map     : tourMap,
// });

//-----------관광지 위치 파노라마 테스트-----------
    var pano = null;

    naver.maps.onJSContentLoaded = function() {
    // 기존 맵 객체 대신 파노라마 객체를 생성합니다.
    pano = new naver.maps.Panorama("tourMap", {
        position: new naver.maps.LatLng(tourAttrLatitude, tourAttrLongitude),
        pov: {
            pan: -135,
            tilt: 29,
            fov: 100
        },
        flightSpot: true, // 항공 아이콘 표시 여부, default: true
    });
};



//--------길찾기 맵(페이지 하단)-----------
//길찾기 맵
let naviMap = new naver.maps.Map('naviMap', mapOptions);

//사용자가 입력한 주소의 위치에 찍는 마커
var myLocationMarker = null;

//길찾기 맵 관광지 위치 마커
var tourAttrMarker = new naver.maps.Marker({
    position: new naver.maps.LatLng(tourAttrLatitude, tourAttrLongitude),
    map     : naviMap,
    icon    : {
        content:
            `<svg xmlns="http://www.w3.org/2000/svg"
                    width="24" height="24" viewBox="0 0 24 24" fill="red" stroke="black"
                     stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
                     class="feather feather-map-pin">
                     <path d="M21 10c0 5.25-9 13-9 13S3 15.25 3 10a9 9 0 1 1 18 0z"></path>
                     <circle cx="12" cy="10" r="3"></circle>
                 </svg>`,
        size   : new naver.maps.Size(24, 24), anchor: new naver.maps.Point(12, 24)
    }

});


//-------------------------------길찾기-------------------------------------

let myLocationStatus = document.getElementById("myLocationStatus");

//------------사용자 위치 확인------------

//내 위치 확인
function getMyLocation() {
    window.navigator.geolocation.getCurrentPosition(success, error, options);
}

function success(position) {
    //좌표받기
    myLatitude = position.coords.latitude;
    myLongitude = position.coords.longitude;

    //좌표를 올바르게 받아왔을 때 다음함수 실행
    if (myLatitude !== "" && myLongitude !== "") {
        myLocationStatus.textContent = "위치가져오기 성공";

        //1. 좌표를 주소로 변환
        findAddress(myLatitude, myLongitude);
    }

}

function error() {
    myLocationStatus.textContent = "현재 위치를 가져올 수 없음";
}

const options = {
    enableHighAccuracy: true, //상세위치받이오기 true / false.
    timeout           : 5000, // 5 seconds //위치받아오기 기다리는 시간
    maximumAge        : 600000, // 1 minute //10분 생존
};

//-------------루트 나타내기-----------


//지도에 루트그리기
const drawRoute = (resultPath) => {
    if (polyline !== null) polyline.setMap(null)
    const path = []
    resultPath.forEach(point => {
        path.push(new naver.maps.LatLng(point.lat, point.lng))
    })

    polyline = new naver.maps.Polyline({
        map : naviMap,
        path: path
    })
}

//사용자가 입력한 주소 -> 관광지까지의 길찾기
async function findRoute() {
    let address = document.getElementById("adressInput").value;

    // 사용자가 입력한 주소를 좌표로 변경
    await findXY(address);

    // 좌표 변경 후 startXY 배열의 값을 사용
    const startLatitude = startXY[0];
    const startLongitude = startXY[1];

    // 두 좌표 사이 루트 가져오기
    fetch('/navigate/getGuideAndRouteInfo', {
        method : 'post',
        headers: {
            'content-type': 'application/json'
        },
        body   : JSON.stringify({
            // 길찾기 시작좌표
            start: {
                lat: startLatitude,
                lng: startLongitude
            },
            // 길찾기 도착좌표(관광지)
            goal: {
                lat: tourAttrLatitude,
                lng: tourAttrLongitude
            }
        })
    }).then(async response => {
        console.log(response)
        if (response.ok) {
            console.log("가져오기 완료");
            //마커 추가
            //(첫 실행) 마커가 null이라면 마커찍기
            if (myLocationMarker == null) {
                //사용자가 입력한 주소 위치에 마커 추가
                myLocationMarker = new naver.maps.Marker({
                    position: new naver.maps.LatLng(startLatitude, startLongitude),
                    map     : naviMap,
                    icon    : {
                        content:
                            `<svg xmlns="http://www.w3.org/2000/svg"
                                    width="24" height="24" viewBox="0 0 24 24" fill="green" stroke="black"
                                     stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
                                     class="feather feather-map-pin">
                                     <path d="M21 10c0 5.25-9 13-9 13S3 15.25 3 10a9 9 0 1 1 18 0z"></path>
                                     <circle cx="12" cy="10" r="3"></circle>
                                 </svg>`,
                        size   : new naver.maps.Size(24, 24), anchor: new naver.maps.Point(12, 24)
                    }
                });
            } else {
                //기존에 사용자가 입력한 마커가 null이 아니라면 (마커가 찍혀있다면) 지우고 새로운 위치에 마커찍기
                myLocationMarker.setMap(null);
                myLocationMarker = new naver.maps.Marker({
                    position: new naver.maps.LatLng(startLatitude, startLongitude),
                    map     : naviMap,
                    icon    : {
                        content:
                            `<svg xmlns="http://www.w3.org/2000/svg"
                                    width="24" height="24" viewBox="0 0 24 24" fill="green" stroke="black"
                                     stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
                                     class="feather feather-map-pin">
                                     <path d="M21 10c0 5.25-9 13-9 13S3 15.25 3 10a9 9 0 1 1 18 0z"></path>
                                     <circle cx="12" cy="10" r="3"></circle>
                                 </svg>`,
                        size   : new naver.maps.Size(24, 24), anchor: new naver.maps.Point(12, 24)
                    }
                });
            }

            //경로 길이에 따른 지도 줌 변경
            adjustMapBounds(startLatitude, startLongitude);

            //받아온 데이터
            const data = await response.json();
            //길찾기 경로 그려주기
            drawRoute(data.naviRouteDto.path);
            //지도의 가이드 정보
            //지도에 가이드 좌표 마커로 띄우기
            initGuideMarkers(data.guideAndPointDto.pointDtoList, data.guideAndPointDto.guideDtoList);
        }
    });
}

//지도에 가이드 좌표들 마커로 띄우기
function initGuideMarkers(pointDtoList, guideDtoList) {
    //이미 다른 가이드 마커가 띄워져있다면 지워주기
    if (guideMarkers.length !== 0){
        clearMarkers(guideMarkers);
    }
    let markerPontList = pointDtoList;
    guideMarkers = [];
    guideInfo = guideDtoList;

    //가이드정보 클릭시 정보를 띄우는 자그마한 창 배열
    const infoWindows = [];
    for (let i = 0; i < markerPontList.length; i++) {
        //가이드마커
        let guideMarker = new naver.maps.Marker({
            position: new naver.maps.LatLng(markerPontList[i].lat, markerPontList[i].lng),
            map     : naviMap
        });
        // 클릭했을 때 정보 띄우기
        let guideMarkerClickInfo = new naver.maps.InfoWindow({
            content: '<div style="width: 200px; text-align: center; padding: 10px;"><b>' + guideInfo[i].instructions + '</b></div>'
        });
        //정보창 배열에 저장
        infoWindows.push(guideMarkerClickInfo);

        // 마커에 클릭 이벤트 추가
        naver.maps.Event.addListener(guideMarker, 'click', function (e) {
            if (guideMarkerClickInfo.getMap()) {
                guideMarkerClickInfo.close();
            } else {
                // 다른 모든 정보창을 닫기
                infoWindows.forEach(function (infoWindow) {
                    infoWindow.close();
                });
                guideMarkerClickInfo.open(naviMap, guideMarker);
            }
        });
        guideMarkers.push(guideMarker);
    }
}

// 모든 마커를 삭제하는 함수
function clearMarkers(guideMarkers) {
    for (var i = 0; i < guideMarkers.length; i++) {
        guideMarkers[i].setMap(null);
    }
}

//주소 -> 좌표
function findXY(address) {
    console.log('Sending request with address:', address);

    return fetch('/navigate/getPoints', {
        method : 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body   : JSON.stringify({address: address})
    }).then(response => {
        if (response.ok) {
            return response.json().then(body => {
                console.log('Response:', body);
                startXY[0] = body.lat;
                startXY[1] = body.lng;
            });
        } else {
            console.error('Error:', response.status, response.statusText);
        }
    }).catch(error => {
        console.error('Fetch error:', error);
    });
}


//좌표의 주소 얻어내기
function findAddress(lat, lng) {

    fetch('/navigate/get-address', {
        method : 'post',
        headers: {
            'content-type': 'application/json'
        },
        body   : JSON.stringify({
            lat, lng
        })
    }).then(response => {
        if (response.ok) response.json()
            .then(body => {
                //주소입력창에 주소 넣어주기
                document.getElementById("adressInput").value = body.address;
            })
    })

}

// 두 마커 사이의 경계(bound)를 계산하고 줌 레벨과 중심을 조정하는 함수 추가
function adjustMapBounds(startLatitude, startLongitude) {
    let padding = 0.001; // 패딩 값 (경계를 넓히기 위한 값)
    let bounds = new naver.maps.LatLngBounds(
        new naver.maps.LatLng(Math.min(startLatitude, tourAttrLatitude) - padding, Math.min(startLongitude, tourAttrLongitude) - padding),
        new naver.maps.LatLng(Math.max(startLatitude, tourAttrLatitude) + padding, Math.max(startLongitude, tourAttrLongitude) + padding));
    naviMap.fitBounds(bounds);
}
