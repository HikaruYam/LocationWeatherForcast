# CLAUDE.md
すべてのやりとりは日本語で行ってください。
app_requirement.mdにはこのアプリの要件定義書になっています。
実装が終わったら以下のコマンドでビルドが通るか確認し、エラーが発生したら、その原因を解消してください。
```bash
 ./gradlew assembleDebug
```
作業は`claude-code`ブランチから作成したブランチで作業してください。
作業は細かくコミットしてください。
実装が終わったら`claude-code`向けのRPを作成してください。

このファイルは、Claude Code (claude.ai/code) がこのリポジトリでコードを扱う際のガイダンスを提供します。

## ビルドと開発コマンド

```bash
# プロジェクトをビルド
./gradlew build

# 接続されたデバイス/エミュレータでデバッグビルドを実行
./gradlew installDebug

# ユニットテストを実行
./gradlew test

# インストゥルメンテーションテストを実行（デバイス/エミュレータが必要）
./gradlew connectedAndroidTest

# ビルド成果物をクリーンアップ
./gradlew clean

# APKを生成
./gradlew assembleDebug

# コードをLint
./gradlew lint
```

## アーキテクチャ概要

これは、Kotlin と Jetpack Compose で構築された Android 天気予報アプリケーションで、ユーザーの現在地に基づいて明日の天気を表示します。

### 主要なアーキテクチャコンポーネント

**Repository パターンを使用した MVVM:**
- `WeatherViewModel`: UI状態とビジネスロジックを管理
- `WeatherRepository`: ロケーションサービスと天気APIの間を調整
- `LocationService`: FusedLocationProviderClientを使用してGPSロケーションリクエストを処理
- `WeatherApiService`: Open-Meteo天気API用のRetrofitインターフェース

**データフロー:**
1. ユーザーが位置情報許可を付与 → `LocationService`がGPS座標を取得
2. `WeatherRepository`が座標を使用してOpen-Meteo APIから天気データを取得
3. APIレスポンスが`WeatherData`モデルにマッピング（明日の予報のみ）
4. `WeatherViewModel`が`WeatherUiState`をCompose UIに公開

**主要なデータモデル:**
- `WeatherData`: 日本語の天気説明を含むコア天気情報
- `LocationData`: GPS座標
- `WeatherResponse/DailyWeather`: Open-Meteo用のAPIレスポンスモデル

**UIコンポーネント:**
- `WeatherScreen`: 許可処理と状態管理を含むメイン画面
- `WeatherCard`, `LoadingComponent`, `ErrorComponent`: 再利用可能なUIコンポーネント
- Material3デザインシステムを使用

### 技術スタック
- **UI**: Jetpack Compose with Material3
- **ネットワーク**: Retrofit + OkHttp with kotlinx.serialization
- **位置情報**: Google Play Services FusedLocationProviderClient
- **アーキテクチャ**: Repository パターンを使用した MVVM
- **非同期処理**: Kotlin Coroutines with StateFlow

### API統合
Open-Meteo (api.open-meteo.com) の無料天気APIを使用 - APIキー不要。2日間の予報を取得しますが、日本語の説明で明日の天気のみを表示します。

### 必要な権限
- GPS用の `ACCESS_FINE_LOCATION` と `ACCESS_COARSE_LOCATION`
- 天気API呼び出し用の `INTERNET`

## コードスタイルガイドライン

### コメント規約
- **将来の作業**: 明確な説明を含む適切なTODO形式を使用
  ```kotlin
  // TODO: Replace with FavoriteLocationRepository when available
  // TODO: Add reverse geocoding for location names
  ```
- **非公式なコメントを避ける**: "(will be replaced with Repository)" や "For now, use mock data" のような非公式なコメントは使用しない
- **具体的に**: TODOコメントは、何をいつ行う必要があるかを明確に示す必要がある