package com.troplo.privateuploader.screens

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.troplo.privateuploader.api.ChatStore
import com.troplo.privateuploader.api.SessionManager
import com.troplo.privateuploader.components.chat.ChatActions
import com.troplo.privateuploader.components.chat.ChatItem
import com.troplo.privateuploader.components.chat.dialogs.NewChatDialog
import com.troplo.privateuploader.components.core.OverlappingPanelsState
import com.troplo.privateuploader.data.model.Chat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    openChat: (Int) -> Unit = {},
    panelState: OverlappingPanelsState?,
    navController: NavController,
) {
    val loading = remember { mutableStateOf(true) }
    val chatViewModel = remember { ChatHomeViewModel() }
    val chats = ChatStore.chats
    val createChat = remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    LaunchedEffect(Unit) {
        chatViewModel.getChats().also {
            loading.value = false
        }
    }

    if (createChat.value) {
        NewChatDialog(createChat, navController)
    }

    LaunchedEffect(chats) {
        if (listState.firstVisibleItemIndex == 1) listState.scrollToItem(0)
    }

    Column {
        ListItem(
            headlineContent = {
                Text("Chats")
            },
            trailingContent = {
                IconButton(onClick = { createChat.value = true }) {
                    Icon(Icons.Filled.Add, contentDescription = "Create chat")
                }
            }
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize(), state = listState) {
                chats.forEach {
                    item(
                        key = it.id
                    ) {
                        ChatItem(it, navController)
                    }
                }
            }
        }
    }
}

class ChatHomeViewModel : ViewModel() {
    fun getChats() {
        viewModelScope.launch(Dispatchers.IO) {
            ChatStore.initializeChats()
        }
    }
}