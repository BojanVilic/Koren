package com.koren.onboarding.ui.create_family

import androidx.lifecycle.viewModelScope
import com.koren.common.models.suggestion.SuggestionResponse
import com.koren.common.models.user.UserLocation
import com.koren.common.util.StateViewModel
import com.koren.common.util.orUnknownError
import com.koren.data.repository.InvitationRepository
import com.koren.domain.GetFamilyUseCase
import com.koren.domain.CreateFamilyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class CreateFamilyViewModel @Inject constructor(
    private val createFamilyUseCase: CreateFamilyUseCase,
    private val getFamilyUseCase: GetFamilyUseCase,
    private val invitationRepository: InvitationRepository
    ): StateViewModel<CreateFamilyEvent, CreateFamilyUiState, CreateFamilySideEffect>() {

    override fun setInitialState(): CreateFamilyUiState = CreateFamilyUiState.Loading

    init {
        viewModelScope.launch {
            getFamilyUseCase()
                .onSuccess { family -> _uiState.update { CreateFamilyUiState.Error(errorMessage = "You are already a member of the ${family.name} family.\n\nTo create a new family, please leave your current family from the account section in the app.") } }
                .onFailure { _uiState.update { CreateFamilyUiState.Step(eventSink = ::handleEvent) } }
        }
        observeLocationSuggestions()
    }

    override fun handleEvent(event: CreateFamilyEvent) {
        withEventfulState<CreateFamilyUiState.Step> { currentState ->
            when (event) {
                is CreateFamilyEvent.SetFamilyName -> _uiState.update { currentState.copy(familyName = event.name) }
                is CreateFamilyEvent.SetPhotoUri -> _uiState.update { currentState.copy(photoUri = event.uri) }
                is CreateFamilyEvent.NextStep -> nextStep(currentState)
                is CreateFamilyEvent.PreviousStep -> previousStep(currentState)
                is CreateFamilyEvent.CreateFamily -> createFamily(currentState)
                is CreateFamilyEvent.SetHomeAddress -> _uiState.update { currentState.copy(addressText = event.addressText) }
                is CreateFamilyEvent.CollapseCreateQRInvitation -> _uiState.update { currentState.copy(isCreateQRInvitationExpanded = false) }
                is CreateFamilyEvent.EmailInviteClick -> _uiState.update { currentState.copy(isEmailInviteExpanded = !currentState.isEmailInviteExpanded) }
                is CreateFamilyEvent.EmailInviteTextChange -> _uiState.update { currentState.copy(emailInviteText = event.email) }
                is CreateFamilyEvent.InviteViaEmailClick -> inviteViaEmail(currentState)
                is CreateFamilyEvent.CreateQRInvitation -> createInvitation(currentState)
                is CreateFamilyEvent.CollapseSearchBar -> _uiState.update { currentState.copy(searchBarExpanded = false) }
                is CreateFamilyEvent.ExpandSearchBar -> _uiState.update { currentState.copy(searchBarExpanded = true) }
                is CreateFamilyEvent.LocationSuggestionClicked -> _uiState.update {
                    currentState.copy(
                        homeAddress = UserLocation(
                            latitude = event.location.latitude,
                            longitude = event.location.longitude,
                        ),
                        searchQuery = event.location.primaryText,
                        searchBarExpanded = false
                    )
                }
                is CreateFamilyEvent.SearchTextChanged -> {
                    _uiState.update { currentState.copy(searchQuery = event.text) }
                    _sideEffects.emitSuspended(CreateFamilySideEffect.GetNewLocationSuggestions(event.text))
                }
            }
        }
    }

    private fun observeLocationSuggestions() {
        viewModelScope.launch(Dispatchers.Default) {
            _sideEffects.asSharedFlow()
                .debounce(300)
                .filterIsInstance<CreateFamilySideEffect.GetNewLocationSuggestions>()
                .flatMapLatest {
                    //                        locationService.getPlaceSuggestions(it.newQuery)
                    getDummyLocationSuggestions()
                }
                .collect { suggestions ->
                    val currentState = (_uiState.value as? CreateFamilyUiState.Step) ?: return@collect
                    _uiState.update { currentState.copy(locationSuggestions = suggestions) }
                }
        }
    }

    private fun getDummyLocationSuggestions(): Flow<List<SuggestionResponse>> {
        val suggestions = listOf(
            SuggestionResponse(
                primaryText = "5550 McGrail Avenue",
                secondaryText = "Niagara Falls, ON, Canada",
                latitude = 43.094260528205254,
                longitude = -79.0765215277345
            ),
            SuggestionResponse(
                primaryText = "6430 Montrose Road",
                secondaryText = "Niagara Falls, ON, Canada",
                latitude = 43.08167874422444,
                longitude = -79.1219035989796
            ),
            SuggestionResponse(
                primaryText = "6767 Morrison St",
                secondaryText = "Niagara Falls, ON, Canada",
                latitude = 43.10512883109716,
                longitude = -79.10819105821295
            )
        )

        return flowOf(suggestions)
    }

    private fun inviteViaEmail(currentState: CreateFamilyUiState.Step) {
        viewModelScope.launch {
            _uiState.update { currentState.copy(emailInvitationLoading = true) }
            invitationRepository.createInvitationViaEmail(currentState.emailInviteText)
                .onSuccess { result ->
                    _uiState.update { currentState.copy(emailInvitation = result, emailInvitationLoading = false) }
                }
                .onFailure { error ->
                    _sideEffects.emitSuspended(CreateFamilySideEffect.ShowError(error.message.orUnknownError()))
                }
        }
    }

    private fun createInvitation(currentState: CreateFamilyUiState.Step) {
        if (currentState.qrInvitation != null) {
            _uiState.update { currentState.copy(isCreateQRInvitationExpanded = !currentState.isCreateQRInvitationExpanded) }
            return
        }
        viewModelScope.launch {
            _uiState.update { currentState.copy(qrInvitationLoading = true, isCreateQRInvitationExpanded = true) }
            invitationRepository.createInvitation()
                .onSuccess { result ->
                    _uiState.update { currentState.copy(qrInvitation = result, isCreateQRInvitationExpanded = true, qrInvitationLoading = false) }
                }
                .onFailure { error ->
                    _sideEffects.emitSuspended(CreateFamilySideEffect.ShowError(error.message.orUnknownError()))
                }
        }
    }

    private fun nextStep(currentState: CreateFamilyUiState.Step) {
        when (currentState.currentStep) {
            CreateFamilyStep.ADD_FAMILY_PORTRAIT -> _uiState.update { currentState.copy(currentStep = CreateFamilyStep.ADD_FAMILY_NAME) }
            CreateFamilyStep.ADD_FAMILY_NAME -> _uiState.update { currentState.copy(currentStep = CreateFamilyStep.ADD_HOUSE_ADDRESS) }
            CreateFamilyStep.ADD_HOUSE_ADDRESS -> createFamily(currentState)
            CreateFamilyStep.INVITE_FAMILY_MEMBERS -> _uiState.update { CreateFamilyUiState.FamilyCreated }
        }
    }

    private fun previousStep(currentState: CreateFamilyUiState.Step) {
        when (currentState.currentStep) {
            CreateFamilyStep.ADD_FAMILY_PORTRAIT -> Unit
            CreateFamilyStep.ADD_FAMILY_NAME -> _uiState.update { currentState.copy(currentStep = CreateFamilyStep.ADD_FAMILY_PORTRAIT) }
            CreateFamilyStep.ADD_HOUSE_ADDRESS -> _uiState.update { currentState.copy(currentStep = CreateFamilyStep.ADD_FAMILY_NAME) }
            CreateFamilyStep.INVITE_FAMILY_MEMBERS -> _uiState.update { currentState.copy(currentStep = CreateFamilyStep.ADD_HOUSE_ADDRESS) }
        }
    }

    private fun createFamily(currentState: CreateFamilyUiState.Step) {
        viewModelScope.launch {
            _uiState.update { CreateFamilyUiState.CreatingFamily }
            try {
                createFamilyUseCase(currentState.familyName, currentState.photoUri)
                _uiState.update { 
                    currentState.copy(currentStep = CreateFamilyStep.INVITE_FAMILY_MEMBERS)
                }
            } catch (e: Exception) {
                _uiState.update { CreateFamilyUiState.Error(e.message?: "Unknown error.") }
            }
        }
    }
}