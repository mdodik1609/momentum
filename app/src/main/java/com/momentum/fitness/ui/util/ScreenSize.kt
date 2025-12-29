package com.momentum.fitness.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Screen size utilities for responsive design
 */
object ScreenSize {
    enum class ScreenType {
        Small,      // < 600dp width (phones)
        Medium,     // 600-840dp (tablets)
        Large       // > 840dp (large tablets)
    }

    @Composable
    fun getScreenType(): ScreenType {
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp
        
        return when {
            screenWidth < 600 -> ScreenType.Small
            screenWidth < 840 -> ScreenType.Medium
            else -> ScreenType.Large
        }
    }

    @Composable
    fun isTablet(): Boolean {
        return getScreenType() != ScreenType.Small
    }

    @Composable
    fun getMapHeight(): Dp {
        return when (getScreenType()) {
            ScreenType.Small -> 200.dp
            ScreenType.Medium -> 300.dp
            ScreenType.Large -> 400.dp
        }
    }

    @Composable
    fun getGraphHeight(): Dp {
        return when (getScreenType()) {
            ScreenType.Small -> 200.dp
            ScreenType.Medium -> 250.dp
            ScreenType.Large -> 300.dp
        }
    }

    @Composable
    fun getCardPadding(): Dp {
        return when (getScreenType()) {
            ScreenType.Small -> 16.dp
            ScreenType.Medium -> 20.dp
            ScreenType.Large -> 24.dp
        }
    }

    @Composable
    fun getColumnCount(): Int {
        return when (getScreenType()) {
            ScreenType.Small -> 1
            ScreenType.Medium -> 2
            ScreenType.Large -> 3
        }
    }
}







