## Yozora　- 景色共有アプリ



星や夜景などの写真を共有できるWebアプリです。
地図上にお気に入りの景色スポットを投稿し、複数の写真や感想を共有できます。

「穴場のきれいな景色をもっと簡単に共有、発見したい」という想いから開発しました。

---

## 機能一覧

- **認証**
  - 新規登録、ログイン（Spring　security）
  - パスワードリセット（メールリンク + 期限付きトークン）

- **ユーザー管理**
  - 新規登録 / ログイン / ログアウト / アカウント削除
  - プロフィール編集（ユーザー名・メール・アイコン画像・パスワード）
  - パスワード編集・リセット
  - ゲスト（投稿閲覧・投稿検索・投稿場所までのルート案内のみ可能）

- **投稿機能**
  - タイトル・概要欄・住所・位置情報（緯度/経度）を入力して投稿（住所、位置情報は自動入力で編集不可）
  - 複数画像をアップロード可能（カルーセル形式で複数表示）
  - 投稿編集（タイトル・概要欄・画像）
  - 投稿詳細表示

- **コメント機能**
  - 投稿のコメント総数の表示
  - 投稿にコメントを追加
  - 自分のコメントの編集・削除（モーダル形式）

- **いいね機能**
  - いいね数の表示
  - 投稿に対する「いいね」追加/解除

- **地図表示**
  - Google Maps 上に新規投稿ピンを表示
  - Google Maps 上に投稿ピンを表示
  - ピンをクリックして投稿詳細へ遷移
  - ルート案内リンク
  - 地図の形式の変更（航空写真・衛星写真）
  - ストリートビュー
  - 投稿検索（検索候補に出てくる投稿場所クリックで近くに移動）

- **一覧**
  - 投稿一覧表示（新着順 / 人気順 / ランダム順 / 過去順）

- **マイページ**
  - 自分の投稿一覧を表示
  - 編集（投稿 / プロフィール）

---

## 技術スタック

- **バックエンド**
  - Java 21
  - Spring Boot 3.5.3（MVC / Security / Validation / Data JPA / JDBC / Mail）
  - ModelMapper 3.2.0
  - Maven

- **フロントエンド**
  - Thymeleaf
  - HTML / CSS / JavaScript
  - Swiper.js（画像カルーセル）

- **データベース**
  - MySQL 8.0.36

- **インフラ / 開発環境**
  - Docker / Docker Compose
  - GitHub（ソースコード管理）
  - VS Code

---

### アーキテクチャ概要
MVCモデルに基づいて設計

- Users（ブラウザ）からのリクエストを Controller が受け取り
- Service 層でビジネスロジックを実行し
- Repository 層を通じて MySQL にアクセスします
- Controller は結果を View（Thymeleaf）に渡し、HTMLとしてユーザーに返却します

---

## ER図・テーブル設計

---

### ディレクトリ構造
Yozora/
|---.env
|---.gitignore
|---docker-compose.yml
|---Dockerfile
|---mvnw
|---mvnw.cmd
|---pom.xml
|---README.md
+---.mvn/
|   \---wrapper/
|           maven-wrapper.properties
+---.vscode/
+---images/
+---src/
    +---main/
    |   +---java/com/example/yozora/
    |   |   +---YozoraApplication.java
    |   |   |---common/
    |   |   |   +---annotation/
    |   |   |   |---config/
    |   |   |   |---validator/
    |   |   |--config/
    |   |   |---controller/
    |   |   |---entity/
    |   |   |---form/
    |   |   |---mapper/
    |   |   |---repository/
    |   |   |---security/
    |   |   \---service/
    |   \---resources/
    |       |   application.properties
    |       +---static/
    |       |   +---css/
    |       |   \---js/
    |       \---templates/
    |           \---fragments/
    \---test/

---

## セットアップ方法

### 前提
- Docker / Docker Compose がインストール済み

### 手順
```bash
# 1. リポジトリを取得
git clone <YOUR_REPO_URL>
cd yozora

# 2. 環境変数ファイルを作成（.env）
cat <<EOF > .env
MYSQL_ROOT_PASSWORD=changeme
MYSQL_DATABASE=yozora
MYSQL_USER=app
MYSQL_PASSWORD=app
EOF

# 3. DB初期化（必要に応じて）
docker compose exec db sh -c "mysql -u$MYSQL_USER -p$MYSQL_PASSWORD $MYSQL_DATABASE < /docker-entrypoint-initdb.d/schema.sql"
docker compose exec db sh -c "mysql -u$MYSQL_USER -p$MYSQL_PASSWORD $MYSQL_DATABASE < /docker-entrypoint-initdb.d/seed.sql"

# 4. コンテナ起動
docker compose up -d --build

# 5. ブラウザでアクセス
http://localhost:8080

# 6. Google Maps API キー（任意）
Google Maps を表示するには、Google Cloud Platform で API キーを発行し、
`.env` に以下を追加してください。

GOOGLE_MAPS_API_KEY=your_api_key_here
```

---

## 今後の課題 / 改善ポイント

- **テストコード**
  - 現状は動作確認のみで自動テストは未整備。JUnit などで単体・結合テストを書いて品質を担保していきたい。

- **エラーハンドリング**
  - 設計で例外が出ないようにしているが、共通のエラーページを用意するとユーザー体験が向上する。

- **パフォーマンス**
  - 大量データでの動作は未検証。現状ではパスワードリセットメールが遅いと感じるので改善余地あり。

- **セキュリティ**
  - 今は弱点を特に感じていないが、自分で気づいていないリスクがある可能性がある。CSRF/XSS など基本的な脆弱性への対策を再確認したい。

- **開発フロー**
  - デプロイやCI/CDは未経験。今後は GitHub Actions などを使った自動テスト・自動デプロイを学びたい。

- **UI/UX**
  - 改善したいポイントが多い。特にモバイル対応やマイページ/投稿画面の見やすさを強化していきたい。

- **機能追加**
  - フォロー機能や「いいねした投稿を一覧で見られる機能」を追加したい。

- **画像保存**
  - 現在はローカルに画像を保存している。将来的には **Amazon S3** を利用して外部ストレージに保存し、スケーラビリティと信頼性を高めたい。

- **バッチ処理**
  - 今はパスワードリセット用トークン削除のバッチしかない。将来的にはメール通知や定期処理など、自動実行タスクを増やしたい。

- **監視**
  - エラーや異常を可視化する仕組みは未導入。今後は Actuator や外部サービスを使ってアプリの稼働状況を監視できるようにしたい。

---

## 実際の画面

- ログイン画面


- 新規登録画面


- TOP画面


- 景色一覧画面


- マイページ画面


- 投稿詳細画面


- プロフィール編集画面


- 新規投稿画面


- 投稿編集画面
<p>&nbsp;</p>

- パスワードを忘れた方用の画面


- パスワード再設定のご案内メール


- パスワード再設定画面



- TOP画面の詳細ピン、投稿ピン


- コメント欄


- コメント編集機能


- 詳細画面のコメント欄

