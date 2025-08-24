## 概要

**Yozora** は、星や夜景などの写真を共有できるWebアプリです。  
Google Map上にピンを立てて投稿し、全国の絶景スポットを記録・共有できます。


---

##　実際の画面

- ログイン画面
<img width="1361" height="628" alt="ログイン画面" src="https://github.com/user-attachments/assets/5007ecce-2cbd-4c91-972f-24bdc68d6739" />

- 新規登録画面
<img width="1359" height="627" alt="新規登録画面" src="https://github.com/user-attachments/assets/13e83310-1bd3-4c29-988f-52cbbd831c79" />

- TOP画面
<img width="1363" height="632" alt="TOP画面" src="https://github.com/user-attachments/assets/d3e8f397-7fe0-4dfe-b058-c54d01395bb7" />

- 景色一覧画面
<img width="1364" height="632" alt="景色一覧画面" src="https://github.com/user-attachments/assets/4fbd8653-6ed7-47aa-84d5-3f776883ba26" />

- マイページ画面
<img width="1365" height="631" alt="マイページ画面、投稿あり" src="https://github.com/user-attachments/assets/371d034c-3eef-4d5f-b031-44090c027821" />

- 投稿詳細画面
<img width="1343" height="622" alt="詳細画面" src="https://github.com/user-attachments/assets/91afb741-7028-449f-8a58-4bb6b3b3e0f2" />

- プロフィール編集画面
<img width="1365" height="625" alt="ユーザー編集画面" src="https://github.com/user-attachments/assets/3dae5829-c808-414f-9268-a97c1d973d6b" />

- 新規投稿画面
<img width="1365" height="632" alt="新規投稿画面" src="https://github.com/user-attachments/assets/0bb1023e-8b30-4b50-9541-37a99e76a97b" />

- 投稿編集画面
<img width="1365" height="632" alt="投稿編集画面" src="https://github.com/user-attachments/assets/8cb30ba9-95e9-4827-8d8d-08fd5b090a5a" />

- コメント欄
<img width="1365" height="636" alt="コメント欄" src="https://github.com/user-attachments/assets/4681657f-2a62-49c0-b361-11552c72ed3a" />

- コメント編集機能
<img width="679" height="309" alt="コメント編集モーダル" src="https://github.com/user-attachments/assets/587823dd-105a-4add-98ca-b8a249c3fe5a" />

- 詳細画面のコメント欄
<img width="764" height="414" alt="詳細画面のコメント欄" src="https://github.com/user-attachments/assets/97b9a455-aac9-4151-bd0d-78c0ef844b30" />

- ER図
<img width="1020" height="429" alt="Yozora_ER図" src="https://github.com/user-attachments/assets/3bbc6fe8-7a63-46ea-b4b8-2ef4f8821bbd" />



---

## 機能一覧

- ユーザー登録 / ログイン（Spring Security + BCrypt）
- 投稿機能（画像＋テキスト＋位置情報）
- Google Maps で投稿位置を表示
- いいね・コメント・フォロー機能
- マイページ（投稿一覧）
- ゲストユーザー対応（閲覧のみ可）

---

## 技術スタック

### Backend
- Java 21
- Spring Boot 3.5.3
- Spring Web
- Spring Data JPA
- Spring JDBC
- Spring Validation
- Spring Security
- ModelMapper 3.2.0
- Lombok
- Maven

### Frontend
- Thymeleaf
- JavaScript (ES202x)
- Swiper.js
- CSS

### Database
- MySQL 8.x
  - 主要テーブル：`users`, `posts`, `post_images`, `comments`, `likes`

### Infra / DevOps
- Docker Compose（`app` / `db` のコンテナ構成）
- GitHub

### External APIs / SDK
- Google Maps JavaScript API

### 選定理由

本アプリは **Javaをメイン言語として開発**しました。

- **Java 21**: 最新LTSでモダンな構文を利用可能。  
- **Spring Boot 3.5.3**: Webアプリ開発に必要な機能を統合、開発効率が高い。  
- **Spring Web**: 
- **Spring Data JPA**: CRUD処理の効率化。  
- **Spring Security**: 認証・認可を標準的かつ安全に実装。  
- **Thymeleaf**: SSRでフォーム処理と相性が良い。  
- **MySQL 8.x**: 一般的なRDBMSで、実務と学習両面で有効。  
- **Docker Compose**: 環境差異をなくし、再現性を確保。  
- **Google Maps API**: 景色共有アプリの地図・ピン立てに必須。

### DB
<img width="1020" height="429" alt="Yozora_ER図" src="https://github.com/user-attachments/assets/c6817552-2dad-4b34-a9b0-484f26d318f7" />

# セットアップ
- JDK
- Docker
- 手順
-docker compose up --build →　http://localhost:8080/login　で起動
