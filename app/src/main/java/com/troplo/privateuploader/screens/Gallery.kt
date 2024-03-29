package com.troplo.privateuploader.screens

import android.app.Activity
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentSender
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.troplo.privateuploader.MainActivity
import com.troplo.privateuploader.api.SocketHandler
import com.troplo.privateuploader.api.TpuApi
import com.troplo.privateuploader.api.stores.CollectionStore
import com.troplo.privateuploader.api.stores.UploadStore
import com.troplo.privateuploader.components.core.ExpandedFloatingActionButton
import com.troplo.privateuploader.components.core.Paginate
import com.troplo.privateuploader.components.gallery.GalleryItem
import com.troplo.privateuploader.data.model.FriendRequest
import com.troplo.privateuploader.data.model.Gallery
import com.troplo.privateuploader.data.model.Pager
import com.troplo.privateuploader.data.model.TenorResponse
import com.troplo.privateuploader.data.model.Upload
import com.troplo.privateuploader.data.model.UploadResponse
import com.troplo.privateuploader.data.model.UploadTarget
import com.troplo.privateuploader.data.model.defaultUser
import com.troplo.privateuploader.ui.theme.PrivateUploaderTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Response
import java.util.Date

public fun getFileName(uri: Uri, ctx: Context): String {
    val contentResolver: ContentResolver = ctx.contentResolver
    var result: String? = null
    if (uri.scheme == "content") {
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor.use { c ->
            if (c != null && c.moveToFirst()) {
                val index = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                result = c.getString(index)
            }
        }
    }
    if (result == null) {
        result = uri.path
        val cut = result!!.lastIndexOf('/')
        if (cut != -1) {
            result = result!!.substring(cut + 1)
        }
    }
    return result as String
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
@Preview(showBackground = true)
fun GalleryScreen(
    // gallery or starred
    type: String = "gallery",
    inline: Boolean = false,
    onClick: (Upload) -> Unit = {},
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { data ->
       Log.d("TPU.UploadResponse", "Upload response received, data: $data")
        val files = mutableListOf<UploadTarget>()
        for (uri in data) {
            files.add(UploadTarget(uri = uri, name = getFileName(uri, context)))
        }
        MainActivity().upload(files, false, context)
    }


    val galleryViewModel = remember { GalleryViewModel() }
    val searchState = remember { mutableStateOf(galleryViewModel.search) }
    val listState = rememberLazyListState()
    val activity = LocalContext.current as Activity
    var expanded by remember { mutableStateOf(false) }
    var selectedCollectionText = remember { mutableStateOf("None") }
    var selectedCollectionId: MutableState<Int> = remember { mutableIntStateOf(0) }
    val collections = CollectionStore.collections.collectAsState()

    LaunchedEffect(selectedCollectionId.value) {
        galleryViewModel.getGalleryItems(type, selectedCollectionId.value)
    }

    LaunchedEffect(Unit) {
        galleryViewModel.getGalleryItems(type, selectedCollectionId.value)
        galleryViewModel.onMount()
    }

    DisposableEffect(galleryViewModel) {
        onDispose { galleryViewModel.onStop() }
    }

    // scroll to top when gallery changes
    LaunchedEffect(galleryViewModel.gallery.value) {
        listState.animateScrollToItem(0)
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                TextField(
                    value = searchState.value.value,
                    onValueChange = { searchState.value.value = it },
                    label = { Text("Search") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    trailingIcon = {
                        IconButton(onClick = {
                            galleryViewModel.getGalleryItems(type)
                        }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search"
                            )
                        }
                    },
                    singleLine = true
                )
                if(type != "tenor") {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        CompositionLocalProvider(
                            LocalTextInputService provides null
                        ) {
                            TextField(
                                // The `menuAnchor` modifier must be passed to the text field for correctness.
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                value = selectedCollectionText.value,
                                onValueChange = {},
                                // this could be an accessibility problem
                                enabled = false,
                                label = { Text("Filter by Collection") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                colors = ExposedDropdownMenuDefaults.textFieldColors(
                                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface,
                                    disabledIndicatorColor = MaterialTheme.colorScheme.onSurface
                                ),
                            )
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .exposedDropdownSize()
                                    .padding(8.dp),
                            ) {
                                DropdownMenuItem(
                                    text = { Text("None") },
                                    onClick = {
                                        selectedCollectionText.value = "None"
                                        selectedCollectionId.value = 0
                                        expanded = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                )
                                DropdownMenuItem(
                                    text = { Text("Uncollectivized") },
                                    onClick = {
                                        selectedCollectionText.value = "Uncollectivized"
                                        selectedCollectionId.value = -1
                                        expanded = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                )
                                DropdownMenuItem(
                                    text = { Text("Starred") },
                                    onClick = {
                                        selectedCollectionText.value = "Starred"
                                        selectedCollectionId.value = -2
                                        expanded = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                )
                                Divider()
                                collections.value.items.forEach { collection ->
                                    DropdownMenuItem(
                                        text = { Text(collection.name) },
                                        onClick = {
                                            selectedCollectionText.value = collection.name
                                            selectedCollectionId.value = collection.id
                                            expanded = false
                                        },
                                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        content = {
            if(galleryViewModel.loading.value) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                    )
                }
            } else {
                Box {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                top = it.calculateTopPadding(),
                                bottom = it.calculateBottomPadding()
                            )
                    ) {
                        galleryViewModel.gallery.value?.gallery?.forEach {
                            item(
                                key = it.id
                            ) {
                                GalleryItem(it, inline, onClick = { onClick(it) }, onDelete = {
                                    galleryViewModel.deleteItem(it)
                                }, selectedCollectionId = selectedCollectionId, selectedCollectionText = selectedCollectionText)
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            Paginate(modelValue = galleryViewModel.gallery.value?.pager?.currentPage ?: 1, totalPages = galleryViewModel.gallery.value?.pager?.totalPages ?: 1, onUpdateModelValue = {
                galleryViewModel.gallery.value?.pager?.currentPage = it
                galleryViewModel.getGalleryItems(type)
            })
        },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            if(type != "tenor") {
                ExpandedFloatingActionButton(
                    onClick = {
                        launcher.launch(arrayOf("*/*"))
                    },
                    extended = listState.isScrollingUp(),
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Upload,
                            contentDescription = "Upload"
                        )
                    },
                    text = {
                        Text("Upload")
                    }
                )
            }
        }
    )
}


class GalleryViewModel : ViewModel() {
    val gallery = mutableStateOf<Gallery?>(null)
    val search = mutableStateOf("")
    val loading = mutableStateOf(true)

    fun onMount() {
        val socket = SocketHandler.getGallerySocket()
        socket?.off("create")
        socket?.off("update")
        socket?.on("create") {
            val jsonArray = it[0] as JSONArray
            val payload = jsonArray.toString()
            val uploads = SocketHandler.gson.fromJson(payload, Array<UploadResponse>::class.java).toList()
            if(search.value.isEmpty()) {
                Log.d("GalleryViewModel", "Received upload: $uploads")
                // add all uploads to start of gallery using copy
                gallery.value = gallery.value?.copy(
                    gallery = uploads.map { upload -> upload.upload } + gallery.value?.gallery.orEmpty()
                )
            }
        }

        socket?.on("update") {
            val jsonArray = it[0] as JSONArray
            val payload = jsonArray.toString()
            val uploads = SocketHandler.gson.fromJson(payload, Array<Upload>::class.java).toList()

            if (search.value.isEmpty()) {
                Log.d("GalleryViewModel", "Received upload: $uploads")

                // Update the existing gallery with new data based on matching IDs
                gallery.value = gallery.value?.copy(
                    gallery = gallery.value?.gallery?.map { existingUpload ->
                        uploads.find { it.id == existingUpload.id }?.let { newUpload ->
                            // Create a new upload by spreading the fields of the existing upload
                            existingUpload.copy(
                                name = newUpload.name ?: existingUpload.name,
                                collections = newUpload.collections ?: existingUpload.collections,
                                starred = newUpload.starred ?: existingUpload.starred,
                                textMetadata = newUpload.textMetadata ?: existingUpload.textMetadata
                            )
                        } ?: existingUpload // If no match found, keep the existing upload
                    }.orEmpty()
                )
            }
        }
    }

    fun onStop() {
        Log.d("GalleryViewModel", "onStop")
        SocketHandler.getGallerySocket()?.off("create")
        SocketHandler.getGallerySocket()?.off("update")
    }

    fun getGalleryItems(t: String = "gallery", collectionId: Int = 0) {
        this.loading.value = true
        val type = if(t == "gallery" && collectionId != -2) "gallery" else "starred"
        viewModelScope.launch(Dispatchers.IO) {
            if(t != "tenor") {
                val response: Response<Gallery> =
                    if (type == "starred") TpuApi.retrofitService.getStarredGallery(
                        search = search.value,
                        page = gallery.value?.pager?.currentPage ?: 1
                    ).execute()
                    else if(collectionId == -1 || collectionId == 0) TpuApi.retrofitService.getGallery(
                        search = search.value,
                        page = gallery.value?.pager?.currentPage ?: 1,
                        filter = if(collectionId == -1) "nonCollectivized" else "all"
                    ).execute()
                    else TpuApi.retrofitService.getCollectionGallery(
                        collectionId = collectionId,
                        search = search.value,
                        page = gallery.value?.pager?.currentPage ?: 1
                    ).execute()

                withContext(Dispatchers.Main) {
                    gallery.value = response.body()
                    loading.value = false
                }
            } else {
                val response: Response<TenorResponse> = TpuApi.retrofitService.getTenorGallery(next = "", search = search.value).execute()

                withContext(Dispatchers.Main) {
                    if(response.isSuccessful) {
                        val body = response.body()
                        gallery.value = body?.results?.map {
                            Upload(
                                id = it.created.toInt(),
                                name = it.title,
                                attachment = it.media_formats.gif.url,
                                type = "image-tenor",
                                collections = listOf(),
                                starred = null,
                                createdAt = "",
                                updatedAt = "",
                                data = null,
                                deletable = false,
                                fileSize = 0,
                                originalFilename = "",
                                textMetadata = "",
                                urlRedirect = "",
                                user = defaultUser(),
                                userId = 0
                            )
                        }.let {
                            Gallery(
                                pager = Pager(
                                    currentPage = 0,
                                    totalPages = 0,
                                    totalItems = 0,
                                    endIndex = 0,
                                    startIndex = 0,
                                    pageSize = 0,
                                    pages = listOf(),
                                    endPage = 0,
                                    startPage = 0
                                ),
                                gallery = it ?: listOf()
                            )
                        }
                        loading.value = false
                    }
                }
            }
        }
    }

    fun deleteItem(upload: Upload) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = TpuApi.retrofitService.deleteUpload(upload.id).execute()
            if(response.isSuccessful) {
                withContext(Dispatchers.Main) {
                    gallery.value = gallery.value?.copy(
                        gallery = gallery.value?.gallery?.filter { it.id != upload.id }.orEmpty()
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    PrivateUploaderTheme(
        content = {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                GalleryScreen()
            }
        }
    )
}

/**
 * Returns whether the lazy list is currently scrolling up.
 */
@Composable
private fun LazyListState.isScrollingUp(): Boolean {
    var previousIndex by remember(this) { mutableIntStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableIntStateOf(firstVisibleItemScrollOffset) }
    return remember(this) {
        derivedStateOf {
            if (previousIndex != firstVisibleItemIndex) {
                previousIndex > firstVisibleItemIndex
            } else {
                previousScrollOffset >= firstVisibleItemScrollOffset
            }.also {
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }.value
}

fun Context.getActivity(): AppCompatActivity? = when (this) {
    is AppCompatActivity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}