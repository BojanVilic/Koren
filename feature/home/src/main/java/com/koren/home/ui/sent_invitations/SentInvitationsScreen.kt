package com.koren.home.ui.sent_invitations

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.koren.common.util.Destination
import com.koren.designsystem.theme.LocalScaffoldStateProvider
import com.koren.designsystem.theme.ScaffoldState
import kotlinx.serialization.Serializable

@Serializable
object SentInvitationsDestination : Destination

@Composable
fun SentInvitationsScreen() {

    LocalScaffoldStateProvider.current.setScaffoldState(
        ScaffoldState(
            title = "Sent Invitations",
            isTopBarVisible = true,
            isBottomBarVisible = true
        )
    )

    SentInvitationsContent()
}

@Composable
private fun SentInvitationsContent(

) {
    Text(text = "Sent Invitations")
}