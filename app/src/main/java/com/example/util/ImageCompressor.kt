package com.example.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.net.Uri
import android.util.Base64
import java.io.ByteArrayOutputStream

object ImageCompressor {
    const val MAX_FILE_SIZE_BYTES = 350 * 1024 // 350 KB limit

    data class CompressionResult(
        val base64Data: String,
        val sizeInBytes: Int,
        val sizeInKb: Float,
        val width: Int,
        val height: Int
    )

    fun compressBitmap(bitmap: Bitmap): CompressionResult {
        var quality = 90
        var stream = ByteArrayOutputStream()
        
        // Downscale first if larger than 800x800 for speed and small size
        val maxDim = 800
        val scaledBitmap = if (bitmap.width > maxDim || bitmap.height > maxDim) {
            val ratio = bitmap.width.toFloat() / bitmap.height.toFloat()
            val newWidth: Int
            val newHeight: Int
            if (ratio > 1) {
                newWidth = maxDim
                newHeight = (maxDim / ratio).toInt()
            } else {
                newHeight = maxDim
                newWidth = (maxDim * ratio).toInt()
            }
            Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
        } else {
            bitmap
        }

        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
        var bytes = stream.toByteArray()

        while (bytes.size > MAX_FILE_SIZE_BYTES && quality > 20) {
            stream.reset()
            quality -= 15
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
            bytes = stream.toByteArray()
        }

        val base64 = "data:image/jpeg;base64," + Base64.encodeToString(bytes, Base64.NO_WRAP)
        return CompressionResult(
            base64Data = base64,
            sizeInBytes = bytes.size,
            sizeInKb = bytes.size / 1024f,
            width = scaledBitmap.width,
            height = scaledBitmap.height
        )
    }

    fun compressUri(context: Context, uri: Uri): CompressionResult? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val bitmap = BitmapFactory.decodeStream(inputStream)
                bitmap?.let { compressBitmap(it) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun generateAvatarBitmap(name: String, rollNo: String): Bitmap {
        val size = 240
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Clean modern circular badge
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val colors = listOf(0xFF1E3A8A.toInt(), 0xFF0F766E.toInt(), 0xFF6366F1.toInt(), 0xFFD97706.toInt())
        val colorIndex = Math.abs(name.hashCode()) % colors.size
        paint.color = colors[colorIndex]
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint)

        // Initials
        val initials = name.split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .map { it.first().uppercase() }
            .joinToString("")
            .ifEmpty { "ST" }

        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 80f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }

        val yPos = (canvas.height / 2f - (textPaint.descent() + textPaint.ascent()) / 2f)
        canvas.drawText(initials, size / 2f, yPos, textPaint)
        return bitmap
    }
}
