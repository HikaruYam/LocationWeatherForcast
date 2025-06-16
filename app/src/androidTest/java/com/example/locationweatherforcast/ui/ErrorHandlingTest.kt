package com.example.locationweatherforcast.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.locationweatherforcast.ui.components.*
import com.example.locationweatherforcast.ui.theme.LocationWeatherForcastTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI Tests for error state handling and recovery
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ErrorHandlingTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun genericErrorStateDisplaysCorrectly() {
        var retryClicked = false

        composeTestRule.setContent {
            LocationWeatherForcastTheme {
                ErrorStateComponent(
                    title = "エラーが発生しました",
                    message = "予期しないエラーが発生しました。もう一度お試しください。",
                    primaryAction = ErrorAction(
                        text = "再試行",
                        icon = Icons.Default.Refresh,
                        onClick = { retryClicked = true }
                    )
                )
            }
        }

        // Verify error title and message are displayed
        composeTestRule
            .onNodeWithText("エラーが発生しました")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("予期しないエラーが発生しました。もう一度お試しください。")
            .assertIsDisplayed()

        // Verify retry button is displayed and clickable
        composeTestRule
            .onNodeWithText("再試行")
            .assertIsDisplayed()
            .performClick()

        // Verify click was handled
        assert(retryClicked)
    }

    @Test
    fun networkErrorComponentDisplaysCorrectly() {
        var retryClicked = false

        composeTestRule.setContent {
            LocationWeatherForcastTheme {
                NetworkErrorComponent(
                    onRetry = { retryClicked = true }
                )
            }
        }

        // Verify network error specific content
        composeTestRule
            .onNodeWithText("インターネット接続エラー")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("インターネット接続を確認してください。ネットワークに接続されていないか、接続が不安定な可能性があります。")
            .assertIsDisplayed()

        // Test retry functionality
        composeTestRule
            .onNodeWithText("再試行")
            .assertIsDisplayed()
            .performClick()

        assert(retryClicked)
    }

    @Test
    fun locationPermissionErrorDisplaysCorrectly() {
        var permissionRequested = false
        var manualLocationClicked = false

        composeTestRule.setContent {
            LocationWeatherForcastTheme {
                LocationPermissionErrorComponent(
                    onRequestPermission = { permissionRequested = true },
                    onManualLocation = { manualLocationClicked = true }
                )
            }
        }

        // Verify permission error content
        composeTestRule
            .onNodeWithText("位置情報の許可が必要です")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("現在地の天気を表示するために、位置情報へのアクセス許可が必要です。設定から許可してください。")
            .assertIsDisplayed()

        // Test permission request button
        composeTestRule
            .onNodeWithText("許可する")
            .assertIsDisplayed()
            .performClick()

        assert(permissionRequested)

        // Test manual location button
        composeTestRule
            .onNodeWithText("手動で場所を設定")
            .assertIsDisplayed()
            .performClick()

        assert(manualLocationClicked)
    }

    @Test
    fun gpsDisabledErrorDisplaysCorrectly() {
        var gpsEnabled = false
        var manualLocationClicked = false

        composeTestRule.setContent {
            LocationWeatherForcastTheme {
                GpsDisabledErrorComponent(
                    onEnableGps = { gpsEnabled = true },
                    onManualLocation = { manualLocationClicked = true }
                )
            }
        }

        // Verify GPS disabled content
        composeTestRule
            .onNodeWithText("GPS が無効です")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("位置情報サービスが無効になっています。設定でGPSを有効にしてください。")
            .assertIsDisplayed()

        // Test GPS enable button
        composeTestRule
            .onNodeWithText("GPS設定を開く")
            .assertIsDisplayed()
            .performClick()

        assert(gpsEnabled)

        // Test manual location button
        composeTestRule
            .onNodeWithText("手動で場所を設定")
            .assertIsDisplayed()
            .performClick()

        assert(manualLocationClicked)
    }

    @Test
    fun apiErrorComponentDisplaysCorrectly() {
        var retryClicked = false
        val testMessage = "API サーバーに接続できません"

        composeTestRule.setContent {
            LocationWeatherForcastTheme {
                ApiErrorComponent(
                    message = testMessage,
                    onRetry = { retryClicked = true }
                )
            }
        }

        // Verify API error content
        composeTestRule
            .onNodeWithText("サービス一時停止中")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("$testMessage\n\nしばらく待ってから再度お試しください。")
            .assertIsDisplayed()

        // Test retry functionality
        composeTestRule
            .onNodeWithText("再試行")
            .assertIsDisplayed()
            .performClick()

        assert(retryClicked)
    }

    @Test
    fun emptyFavoritesComponentDisplaysCorrectly() {
        var currentLocationAdded = false
        var manualLocationAdded = false

        composeTestRule.setContent {
            LocationWeatherForcastTheme {
                EmptyFavoritesComponent(
                    onAddCurrentLocation = { currentLocationAdded = true },
                    onAddManualLocation = { manualLocationAdded = true }
                )
            }
        }

        // Verify empty state content
        composeTestRule
            .onNodeWithText("お気に入りの場所がありません")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("天気を確認したい場所を追加して、いつでも簡単にチェックできるようにしましょう。")
            .assertIsDisplayed()

        // Test add current location button
        composeTestRule
            .onNodeWithText("現在地を追加")
            .assertIsDisplayed()
            .performClick()

        assert(currentLocationAdded)

        // Test add manual location button
        composeTestRule
            .onNodeWithText("場所を手動で追加")
            .assertIsDisplayed()
            .performClick()

        assert(manualLocationAdded)
    }

    @Test
    fun offlineModeIndicatorDisplaysCorrectly() {
        composeTestRule.setContent {
            LocationWeatherForcastTheme {
                OfflineModeIndicator()
            }
        }

        // Verify offline indicator content
        composeTestRule
            .onNodeWithText("オフラインモード - キャッシュされたデータを表示")
            .assertIsDisplayed()
    }

    @Test
    fun errorStateWithSecondaryActionDisplaysCorrectly() {
        var primaryClicked = false
        var secondaryClicked = false

        composeTestRule.setContent {
            LocationWeatherForcastTheme {
                ErrorStateComponent(
                    title = "場所を削除してください",
                    message = "お気に入りの場所が上限に達しています",
                    primaryAction = ErrorAction(
                        text = "場所を削除",
                        icon = Icons.Default.Refresh,
                        onClick = { primaryClicked = true }
                    ),
                    secondaryAction = ErrorAction(
                        text = "キャンセル",
                        icon = Icons.Default.Refresh,
                        onClick = { secondaryClicked = true }
                    )
                )
            }
        }

        // Verify both actions are displayed
        composeTestRule
            .onNodeWithText("場所を削除")
            .assertIsDisplayed()
            .performClick()

        composeTestRule
            .onNodeWithText("キャンセル")
            .assertIsDisplayed()
            .performClick()

        assert(primaryClicked)
        assert(secondaryClicked)
    }

    @Test
    fun errorStateAccessibility() {
        composeTestRule.setContent {
            LocationWeatherForcastTheme {
                ErrorStateComponent(
                    title = "アクセシビリティテスト",
                    message = "このエラーはアクセシブルです",
                    primaryAction = ErrorAction(
                        text = "再試行",
                        icon = Icons.Default.Refresh,
                        onClick = { }
                    )
                )
            }
        }

        // Verify content is accessible
        composeTestRule
            .onNodeWithText("アクセシビリティテスト")
            .assertIsDisplayed()

        // Verify retry button is accessible
        composeTestRule
            .onNodeWithText("再試行")
            .assertIsDisplayed()
    }

    @Test
    fun errorRecoveryFlow() {
        var currentState = "error"
        var retryAttempts = 0

        composeTestRule.setContent {
            LocationWeatherForcastTheme {
                when (currentState) {
                    "error" -> {
                        NetworkErrorComponent(
                            onRetry = {
                                retryAttempts++
                                if (retryAttempts >= 2) {
                                    currentState = "success"
                                }
                            }
                        )
                    }
                    "success" -> {
                        // Show success content
                        androidx.compose.material3.Text("接続が復旧しました")
                    }
                }
            }
        }

        // Start in error state
        composeTestRule
            .onNodeWithText("インターネット接続エラー")
            .assertIsDisplayed()

        // First retry attempt
        composeTestRule
            .onNodeWithText("再試行")
            .performClick()

        composeTestRule.waitForIdle()

        // Still in error state after first attempt
        composeTestRule
            .onNodeWithText("インターネット接続エラー")
            .assertIsDisplayed()

        // Second retry attempt should succeed
        composeTestRule
            .onNodeWithText("再試行")
            .performClick()

        composeTestRule.waitForIdle()

        // Should now show success message
        composeTestRule
            .onNodeWithText("接続が復旧しました")
            .assertIsDisplayed()
    }
}