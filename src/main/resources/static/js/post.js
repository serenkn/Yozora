// ======== ページ種別の取得（create/edit の時のみ true） ========
const page = document.body.dataset.page;
const showDelete = (page === 'create' || page === 'edit');

// ======== Swiper 初期化関数 ========
let swiper;
function initSwiper() {
    swiper = new Swiper('.swiper', {
        loop: false,
        slidesPerView: 1,
        navigation: {
            nextEl: '.swiper-button-next',
            prevEl: '.swiper-button-prev'
        },
        pagination: {
            el: '.swiper-pagination',
            clickable: true
        }
    });
}

// ======== 新規・編集ページ用：画像アップロード & プレビュー ========
function initImageUploadWithPreview() {
    initSwiper();

    const input = document.getElementById('imageInput');
    const wrapper = document.querySelector('.swiper-wrapper');
    if (!input || !wrapper) return;

    input.addEventListener('change', () => {
        wrapper.innerHTML = ''; // プレビュー初期化

        Array.from(input.files).forEach((file, index) => {
            const reader = new FileReader();
            reader.onload = function (e) {
                const slide = document.createElement('div');
                slide.className = 'swiper-slide';

                slide.innerHTML = `
                    <div class="preview-box">
                        <img src="${e.target.result}" alt="preview">
                        ${showDelete ? `<button type="button" class="delete-btn" data-index="${index}">×</button>` : ''}
                    </div>
                `;

                wrapper.appendChild(slide);
                swiper.update();
            };
            reader.readAsDataURL(file);
        });
    });

    // 画像削除ボタンの処理（新規・編集時のみ）
    if (showDelete) {
        document.addEventListener('click', function (e) {
            if (e.target.classList.contains('delete-btn')) {
                e.target.closest('.swiper-slide').remove();
                swiper.update();
            }
        });
    }

    //投稿編集時、既存画像がある場合はスルー
    document.querySelector("form").addEventListener("submit", function (e) {
        const imageFiles = document.getElementById("imageInput").files;
        const existingImageInputs = document.querySelectorAll("input[name='imageUrls']");

        if (imageFiles.length === 0 && existingImageInputs.length === 0) {
            e.preventDefault();
            alert("画像を1枚以上選択してください");
        }
    });

    // JSバリデーション（画像未選択なら送信不可）
    const form = document.querySelector('form[method="post"]');
    if (form) {
        form.addEventListener('submit', function (e) {
            if (input.files.length === 0) {
                alert('画像を1枚以上選択してください');
                e.preventDefault();
            }
        });
    }
}

// ======== 投稿詳細ページ用 Swiper 初期化（画像表示のみ） ========
function initDetailView() {
    initSwiper(); // HTMLに埋め込み済みの画像をそのまま表示
}

// ==================== Google マップへルート案内 ====================
function openGoogleMapsRoute(destLat, destLng) {
    if (!destLat || !destLng) {
        alert("目的地の位置情報が取得できません");
        return;
    }
    const url = `https://www.google.com/maps/dir/?api=1&destination=${destLat},${destLng}`;
    window.open(url, '_blank');
}

// ==================== いいねトグル機能 ====================
function toggleLike(button) {
    const postId = button.dataset.postId;
    const liked = button.dataset.liked === "true";

    // 連打防止
    if (button.disabled) return;
    button.disabled = true;

    const countSpan = button.querySelector("#like-count");
    let count = parseInt(countSpan.textContent, 10);

    // 表示だけ先に更新
    if (liked) {
        button.dataset.liked = "false";
        button.innerHTML = `★ <span id="like-count">${count - 1}</span>`;
    } else {
        button.dataset.liked = "true";
        button.innerHTML = `⭐ <span id="like-count">${count + 1}</span>`;
    }

    // サーバーにトグル送信
    fetch(`/like/toggle?postId=${postId}`, {
        method: "POST"
    })
        .catch(err => {
            alert("通信エラーが発生しました");
            console.error(err);

            // エラー時は状態・表示を戻す
            button.dataset.liked = liked.toString();
            button.innerHTML = `${liked ? "⭐" : "★"} <span id="like-count">${count}</span>`;
        })
        .finally(() => {
            button.disabled = false;
        });
}
// ==================== コメント編集モーダル ====================
function openEditModal(button) {
    const commentId = button.dataset.id;
    const commentText = button.closest('.comment').querySelector('.comment-body').textContent;

    document.getElementById("editCommentId").value = commentId;
    document.getElementById("editCommentText").value = commentText;

    document.getElementById("editModal").style.display = "block";
}

function closeEditModal() {
    document.getElementById("editModal").style.display = "none";
}

// ======== 初期化処理 ========
document.addEventListener('DOMContentLoaded', function () {
    if (showDelete) {
        initImageUploadWithPreview();
    } else {
        initDetailView();
    }
});