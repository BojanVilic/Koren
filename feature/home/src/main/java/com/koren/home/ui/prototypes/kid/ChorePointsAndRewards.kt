@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.koren.home.ui.prototypes.kid

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.koren.designsystem.theme.KorenTheme // Assuming KorenTheme is defined
import com.koren.designsystem.theme.ThemePreview // Assuming ThemePreview is defined

// --- Data Models (Firebase-friendly structure) ---

// Represents a reward item that can be redeemed with points
data class RewardItem(
    val id: String? = null, // Firebase key
    val name: String = "",
    val description: String? = null,
    val pointsCost: Int = 0,
    val type: String = "digital_item", // e.g., "digital_icon", "screen_time_minutes", "allowance_amount", "chore_pass"
    val value: String? = null, // e.g., "30" for screen time, "5.00" for allowance, "clean_toilet_chore_id"
    val iconUrl: String? = null // URL for a custom reward icon
)

// UserPoints data class is already defined in the Task prototype

// Represents a reward that a user has redeemed
data class RedeemedReward(
    val id: String? = null, // Firebase key
    val userId: String = "",
    val rewardId: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "pending" // e.g., "pending", "approved", "delivered", "used"
)


// --- Composable Prototypes ---

/**
 * Prototype screen for the Points and Rewards Store.
 */
@Composable
fun RewardsStoreScreenPrototype(
    currentPoints: Int,
    availableRewards: List<RewardItem>,
    onRedeemRewardClick: (RewardItem) -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rewards Store") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Your Points:", style = MaterialTheme.typography.titleMedium)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.Star, contentDescription = "Points", tint = MaterialTheme.colorScheme.primary)
                        Text("$currentPoints", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            Text("Available Rewards", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2), // Two items per row
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(availableRewards) { reward ->
                    RewardItemCard(
                        reward = reward,
                        canAfford = currentPoints >= reward.pointsCost,
                        onRedeemClick = onRedeemRewardClick
                    )
                }
            }
        }
    }
}

@Composable
fun RewardItemCard(
    reward: RewardItem,
    canAfford: Boolean,
    onRedeemClick: (RewardItem) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = canAfford) { onRedeemClick(reward) },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (canAfford) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Reward Icon/Image (Placeholder)
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = if (canAfford) 1f else 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                val rewardIcon = when (reward.type) {
                    "screen_time_minutes" -> Icons.Default.Settings
                    "allowance_amount" -> Icons.Default.Done
                    "chore_pass" -> Icons.Default.Search
                    "digital_icon" -> Icons.Default.Face // Trophy/Prize icon
                    else -> Icons.Default.Star // Default icon
                }
                Icon(rewardIcon, contentDescription = reward.name, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = if (canAfford) 1f else 0.5f))
            }
            Spacer(Modifier.height(8.dp))
            Text(reward.name, style = MaterialTheme.typography.titleSmall, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
            reward.description?.let {
                Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
            }
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = "Points Cost", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary.copy(alpha = if (canAfford) 1f else 0.5f))
                Text(
                    "${reward.pointsCost}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = if (canAfford) 1f else 0.5f),
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
            if (!canAfford) {
                Text("Not enough points", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 4.dp))
            }
        }
    }
}


// --- Previews ---

@ThemePreview
@Composable
fun PreviewRewardsStoreScreen() {
    KorenTheme {
        Surface {
            RewardsStoreScreenPrototype(
                currentPoints = 120,
                availableRewards = listOf(
                    RewardItem(id = "r1", name = "30 mins Screen Time", pointsCost = 100, type = "screen_time_minutes", value = "30"),
                    RewardItem(id = "r2", name = "$5 Allowance", pointsCost = 250, type = "allowance_amount", value = "5.00"),
                    RewardItem(id = "r3", name = "Skip 1 Chore", pointsCost = 150, type = "chore_pass", description = "Get out of one assigned chore."),
                    RewardItem(id = "r4", name = "Cool Profile Icon", pointsCost = 50, type = "digital_icon", iconUrl = "url_to_icon"),
                    RewardItem(id = "r5", name = "Extra Dessert", pointsCost = 75, description = "Choose an extra dessert tonight.")
                ),
                onRedeemRewardClick = {},
                onBackClick = {}
            )
        }
    }
}