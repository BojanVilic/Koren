# Koren - Family Location & Management App

Koren is a modern Android application designed to help families stay connected, track locations of family members, and manage household tasks efficiently.

## Features

- **Real-time Location Tracking**: See family members' locations and distances
- **Family Member Management**: View and edit family member roles and information
- **Communication Tools**: Easy access to call home functionality
- **Task Management**: Assign and track tasks for family members
- **Calendar Integration**: Manage family events and schedules
- **Interactive Map**: Find and navigate to family members
- **Account Management**: Personalize your profile and settings
- **Invitation System**: Invite new members to join your family group!

![image (3)](https://github.com/user-attachments/assets/a9e0ab6b-445b-48fc-825d-7d4003400ca3)
![image (2)](https://github.com/user-attachments/assets/ccaa2290-f730-40a8-a46a-c23fcc8da514)
![image (1)](https://github.com/user-attachments/assets/f80f1550-b328-4c4f-897e-262b8805be08)


## Architecture

### Multi-Module Structure

Koren follows a modular architecture pattern that separates concerns and promotes scalability:

```mermaid
%%{
  init: {
    'theme': 'neutral'
  }
}%%

graph LR
  subgraph :core
    :core:designsystem["designsystem"]
    :core:common["common"]
    :core:data["data"]
    :core:domain["domain"]
    :core:notifications["notifications"]
  end
  subgraph :feature
    :feature:activity["activity"]
    :feature:calendar["calendar"]
    :feature:account["account"]
    :feature:auth["auth"]
    :feature:home["home"]
    :feature:onboarding["onboarding"]
    :feature:map["map"]
    :feature:invitation["invitation"]
  end
  :feature:activity --> :core:designsystem
  :feature:activity --> :core:common
  :feature:activity --> :core:data
  :feature:activity --> :core:domain
  :feature:calendar --> :core:designsystem
  :feature:calendar --> :core:common
  :feature:calendar --> :core:domain
  :feature:calendar --> :core:data
  :feature:account --> :core:designsystem
  :feature:account --> :core:common
  :feature:account --> :core:domain
  :feature:account --> :core:data
  :app --> :core:designsystem
  :app --> :core:common
  :app --> :feature:auth
  :app --> :feature:home
  :app --> :feature:onboarding
  :app --> :feature:map
  :app --> :feature:activity
  :app --> :feature:account
  :app --> :feature:invitation
  :app --> :feature:calendar
  :app --> :core:notifications
  :core:notifications --> :core:designsystem
  :core:notifications --> :core:common
  :core:notifications --> :core:domain
  :feature:onboarding --> :core:designsystem
  :feature:onboarding --> :core:common
  :feature:onboarding --> :core:domain
  :core:data --> :core:common
  :feature:auth --> :core:designsystem
  :feature:auth --> :core:common
  :feature:auth --> :core:data
  :feature:auth --> :core:domain
  :feature:home --> :core:designsystem
  :feature:home --> :core:common
  :feature:home --> :core:data
  :feature:home --> :core:domain
  :core:domain --> :core:common
  :core:domain --> :core:data
  :feature:invitation --> :core:designsystem
  :feature:invitation --> :core:common
  :feature:invitation --> :core:data
  :feature:invitation --> :core:domain
  :feature:map --> :core:designsystem
  :feature:map --> :core:common
  :feature:map --> :core:domain
  :feature:map --> :core:data
```

### UiStateManager Pattern

The app implements a custom UI state management pattern inspired by Slack's Circuit framework, defined in the `common` module:

#### Key Components

- **UiState**: Interface representing the current state of a screen
- **UiEvent**: Interface for user interactions (button clicks, text inputs, etc.)
- **UiSideEffect**: Interface for one-time events (navigation, toasts, etc.)
- **EventHandler**: Interface that combines state with event handling capabilities
- **StateViewModel**: Base ViewModel class implementing the pattern

#### How It Works

1. **State Management**: Each screen has a dedicated ViewModel extending `StateViewModel` that maintains UI state
2. **Event Handling**: Events are passed to the ViewModel via a lambda function (`eventSink`) embedded in the state
3. **Side Effects**: One-time actions are emitted via `SharedFlow` and collected in the UI
4. **Type Safety**: The pattern ensures type safety through generics and reified type parameters

#### Example Usage

```kotlin
// Define states, events, and side effects
sealed interface MyScreenUiState : UiState {
    data object Loading : MyScreenUiState
    data class Shown(
        val data: List<Item>,
        override val eventSink: (MyScreenUiEvent) -> Unit
    ) : MyScreenUiState, EventHandler<MyScreenUiEvent>
}

sealed interface MyScreenUiEvent : UiEvent {
    data object RefreshClicked : MyScreenUiEvent
    data class ItemClicked(val id: String) : MyScreenUiEvent
}

sealed interface MyScreenUiSideEffect : UiSideEffect {
    data class NavigateToDetail(val id: String) : MyScreenUiSideEffect
}

@HiltViewModel
class MyScreenViewModel @Inject constructor(
    ...
): StateViewModel<MyScreenUiEvent, MyScreenUiState, MyScreenUiSideEffect>() {
    
  override fun setInitialState(): MyScreenUiState = MyScreenUiState.Loading

  override fun handleEvent(event: MyScreenUiEvent) {
    withEventfulState<MyScreenUiEvent.Shown> { current ->
        when (event) {
            is MyScreenUiEvent.RefreshClicked -> Unit
            is MyScreenUiEvent.ItemClicked -> {
               _sideEffects.emitSuspended(MyScreenUiSideEffect.NavigateToDetail(event.id))
            }
        }
    }
}

// In the UI
@Composable
fun MyScreen(viewModel: MyScreenViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CollectSideEffects(viewModel) { effect ->
        when (effect) {
            is MyScreenUiSideEffect.NavigateToDetail -> navigateToDetail(effect.id)
        }
    }

    when (val state = uiState) {
        is MyScreenUiState.Loading -> LoadingIndicator()
        is MyScreenUiState.Success -> {
            MyScreenContent(
                items = state.data,
                onItemClick = { id -> state.eventSink(MyScreenUiEvent.ItemClicked(id)) },
                onRefresh = { state.eventSink(MyScreenUiEvent.RefreshClicked) }
            )
        }
    }
}
```

####  Technical Stack:
- **Language:** Kotlin
- **UI Framework:** Jetpack Compose
- **Architecture:** MVVM with UiStateManager pattern
- **Dependency Injection:** Hilt
- **Build System:** Gradle (Kotlin DSL)
- **Concurrency:** Kotlin Coroutines & Flow
