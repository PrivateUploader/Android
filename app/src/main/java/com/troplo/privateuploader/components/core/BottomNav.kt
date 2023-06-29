package com.troplo.privateuploader.components.core

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.troplo.privateuploader.api.ChatStore
import com.troplo.privateuploader.api.stores.UserStore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomBarNav(
    navController: NavController,
    panelState: OverlappingPanelsState,
    closePanels: () -> Unit,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    if (currentRoute == null || currentRoute == NavRoute.Login.path) {
        return
    }
    val user = UserStore.user.collectAsState()

    AnimatedVisibility(
        visible = panelState.offset.value > 200 || !currentRoute.contains("chat/"),
        enter = expandVertically(animationSpec = tween(durationMillis = 200)) { fullWidth ->
            // Offsets the content by 1/3 of its width to the left, and slide towards right
            // Overwrites the default animation with tween for this slide animation.
            -fullWidth / 3
        } + fadeIn(
            // Overwrites the default animation with tween
            animationSpec = tween(durationMillis = 100)
        ),
        exit = shrinkVertically(animationSpec = spring(stiffness = Spring.StiffnessHigh)) {
            // Overwrites the ending position of the slide-out to 200 (pixels) to the right
            0
        } + fadeOut()) {
        Box {
            NavigationBar {
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Chat,
                            contentDescription = NavRoute.Home.path
                        )
                    },
                    selected = currentRoute.startsWith("chat/"),
                    onClick = {
                        if (!currentRoute.startsWith("chat/")) {
                            navController.navigate("${NavRoute.Chat.path}/${ChatStore.associationId.value}") {
                                popUpTo("chat/${ChatStore.associationId.value}") {
                                    inclusive = true
                                }
                            }
                        }
                    },
                    // TO LOCALIZE
                    label = { Text("Chat") }
                )

                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = NavRoute.Gallery.path
                        )
                    },
                    selected = currentRoute == NavRoute.Gallery.path,
                    onClick = {
                        closePanels()
                        if (currentRoute != NavRoute.Gallery.path) {
                            navController.navigate(NavRoute.Gallery.path) {
                                popUpTo(NavRoute.Gallery.path) { inclusive = true }
                            }
                        }
                    },
                    // TO LOCALIZE
                    label = { Text("Gallery") }
                )

                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.People,
                            contentDescription = NavRoute.Friends.path
                        )
                    },
                    selected = currentRoute == NavRoute.Friends.path,
                    onClick = {
                        closePanels()
                        if (currentRoute != NavRoute.Friends.path) {
                            navController.navigate(NavRoute.Friends.path) {
                                popUpTo(NavRoute.Friends.path) { inclusive = true }
                            }
                        }
                    },
                    // TO LOCALIZE
                    label = { Text("Friends") }
                )

                NavigationBarItem(
                    icon = {
                        UserAvatar(
                            avatar = user.value?.avatar,
                            username = user.value?.username ?: "Deleted User",
                            modifier = Modifier.size(28.dp),
                            showStatus = false
                        )
                    },
                    selected = currentRoute == NavRoute.Settings.path,
                    onClick = {
                        closePanels()
                        if (currentRoute != NavRoute.Settings.path) {
                            navController.navigate(NavRoute.Settings.path) {
                                popUpTo(NavRoute.Settings.path) { inclusive = true }
                            }
                        }
                    },
                    // TO LOCALIZE
                    label = { Text("Settings") }
                )
            }
        }
    }
}