package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.SchoolViewModel
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NavyPrimary
import com.example.utils.AppUpdateManager
import com.example.utils.InstalledAppItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppUpdatesScreen(
    viewModel: SchoolViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var installedApps by remember { mutableStateOf<List<InstalledAppItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }
    var filterType by remember { mutableStateOf(0) } // 0: Toutes, 1: Utilisateur, 2: Système

    // Chargement asynchrone des applications installées
    fun loadApps() {
        isLoading = true
        coroutineScope.launch {
            val apps = withContext(Dispatchers.IO) {
                AppUpdateManager.getInstalledApps(context)
            }
            installedApps = apps
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        loadApps()
    }

    val filteredApps = remember(installedApps, searchQuery, filterType) {
        installedApps.filter { app ->
            val matchesFilter = when (filterType) {
                1 -> !app.isSystemApp
                2 -> app.isSystemApp
                else -> true
            }
            val matchesQuery = searchQuery.isBlank() ||
                    app.appName.contains(searchQuery, ignoreCase = true) ||
                    app.packageName.contains(searchQuery, ignoreCase = true)

            matchesFilter && matchesQuery
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mises à jour des Applications",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { loadApps() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Actualiser la liste"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NavyPrimary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        modifier = modifier
    ) { padding ->
        LazyColumn(
            contentPadding = padding,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(14.dp))

                // Hero Card: Global Update Hub
                ElevatedCard(
                    colors = CardDefaults.elevatedCardColors(containerColor = NavyPrimary),
                    shape = RoundedCornerShape(18.dp),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = GoldAccent,
                                shape = CircleShape,
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.SystemUpdate,
                                        contentDescription = null,
                                        tint = NavyPrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Mises à jour de l'Appareil",
                                    color = Color.White,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Gestionnaire centralisé pour toutes vos applications",
                                    color = Color(0xFFCBD5E1),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Vous pouvez déclencher en un seul clic la mise à jour globale de toutes les applications installées sur votre smartphone via Google Play Store.",
                            color = Color(0xFFE2E8F0),
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Bouton principal : Tout mettre à jour via Google Play
                        Button(
                            onClick = { AppUpdateManager.launchGooglePlayUpdatesAll(context) },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("update_all_play_store_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudDownload,
                                contentDescription = null,
                                tint = NavyPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Lancer la mise à jour de TOUT (Google Play)",
                                color = NavyPrimary,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Bouton secondaire : Paramètres de mise à jour système Android
                        OutlinedButton(
                            onClick = { AppUpdateManager.launchSystemUpdateSettings(context) },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF64748B)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Vérifier les Mises à jour du Système Android",
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section title & Search
                Text(
                    text = "Applications Installées (${installedApps.size})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Rechercher une application installée...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Filter chips
                val userCount = installedApps.count { !it.isSystemApp }
                val systemCount = installedApps.count { it.isSystemApp }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FilterChip(
                        selected = filterType == 0,
                        onClick = { filterType = 0 },
                        label = { Text("Toutes (${installedApps.size})", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NavyPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                    FilterChip(
                        selected = filterType == 1,
                        onClick = { filterType = 1 },
                        label = { Text("Utilisateur ($userCount)", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NavyPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                    FilterChip(
                        selected = filterType == 2,
                        onClick = { filterType = 2 },
                        label = { Text("Système ($systemCount)", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NavyPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = NavyPrimary)
                    }
                }
            } else if (filteredApps.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Aucune application correspondante.",
                            color = Color.Gray,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                items(filteredApps, key = { it.packageName }) { appItem ->
                    InstalledAppRowCard(
                        appItem = appItem,
                        onUpdateClick = {
                            AppUpdateManager.launchAppUpdateInPlayStore(context, appItem.packageName)
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun InstalledAppRowCard(
    appItem: InstalledAppItem,
    onUpdateClick: () -> Unit
) {
    val dateStr = remember(appItem.lastUpdateTime) {
        if (appItem.lastUpdateTime > 0) {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            sdf.format(Date(appItem.lastUpdateTime))
        } else "Inconnue"
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App Icon
            val safeBitmap = remember(appItem.icon) {
                appItem.icon?.toSafeBitmap()
            }

            if (safeBitmap != null) {
                Image(
                    bitmap = safeBitmap.asImageBitmap(),
                    contentDescription = appItem.appName,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            } else {
                Surface(
                    color = if (appItem.isSystemApp) Color(0xFFE2E8F0) else NavyPrimary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (appItem.isSystemApp) Icons.Default.Android else Icons.Default.Apps,
                            contentDescription = null,
                            tint = if (appItem.isSystemApp) Color.Gray else NavyPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // App details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = appItem.appName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                Text(
                    text = appItem.packageName,
                    fontSize = 10.sp,
                    color = Color.Gray,
                    maxLines = 1
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Surface(
                        color = Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "v${appItem.versionName}",
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF475569),
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                    if (appItem.isSystemApp) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Surface(
                            color = Color(0xFFE0E7FF),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "Système",
                                fontSize = 9.sp,
                                color = Color(0xFF3730A3),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Màj : $dateStr",
                        fontSize = 9.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Update button
            Button(
                onClick = onUpdateClick,
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                shape = RoundedCornerShape(8.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                modifier = Modifier.height(34.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = null,
                    tint = GoldAccent,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Màj",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

/**
 * Convertit un Drawable quelconque en Bitmap sûr pour Compose.
 */
private fun Drawable.toSafeBitmap(): Bitmap {
    if (this is BitmapDrawable && bitmap != null) {
        return bitmap
    }
    val width = if (intrinsicWidth > 0) intrinsicWidth else 96
    val height = if (intrinsicHeight > 0) intrinsicHeight else 96
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    setBounds(0, 0, canvas.width, canvas.height)
    draw(canvas)
    return bitmap
}
