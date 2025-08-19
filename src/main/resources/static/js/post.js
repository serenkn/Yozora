// ===== ページ種別（create/edit の時のみ true） =====
const page = document.body.dataset.page;
const showDelete = (page === 'create' || page === 'edit');

// ===== Swiper 初期化 =====
let swiper;
function initSwiper() {
    swiper = new Swiper('.swiper', {
        autoHeight: true,
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

// ===== 新規・編集：画像アップロード & プレビュー =====
function initImageUploadWithPreview() {
    initSwiper();

    const input = document.getElementById('imageInput');
    const wrapper = document.querySelector('.swiper-wrapper');
    if (!input || !wrapper) return;

    input.addEventListener('change', () => {
        wrapper.innerHTML = '';

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

    if (showDelete) {
        document.addEventListener('click', function (e) {
            if (e.target.classList.contains('delete-btn')) {
                e.target.closest('.swiper-slide').remove();
                swiper.update();
            }
        });
    }

    document.querySelector("form").addEventListener("submit", function (e) {
        const imageFiles = document.getElementById("imageInput").files;
        const existingImageInputs = document.querySelectorAll("input[name='imageUrls']");
        if (imageFiles.length === 0 && existingImageInputs.length === 0) {
            e.preventDefault();
            alert("画像を1枚以上選択してください");
        }
    });

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

// ===== 投稿詳細：Swiper 初期化 =====
function initDetailView() {
    initSwiper();
}

// ===== Google マップへルート案内 =====
function openGoogleMapsRoute(destLat, destLng) {
    if (!destLat || !destLng) {
        alert("目的地の位置情報が取得できません");
        return;
    }
    const url = `https://www.google.com/maps/dir/?api=1&destination=${destLat},${destLng}`;
    window.open(url, '_blank');
}

// ===== いいねトグル =====
function toggleLike(button) {
    const postId = button.dataset.postId;
    const liked = button.dataset.liked === "true";

    if (button.disabled) return;
    button.disabled = true;

    const countSpan = button.querySelector("#like-count");
    let count = parseInt(countSpan.textContent, 10);

    if (liked) {
        button.dataset.liked = "false";
        button.innerHTML = `★ <span id="like-count">${count - 1}</span>`;
    } else {
        button.dataset.liked = "true";
        button.innerHTML = `⭐ <span id="like-count">${count + 1}</span>`;
    }

    fetch(`/like/toggle?postId=${postId}`, { method: "POST" })
        .catch(err => {
            alert("通信エラーが発生しました");
            console.error(err);
            button.dataset.liked = liked.toString();
            button.innerHTML = `${liked ? "⭐" : "★"} <span id="like-count">${count}</span>`;
        })
        .finally(() => {
            button.disabled = false;
        });
}

// ===== 編集モーダル =====
window.openEditModal = function (button) {
    const commentId = button.dataset.id;
    const commentText = button.closest('.comment').querySelector('.comment-body').innerText;

    document.getElementById("editCommentId").value = commentId;
    document.getElementById("editCommentText").value = commentText;

    // いまのURLを戻り先として付与（編集）
    const ret = encodeURIComponent(location.pathname + location.search);
    const editForm = document.querySelector('#editModal form');
    if (editForm) editForm.action = '/comment/edit?ret=' + ret;

    document.getElementById("editModal").style.display = "block";
};

window.closeEditModal = function () {
    document.getElementById("editModal").style.display = "none";
};

// ===== コメントモーダル =====
function openCommentModal(button) {
    const postId = button.dataset.postId || "";
    const count = button.dataset.count ? parseInt(button.dataset.count, 10) : 0;

    // 埋め込みJSONを展開
    const jsonEl = document.getElementById(`comments-${postId}`);
    let comments = [];
    if (jsonEl && jsonEl.textContent) {
        try { comments = JSON.parse(jsonEl.textContent); } catch (_) { comments = []; }
    }

    const listEl = document.getElementById('cm-list');
    const countEl = document.getElementById('cm-count');
    const postIdEl = document.getElementById('cm-postId');

    // 件数
    countEl.textContent = Number.isFinite(count) && count >= 0 ? count : comments.length;

    // hidden の postId
    postIdEl.value = postId;

    // いまのURLを戻り先として付与（追加・削除）
    const ret = encodeURIComponent(location.pathname + location.search);

    // 追加フォームの action を差し替え
    const addForm = document.querySelector('#commentModal form');
    if (addForm) addForm.action = '/comment/add?ret=' + ret;

    // ログインユーザーID（必ず存在する前提）
    const loginUserId = document.getElementById('loginUserId').value;

    // コメント描画（本人のみ編集/削除表示）
    listEl.innerHTML = (comments && comments.length)
        ? comments.map(c => {
            const isMine = String(c.userId) === String(loginUserId);
            return `
          <div class="comment" data-user-id="${c.userId}">
            <div class="comment-header">
              <img src="${c.profileImage || ''}" alt="" class="comment-icon">
              <span class="comment-username">${c.userName || ''}</span>
              <span class="comment-date date-time">${c.createdAt || ''}</span>
            </div>
            <div class="comment-body">${c.text || ''}</div>
            ${isMine ? `
              <div class="comment-actions">
                <button type="button" onclick="openEditModal(this)" data-id="${c.id}">編集</button>
                <form method="post" action="/comment/delete?ret=${ret}" style="display:inline;">
                  <input type="hidden" name="id" value="${c.id}">
                  <button type="submit">削除</button>
                </form>
              </div>
            ` : ""}
          </div>
        `;
        }).join("")
        : `<div class="comment"><div class="comment-body">まだコメントはありません</div></div>`;

    // 日付の相対表示（定義があれば）
    if (typeof dateConvertTimes === "function") {
        try { dateConvertTimes(); } catch (_) { }
    }

    const modal = document.getElementById('commentModal');
    modal.style.display = 'flex';
    document.body.classList.add('modal-open');
}

// コメントモーダルを閉じる
function closeCommentModal() {
    const modal = document.getElementById('commentModal');
    modal.style.display = 'none';
    document.body.classList.remove('modal-open');
}

// ===== 日付変換 =====
function formatDateTime(dateTimeString) {
    const now = new Date();
    const target = new Date(dateTimeString);

    if (isNaN(target)) return '日時エラー';

    const diffMs = now - target;
    const diffSec = Math.floor(diffMs / 1000);
    const diffMin = Math.floor(diffSec / 60);
    const diffHour = Math.floor(diffMin / 60);
    const diffDay = Math.floor(diffHour / 24);
    const diffWeek = Math.floor(diffDay / 7);
    const diffMonth = Math.floor(diffDay / 30);

    if (diffSec < 60) return `${diffSec}秒前`;
    if (diffMin < 60) return `${diffMin}分前`;
    if (diffHour < 24) return `${diffHour}時間前`;
    if (diffDay < 7) return `${diffDay}日前`;
    if (diffWeek < 5) return `${diffWeek}週間前`;
    if (diffMonth < 12) return `${diffMonth}ヶ月前`;

    return `${target.getFullYear()}年${target.getMonth() + 1}月${target.getDate()}日`;
}

function dateConvertTimes() {
    document.querySelectorAll('.date-time').forEach(el => {
        const raw = el.textContent.trim();
        if (raw) el.textContent = formatDateTime(raw);
    });
}

// ===== スクロール復元 =====
function restoreScrollPosition() {
    const detailLinks = document.querySelectorAll(".detail-link");
    detailLinks.forEach(link => {
        link.addEventListener("click", () => {
            localStorage.setItem("scrollPosition", window.scrollY);
        });
    });

    const scrollY = localStorage.getItem("scrollPosition");
    if (scrollY !== null) {
        window.scrollTo(0, parseInt(scrollY));
        localStorage.removeItem("scrollPosition");
    }
}

// ===== 初期化 =====
function init() {
    if (showDelete) {
        initImageUploadWithPreview();
    } else {
        initDetailView();
    }

    dateConvertTimes();
    restoreScrollPosition();
}

document.addEventListener('DOMContentLoaded', init);