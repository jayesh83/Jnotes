package com.jayesh.jnotes.ui.share

import android.app.Activity
import android.content.ComponentName
import android.content.pm.ActivityInfo
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.jayesh.jnotes.R
import com.jayesh.jnotes.ui.detail.BasicTopAppBar
import com.jayesh.jnotes.ui.detail.NoteDetailViewmodelImpl
import com.jayesh.jnotes.ui.theme.JnotesTheme
import com.jayesh.jnotes.util.Constants
import com.jayesh.jnotes.util.LockScreenOrientation
import com.jayesh.jnotes.util.LogCompositions
import com.jayesh.jnotes.util.PlatformUtils
import com.jayesh.jnotes.util.StringUtils
import dev.shreyaspatil.capturable.Capturable
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

@Composable
fun ShareNoteScreen(
    navController: NavController,
    noteDetailViewModel: NoteDetailViewmodelImpl
) {
    LogCompositions(tag = noteDetailViewModel.hashCode().toString())
    fun goBack() = navController.navigateUp()

    val title = remember(key1 = noteDetailViewModel.titleTextFieldState) {
        noteDetailViewModel.titleTextFieldState.text
    }
    val note = remember(key1 = noteDetailViewModel.noteTextFieldState) {
        noteDetailViewModel.noteTextFieldState.text
    }

    val captureController = rememberCaptureController()
    val context = LocalContext.current

    var selectedAppPackageName by remember {
        mutableStateOf(ComponentName("", ""))
    }

    val launchersListHeight = remember { Constants.topAppBarHeight * 2 }

    val coroutineScope = rememberCoroutineScope()

    val shareActivityLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                goBack()
            }
        }
    )

    val handleOnCaptured = { bitmap: ImageBitmap?, error: Throwable? ->
        if (error != null) {
            Toast.makeText(
                context,
                "Could not share",
                Toast.LENGTH_SHORT
            ).show()
        }
        if (bitmap != null) {
            coroutineScope.launch {
                val fileUri = noteDetailViewModel.getBitmapFileUri(
                    name = noteDetailViewModel.getNoteId() ?: "jnote.jpeg",
                    bitmap = bitmap
                )
                if (fileUri != null) {
                    PlatformUtils.shareImageOnApp(
                        uri = fileUri,
                        componentName = selectedAppPackageName,
                        launcher = shareActivityLauncher
                    )
                } else {
                    Toast.makeText(
                        context,
                        "Could not share",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

    JnotesTheme(
        backgroundColor = noteDetailViewModel.selectedBackgroundType.backgroundColor,
        contentColor = noteDetailViewModel.selectedBackgroundType.contentColor
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            Column {
                BasicTopAppBar(
                    onBack = ::goBack,
                    backgroundColor = Color.Transparent,
                    contentColor = MaterialTheme.colors.onSurface,
                    showNavigationIcon = true,
                    contentAbove = { internalModifier ->
                        Text(
                            text = stringResource(R.string.share_note),
                            style = MaterialTheme.typography.subtitle2.copy(
                                color = MaterialTheme.colors.onBackground,
                                textAlign = TextAlign.Center
                            ),
                            modifier = internalModifier
                        )
                    },
                    rightSideContentSlot = {}
                )
                Capturable(
                    controller = captureController,
                    onCaptured = handleOnCaptured,
                    modifier = Modifier.padding(bottom = launchersListHeight)
                ) {
                    Surface(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colors.background,
                        contentColor = MaterialTheme.colors.onBackground
                    ) {
                        LazyColumn(
                            contentPadding = PaddingValues(32.dp),
                            verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            if (title.isNotBlank()) {
                                item {
                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.h5
                                    )
                                }
                            }
                            if (note.isNotBlank()) {
                                item {
                                    Text(
                                        text = note,
                                        style = MaterialTheme.typography.body2.copy(
                                            lineHeight = 24.sp,
                                            fontSize = 15.sp,
                                            textAlign = TextAlign.Justify
                                        )
                                    )
                                }
                            }
                            item { PoweredByJnotes() }
                        }
                    }
                }
            }

            ShareableAppsList(
                onSelect = {
                    selectedAppPackageName = it
                    captureController.capture()
                },
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .height(launchersListHeight)
            )
        }
    }
}

@Composable
fun PoweredByJnotes(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        Divider(color = MaterialTheme.colors.onBackground.copy(alpha = 0.2f))
        Text(
            text = stringResource(R.string.powered_by_jnotes),
            style = MaterialTheme.typography.h6.copy(fontSize = 10.sp)
        )
    }
}

@Composable
fun ShareableAppsList(
    modifier: Modifier = Modifier,
    onSelect: (componentName: ComponentName) -> Unit
) {
    val context = LocalContext.current
    var launcherAppsResolveInfo by remember { mutableStateOf(listOf<ResolveInfo>()) }

    LaunchedEffect(key1 = Unit) {
        withContext(Dispatchers.Default) {
            val intent = PlatformUtils.prepareJpegImageSharingIntent()
            val launchers = PlatformUtils.listAllAppsForMatchingIntent(context, intent)
            Timber.d(launchers.toString())
            launcherAppsResolveInfo = launchers
        }
    }

    Column(modifier = modifier) {
        Divider(
            modifier = Modifier.fillMaxWidth(),
            thickness = (0.2f).dp,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.2f)
        )
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item { Spacer(modifier = Modifier.width(8.dp)) }
            itemsIndexed(
                items = launcherAppsResolveInfo,
                itemContent = { _, launcherApp ->
                    val componentName = ComponentName(
                        launcherApp.activityInfo.applicationInfo.packageName,
                        launcherApp.activityInfo.name
                    )
                    LauncherApp(
                        componentName = componentName,
                        name = launcherApp.loadLabel(context.packageManager).toString(),
                        icon = launcherApp.loadIcon(context.packageManager),
                        onClick = onSelect,
                        modifier = Modifier.fillMaxHeight()
                    )
                }
            )
            item { Spacer(modifier = Modifier.width(8.dp)) }
        }
    }
}

@Composable
fun LauncherApp(
    modifier: Modifier,
    componentName: ComponentName,
    name: String,
    icon: Drawable,
    onClick: (componentName: ComponentName) -> Unit
) {
    val currentOnSelect by rememberUpdatedState(newValue = onClick)

    Column(
        modifier = modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = { currentOnSelect(componentName) }
        ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier.requiredSize(56.dp),
            painter = rememberDrawablePainter(drawable = icon),
            contentDescription = "$name icon"
        )
        Text(
            text = StringUtils.overflowText(name, 10),
            style = MaterialTheme.typography.subtitle1.copy(
                color = MaterialTheme.colors.onSurface,
                fontSize = 8.sp
            )
        )
    }
}