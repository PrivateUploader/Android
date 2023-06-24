package com.troplo.privateuploader.components.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.troplo.privateuploader.api.TpuFunctions
import com.troplo.privateuploader.components.core.UserAvatar
import com.troplo.privateuploader.data.model.ChatAssociation
import com.troplo.privateuploader.data.model.Message
import com.troplo.privateuploader.data.model.User
import android.text.format.DateFormat
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.LocalContentColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.troplo.privateuploader.data.model.Embed
import dev.jeziellago.compose.markdowntext.MarkdownText
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
@Preview
fun Message(@PreviewParameter(
    SampleMessageProvider::class
) message: Message, compact: String = "none"
) {
    Column {
        if (compact == "separator") {
            Column(
                modifier = Modifier.padding(top = 12.dp, start = 16.dp, end = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Divider(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = DateFormat.format("EEE, dd MMM yyyy", message.createdAt).toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    Divider(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        val normal = compact == "separator" || compact == "none"

        Row(
            modifier = if (normal) Modifier.padding(
                start = 16.dp,
                top = 16.dp,
                end = 16.dp
            ) else Modifier.padding(start = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (normal) {
                UserAvatar(
                    avatar = message.user?.avatar,
                    username = message.user?.username ?: "Deleted User",
                    modifier = Modifier.align(Alignment.Top)
                )
            }
            if (!normal) {
                Spacer(modifier = Modifier.width(40.dp))
            }
            Column(modifier = Modifier.padding(start = 8.dp)) {
                if (normal) {
                    Row {
                        Text(
                            text = message.user?.username ?: "Deleted User",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = TpuFunctions.formatDate(message.createdAt).toString(),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }

                val color = if(message.pending == false) {
                    LocalContentColor.current
                } else if(message.error == true) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }

                MarkdownText(
                    markdown = message.content ?: "",
                    textAlign = TextAlign.Center,
                    color = color
                )

                message.embeds.forEach {
                    Embed(embed = it)
                }
            }
        }
    }
}

class SampleMessageProvider : PreviewParameterProvider<Message> {
    override val values: Sequence<Message>
        get() = sequenceOf(
            Message(
                id = 1,
                user = User(
                    id = 1,
                    username = "Troplo",
                    avatar = "deez",
                    banner = "deez"
                ),
                chatId = 1,
                content = "Hello World!",
                createdAt = Date("2021-09-01T00:00:00.000Z"),
                updatedAt = Date("2021-09-01T00:00:00.000Z"),
                edited = false,
                editedAt = null,
                embeds = emptyList<Embed>(),
                error = false,
                legacyUser = null,
                legacyUserId = null,
                pending = false,
                pinned = false,
                readReceipts = emptyList<ChatAssociation>(),
                reply = null,
                replyId = null,
                tpuUser = null,
                userId = 1,
                type = "message"
            )
        )

    override val count: Int
        get() = values.count()
}