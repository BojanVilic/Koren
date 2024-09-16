package com.koren.home.ui.create_family

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedIconButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
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

    var familyName by remember { mutableStateOf("") }
    var familyPortraitPath by remember { mutableStateOf<Uri?>(null) }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            familyPortraitPath = uri
        }
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedIconButton(
                    modifier = Modifier.size(100.dp),
                    onClick = {
                        imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.family_parents_icon),
                        contentDescription = null
                    )
                }

                OutlinedTextField(
                    value = familyName,
                    onValueChange = {
                        familyName = it
                    }
                )

                Button(
                    onClick = { createFamily(familyName, familyPortraitPath) }
                ) {
                    Text(text = stringResource(R.string.create_family_label))
                }
            }
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