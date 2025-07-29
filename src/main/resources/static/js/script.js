// ======== 初期定義 ========
let map;
let markers = [];
let tempMarker = null;
let tempInfoWindow = null;

// 投稿データのサンプル
function initMap() {
    navigator.geolocation.getCurrentPosition(
        function (position) {
            const center = {
                lat: position.coords.latitude,
                lng: position.coords.longitude
            };

            map = new google.maps.Map(document.getElementById("map"), {
                // 現在地を中心に地図を初期化
                center: center,
                zoom: 30,
                mapTypeId: 'hybrid',
                mapTypeControl: false,
                fullscreenControl: false,
                zoomControl: false,
                streetViewControl: true,
                rotateControl: false,
            });

            const infoWindow = new google.maps.InfoWindow();

            // 投稿仮ピン設置
            map.addListener("click", function (e) {
                if (tempMarker) tempMarker.setMap(null);
                if (tempInfoWindow) tempInfoWindow.close();

                tempMarker = new google.maps.Marker({
                    position: e.latLng,
                    map: map
                });

                tempInfoWindow = new google.maps.InfoWindow({
                    // ピンをクリックしたときの内容
                    content: `
            <div>
              <a href="/postCreate?lat=${e.latLng.lat()}&lng=${e.latLng.lng()}">投稿する</a><br>
            </div>
          `
                });

                tempInfoWindow.open(map, tempMarker);

                google.maps.event.addListener(tempInfoWindow, "closeclick", function () {
                    if (tempMarker) {
                        tempMarker.setMap(null);
                        tempMarker = null;
                    }
                    tempInfoWindow = null;
                });
            });

            // 投稿ピン描画
            posts.forEach(function (post) {
                const latLng = { lat: post.latitude, lng: post.longitude };
                const marker = new google.maps.Marker({
                    position: latLng,
                    map: map
                });

                marker.addListener("click", function () {
                    const content = `
            <div style="max-width: 250px;">
              <img src="${post.imageUrls[0]}" alt="投稿画像" style="width: 100%; border-radius: 5px;" onerror="this.src='/images/default-image.jpg'"><br>
              <strong>${post.title}</strong><br>
              <a href="/top?lat=${post.latitude}&lng=${post.longitude}">ここまでのルート</a><br>
              <a href="/post/${post.postId}">投稿詳細を見る</a>
            </div>
          `;
                    infoWindow.setContent(content);
                    infoWindow.open(map, marker);
                });

                markers.push({ marker: marker, post: post });
            });

            // URLパラメータでルート描画
            const urlParams = new URLSearchParams(window.location.search);
            const lat = parseFloat(urlParams.get("lat"));
            const lng = parseFloat(urlParams.get("lng"));
            if (!isNaN(lat) && !isNaN(lng)) {
                drawRoute(lat, lng);
            }

            // StreetView中は UI 非表示
            map.getStreetView().addListener('visible_changed', () => {
                const controls = document.getElementById("map-controls");
                if (!controls) return;
                controls.style.display = map.getStreetView().getVisible() ? "none" : "flex";
            });
        },
        function () {
            alert("現在地の取得に失敗しました。");
        }
    );
}

// ルート案内機能
function drawRoute(destLat, destLng) {
    const directionsService = new google.maps.DirectionsService();
    const directionsRenderer = new google.maps.DirectionsRenderer();
    directionsRenderer.setMap(map);

    navigator.geolocation.getCurrentPosition(function (position) {
        const request = {
            origin: {
                lat: position.coords.latitude,
                lng: position.coords.longitude
            },
            destination: {
                lat: destLat,
                lng: destLng
            },
            travelMode: google.maps.TravelMode.WALKING
        };

        directionsService.route(request, function (result, status) {
            if (status === google.maps.DirectionsStatus.OK) {
                directionsRenderer.setDirections(result);
            } else {
                alert("ルート案内に失敗しました: " + status);
            }
        });
    });
}

// 地図タイプ切替
function setMapType(type) {
    if (map) map.setMapTypeId(type);
}

// 検索機能
document.addEventListener("DOMContentLoaded", function () {
    const searchInput = document.getElementById("search-input");
    const suggestions = document.getElementById("search-suggestions");

    if (searchInput && suggestions) {
        searchInput.addEventListener("input", function () {
            const keyword = searchInput.value.trim().toLowerCase();
            suggestions.innerHTML = "";
            if (keyword === "") {
                suggestions.classList.add("hidden");
                return;
            }

            const matched = posts.filter(post =>
                post.title.toLowerCase().includes(keyword) ||
                post.address.toLowerCase().includes(keyword)
            );

            matched.forEach(post => {
                const li = document.createElement("li");
                li.textContent = post.title + "（" + post.address + "）";
                li.classList.add("suggestion-item");
                li.addEventListener("click", function () {
                    map.panTo({ lat: post.latitude, lng: post.longitude });
                    map.setZoom(13);
                    suggestions.classList.add("hidden");
                });
                suggestions.appendChild(li);
            });

            suggestions.classList.toggle("hidden", matched.length === 0);
        });
    }
});

// GoogleMap 初期化
window.initMap = initMap;

// 共通アラート処理
function showErrorAlert() {
    const errorElement = document.getElementById("error-msg");
    if (errorElement) {
        const error = errorElement.dataset.error;
        if (error && error.trim() !== "") {
            alert(error);
        }
    }
}
window.addEventListener("DOMContentLoaded", showErrorAlert);

// プロフィール画像プレビュー
document.addEventListener('DOMContentLoaded', function () {
    const input = document.getElementById('imageInput');
    const preview = document.getElementById('profilePreview');

    input?.addEventListener('change', function () {
        const file = this.files[0];
        if (!file) return;

        const reader = new FileReader();
        reader.onload = function (e) {
            preview.src = e.target.result;
        };
        reader.readAsDataURL(file);
    });
});