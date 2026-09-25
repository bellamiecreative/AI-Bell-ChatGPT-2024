package com.bellamie.aibell

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

private val ChatGreen = Color(0xFF10A37F)
private val Sidebar = Color(0xFFF7F7F8)
private val Border = Color(0xFFE5E5E5)

data class ChatMessage(
    val id: Long,
    val role: String,
    val text: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AIBellApp() }
    }
}

@Composable
private fun AIBellApp() {
    MaterialTheme(colorScheme = lightColorScheme(primary = ChatGreen)) {
        var drawerOpen by remember { mutableStateOf(true) }
        var showSettings by remember { mutableStateOf(false) }
        var draft by remember { mutableStateOf("") }
        val messages = remember { mutableStateListOf<ChatMessage>() }
        val listState = rememberLazyListState()
        val scope = rememberCoroutineScope()

        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            if (drawerOpen) {
                SidebarPanel(
                    onNewChat = {
                        messages.clear()
                        showSettings = false
                    },
                    onSettings = { showSettings = true }
                )
            }

            Column(Modifier.fillMaxSize()) {
                TopBar(
                    onMenu = { drawerOpen = !drawerOpen },
                    onSettings = { showSettings = true }
                )

                if (showSettings) {
                    SettingsScreen(onBack = { showSettings = false })
                } else if (messages.isEmpty()) {
                    Welcome()
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        contentPadding = PaddingValues(vertical = 18.dp)
                    ) {
                        items(messages, key = { it.id }) { message ->
                            MessageRow(message)
                        }
                    }
                }

                if (!showSettings) {
                    Composer(
                        value = draft,
                        onValueChange = { draft = it },
                        onSend = {
                            val text = draft.trim()
                            if (text.isNotEmpty()) {
                                messages.add(ChatMessage(System.nanoTime(), "user", text))
                                messages.add(
                                    ChatMessage(
                                        System.nanoTime(),
                                        "assistant",
                                        "AI Bell is ready. Add your AI provider settings to enable live responses."
                                    )
                                )
                                draft = ""
                                scope.launch {
                                    listState.animateScrollToItem(messages.lastIndex)
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SidebarPanel(
    onNewChat: () -> Unit,
    onSettings: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(280.dp)
            .fillMaxHeight()
            .background(Sidebar)
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .clickable(onClick = onNewChat)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("AI Bell", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.weight(1f))
            Icon(Icons.Default.Edit, "New chat", Modifier.size(19.dp))
        }

        Spacer(Modifier.height(8.dp))

        OutlinedTextButtonLike(
            text = "New chat",
            icon = Icons.Default.Add,
            onClick = onNewChat
        )

        Spacer(Modifier.height(18.dp))
        Text(
            "Today",
            modifier = Modifier.padding(horizontal = 12.dp),
            fontSize = 12.sp,
            color = Color.Gray
        )
        Spacer(Modifier.height(6.dp))

        listOf(
            "Welcome to AI Bell",
            "Ideas for my project",
            "Travel planning",
            "Kurdish language help"
        ).forEach { title ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {}
                    .padding(horizontal = 12.dp, vertical = 11.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.ChatBubbleOutline, null, Modifier.size(16.dp), tint = Color.DarkGray)
                Spacer(Modifier.width(10.dp))
                Text(title, maxLines = 1, fontSize = 14.sp)
            }
        }

        Spacer(Modifier.weight(1f))
        HorizontalDivider(color = Border)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onSettings)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Settings, null, Modifier.size(23.dp))
            Spacer(Modifier.width(10.dp))
            Column {
                Text("AI Bell", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                Text("Settings", fontSize = 12.sp, color = Color.Gray)
            }
            Spacer(Modifier.weight(1f))
            Icon(Icons.Default.MoreHoriz, null)
        }
    }
}

@Composable
private fun OutlinedTextButtonLike(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = Color.Transparent,
        border = androidx.compose.foundation.BorderStroke(1.dp, Border)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(icon, null, Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text(text)
        }
    }
}

@Composable
private fun TopBar(
    onMenu: () -> Unit,
    onSettings: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onMenu) {
            Icon(Icons.Default.Menu, "Menu")
        }
        Spacer(Modifier.width(4.dp))
        Text("AI Bell", fontWeight = FontWeight.Medium, fontSize = 16.sp)
        Spacer(Modifier.width(6.dp))
        Text("·", color = Color.Gray)
        Spacer(Modifier.width(6.dp))
        Text("AI chat", color = Color.Gray, fontSize = 14.sp)
        Spacer(Modifier.weight(1f))
        IconButton(onClick = onSettings) {
            Icon(Icons.Default.Settings, "Settings")
        }
    }
}

@Composable
private fun Welcome() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("AI Bell", fontSize = 30.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        Text(
            "How can I help you today?",
            fontSize = 18.sp,
            color = Color.Gray
        )
    }
}

@Composable
private fun MessageRow(message: ChatMessage) {
    val user = message.role == "user"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 14.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            modifier = Modifier.size(32.dp),
            shape = RoundedCornerShape(7.dp),
            color = if (user) Color(0xFFE8E8E8) else ChatGreen
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    if (user) "B" else "✦",
                    fontWeight = FontWeight.Bold,
                    color = if (user) Color.DarkGray else Color.White
                )
            }
        }

        Spacer(Modifier.width(16.dp))
        Text(
            message.text,
            modifier = Modifier.weight(1f),
            fontSize = 15.sp,
            lineHeight = 23.sp
        )
    }
}

@Composable
private fun Composer(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 12.dp)
            .windowInsetsPadding(WindowInsets.ime)
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Message AI Bell") },
            trailingIcon = {
                IconButton(onClick = onSend, enabled = value.isNotBlank()) {
                    Icon(Icons.Default.ArrowUpward, "Send")
                }
            },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Border,
                unfocusedBorderColor = Border
            ),
            maxLines = 6
        )

        Spacer(Modifier.height(6.dp))
        Text(
            "AI Bell can make mistakes. Check important information.",
            modifier = Modifier.fillMaxWidth(),
            fontSize = 11.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SettingsScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Text("Settings", fontSize = 28.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        Text(
            "AI provider configuration will be connected in the next implementation step.",
            color = Color.Gray,
            lineHeight = 21.sp
        )
        Spacer(Modifier.height(20.dp))
        Text(
            "This starter keeps API credentials out of the source code. Never commit a real API key to GitHub.",
            fontSize = 13.sp,
            color = Color.Gray,
            lineHeight = 19.sp
        )
        Spacer(Modifier.weight(1f))
        OutlinedTextButtonLike(
            text = "Back to chat",
            icon = Icons.Default.ChatBubbleOutline,
            onClick = onBack
        )
    }
}
