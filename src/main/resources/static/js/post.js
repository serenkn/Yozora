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
    const form = document.querySelector('form');

    if (!input || !wrapper || !form) return;

    // 1) 初期の既存URLをキャッシュ（後で選び直し時に復元するため）
    let existingUrls = Array.from(form.querySelectorAll('input[name="imageUrls"]'))
        .map(el => el.value);

    // 既存URLからスライドを再描画（選び直し用）
    function renderExisting() {
        wrapper.innerHTML = '';
        existingUrls.forEach(url => {
            const slide = document.createElement('div');
            slide.className = 'swiper-slide';
            slide.innerHTML = `
                <div class="preview-box">
                    <img src="${url}" alt="投稿画像" class="carousel-image">
                    ${showDelete ? `<button type="button" class="delete-btn">×</button>` : ''}
                    <input type="hidden" name="imageUrls" value="${url}">
                </div>
            `;
            wrapper.appendChild(slide);
        });
        swiper.update();
        swiper.slideTo(0);
    }

    // 2) ファイル選択 → 「全入れ替えモード」で新規のみ表示（既存は表示から外れる）
    input.addEventListener('change', () => {
        wrapper.innerHTML = '';
        const files = Array.from(input.files);
        files.forEach(file => {
            const reader = new FileReader();
            reader.onload = function (e) {
                const slide = document.createElement('div');
                slide.className = 'swiper-slide';
                // data-new で「新規スライド」印を付与（hiddenは作らない）
                slide.setAttribute('data-new', 'true');
                slide.innerHTML = `
                    <div class="preview-box">
                        <img src="${e.target.result}" alt="preview">
                        ${showDelete ? `<button type="button" class="delete-btn">×</button>` : ''}
                    </div>
                `;
                wrapper.appendChild(slide);
                swiper.update();
            };
            reader.readAsDataURL(file);
        });
        if (files.length) swiper.slideTo(0);
    });

    // 3) バツボタン
    if (showDelete) {
        document.addEventListener('click', function (e) {
            if (!e.target.classList.contains('delete-btn')) return;

            const slide = e.target.closest('.swiper-slide');
            if (!slide) return;

            // 3-1) 既存スライド：hiddenを持っている → その1枚だけ削除
            const hidden = slide.querySelector('input[name="imageUrls"]');
            if (hidden) {
                // existingUrls からも削除して整合
                const url = hidden.value;
                existingUrls = existingUrls.filter(u => u !== url);

                slide.remove();
                swiper.update();
                return;
            }

            // 3-2) 新規スライド：個別削除は不可 → いったん全部クリアして選び直し
            if (slide.hasAttribute('data-new')) {
                if (confirm('新規追加は全入れ替えです。この選択をクリアして選び直しますか？')) {
                    // 入力とプレビューをリセット
                    input.value = '';
                    renderExisting(); // 既存を復元
                }
            }
        });
    }

    // 4) 送信前チェック（新規 or 既存のどちらかが1枚以上）
    form.addEventListener('submit', function (e) {
        const hasFiles = input.files && input.files.length > 0;
        const hasExisting = form.querySelectorAll('input[name="imageUrls"]').length > 0;
        if (!hasFiles && !hasExisting) {
            e.preventDefault();
            alert('画像を1枚以上選択してください');
        }
    });
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

// ==================== コメント編集モーダル ====================

// モーダルを開く
window.openEditModal = function (button) {
    const modal = document.getElementById("editModal");       // モーダル本体
    const idInput = document.getElementById("editCommentId"); // hidden コメントID
    const textInput = document.getElementById("editCommentText"); // text
    const saveBtn = modal.querySelector('button[type="submit"]');

    // 編集対象のコメント要素を取得
    const row = button.closest(".comment");
    if (!row || !modal) return;

    // コメントIDをセット
    idInput.value = button.dataset.id;

    // コメント本文をセット
    const body = row.querySelector(".comment-body");
    const original = body ? body.innerText : "";
    textInput.value = original;
    textInput.dataset.original = original;

    // ===  ret hidden をセット ===
    const form = modal.querySelector('form');
    let retEl = form.querySelector('input[name="ret"]');
    if (!retEl) {
        retEl = document.createElement('input');
        retEl.type = 'hidden';
        retEl.name = 'ret';
        form.prepend(retEl);
    }
    retEl.value = location.pathname + location.search;

    // 入力チェック（未変更 or 空なら保存ボタンOFF）
    function check() {
        const cur = textInput.value.trim();
        const changed = cur !== textInput.dataset.original.trim();
        saveBtn.disabled = !changed || cur === "";
    }
    textInput.oninput = check;
    check();

    // モーダル表示
    modal.style.display = "flex";
    textInput.focus();
};

// モーダルを閉じる
window.closeEditModal = function () {
    const modal = document.getElementById("editModal");
    if (modal) modal.style.display = "none";
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

    //  送信フォームの action に ret を JS で付与（hidden は使わない）
    const addForm = document.querySelector('#commentModal form');
    if (addForm) {
        const ret = encodeURIComponent(location.pathname + location.search);
        addForm.action = '/comment/add?ret=' + ret;
    }

    // hidden の 
    const retEl = document.getElementById('cm-ret');
    if (retEl) {
        retEl.value = location.pathname + location.search;
    }

    // ログインユーザーID
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
                <form method="post" action="/comment/delete" style="display:inline;" onsubmit="return confirm('コメントを削除しますか？');">
                  <input type="hidden" name="id" value="${c.id}">
                  <input type="hidden" name="ret" value="${location.pathname + location.search}">
                  <button type="submit">削除</button>
                </form>
              </div>
            ` : ""}
          </div>
        `;
        }).join("")
        : `<div class="comment"><div class="comment-body">まだコメントはありません</div></div>`;

    // 日付の相対表示
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

//今いるページをチェック：フッター
document.addEventListener("DOMContentLoaded", () => {
    // 末尾のスラッシュを除去して正規化
    const norm = p => (p || "/").replace(/\/+$/, "") || "/";

    const path = norm(window.location.pathname);

    document.querySelectorAll(".footer-nav a").forEach(a => {
        const href = norm(a.getAttribute("href") || "");
        // 完全一致 or 下位パス
        if (path === href || path.startsWith(href + "/")) {
            a.classList.add("active");
        }
    });
});

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