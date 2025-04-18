package com.koren.onboarding.ui.create_family

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.koren.designsystem.theme.KorenTheme
import com.koren.designsystem.theme.ThemePreview
import com.koren.onboarding.R

@Composable
internal fun AddNameStep(
    familyName: String,
    setFamilyName: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            modifier = Modifier.padding(bottom = 16.dp),
            text = stringResource(R.string.add_family_name_title),
            style = MaterialTheme.typography.displaySmall
        )

        Text(
            text = stringResource(R.string.add_family_name_subtitle),
            style = MaterialTheme.typography.titleMedium
        )

        OutlinedTextField(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 32.dp)
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally),
            value = familyName,
            onValueChange = {
                setFamilyName(it)
            },
            label = {
                Text(text = stringResource(R.string.family_name_label))
            },
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            trailingIcon = if (familyName.isNotBlank()) {
                {
                    IconButton(
                        onClick = {
                            setFamilyName("")
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Clear,
                            contentDescription = null
                        )
                    }
                }
            } else null
        )
    }
}

@ThemePreview
@Composable
fun AddNameStepPreview() {
    KorenTheme {
        AddNameStep(
            familyName = "",
            setFamilyName = {}
        )
    }
}