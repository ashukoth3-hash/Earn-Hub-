package com.example.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.Casino
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.OndemandVideo
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GoldAccent
import com.example.ui.viewmodel.AppTab

@Composable
fun RewardBottomNavBar(
    selectedTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier.testTag("reward_bottom_nav"),
        containerColor = MaterialTheme.colorScheme.surface,
        windowInsets = WindowInsets.navigationBars,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = selectedTab == AppTab.HOME,
            onClick = { onTabSelected(AppTab.HOME) },
            icon = {
                Icon(
                    imageVector = if (selectedTab == AppTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                    contentDescription = "Home",
                    modifier = Modifier.size(22.dp)
                )
            },
            label = { Text("Home", fontSize = 11.sp, fontWeight = if (selectedTab == AppTab.HOME) FontWeight.Bold else FontWeight.Normal) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = GoldAccent,
                selectedTextColor = GoldAccent,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.testTag("nav_tab_home")
        )

        NavigationBarItem(
            selected = selectedTab == AppTab.GAMES,
            onClick = { onTabSelected(AppTab.GAMES) },
            icon = {
                Icon(
                    imageVector = if (selectedTab == AppTab.GAMES) Icons.Filled.SportsEsports else Icons.Outlined.SportsEsports,
                    contentDescription = "Games",
                    modifier = Modifier.size(22.dp)
                )
            },
            label = { Text("Games", fontSize = 11.sp, fontWeight = if (selectedTab == AppTab.GAMES) FontWeight.Bold else FontWeight.Normal) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = GoldAccent,
                selectedTextColor = GoldAccent,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.testTag("nav_tab_games")
        )

        NavigationBarItem(
            selected = selectedTab == AppTab.SPIN,
            onClick = { onTabSelected(AppTab.SPIN) },
            icon = {
                Icon(
                    imageVector = if (selectedTab == AppTab.SPIN) Icons.Filled.Casino else Icons.Outlined.Casino,
                    contentDescription = "Spin",
                    modifier = Modifier.size(22.dp)
                )
            },
            label = { Text("Spin", fontSize = 11.sp, fontWeight = if (selectedTab == AppTab.SPIN) FontWeight.Bold else FontWeight.Normal) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = GoldAccent,
                selectedTextColor = GoldAccent,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.testTag("nav_tab_spin")
        )

        NavigationBarItem(
            selected = selectedTab == AppTab.WATCH,
            onClick = { onTabSelected(AppTab.WATCH) },
            icon = {
                Icon(
                    imageVector = if (selectedTab == AppTab.WATCH) Icons.Filled.OndemandVideo else Icons.Outlined.OndemandVideo,
                    contentDescription = "Watch",
                    modifier = Modifier.size(22.dp)
                )
            },
            label = { Text("Watch", fontSize = 11.sp, fontWeight = if (selectedTab == AppTab.WATCH) FontWeight.Bold else FontWeight.Normal) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = GoldAccent,
                selectedTextColor = GoldAccent,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.testTag("nav_tab_watch")
        )

        NavigationBarItem(
            selected = selectedTab == AppTab.TASKS,
            onClick = { onTabSelected(AppTab.TASKS) },
            icon = {
                Icon(
                    imageVector = if (selectedTab == AppTab.TASKS) Icons.Filled.Assignment else Icons.Outlined.Assignment,
                    contentDescription = "Tasks",
                    modifier = Modifier.size(22.dp)
                )
            },
            label = { Text("Tasks", fontSize = 11.sp, fontWeight = if (selectedTab == AppTab.TASKS) FontWeight.Bold else FontWeight.Normal) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = GoldAccent,
                selectedTextColor = GoldAccent,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.testTag("nav_tab_tasks")
        )

        NavigationBarItem(
            selected = selectedTab == AppTab.REFERRAL,
            onClick = { onTabSelected(AppTab.REFERRAL) },
            icon = {
                Icon(
                    imageVector = if (selectedTab == AppTab.REFERRAL) Icons.Filled.People else Icons.Outlined.People,
                    contentDescription = "Refer",
                    modifier = Modifier.size(22.dp)
                )
            },
            label = { Text("Refer", fontSize = 11.sp, fontWeight = if (selectedTab == AppTab.REFERRAL) FontWeight.Bold else FontWeight.Normal) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = GoldAccent,
                selectedTextColor = GoldAccent,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.testTag("nav_tab_referral")
        )

        NavigationBarItem(
            selected = selectedTab == AppTab.WALLET,
            onClick = { onTabSelected(AppTab.WALLET) },
            icon = {
                Icon(
                    imageVector = if (selectedTab == AppTab.WALLET) Icons.Filled.AccountBalanceWallet else Icons.Outlined.AccountBalanceWallet,
                    contentDescription = "Wallet",
                    modifier = Modifier.size(22.dp)
                )
            },
            label = { Text("Wallet", fontSize = 11.sp, fontWeight = if (selectedTab == AppTab.WALLET) FontWeight.Bold else FontWeight.Normal) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = GoldAccent,
                selectedTextColor = GoldAccent,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.testTag("nav_tab_wallet")
        )
    }
}
