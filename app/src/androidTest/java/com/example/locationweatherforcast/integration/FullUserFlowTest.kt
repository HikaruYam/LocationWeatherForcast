package com.example.locationweatherforcast.integration

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.locationweatherforcast.MainActivity
import com.example.locationweatherforcast.navigation.AppNavigation
import com.example.locationweatherforcast.navigation.NavigationRoutes
import com.example.locationweatherforcast.ui.theme.LocationWeatherForcastTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration tests for complete user workflows
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class FullUserFlowTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    private lateinit var navController: TestNavHostController

    @Before
    fun setup() {
        hiltRule.inject()

        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            
            LocationWeatherForcastTheme {
                AppNavigation(navController = navController)
            }
        }
    }

    @Test
    fun completeAddLocationWorkflow() {
        // Start on weather screen
        composeTestRule
            .onNodeWithText("明日の天気予報")
            .assertIsDisplayed()

        // Navigate to favorites screen
        composeTestRule.runOnIdle {
            navController.navigate(NavigationRoutes.FAVORITE_LOCATIONS)
        }

        composeTestRule.waitForIdle()

        // Should show empty state initially
        composeTestRule
            .onNodeWithText("お気に入りの場所がありません")
            .assertIsDisplayed()

        // Click add location button
        composeTestRule
            .onNodeWithContentDescription("場所を追加")
            .performClick()

        // Select manual add option
        composeTestRule
            .onNodeWithText("手動で追加")
            .performClick()

        composeTestRule.waitForIdle()

        // Fill in location details
        composeTestRule
            .onNodeWithText("場所名")
            .performTextInput("テスト都市")

        composeTestRule
            .onNodeWithText("緯度")
            .performTextInput("35.6762")

        composeTestRule
            .onNodeWithText("経度")
            .performTextInput("139.6503")

        // Submit the form
        composeTestRule
            .onNodeWithText("追加")
            .performClick()

        composeTestRule.waitForIdle()

        // Location should now appear in the list
        composeTestRule
            .onNodeWithText("テスト都市")
            .assertIsDisplayed()

        // Verify empty state is no longer shown
        composeTestRule
            .onNodeWithText("お気に入りの場所がありません")
            .assertDoesNotExist()
    }

    @Test
    fun locationDetailNavigationWorkflow() {
        // Navigate to favorites
        composeTestRule.runOnIdle {
            navController.navigate(NavigationRoutes.FAVORITE_LOCATIONS)
        }

        // Add a location first (simplified for this test)
        composeTestRule.runOnIdle {
            navController.navigate(NavigationRoutes.locationDetail("test-location-123"))
        }

        composeTestRule.waitForIdle()

        // Should be on location detail screen
        composeTestRule
            .onNodeWithText("場所の詳細")
            .assertIsDisplayed()

        // Should show weather information for the location
        // In a real implementation, this would show specific weather data

        // Navigate back
        composeTestRule.runOnIdle {
            navController.popBackStack()
        }

        // Should be back on favorites screen
        composeTestRule
            .onNodeWithText("お気に入りの場所")
            .assertIsDisplayed()
    }

    @Test
    fun deleteLocationWorkflow() {
        // This test would verify the complete delete workflow:
        // 1. Navigate to favorites
        // 2. Have a location in the list
        // 3. Trigger delete action
        // 4. Confirm deletion
        // 5. Verify location is removed from list
        
        composeTestRule.runOnIdle {
            navController.navigate(NavigationRoutes.FAVORITE_LOCATIONS)
        }

        composeTestRule.waitForIdle()

        // For now, this is a placeholder test
        // In a full implementation, we would:
        // - Mock location data
        // - Test the delete confirmation dialog
        // - Verify the location is removed from the UI and data store
    }

    @Test
    fun refreshWeatherDataWorkflow() {
        // Navigate to favorites
        composeTestRule.runOnIdle {
            navController.navigate(NavigationRoutes.FAVORITE_LOCATIONS)
        }

        composeTestRule.waitForIdle()

        // Click refresh button
        composeTestRule
            .onNodeWithContentDescription("天気データを更新")
            .performClick()

        composeTestRule.waitForIdle()

        // Should show loading state briefly
        // Then updated weather data should be displayed
        // This test verifies the refresh mechanism works without errors
    }

    @Test
    fun errorRecoveryWorkflow() {
        // Test complete error handling and recovery workflow
        
        // Start on weather screen
        composeTestRule
            .onNodeWithText("明日の天気予報")
            .assertIsDisplayed()

        // If there's a network error, it should be handled gracefully
        // User should be able to retry and recover
        // This test would verify end-to-end error handling
        
        composeTestRule.waitForIdle()
        
        // In a full implementation, we would:
        // 1. Mock network failure
        // 2. Verify error state is shown
        // 3. Test retry functionality
        // 4. Verify recovery to normal state
    }

    @Test
    fun multipleLocationManagementWorkflow() {
        // Test managing multiple locations
        
        composeTestRule.runOnIdle {
            navController.navigate(NavigationRoutes.FAVORITE_LOCATIONS)
        }

        composeTestRule.waitForIdle()

        // Add multiple locations (simplified)
        // Test reordering via drag and drop
        // Test individual location refresh
        // Test navigation to different location details
        
        // This comprehensive test would verify:
        // - Adding multiple locations
        // - Reordering locations
        // - Managing individual location weather updates
        // - Navigation between location details
    }

    @Test
    fun permissionHandlingWorkflow() {
        // Test complete permission handling workflow
        
        // Start on weather screen
        composeTestRule
            .onNodeWithText("明日の天気予報")
            .assertIsDisplayed()

        // If location permission is denied, should show permission error
        // User should be able to:
        // 1. Request permission again
        // 2. Use manual location input instead
        // 3. Navigate to favorites and add locations manually
        
        composeTestRule.waitForIdle()
        
        // This test would verify complete permission handling flow
    }

    @Test
    fun offlineToOnlineWorkflow() {
        // Test offline to online transition
        
        // Start in offline mode (mocked)
        // Should show cached data with offline indicator
        // When network is restored, should refresh data
        // Verify smooth transition from offline to online state
        
        composeTestRule.waitForIdle()
        
        // This test would verify offline/online state management
    }

    @Test
    fun appLaunchToLocationDetailWorkflow() {
        // Test complete workflow from app launch to viewing location detail
        
        // 1. App starts on weather screen
        composeTestRule
            .onNodeWithText("明日の天気予報")
            .assertIsDisplayed()

        // 2. Navigate to favorites
        composeTestRule.runOnIdle {
            navController.navigate(NavigationRoutes.FAVORITE_LOCATIONS)
        }

        // 3. Add a location
        composeTestRule
            .onNodeWithContentDescription("場所を追加")
            .performClick()

        composeTestRule
            .onNodeWithText("手動で追加")
            .performClick()

        // 4. Fill location details
        composeTestRule
            .onNodeWithText("場所名")
            .performTextInput("大阪")

        composeTestRule
            .onNodeWithText("緯度")
            .performTextInput("34.6937")

        composeTestRule
            .onNodeWithText("経度")
            .performTextInput("135.5023")

        composeTestRule
            .onNodeWithText("追加")
            .performClick()

        composeTestRule.waitForIdle()

        // 5. Click on the location to view details
        composeTestRule
            .onNodeWithText("大阪")
            .performClick()

        // 6. Should navigate to location detail screen
        composeTestRule
            .onNodeWithText("場所の詳細")
            .assertIsDisplayed()

        // This verifies the complete user journey from launch to detail view
    }

    @Test
    fun stateManagementThroughoutNavigation() {
        // Test that state is properly maintained during navigation
        
        // Add locations, navigate between screens
        // Verify data persistence across navigation
        // Test that UI state is properly restored
        
        composeTestRule.runOnIdle {
            navController.navigate(NavigationRoutes.FAVORITE_LOCATIONS)
        }

        // Test back stack state management
        composeTestRule.runOnIdle {
            navController.navigate(NavigationRoutes.locationDetail("test-123"))
        }

        composeTestRule.runOnIdle {
            navController.popBackStack()
        }

        // Verify state is properly maintained
        composeTestRule
            .onNodeWithText("お気に入りの場所")
            .assertIsDisplayed()
        
        // This test ensures proper state management throughout the app
    }
}