<img width="1361" height="628" alt="ログイン画面" src="https://github.com/user-attachments/assets/21ff8555-654b-4d9a-8830-3e7b90e0cb19" /># Yozora - 景色共有アプリ

## 概要

**Yozora** は、星や夜景などの写真を共有できるWebアプリです。  
Google Map上にピンを立てて投稿し、全国の絶景スポットを記録・共有できます。


---

##　実際の画面

- ログイン画面
<img width="1361" height="628" alt="ログイン画面" src="https://github.com/user-attachments/assets/5007ecce-2cbd-4c91-972f-24bdc68d6739" />


---

## 主な機能

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

## 環境構築（Docker使用）

# 初回セットアップ（Yozora直下で）
docker compose up --build

gitコマンド
ステージング→コミット→プッシュ

git add .
git commit -m "コメント"
git push origin main


http://localhost:8080/login
