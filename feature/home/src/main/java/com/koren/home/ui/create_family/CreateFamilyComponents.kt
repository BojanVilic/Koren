package com.koren.home.ui.create_family

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateSizeAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.ArrowForward
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.koren.common.util.Destination
import com.koren.common.util.Resource
import com.koren.designsystem.components.dashedBorder
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.LocalScaffoldStateProvider
import com.koren.designsystem.theme.ScaffoldState
import com.koren.designsystem.theme.ThemePreview
import com.koren.home.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
object CreateFamilyScreenDestination : Destination

@Composable
fun CreateFamilyScreen(
    createFamilyViewModel: CreateFamilyViewModel = hiltViewModel()
) {

    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { 3 })
    val state by createFamilyViewModel.state.collectAsStateWithLifecycle()

    LocalScaffoldStateProvider.current.setScaffoldState(
        state = ScaffoldState(
            title = "",
            customBackAction = if (pagerState.currentPage > 0) {
                {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        createFamilyViewModel.previousStep()
                    }
                }
            } else null
        )
    )

    CreateFamilyContent(
        state = state,
        pagerState = pagerState,
        setFamilyPortraitPath = {
            createFamilyViewModel.setPhotoUri(it)
        },
        setFamilyName = {
            createFamilyViewModel.setFamilyName(it)
        },
        onNextStep = {
            createFamilyViewModel.nextStep()
        },
        createFamily = {
            createFamilyViewModel.createFamily()
        }
    )
}

@Composable
private fun CreateFamilyContent(
    state: CreateFamilyState,
    pagerState: PagerState,
    setFamilyPortraitPath: (Uri?) -> Unit,
    setFamilyName: (String) -> Unit,
    onNextStep: () -> Unit,
    createFamily: () -> Unit
) {

    val coroutineScope = rememberCoroutineScope()
    val currentPage by remember { derivedStateOf { pagerState.currentPage } }
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(
        modifier = Modifier.imePadding()
    ) {
        Column(
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            Row(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                LazyRow {
                    items(state.totalSteps) { iteration ->
                        val color = if (currentPage == iteration) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                        val width = if (currentPage == iteration) 64.dp else 24.dp

                        val size = animateSizeAsState(
                            targetValue = if (currentPage == iteration) {
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
            }

            HorizontalPager(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp),
                state = pagerState,
                userScrollEnabled = false
            ) { page ->
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    when (page) {
                        0 -> AddImageStep(
                            setFamilyPortraitPath = setFamilyPortraitPath,
                            familyPortraitPath = state.photoUri
                        )
                        1 -> AddNameStep(
                            familyName = state.familyName,
                            setFamilyName = setFamilyName
                        )
                        2 -> CreateFamilyStep(
                            familyCreationStatus = state.familyCreationStatus
                        )
                    }
                }
            }
        }

        if (state.familyCreationStatus !is Resource.Loading) {
            Button(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 48.dp, vertical = 16.dp)
                    .fillMaxWidth(),
                onClick = {
                    if (currentPage < state.totalSteps - 1) {
                        if (state.isStepValid) {
                            coroutineScope.launch {
                                pagerState.scrollToPage(currentPage + 1)
                                onNextStep()
                                keyboardController?.hide()
                            }
                        }
                    } else {
                        createFamily()
                    }
                },
                enabled = state.isStepValid,
            ) {
                Text(text = if (currentPage < state.totalSteps - 1) stringResource(R.string.continue_label) else stringResource(R.string.create_family_label))
                Spacer(modifier = Modifier.width(8.dp))
                Image(
                    imageVector = Icons.AutoMirrored.TwoTone.ArrowForward,
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(color = if (state.isStepValid) ButtonDefaults.buttonColors().contentColor else ButtonDefaults.buttonColors().disabledContentColor)
                )
            }
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
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            modifier = Modifier.padding(bottom = 16.dp),
            text = stringResource(R.string.add_family_name_title),
            style = MaterialTheme.typography.displayLarge
        )

        Text(
            text = stringResource(R.string.add_family_name_subtitle),
            style = MaterialTheme.typography.titleMedium
        )

        Card(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 32.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (familyPortraitPath != null) {
                    AsyncImage(
                        modifier = Modifier
                            .size(128.dp)
                            .clip(CircleShape)
                            .dashedBorder(
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape,
                                strokeWidth = 4.dp,
                                gapLength = 8.dp
                            ),
                        model = ImageRequest.Builder(LocalContext.current)
                            .crossfade(true)
                            .data(familyPortraitPath)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        modifier = Modifier
                            .size(128.dp)
                            .clip(CircleShape)
                            .dashedBorder(
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape,
                                strokeWidth = 4.dp,
                                gapLength = 8.dp
                            )
                            .clickable {
                                imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            },
                        painter = painterResource(R.drawable.ic_add_photo),
                        contentDescription = null,
                        contentScale = ContentScale.Inside,
                        colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.primary)
                    )
                }
                Column(
                    modifier = Modifier
                        .height(IntrinsicSize.Max)
                        .padding(horizontal = 12.dp),
                ) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        }
                    ) {
                        Text(text = stringResource(R.string.add_image_button_label))
                    }

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            setFamilyPortraitPath(null)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            contentColor = MaterialTheme.colorScheme.onBackground
                        )
                    ) {
                        Text(text = stringResource(R.string.remove_image_button_label))
                    }
                }
            }
        }
    }
}

@Composable
private fun AddNameStep(
    familyName: String,
    setFamilyName: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
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

        OutlinedTextField(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 32.dp)
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally),
            value = familyName,
            onValueChange = {
                setFamilyName(it)
            },
            label = {
                Text(text = stringResource(R.string.family_name_label))
            },
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            trailingIcon = if (familyName.isNotBlank()) {
                {
                    IconButton(
                        onClick = {
                            setFamilyName("")
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Clear,
                            contentDescription = null
                        )
                    }
                }
            } else null
        )
    }
}

@Composable
private fun CreateFamilyStep(
    familyCreationStatus: Resource<Unit>? = null
) {

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.congratulations_confetti))
    val preloaderProgress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = familyCreationStatus is Resource.Success
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            modifier = Modifier.align(Alignment.Center),
            progress = { preloaderProgress },
            composition = composition
        )

        when (familyCreationStatus) {
            is Resource.Error -> Text(text = familyCreationStatus.throwable?.message?: "")
            is Resource.Loading -> FamilyCreationLoadingScreen()
            is Resource.Success -> {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth()
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(R.string.thank_you_label),
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(R.string.family_created_label),
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
            else -> Unit
        }
    }
}

@Composable
fun FamilyCreationLoadingScreen() {
    val messageIds = listOf(
        R.string.building_family_tree,
        R.string.gathering_together,
        R.string.tying_family_bonds,
        R.string.setting_up_home,
        R.string.adding_splash_of_love,
        R.string.getting_photos_ready,
        R.string.making_space_for_memories,
        R.string.loading_fun_times,
        R.string.unlocking_family_secrets,
        R.string.planting_family_roots
    )

    var currentMessageId by remember { mutableIntStateOf(messageIds.random()) }

    fun getNextMessageId(): Int {
        var newMessageId: Int
        do {
            newMessageId = messageIds.random()
        } while (newMessageId == currentMessageId)
        return newMessageId
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(2000)
            currentMessageId = getNextMessageId()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Crossfade(
                targetState = currentMessageId,
                label = "",
                animationSpec = tween(
                    durationMillis = 1000,
                    delayMillis = 0,
                    easing = LinearOutSlowInEasing
                )
            ) { messageId  ->
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    text = stringResource(id = messageId),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
            CircularProgressIndicator(
                modifier = Modifier.size(50.dp)
            )
        }
    }
}

@ThemePreview
@Composable
fun CreateFamilyPreview() {
    KorenTheme {
        CreateFamilyContent(
            state = CreateFamilyState(),
            pagerState = rememberPagerState(pageCount = { 3 }),
            setFamilyPortraitPath = {},
            setFamilyName = {},
            onNextStep = {},
            createFamily = {}
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
            familyCreationStatus = Resource.Success(Unit)
        )
    }
}