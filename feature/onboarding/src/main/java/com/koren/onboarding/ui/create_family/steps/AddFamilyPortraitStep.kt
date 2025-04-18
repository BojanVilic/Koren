package com.koren.onboarding.ui.create_family.steps

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.koren.designsystem.components.dashedBorder
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.ThemePreview
import com.koren.onboarding.R

@Composable
internal fun AddImageStep(
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
            text = stringResource(R.string.add_image_title),
            style = MaterialTheme.typography.displaySmall
        )

        Text(
            text = stringResource(R.string.add_image_subtitle),
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