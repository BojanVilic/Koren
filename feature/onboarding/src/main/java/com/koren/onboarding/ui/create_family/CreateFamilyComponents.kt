package com.koren.onboarding.ui.create_family

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateSizeAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.koren.designsystem.components.BrokenBranchErrorScreen
import com.koren.designsystem.components.LoadingContent
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.LocalScaffoldStateProvider
import com.koren.designsystem.theme.ScaffoldState
import com.koren.designsystem.theme.ThemePreview
import com.koren.onboarding.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
object CreateFamilyDestination

@Composable
fun CreateFamilyScreen(
    createFamilyViewModel: CreateFamilyViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit
) {

    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { (createFamilyViewModel.uiState.value as? CreateFamilyUiState.Step)?.totalSteps?: 0 })
    val state by createFamilyViewModel.uiState.collectAsStateWithLifecycle()

    LocalScaffoldStateProvider.current.setScaffoldState(
        state = ScaffoldState(
            title = "",
            customBackAction = if (pagerState.currentPage > 0) {
                {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        (state as? CreateFamilyUiState.Step)?.let {
                            it.eventSink(CreateFamilyEvent.PreviousStep)
                        }
                    }
                }
            } else null,
            isBottomBarVisible = false
        )
    )

    CreateFamilyContent(
        state = state,
        pagerState = pagerState,
        onNavigateToHome = onNavigateToHome
    )
}

@Composable
private fun CreateFamilyContent(
    state: CreateFamilyUiState,
    pagerState: PagerState,
    onNavigateToHome: () -> Unit
) {
    when (state) {
        is CreateFamilyUiState.Step -> CreateFamilyStepContent(
            state = state,
            pagerState = pagerState
        )
        is CreateFamilyUiState.Error -> BrokenBranchErrorScreen(errorMessage = state.errorMessage)
        is CreateFamilyUiState.CreatingFamily -> FamilyCreationLoadingScreen()
        is CreateFamilyUiState.FamilyCreated -> FamilyCreated(onNavigateToHome = onNavigateToHome)
        is CreateFamilyUiState.Loading -> LoadingContent()
    }
}

@Composable
private fun CreateFamilyStepContent(
    state: CreateFamilyUiState.Step,
    pagerState: PagerState
) {
    val coroutineScope = rememberCoroutineScope()
    val currentPage by remember { derivedStateOf { pagerState.currentPage } }
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        modifier = Modifier.imePadding(),
        bottomBar = {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedVisibility(currentPage > 0) {
                    OutlinedButton(
                        modifier = Modifier
                            .weight(0.3f),
                        onClick = {
                            coroutineScope.launch {
                                pagerState.scrollToPage(currentPage - 1)
                                state.eventSink(CreateFamilyEvent.PreviousStep)
                                keyboardController?.hide()
                            }
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.back_label)
                        )
                    }
                }

                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        if (state.isStepValid) {
                            coroutineScope.launch {
                                pagerState.scrollToPage(currentPage + 1)
                                state.eventSink(CreateFamilyEvent.NextStep)
                                keyboardController?.hide()
                            }
                        }
                    },
                    enabled = state.isStepValid,
                ) {
                    Text(
                        text = if (state.currentStep != CreateFamilyStep.INVITE_FAMILY_MEMBERS) 
                            stringResource(R.string.continue_label) 
                        else 
                            stringResource(R.string.create_family_label)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Image(
                        imageVector = Icons.AutoMirrored.TwoTone.ArrowForward,
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(color = if (state.isStepValid) ButtonDefaults.buttonColors().contentColor else ButtonDefaults.buttonColors().disabledContentColor)
                    )
                }
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
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
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Top
                ) {
                    when (state.currentStep) {
                        CreateFamilyStep.ADD_FAMILY_PORTRAIT -> AddImageStep(
                            setFamilyPortraitPath = {
                                state.eventSink(CreateFamilyEvent.SetPhotoUri(it))
                            },
                            familyPortraitPath = state.photoUri
                        )
                        CreateFamilyStep.ADD_FAMILY_NAME -> AddNameStep(
                            familyName = state.familyName,
                            setFamilyName = {
                                state.eventSink(CreateFamilyEvent.SetFamilyName(it))
                            }
                        )
                        CreateFamilyStep.ADD_HOUSE_ADDRESS -> AddHouseAddressStep(uiState = state)
                        CreateFamilyStep.INVITE_FAMILY_MEMBERS -> InviteFamilyMembersStep(uiState = state)
                    }
                }
            }
        }
    }
}

@Composable
private fun FamilyCreated(
    onNavigateToHome: () -> Unit
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.congratulations_confetti))
    val preloaderProgress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = true
    )

    LaunchedEffect(preloaderProgress) {
        if (preloaderProgress == 1f) {
            onNavigateToHome()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            modifier = Modifier.align(Alignment.Center),
            progress = { preloaderProgress },
            composition = composition
        )

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.congrats_label),
                style = MaterialTheme.typography.displaySmall,
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
        CreateFamilyStepContent(
            state = CreateFamilyUiState.Step(eventSink = {}),
            pagerState = rememberPagerState(pageCount = { 3 })
        )
    }
}

@ThemePreview
@Composable
fun CreateFamilyStepPreview() {
    KorenTheme {
        FamilyCreated(onNavigateToHome = {})
    }
}