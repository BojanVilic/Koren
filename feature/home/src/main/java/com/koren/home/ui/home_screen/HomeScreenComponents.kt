package com.koren.home.ui.home_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.koren.common.models.Invitation
import com.koren.common.models.UserData
import com.koren.common.models.toHumanReadableDateTime
import com.koren.common.util.Destination
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.ThemePreview
import com.koren.home.R
import com.koren.home.ui.HomeViewModel
import kotlinx.serialization.Serializable

@Serializable
object HomeDestination : Destination

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    logOut: () -> Unit,
    createFamily: () -> Unit,
    inviteFamilyMember: () -> Unit
) {

    val currentUser by homeViewModel.currentUser.collectAsStateWithLifecycle()

    HomeContent(
        logOut = logOut,
        createFamily = createFamily,
        currentUser = currentUser,
        inviteFamilyMember = inviteFamilyMember,
        invitations = homeViewModel.invitations,
        acceptInvitation = homeViewModel::acceptInvitation,
        declineInvitation = homeViewModel::declineInvitation
    )
}

@Composable
private fun HomeContent(
    logOut: () -> Unit,
    createFamily: () -> Unit,
    currentUser: UserData?,
    inviteFamilyMember: () -> Unit,
    invitations: List<Invitation>,
    acceptInvitation: (Invitation) -> Unit,
    declineInvitation: (String) -> Unit
) {
    Column {
        Text(
            text = stringResource(R.string.welcome_back_label, currentUser?.displayName?: ""),
            style = MaterialTheme.typography.labelLarge
        )

        Button(
            onClick = logOut
        ) {
            Text(text = "Log out")
        }

        Button(
            onClick = { createFamily() }
        ) {
            Text("Create a family")
        }

        Button(
            onClick = { inviteFamilyMember() }
        ) {
            Text("Invite a family member")
        }

        LazyColumn {
            items(invitations) { invitation ->
                InvitationCard(
                    invitation = invitation,
                    acceptInvitation = acceptInvitation,
                    declineInvitation = declineInvitation
                )
            }
        }
    }
}

@Composable
private fun InvitationCard(
    invitation: Invitation,
    acceptInvitation: (Invitation) -> Unit,
    declineInvitation: (String) -> Unit
) {
    Card {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Invitation from ${invitation.senderId}")
            Text("Status: ${invitation.status}")
            Text("Expiration date: ${invitation.expirationDate.toHumanReadableDateTime()}")
            Text("Recipient email: ${invitation.recipientEmail}")
        }

        Button(
            onClick = { acceptInvitation(invitation) }
        ) {
            Text("Accept")
        }

        Button(
            onClick = { declineInvitation(invitation.id) }
        ) {
            Text("Decline")
        }
    }
}

@ThemePreview
@Composable
fun HomePreview() {
    KorenTheme {
        HomeContent(
            logOut = {},
            createFamily = {},
            currentUser = UserData(
                id = "",
                displayName = "John Doe",
                email = ""
            ),
            inviteFamilyMember = {},
            invitations = emptyList(),
            acceptInvitation = {},
            declineInvitation = {}
        )
    }
}