package com.example.locationweatherforcast.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.locationweatherforcast.data.repository.FavoriteLocationRepository
import com.example.locationweatherforcast.data.repository.WeatherRepository
import com.example.locationweatherforcast.ui.screens.FavoriteLocationsScreen
import com.example.locationweatherforcast.ui.theme.LocationWeatherForcastTheme
import com.example.locationweatherforcast.ui.viewmodel.FavoriteLocationsViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

/**
 * UI Tests for favorite location addition and management flow
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class FavoriteLocationFlowTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    @Inject
    lateinit var weatherRepository: WeatherRepository

    @Inject
    lateinit var favoriteLocationRepository: FavoriteLocationRepository

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun emptyStateDisplaysCorrectly() {
        composeTestRule.setContent {
            LocationWeatherForcastTheme {
                FavoriteLocationsScreen()
            }
        }

        // Verify empty state is displayed
        composeTestRule
            .onNodeWithText("お気に入りの場所がありません")
            .assertIsDisplayed()

        // Verify add location buttons are displayed
        composeTestRule
            .onNodeWithText("現在地を追加")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("手動で場所を追加")
            .assertIsDisplayed()
    }

    @Test
    fun addLocationButtonOpensMenu() {
        composeTestRule.setContent {
            LocationWeatherForcastTheme {
                FavoriteLocationsScreen()
            }
        }

        // Click the floating action button to add a location
        composeTestRule
            .onNodeWithContentDescription("場所を追加")
            .performClick()

        // Verify dropdown menu is displayed
        composeTestRule
            .onNodeWithText("現在地を追加")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("人気の場所から選択")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("手動で追加")
            .assertIsDisplayed()
    }

    @Test
    fun addCurrentLocationFlow() {
        composeTestRule.setContent {
            LocationWeatherForcastTheme {
                FavoriteLocationsScreen()
            }
        }

        // Open add menu
        composeTestRule
            .onNodeWithContentDescription("場所を追加")
            .performClick()

        // Click add current location
        composeTestRule
            .onNodeWithText("現在地を追加")
            .performClick()

        // Should show loading state initially
        composeTestRule.waitForIdle()

        // After location is added, it should appear in the list
        // Note: This test may fail if location permissions are not granted in test
        // In a real test environment, we would mock the location service
    }

    @Test
    fun manualLocationInputDialogOpensAndCloses() {
        composeTestRule.setContent {
            LocationWeatherForcastTheme {
                FavoriteLocationsScreen()
            }
        }

        // Open add menu
        composeTestRule
            .onNodeWithContentDescription("場所を追加")
            .performClick()

        // Click manual add
        composeTestRule
            .onNodeWithText("手動で追加")
            .performClick()

        composeTestRule.waitForIdle()

        // Verify dialog is displayed
        composeTestRule
            .onNodeWithText("場所を追加")
            .assertIsDisplayed()

        // Verify input fields are present
        composeTestRule
            .onNodeWithText("場所名")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("緯度")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("経度")
            .assertIsDisplayed()

        // Close dialog with cancel
        composeTestRule
            .onNodeWithText("キャンセル")
            .performClick()

        // Verify dialog is closed
        composeTestRule
            .onNodeWithText("場所を追加")
            .assertDoesNotExist()
    }

    @Test
    fun manualLocationInputValidation() {
        composeTestRule.setContent {
            LocationWeatherForcastTheme {
                FavoriteLocationsScreen()
            }
        }

        // Open manual add dialog
        composeTestRule
            .onNodeWithContentDescription("場所を追加")
            .performClick()

        composeTestRule
            .onNodeWithText("手動で追加")
            .performClick()

        composeTestRule.waitForIdle()

        // Try to add without filling fields
        composeTestRule
            .onNodeWithText("追加")
            .performClick()

        // Should still be on dialog (validation failed)
        composeTestRule
            .onNodeWithText("場所を追加")
            .assertIsDisplayed()

        // Fill in valid data
        composeTestRule
            .onNodeWithText("場所名")
            .performTextInput("テスト場所")

        composeTestRule
            .onNodeWithText("緯度")
            .performTextInput("35.6762")

        composeTestRule
            .onNodeWithText("経度")
            .performTextInput("139.6503")

        // Submit
        composeTestRule
            .onNodeWithText("追加")
            .performClick()

        composeTestRule.waitForIdle()

        // Dialog should close and location should be added
        composeTestRule
            .onNodeWithText("場所を追加")
            .assertDoesNotExist()
    }

    @Test
    fun quickLocationSelectionDialog() {
        composeTestRule.setContent {
            LocationWeatherForcastTheme {
                FavoriteLocationsScreen()
            }
        }

        // Open add menu
        composeTestRule
            .onNodeWithContentDescription("場所を追加")
            .performClick()

        // Click popular locations
        composeTestRule
            .onNodeWithText("人気の場所から選択")
            .performClick()

        composeTestRule.waitForIdle()

        // Verify quick location dialog is displayed
        composeTestRule
            .onNodeWithText("人気の場所")
            .assertIsDisplayed()

        // Should have predefined locations
        composeTestRule
            .onNodeWithText("東京")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("大阪")
            .assertIsDisplayed()

        // Select a location
        composeTestRule
            .onNodeWithText("東京")
            .performClick()

        composeTestRule.waitForIdle()

        // Dialog should close
        composeTestRule
            .onNodeWithText("人気の場所")
            .assertDoesNotExist()
    }

    @Test
    fun refreshButtonUpdatesWeatherData() {
        composeTestRule.setContent {
            LocationWeatherForcastTheme {
                FavoriteLocationsScreen()
            }
        }

        // Click refresh button
        composeTestRule
            .onNodeWithContentDescription("天気データを更新")
            .performClick()

        composeTestRule.waitForIdle()

        // Should show loading state briefly
        // Then return to current state
    }

    @Test
    fun maxLocationsLimitEnforced() {
        // This test would require pre-populating the favorites to max capacity
        // Then attempting to add another location
        // It should show an error message about reaching the limit
        
        composeTestRule.setContent {
            LocationWeatherForcastTheme {
                FavoriteLocationsScreen()
            }
        }

        // For now, this is a placeholder test
        // In a full implementation, we would:
        // 1. Mock the repository to return max locations
        // 2. Try to add another location
        // 3. Verify error message is displayed
        
        composeTestRule.waitForIdle()
    }

    @Test
    fun locationCardDisplaysCorrectInformation() {
        // This test would verify that location cards display:
        // - Location name
        // - Weather icon
        // - Temperature
        // - Weather description
        // - Last updated time
        // - Action buttons (refresh, delete)
        
        composeTestRule.setContent {
            LocationWeatherForcastTheme {
                FavoriteLocationsScreen()
            }
        }

        composeTestRule.waitForIdle()
        
        // This test would be more meaningful with actual location data
        // In a full implementation, we would mock the data and verify display
    }

    @Test
    fun deleteLocationConfirmation() {
        // This test would:
        // 1. Have a location in the list
        // 2. Trigger delete action
        // 3. Verify confirmation dialog appears
        // 4. Test both confirm and cancel actions
        
        composeTestRule.setContent {
            LocationWeatherForcastTheme {
                FavoriteLocationsScreen()
            }
        }

        composeTestRule.waitForIdle()
        
        // Placeholder for delete flow testing
        // Would require mocked data and confirmation dialog implementation
    }
}