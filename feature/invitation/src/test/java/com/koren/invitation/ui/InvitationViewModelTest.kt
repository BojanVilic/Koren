package com.koren.invitation.ui

import com.koren.common.models.family.Family
import com.koren.data.repository.InvitationRepository
import com.koren.domain.GetFamilyUseCase
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class InvitationViewModelTest {

    @MockK
    private lateinit var invitationRepository: InvitationRepository

    @MockK
    private lateinit var getFamilyUseCase: GetFamilyUseCase

    private lateinit var sut: InvitationViewModel

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this, relaxed = true)
    }

    @Nested
    @DisplayName("Given ViewModel initialization")
    inner class ViewModelInitialization {

        @Test
        @DisplayName("WHEN getFamilyUseCase is successful THEN UI state updates with family name")
        fun `when getFamilyUseCase success then ui state updates with family name`() = runTest {
            val familyName = "The Smiths"
            val mockFamily = Family(id = "1", name = familyName)
            coEvery { getFamilyUseCase() } returns Result.success(mockFamily)

            val collectedStates = mutableListOf<InvitationUiState>()

            sut = InvitationViewModel(invitationRepository, getFamilyUseCase)

            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                sut.uiState.toList(collectedStates)
            }

            assertTrue(collectedStates.isNotEmpty(), "Should have collected at least the initial state.")
            val finalState = collectedStates.last()

            assertInstanceOf(InvitationUiState.Shown::class.java, finalState)
            assertEquals(familyName, (finalState as InvitationUiState.Shown).familyName)
            assertNotNull(finalState.eventSink)
        }

        @Test
        @DisplayName("WHEN getFamilyUseCase fails THEN UI state updates with error message")
        fun `when getFamilyUseCase fails then ui state updates with error message`() = runTest {
            val errorMessage = "Failed to fetch family"
            coEvery { getFamilyUseCase() } returns Result.failure(Exception(errorMessage))

            val collectedStates = mutableListOf<InvitationUiState>()

            sut = InvitationViewModel(invitationRepository, getFamilyUseCase)

            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                sut.uiState.toList(collectedStates)
            }

            assertTrue(collectedStates.isNotEmpty(), "Should have collected at least the initial state.")
            val finalState = collectedStates.last()

            assertInstanceOf(InvitationUiState.Error::class.java, finalState)
            assertEquals(errorMessage, (finalState as InvitationUiState.Error).errorMessage)
        }
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
        unmockkAll()
    }
}