# Yozora - 景色共有アプリ

## 概要

**Yozora** は、星や夜景などの「美しい景色」を共有できるWebアプリです。  
ユーザーは投稿・検索・コメント・いいね・フォローなどの機能を通じて、全国の絶景スポットを記録・共有できます。


---

## 主な機能 ✨

- ユーザー登録 / ログイン（Spring Security + BCrypt）
- 投稿機能（画像＋テキスト＋位置情報）
- Google Maps で投稿位置を表示
- いいね・コメント・フォロー機能
- マイページ（投稿一覧・お気に入り一覧）
- ゲストユーザー対応（閲覧のみ可）

---

## 技術スタック 🛠

| 項目           | 使用技術                               |
|---------------|--------------------------------------|
| 言語           | Java 21                              |
| フレームワーク 　 | Spring Boot                          |
| データベース     | MySQL                                |
| フロント        | HTML / CSS / JavaScript / Thymeleaf  | 
| API           | Google Maps API                      |
| その他    　    | Docker / GitHub / VS Code            |

---

## 環境構築（Docker使用）

```bash
# 初回セットアップ（Yozora直下で）
docker compose up --build

http://localhost:8080/login
http://localhost:8080/userRegister