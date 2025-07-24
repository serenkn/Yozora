
let map;
let markers = [];

function initMap() {
    navigator.geolocation.getCurrentPosition(
        function (position) {
            const center = {
                lat: position.coords.latitude,
                lng: position.coords.longitude
            };

            map = new google.maps.Map(document.getElementById("map"), {
                zoom: 10,
                center: center
            });

            const infoWindow = new google.maps.InfoWindow();
            let tempMarker = null;

            // クリックして投稿ピンを追加
            map.addListener("click", function (e) {
                if (tempMarker) {
                    tempMarker.setMap(null);
                }
                tempMarker = new google.maps.Marker({
                    position: e.latLng,
                    map: map
                });

                const tempInfoWindow = new google.maps.InfoWindow({
                    content: `<a href="/post/new?lat=${e.latLng.lat()}&lng=${e.latLng.lng()}">投稿する</a>`
                });

                tempInfoWindow.open(map, tempMarker);
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
                            <img src="${post.imageUrls[0]}" alt="投稿画像" style="width: 100%; border-radius: 5px;"><br>
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

            // ルートパラメータで自動案内
            const urlParams = new URLSearchParams(window.location.search);
            const lat = parseFloat(urlParams.get("lat"));
            const lng = parseFloat(urlParams.get("lng"));
            if (!isNaN(lat) && !isNaN(lng)) {
                drawRoute(lat, lng);
            }

        },
        function () {
            alert("現在地の取得に失敗しました。");
        }
    );
}

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

window.initMap = initMap;

// エラーアラート処理（共通）
function showErrorAlert() {
    const errorElement = document.getElementById("error-msg");
    if (errorElement) {
        const error = errorElement.dataset.error;
        if (error !== "") {
            alert(error);
        }
    }
}

// DOM読み込み後にエラー表示を実行
window.addEventListener("DOMContentLoaded", showErrorAlert);

