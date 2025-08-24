// ==================== 初期定義 ====================
let map;
let markers = [];
let tempMarker = null;
let tempInfoWindow = null;

// ==================== GoogleMap 初期化 ====================
function initMap() {
    navigator.geolocation.getCurrentPosition(
        function (position) {
            const center = {
                lat: position.coords.latitude,
                lng: position.coords.longitude
            };

            map = new google.maps.Map(document.getElementById("map"), {
                center,
                zoom: 10,
                mapTypeId: 'hybrid',
                mapTypeControl: false,
                fullscreenControl: false,
                zoomControl: false,
                streetViewControl: true,
                rotateControl: false
            });

            if (typeof posts !== "undefined") {
                addPostMarkers(posts);
            }

            map.getStreetView().addListener('visible_changed', () => {
                const controls = document.getElementById("map-controls");
                if (controls) {
                    controls.style.display = map.getStreetView().getVisible() ? "none" : "flex";
                }
            });

            map.addListener("click", function (e) {
                const lat = e.latLng.lat();
                const lng = e.latLng.lng();
                reverseGeocodeAndShowLink(lat, lng);
            });
        },
        function () {
            alert("現在地の取得に失敗しました。");
        }
    );
}
window.initMap = initMap;


// 地図タイプ切替
function setMapType(type) {
    if (map) map.setMapTypeId(type);
}

// ==================== 投稿ピン描画 ====================
function addPostMarkers(posts) {
    const infoWindow = new google.maps.InfoWindow();

    posts.forEach(function (post) {
        const latLng = { lat: post.latitude, lng: post.longitude };

        const marker = new google.maps.Marker({
            position: latLng,
            map: map
        });

        marker.postId = post.id;

        marker.addListener("click", function () {
            const content = `
               <div class="map-infobox">
                    <div class="map-infobox__header">
                        <img src="${post.profileImage}" alt="アイコン"
                            class="map-infobox__icon"
                            onerror="this.src='/images/default_icon.png'">
                        <span class="map-infobox__user">${post.userName}</span>
                    </div>

                    <div class="map-infobox__title"><strong>${post.title}</strong></div>

                    <div class="map-infobox__image-wrap">
                        <img src="${post.imageUrls[0]}" alt="投稿画像"
                            class="map-infobox__image"
                            onerror="this.src='/images/test1.jpg'">
                    </div>

                    <div class="map-infobox__meta">
                        <div>${formatDateTime(post.createdAt)}</div>
                        <div>⭐ ${post.likeCount}</div>
                        <div>💬 ${post.commentCount}</div>
                        <button type="button" class="map-infobox__route"
                                onclick="openGoogleMapsRoute(${post.latitude}, ${post.longitude})">
                        📍ルート
                        </button>
                    </div>

                    <form action="/post" class="detail-link" style="margin-top:6px;">
                        <input type="hidden" name="id" value="${post.id}">
                        <button type="submit">詳細はこちらへ</button>
                    </form>
                </div>
                `;
            infoWindow.setContent(content);
            infoWindow.open(map, marker);
        });

        markers.push({ marker, post });
    });
}

// ==================== 新規投稿、逆ジオコーディング ====================
function reverseGeocodeAndShowLink(lat, lng) {
    const geocoder = new google.maps.Geocoder();
    const latlng = { lat, lng };

    geocoder.geocode({ location: latlng }, function (results, status) {
        if (status === "OK" && results[0]) {
            const address = results[0].formatted_address;

            // 既存の一時ピン・InfoWindow を削除
            if (tempMarker) tempMarker.setMap(null);
            if (tempInfoWindow) tempInfoWindow.close();

            // 新しい仮ピン追加
            tempMarker = new google.maps.Marker({
                position: latlng,
                map: map
            });

            // InfoWindow 表示
            tempInfoWindow = new google.maps.InfoWindow({
                content: `
                     <div class="infobox">
                        <div class="post-link">
                            <a class="post-link-btn"
                            href="/postCreate?lat=${lat}&lng=${lng}&address=${encodeURIComponent(address)}">
                            投稿する
                            </a>
                        </div>
                    </div>`
            });

            tempInfoWindow.open(map, tempMarker);

            // ×ボタンでピンも消す
            tempInfoWindow.addListener("closeclick", () => {
                if (tempMarker) {
                    tempMarker.setMap(null);
                    tempMarker = null;
                }
            });

        } else {
            alert("住所の取得に失敗しました: " + status);
        }
    });
}


// ==================== 検索バー機能 ====================
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
                li.textContent = `${post.title}（${post.address}）`;
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

// ==================== バツボタンで仮ピン削除 ====================
document.addEventListener("DOMContentLoaded", function () {
    const cancelBtn = document.getElementById("cancel-button");
    if (cancelBtn) {
        cancelBtn.addEventListener("click", function () {
            if (tempMarker) {
                tempMarker.setMap(null);
                tempMarker = null;
            }
            if (tempInfoWindow) {
                tempInfoWindow.close();
                tempInfoWindow = null;
            }
        });
    }
});

// ==================== 共通エラー表示 ====================
function showErrorAlert() {
    const errorElement = document.getElementById("error-msg");
    if (errorElement) {
        const error = errorElement.dataset.error;
        alert(error);
    }
}