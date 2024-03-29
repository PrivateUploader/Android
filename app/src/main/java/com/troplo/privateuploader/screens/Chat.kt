package com.troplo.privateuploader.screens

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.troplo.privateuploader.MainActivity
import com.troplo.privateuploader.api.ChatStore
import com.troplo.privateuploader.api.RequestBodyWithProgress
import com.troplo.privateuploader.api.SessionManager
import com.troplo.privateuploader.api.SocketHandler
import com.troplo.privateuploader.api.TpuApi
import com.troplo.privateuploader.api.TpuFunctions
import com.troplo.privateuploader.api.stores.UploadStore
import com.troplo.privateuploader.api.stores.UserStore
import com.troplo.privateuploader.components.chat.Attachment
import com.troplo.privateuploader.components.chat.Message
import com.troplo.privateuploader.components.chat.MessageActions
import com.troplo.privateuploader.components.chat.ReplyMessage
import com.troplo.privateuploader.components.chat.attachment.UriPreview
import com.troplo.privateuploader.components.core.InfiniteListHandler
import com.troplo.privateuploader.components.core.NavRoute
import com.troplo.privateuploader.components.core.OverlappingPanelsState
import com.troplo.privateuploader.data.model.DeleteEvent
import com.troplo.privateuploader.data.model.EditEvent
import com.troplo.privateuploader.data.model.EditRequest
import com.troplo.privateuploader.data.model.Embed
import com.troplo.privateuploader.data.model.EmbedFail
import com.troplo.privateuploader.data.model.EmbedResolutionEvent
import com.troplo.privateuploader.data.model.Message
import com.troplo.privateuploader.data.model.MessageEvent
import com.troplo.privateuploader.data.model.MessageRequest
import com.troplo.privateuploader.data.model.ReadReceiptEvent
import com.troplo.privateuploader.data.model.UploadTarget
import io.socket.emitter.Emitter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import org.json.JSONObject
import retrofit2.HttpException
import java.util.Date
import com.troplo.privateuploader.data.model.Message as MessageModel


@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun ChatScreen(
    chatId: Int?,
    panelsState: OverlappingPanelsState,
) {
    var associationId = chatId
    val context = LocalContext.current
    val chatViewModel = remember { ChatViewModel() }
    val message = remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val messageCtx = remember { mutableStateOf(false) }
    val messageCtxMessage: MutableState<Message?> = remember { mutableStateOf(null) }
    val editId: MutableState<Int> = remember { mutableIntStateOf(0) }
    val jumpToMessage = ChatStore.jumpToMessage.collectAsState()
    val attachment = remember { mutableStateOf(false) }
    val replyId: MutableState<Int> = remember { mutableIntStateOf(0) }
    val uploads = remember { UploadStore.uploads }

    if(chatViewModel.loading.value && chatViewModel.messages.value == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
            )
        }
    }

    DisposableEffect(chatViewModel) {
        onDispose { chatViewModel.onStop() }
    }
    if (attachment.value) {
        Attachment(openBottomSheet = attachment)
    }

    if (associationId == 0 || associationId == null) {
        val lastChatId = SessionManager(context).getLastChatId()
        associationId = lastChatId
    }

    if (associationId == 0) return
    ChatStore.setAssociationId(associationId, context)
    chatViewModel.associationId = associationId

    fun messagesReset() {
        chatViewModel.messages.value = null
        chatViewModel.getMessages(associationId)
        chatViewModel.jumpToBottom.value = false
        chatViewModel.newMessage.value = true
    }

    Scaffold(
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {

                if (uploads.size > 0) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .requiredHeight(120.dp)
                            .padding(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        LazyRow(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            uploads.forEach {
                                item(
                                    key = it.uri
                                ) {
                                    UriPreview(it, onClick = {
                                        UploadStore.uploads.remove(it)
                                    })
                                }
                            }
                        }
                    }
                }

                if(replyId.value != 0) {
                    val msg = chatViewModel.messages.value?.find { it.id == replyId.value }
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 24.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            ReplyMessage(msg, null)
                        }
                        IconButton(
                            onClick = {
                                replyId.value = 0
                            },
                            modifier = Modifier.align(Alignment.CenterEnd)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cancel reply",
                            )
                        }
                    }
                }
                if (editId.value != 0) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Editing message",
                            modifier = Modifier.padding(start = 16.dp)
                        )

                        IconButton(
                            onClick = {
                                editId.value = 0
                                message.value = ""
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Stop editing",
                                modifier = Modifier.padding(end = 16.dp)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Box {
                        val focusRequester = FocusRequester()
                        val keyboardController = LocalSoftwareKeyboardController.current

                        OutlinedTextField(
                            value = message.value,
                            onValueChange = { message.value = it; chatViewModel.typing(associationId) },
                            label = { Text("Message") },
                            placeholder = { Text("Keep it civil") },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                capitalization = KeyboardCapitalization.Sentences
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .focusRequester(focusRequester),
                            supportingText = {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    val typersState = ChatStore.typers.collectAsState()
                                    typersState.value.let { typers ->
                                        if (typers.isNotEmpty()) {
                                            val names =
                                                typers.filter { it.chatId == ChatStore.getChat()?.id }
                                                    .map { it.user.username }
                                            Text(
                                                text = "${names.joinToString(", ")} is typing..."
                                            )
                                        } else {
                                            Text(
                                                text = ""
                                            )
                                        }
                                    }
                                    Text("${message.value.length}/4000")
                                }
                            },
                            trailingIcon = {
                                val enabled = remember(uploads, message.value, uploads) {
                                    uploads.none { it.url == null } && (message.value.isNotEmpty() || uploads.isNotEmpty())
                                }

                                IconButton(
                                    onClick = {
                                        chatViewModel.sendMessage(
                                            associationId,
                                            message.value,
                                            context,
                                            editId.value,
                                            if(replyId.value != 0) replyId.value else null
                                        )
                                        message.value = ""
                                        editId.value = 0
                                        replyId.value = 0
                                    },
                                    enabled = enabled
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Send,
                                        contentDescription = "Send message"
                                    )
                                }
                            },
                            leadingIcon = {
                                IconButton(
                                    onClick = {
                                        attachment.value = !attachment.value
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.AddCircle,
                                        contentDescription = "Add attachment"
                                    )
                                }
                            }
                        )

                        // hide keyboard when sidebar is open
                        // TODO: fix
                        if (panelsState.isStartPanelPartialOpen || panelsState.isEndPanelPartialOpen) {
                            keyboardController?.hide()
                        }

                        // Autofocus the input on mount
                        /* if (!initialLoad.value) {
                        DisposableEffect(Unit) {
                            focusRequester.requestFocus()
                            onDispose { }
                        }
                        initialLoad.value = true
                    }*/
                    }
                }
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = it.calculateBottomPadding())
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                reverseLayout = true,
                state = listState
            ) {
                chatViewModel.messages.value?.forEach { msg ->
                    item(
                        key = msg.id
                    ) {
                        Message(
                            modifier = Modifier.pointerInput(Unit) {
                                detectTapGestures(
                                    onLongPress = {
                                        messageCtxMessage.value = msg
                                        messageCtx.value = true
                                    }
                                )
                            },
                            message = msg,
                            compact = compact(msg, chatViewModel.messages.value!!),
                            onReply = { id ->
                                ChatStore.jumpToMessage.value = id
                            },
                            onLongClick = {
                                messageCtxMessage.value = msg
                                messageCtx.value = true
                            },
                        )
                    }
                }
            }
        }

        if (chatViewModel.jumpToBottom.value) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = it.calculateBottomPadding())
            ) {
                FloatingActionButton(
                    onClick = {
                        messagesReset()
                    },
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.BottomEnd)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowDownward,
                        contentDescription = "Jump to bottom"
                    )
                }
            }
        }
    }

    InfiniteListHandler(listState = listState) {
        Log.d("TPU.Untagged", "Load more" + chatViewModel.messages.value?.size.toString())
        if (chatViewModel.messages.value?.size == 0 || chatViewModel.messages.value?.size == null || chatViewModel.loading.value) return@InfiniteListHandler
        chatViewModel.getMessages(associationId)
    }

    if (messageCtx.value) {
        MessageActions(messageCtxMessage, messageCtx, editId, message, replyId)
    }


    // Watchers
    LaunchedEffect(chatViewModel.newMessage.value) {
        chatViewModel.newMessage.value = false
        if (chatViewModel.jumpToBottom.value) {
            messagesReset()
        }
        listState.animateScrollToItem(0)
    }

    LaunchedEffect(Unit) {
        chatViewModel.getMessages(associationId)
        chatViewModel.onMount()
    }

    // Monitor jumpToMessage to jump to specific message contexts
    LaunchedEffect(jumpToMessage.value) {
        if (jumpToMessage.value != 0) {
            val index =
                chatViewModel.messages.value?.indexOfFirst { it.id == jumpToMessage.value }
            if (index != -1 && index != null) {
                listState.animateScrollToItem(index)
                ChatStore.jumpToMessage.value = 0
            } else {
                chatViewModel.messages.value = null
                chatViewModel.jumpToBottom.value = true
                chatViewModel.getMessages(associationId, jumpToMessage.value + 20, listState)
            }
        }
    }

    LaunchedEffect(UploadStore.uploads.size) {
        Log.d("TPU.Upload", "Uploads changed, ${UploadStore.uploads.toList().toString()}}")
        UploadStore.uploads.forEach { file ->
            Log.d("TPU.Upload", "Checking file: $file")
            if (file.started) return@forEach
            Log.d("TPU.Upload", "Uploading file: $file")
            chatViewModel.uploadAttachment(file, context)
        }
    }
}

class ChatViewModel : ViewModel() {
    // listen for messages
    private val socket = SocketHandler.getSocket()
    var associationId = 0
    val messages = mutableStateOf<List<Message>?>(null)
    val loading = mutableStateOf(false)
    val newMessage = mutableStateOf(false)
    val jumpToBottom = mutableStateOf(false)
    private val lastTypingEvent: MutableState<Long> = mutableLongStateOf(0L)
    private val gson = Gson()
    private var embedFails: Array<EmbedFail> = arrayOf()

    fun onStop() {
        socket?.off("embedResolution", embedResolutionListener)
        socket?.off("messageDelete", messageDeleteListener)
        socket?.off("edit", editListener)
        socket?.off("readReceipt", readReceiptListener)
        socket?.off("message", msgListener)
    }

    private val msgListener: Emitter.Listener = Emitter.Listener {
        onMessage(it)
    }

    private val readReceiptListener: Emitter.Listener = Emitter.Listener {
        onReadReceipt(it)
    }

    private val editListener: Emitter.Listener = Emitter.Listener {
        onEdit(it)
    }

    private val messageDeleteListener: Emitter.Listener = Emitter.Listener {
        onMessageDelete(it)
    }

    private val embedResolutionListener: Emitter.Listener = Emitter.Listener {
        onEmbedResolution(it)
    }

    private fun onMessage(item: Array<Any>) {
        Log.d("Chat", "Message received" + item[0])

        val jsonArray = item[0] as JSONObject
        val payload = jsonArray.toString()
        val messageEvent = gson.fromJson(payload, MessageEvent::class.java)

        // Change in v4 uses uppercase type
        val message = messageEvent.message.copy(
            type = messageEvent.message.type?.lowercase()
        )

        if (associationId != messageEvent.associationId.toInt()) {
            Log.d(
                "Chat",
                "Message not for this association, ${messageEvent.associationId} != $associationId ${associationId::class.java.typeName} ${messageEvent.associationId::class.java.typeName}"
            )
            return
        }
        // see if the message is already in the list
        val existingMessage =
            messages.value?.find { e -> e.id == message.id || (e.content == message.content && e.pending == true && e.userId == message.userId) }
        Log.d(
            "Chat",
            "Message for this association, ${messageEvent.associationId} == $associationId, $existingMessage"
        )
        if (existingMessage == null) {
            // add to start of list
            messages.value = listOf(message, *messages.value.orEmpty().toTypedArray())
        } else {
            val index = messages.value.orEmpty().indexOf(existingMessage)
            val newMessage = message.copy(
                pending = false,
                error = false,
                content = message.content,
                createdAt = message.createdAt
            )
            messages.value = messages.value.orEmpty().toMutableList().also { msg ->
                msg[index] = newMessage
            }
        }
        newMessage.value = true
    }

    fun onEmbedResolution(data: Array<Any>) {
        val chatId = ChatStore.chats.find { it.association?.id == associationId }?.id
        val jsonArray = data[0] as JSONObject
        val payload = jsonArray.toString()
        val embed = gson.fromJson(payload, EmbedResolutionEvent::class.java)
        if (chatId != embed.chatId) {
            Log.d("TPU.Untagged", "Embed not for this chat, ${embed.chatId} != $chatId")
            return
        }


        val index = messages.value.orEmpty().indexOfFirst { e -> e.id == embed.id }
        if (index != -1) {
            // update the message embeds with copy
            val message = messages.value.orEmpty()[index]
            val newMessage = message.copy(embeds = embed.embeds)
            messages.value = messages.value.orEmpty().toMutableList().also {
                it[index] = newMessage
            }
        } else {
            // find in embedFails
            val index = embedFails.indexOfFirst { e -> e.data.id == embed.id }
            Log.d("TPU.Untagged", "Embed not found in messages, ${embed.id}, $index")
            if (index == -1) {
                // add to embedFails
                embedFails += EmbedFail(
                    retries = 1,
                    data = embed
                )
            } else {
                val count = embedFails[index].retries
                if (count > 5) {
                    embedFails = embedFails.filterIndexed { i, _ -> i != index }.toTypedArray()
                    return
                }
                // update the count
                embedFails[index] = EmbedFail(
                    retries = embedFails[index].retries + 1,
                    data = embedFails[index].data
                )
            }

            Handler(Looper.getMainLooper()).postDelayed({
                onEmbedResolution(data)
            }, 50)
        }
    }

    fun onEdit(item: Array<Any>) {
        Log.d("TPU.Untagged", "Message edit received " + item[0])
        val jsonArray = item[0] as JSONObject
        val payload = jsonArray.toString()
        val editEvent = gson.fromJson(payload, EditEvent::class.java)

        val chatId = ChatStore.getChat()?.id
        if (chatId != editEvent.chatId) {
            Log.d(
                "TPU.Untagged",
                "Message edit not for this chat, ${editEvent.chatId} != $chatId"
            )
            return
        }

        val index = messages.value.orEmpty().indexOfFirst { e -> e.id == editEvent.id }
        if (index != -1) {
            // update the message
            messages.value = messages.value.orEmpty().toMutableList().also {
                it[index] = it[index].copy(
                    content = editEvent.content,
                    edited = editEvent.edited,
                    editedAt = editEvent.editedAt,
                    pinned = editEvent.pinned
                )
            }
        }
    }

    fun onMessageDelete(item: Array<Any>) {
        Log.d("TPU.Untagged", "Message delete received " + item[0])
        val jsonArray = item[0] as JSONObject
        val payload = jsonArray.toString()
        val message = gson.fromJson(payload, DeleteEvent::class.java)

        val chatId = ChatStore.getChat()?.id
        if (chatId != message.chatId) {
            Log.d(
                "TPU.Untagged",
                "Message delete not for this chat, ${message.chatId} != $chatId"
            )
            return
        }

        val index = messages.value.orEmpty().indexOfFirst { e -> e.id == message.id }
        if (index != -1) {
            // remove the message
            messages.value = messages.value.orEmpty().toMutableList().also {
                it.removeAt(index)
            }
        }
    }

    fun onReadReceipt(item: Array<Any>) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("ReadReceipts", "Read receipt received " + item[0])
            val jsonArray = item[0] as JSONObject
            val payload = jsonArray.toString()
            val readReceiptEvent = gson.fromJson(payload, ReadReceiptEvent::class.java)

            val chatId = ChatStore.getChat()?.id
            if (chatId != readReceiptEvent.chatId) {
                Log.d(
                    "ReadReceipts",
                    "Read receipt not for this chat, ${readReceiptEvent.chatId} != $chatId"
                )
                return@launch
            }

            // find the old read receipt belonging to the user and delete it in messages[].readreceipts array
            Log.d("ReadReceipts", "$readReceiptEvent")
            val index = messages.value.orEmpty()
                .indexOfFirst { e -> e.readReceipts.find { it.user.id == readReceiptEvent.user.id } != null }
            if (index != -1) {
                // update the message
                messages.value = messages.value.orEmpty().toMutableList().also {
                    val message = it[index]
                    val readReceipts =
                        message.readReceipts.filter { it.user.id != readReceiptEvent.user.id }
                    it[index] = message.copy(readReceipts = readReceipts)
                }
            }

            // add the new read receipt to the message
            val indexNew = messages.value.orEmpty().indexOfFirst { e -> e.id == readReceiptEvent.id }
            if (indexNew != -1) {
                // update the message
                messages.value = messages.value.orEmpty().toMutableList().also {
                    val message = it[indexNew]
                    val readReceipts = message.readReceipts + readReceiptEvent
                    it[indexNew] = message.copy(readReceipts = readReceipts)
                }
            }
        }
    }

    fun onMount() {
        Log.d("TPU.Untagged", "Route Param: ${NavRoute.Chat}}")
        socket?.on("message", msgListener)
        socket?.on("embedResolution", embedResolutionListener)
        socket?.on("messageDelete", messageDeleteListener)
        socket?.on("edit", editListener)
        socket?.on("readReceipt", readReceiptListener)
    }

    fun getMessages(associationId: Int, offset: Int? = null, listState: LazyListState? = null) {
        if (loading.value) return
        viewModelScope.launch(Dispatchers.IO) {
            loading.value = true
            val response = TpuApi.retrofitService.getMessages(
                associationId,
                offset = offset ?: messages.value?.last()?.id?.minus(1)
            ).execute()
            withContext(Dispatchers.Main) {
                messages.value = messages.value.orEmpty() + response.body().orEmpty()
                if (messages.value?.size!! >= 200) {
                    messages.value = messages.value?.takeLast(50)
                    jumpToBottom.value = true
                }
                if (offset !== null) {
                    val index =
                        messages.value?.indexOfFirst { it.id == ChatStore.jumpToMessage.value } ?: 0
                    if (index != -1) {
                        listState?.scrollToItem(index)
                        ChatStore.jumpToMessage.value = 0
                    }
                }
            }
            loading.value = false
        }
    }

    fun sendMessage(
        associationId: Int,
        message: String,
        context: Context,
        editId: Int = 0,
        replyId: Int? = null
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = UserStore.getUser()
            if (user != null) {
                if (editId == 0) {
                    // add it to the list as pending
                    val id = Date().time.toInt()
                    val pendingMessage = MessageModel(
                        id,
                        chatId = associationId,
                        userId = user.id,
                        content = message.trim(),
                        createdAt = TpuFunctions.currentISODate(),
                        updatedAt = TpuFunctions.currentISODate(),
                        user = user,
                        pending = true,
                        edited = false,
                        editedAt = null,
                        embeds = emptyList(),
                        error = false,
                        legacyUser = null,
                        legacyUserId = null,
                        pinned = false,
                        readReceipts = emptyList(),
                        reply = null,
                        replyId = replyId,
                        tpuUser = user,
                        type = "message"
                    )
                    messages.value =
                        listOf(pendingMessage, *messages.value.orEmpty().toTypedArray())
                    try {
                        // ensure url is populated for attachments
                        val uploadedAttachments =
                            UploadStore.uploads.filter { it.url != null }.map { it.url!! }
                        UploadStore.uploads.clear()
                        val response = TpuApi.retrofitService.sendMessage(
                            associationId, MessageRequest(
                                message,
                                attachments = uploadedAttachments,
                                replyId = replyId
                            )
                        ).execute()
                        launch(Dispatchers.IO) {
                            if (!response.isSuccessful) {
                                val oldMessageIndex = messages.value?.indexOfFirst { it.id == id }
                                if (oldMessageIndex != -1 && oldMessageIndex != null) {
                                    messages.value = messages.value?.toMutableList()?.apply {
                                        set(
                                            oldMessageIndex,
                                            pendingMessage.copy(error = true, pending = false)
                                        )
                                    }
                                }
                            }
                        }
                    } catch (e: HttpException) {
                        val oldMessageIndex = messages.value?.indexOfFirst { it.id == id }
                        if (oldMessageIndex != -1 && oldMessageIndex != null) {
                            messages.value = messages.value?.toMutableList()?.apply {
                                set(
                                    oldMessageIndex,
                                    pendingMessage.copy(error = true, pending = false)
                                )
                            }
                        }
                    }
                } else {
                    // User has edited a message
                    try {
                        val response = TpuApi.retrofitService.editMessage(
                            associationId, EditRequest(
                                message,
                                editId
                            )
                        ).execute()
                        launch(Dispatchers.IO) {
                            if (!response.isSuccessful) {
                                val error: JSONObject =
                                    JSONObject(response.errorBody()?.string() ?: "{}")
                                Toast.makeText(
                                    context,
                                    error.getJSONArray("errors").getJSONObject(0)
                                        .getString("message"),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } catch (e: HttpException) {
                        //
                    }
                }
            }
        }
    }

    fun typing(associationId: Int) {
        if(UserStore.user.value?.storedStatus == "invisible") return
        if (Date().time - lastTypingEvent.value > 3000) {
            lastTypingEvent.value = Date().time
            Log.d("TPU.Chat", "Typing")
            socket?.emit("typing", associationId)
        }
    }

    fun uploadAttachment(file: UploadTarget, context: Context) {
        UploadStore.uploads.find { it.uri == file.uri }?.started = true
        Log.d("TPU.Chat", UploadStore.uploads.toList().toString())
        val list = listOf(file)
        MainActivity().upload(list, false, context)
    }
}

private fun compact(message: Message, messages: List<Message>): String {
    if(message.type != "message" && message.type != null) return "none"
    val index = messages.indexOf(message)
    if (index == messages.size - 1) return "none"
    val previousMessage = messages[index + 1]

    val chainMessagesDiff = (TpuFunctions.getDate(message.createdAt)?.time ?: 0) - (TpuFunctions.getDate(previousMessage.createdAt)?.time ?: 0)

    return if (TpuFunctions.formatDateDay(message.createdAt) != TpuFunctions.formatDateDay(previousMessage.createdAt)) {
        "separator"
    } else if (message.userId == previousMessage.userId && message.replyId == null && chainMessagesDiff < 300000) {
        "compact"
    } else {
        "none"
    }
}