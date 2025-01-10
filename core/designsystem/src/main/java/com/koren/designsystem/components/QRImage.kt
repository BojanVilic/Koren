package com.koren.designsystem.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import qrcode.QRCode
import qrcode.color.Colors

@Composable
fun QRCodeImage(
    modifier: Modifier = Modifier,
    data: String,
    color: Int = Colors.BLACK,
    backgroundColor: Int = Colors.WHITE
) {
    val qrCodeBitmap = remember(data) {
        generateQrCodeBitmap(data, color, backgroundColor)
    }

    qrCodeBitmap?.let { bitmap ->
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "QR Code",
            modifier = modifier
        )
    }
}

private fun generateQrCodeBitmap(data: String, color: Int, backgroundColor: Int): Bitmap? {
    return try {
        val qrCode = QRCode.ofSquares()
            .withColor(color)
            .withBackgroundColor(backgroundColor)
            .build(data)
        val qrCodeImage = qrCode.render().getBytes()
        BitmapFactory.decodeByteArray(qrCodeImage, 0, qrCodeImage.size)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

