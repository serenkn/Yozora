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
                <div style="max-width: 250px; font-family: sans-serif;">
                    <div style="display: flex; justify-content: space-between; align-items: center;">
                        <div style="display: flex; align-items: center;">
                            <img src="${post.profileImage}" alt="アイコン"
                                style="width: 24px; height: 24px; border-radius: 50%; object-fit: cover; margin-right: 6px;"
                                onerror="this.src='/images/default_profile.png'">
                            <span style="font-size: 14px; color: black;">${post.userName}</span>
                        </div>
                    </div>

                    <div style="margin-top: 8px;">
                        <img src="${post.imageUrls[0]}" alt="投稿画像"
                            style="width: 100%; border-radius: 4px;"
                            onerror="this.src='/images/default-image.jpg'">
                    </div>

                    <div style="display: flex; justify-content: flex-end; gap: 10px; margin-top: 6px; font-size: 13px; color: black;">
                        <div>${post.createdAt}</div>
                        <div>⭐ ${post.likeCount}</div>
                        <div>💬 ${post.c < div class="comment-summary" >
                💬 コメント < span th: text = "${post.commentList.size()}" > 0</span > 件
            </div > ommentCount
        }</div >
        <button type="button" onclick="openGoogleMapsRoute(${post.latitude}, ${post.longitude})"
            style="background: none; border: none; font-size: 14px; color: #007bff; cursor: pointer;">
            📍ルート
        </button>
                    </div >

                    <div style="margin-top: 4px;">
                        <strong style="font-size: 14px; color: black;">${post.title}</strong>
                    </div>

                    <div style="font-size: 13px; color: black;">
                        ${post.address}
                    </div>

                    <form action="/post" style="text-align: right; margin-top: 6px;">
                        <input type="hidden" name="id" value="${post.id}">
                        <button type="submit" style="font-size: 13px; color: #007bff;">詳細はこちらへ</button>
                    </form>
                </div >
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

            if (tempMarker) tempMarker.setMap(null);
            if (tempInfoWindow) tempInfoWindow.close();

            tempMarker = new google.maps.Marker({ position: latlng, map: map });

            tempInfoWindow = new google.maps.InfoWindow({
                content: `
            < div >
                  <a href="/postCreate?lat=${lat}&lng=${lng}&address=${encodeURIComponent(address)}">投稿する</a><br>
                  <button onclick="tempInfoWindow.close()">閉じる</button>
                  </div>`
            });

            tempInfoWindow.open(map, tempMarker);
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
                li.textContent = `${ post.title }（${ post.address }）`;
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

// ==================== 共通エラー表示 ====================
function showErrorAlert() {
    const errorElement = document.getElementById("error-msg");
    if (errorElement) {
        const error = errorElement.dataset.error;
        alert(error);
    }
}