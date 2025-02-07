package com.koren.account.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.rounded.AccountBox
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.ui.graphics.vector.ImageVector
import com.koren.designsystem.icon.KorenIcons
import com.koren.designsystem.icon.LogOut
import com.koren.designsystem.icon.Privacy

data class AccountOption(
    val text: String,
    val subText: String? = null,
    val icon: ImageVector? = null,
    val event: AccountUiEvent,
    val isDestructive: Boolean = false,
)

val options = listOf(
    AccountOption(
        text = "Edit profile",
        icon = Icons.Default.Edit,
        event = AccountUiEvent.EditProfile
    ),
    AccountOption(
        text = "Change password",
        icon = Icons.Default.Lock,
        event = AccountUiEvent.ChangePassword
    ),
    AccountOption(
        text = "Notifications",
        icon = Icons.Default.Notifications,
        event = AccountUiEvent.Notifications
    ),
    AccountOption(
        text = "Manage Family",
        subText = "Edit roles or remove members",
        icon = Icons.Rounded.Settings,
        event = AccountUiEvent.LeaveFamily
    ),
    AccountOption(
        text = "Terms of service",
        icon = KorenIcons.Privacy,
        event = AccountUiEvent.TermsOfService
    ),
    AccountOption(
        text = "Privacy",
        icon = KorenIcons.Privacy,
        event = AccountUiEvent.Privacy
    ),
    AccountOption(
        text = "Log out",
        icon = KorenIcons.LogOut,
        event = AccountUiEvent.LogOut
    ),
    AccountOption(
        text = "Leave family",
        subText = "You will be removed from the family",
        icon = Icons.Rounded.Warning,
        event = AccountUiEvent.LeaveFamily,
        isDestructive = true
    ),
    AccountOption(
        text = "Delete family",
        subText = "All members will be removed and data will be lost",
        icon = Icons.Rounded.Delete,
        event = AccountUiEvent.LeaveFamily,
        isDestructive = true
    ),
    AccountOption(
        text = "Delete account",
        subText = "You will lose all your data",
        icon = Icons.Rounded.Delete,
        event = AccountUiEvent.DeleteAccount,
        isDestructive = true
    )
)