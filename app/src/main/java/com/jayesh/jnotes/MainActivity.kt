package com.jayesh.jnotes

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.jayesh.jnotes.ui.NewOrEditNote
import com.jayesh.jnotes.ui.models.Tag
import com.jayesh.jnotes.ui.theme.JnotesTheme

@ExperimentalComposeUiApi
@ExperimentalAnimationApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            // Update the system bars to be translucent
            val systemUiController = rememberSystemUiController()
            val useDarkIcons = MaterialTheme.colors.isLight

            SideEffect {
                systemUiController.setSystemBarsColor(Color.Transparent, darkIcons = useDarkIcons)
                changeSystemBarsIconsColor(window, useDarkIcons)
            }

            JnotesTheme {
                ProvideWindowInsets {
                    //changeStatusBarContrastStyle(window, false)
                    /*NewOrEditNote(onBackgroundSelected = {
                        if (it is BackgroundType.SingleColor) {
                            val useDarkStatusBarIcons = it.backgroundColor.luminance() > 0.5f
                            changeStatusBarIconsColor(window = window, useDarkStatusBarIcons)
                            statusBarColor = it.backgroundColor
                        }
                    })*/
                    NewOrEditNote()
                }
            }
        }
    }

    private fun changeStatusBarIconsColor(window: Window, darkIcons: Boolean) {
        val statusBar = WindowCompat.getInsetsController(window, window.decorView)
        statusBar?.isAppearanceLightStatusBars = darkIcons
    }

    private fun changeSystemBarsIconsColor(window: Window, darkIcons: Boolean) {
        val windowController = WindowCompat.getInsetsController(window, window.decorView)
        windowController?.isAppearanceLightStatusBars = darkIcons
        windowController?.isAppearanceLightNavigationBars = darkIcons
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Composable
fun NoteItem(
    title: String,
    noteBrief: String?,
    createdAt: String, // e.g. 10:43 pm, Yesterday, 12/12/21
    modifier: Modifier = Modifier,
    tags: List<Tag>? = null,
    onItemClick: () -> Unit
) {
    Surface(elevation = 2.dp,
        modifier = modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onItemClick() }
            .height(IntrinsicSize.Min)
    ) {
        Column(
            modifier = Modifier.padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.h6,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2,
                    modifier = Modifier.weight(1f)
                )
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Text(
                        text = createdAt,
                        style = MaterialTheme.typography.caption,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 4
                    )
                }
            }
            if (noteBrief != null) {
                Text(
                    text = noteBrief,
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(4.dp)
                .background(color = MaterialTheme.colors.onSurface)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NoteItemPreview() {
    JnotesTheme {
        NoteItem("Android", "Currently learning jetpack compose in android", "10:43 pm", onItemClick = {})
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun NoteItemPreviewNightMode() {
    JnotesTheme {
        NoteItem("Android Night", "Currently learning jetpack compose in android", "Yesterday", onItemClick = {})
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    JnotesTheme {
        Greeting("Android")
    }
}