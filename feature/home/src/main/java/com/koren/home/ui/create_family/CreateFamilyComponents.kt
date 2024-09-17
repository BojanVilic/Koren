package com.koren.home.ui.create_family

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateSizeAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.koren.common.util.Destination
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.ThemePreview
import com.koren.home.R
import com.koren.home.ui.HomeViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
object CreateFamilyScreenDestination : Destination

@Composable
fun CreateFamilyScreen(
    homeViewModel: HomeViewModel = hiltViewModel()
) {

    val coroutineScope = rememberCoroutineScope()

    CreateFamilyContent(
        createFamily = { familyName, familyPortraitPath ->
            coroutineScope.launch {
                homeViewModel.createFamily(familyName, familyPortraitPath)
            }
        }
    )
}

@Composable
private fun CreateFamilyContent(
    createFamily: (String, Uri?) -> Unit
) {

    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { 3 })
    var familyName by remember { mutableStateOf("") }
    var familyPortraitPath by remember { mutableStateOf<Uri?>(null) }

    Box {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = false
        ) { page ->
            Column {
                when (page) {
                    0 -> AddImageStep(
                        setFamilyPortraitPath = {
                            familyPortraitPath = it
                        },
                        familyPortraitPath = familyPortraitPath
                    )
                    1 -> AddNameStep(
                        familyName = familyName,
                        setFamilyName = {
                            familyName = it
                        }
                    )
                    2 -> CreateFamilyStep(
                        createFamily = {
                            createFamily(familyName, familyPortraitPath)
                        }
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pagerState.pageCount) { iteration ->
                val color = if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.inversePrimary
                val width = if (pagerState.currentPage == iteration) 64.dp else 24.dp

                val size = animateSizeAsState(
                    targetValue = if (pagerState.currentPage == iteration) {
                        Size(width.value, 12.dp.value)
                    } else {
                        Size(24.dp.value, 12.dp.value)
                    },
                    animationSpec = tween(
                        durationMillis = 300,
                        delayMillis = 0
                    ),
                    label = ""
                )

                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(RoundedCornerShape(50))
                        .background(color)
                        .size(size.value.width.dp, size.value.height.dp)
                )
            }
        }

        IconButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp),
            onClick = {
                coroutineScope.launch {
                    pagerState.scrollToPage(pagerState.currentPage + 1)
                }
            },
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Image(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null
            )
        }
    }
}

@Composable
private fun AddImageStep(
    setFamilyPortraitPath: (Uri?) -> Unit,
    familyPortraitPath: Uri?
) {

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            setFamilyPortraitPath(uri)
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            modifier = Modifier.padding(bottom = 16.dp),
            text = stringResource(R.string.add_image_title),
            style = MaterialTheme.typography.displayLarge
        )

        Text(
            text = stringResource(R.string.add_image_subtitle),
            style = MaterialTheme.typography.titleMedium
        )

        if (familyPortraitPath != null) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .fillMaxHeight(0.5f)
                    .padding(top = 16.dp)
                    .clip(RoundedCornerShape(10))
                    .border(4.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(10))
                    .clickable {
                        imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }
                    .align(Alignment.CenterHorizontally),
                model = ImageRequest.Builder(LocalContext.current).data(familyPortraitPath).build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
            )
        } else {
            Image(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .fillMaxHeight(0.5f)
                    .padding(top = 16.dp)
                    .clip(RoundedCornerShape(10))
                    .border(4.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(10))
                    .clickable {
                        imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }
                    .align(Alignment.CenterHorizontally),
                painter = painterResource(id = R.drawable.family_parents_icon),
                contentDescription = null,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
            )
        }
    }
}

@Composable
private fun AddNameStep(
    familyName: String,
    setFamilyName: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        OutlinedTextField(
            value = familyName,
            onValueChange = {
                setFamilyName(it)
            }
        )
    }
}

@Composable
private fun CreateFamilyStep(
    createFamily: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Button(
            onClick = { createFamily() }
        ) {
            Text(text = stringResource(R.string.create_family_label))
        }
    }
}

@ThemePreview
@Composable
fun CreateFamilyPreview() {
    KorenTheme {
        CreateFamilyContent(
            createFamily = {_,_->}
        )
    }
}

@ThemePreview
@Composable
fun AddImageStepPreview() {
    KorenTheme {
        AddImageStep(
            setFamilyPortraitPath = {},
            familyPortraitPath = null
        )
    }
}

@ThemePreview
@Composable
fun AddNameStepPreview() {
    KorenTheme {
        AddNameStep(
            familyName = "",
            setFamilyName = {}
        )
    }
}

@ThemePreview
@Composable
fun CreateFamilyStepPreview() {
    KorenTheme {
        CreateFamilyStep(
            createFamily = {}
        )
    }
}