package com.citizenconnect.app.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

object ImageUtils {
    fun compressImage(context: Context, imageUri: Uri): Uri? {
        return try {
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            context.contentResolver.openInputStream(imageUri)?.use { 
                BitmapFactory.decodeStream(it, null, options)
            }

            options.inSampleSize = calculateInSampleSize(options, 1024, 1024)
            options.inJustDecodeBounds = false

            val originalBitmap = context.contentResolver.openInputStream(imageUri)?.use {
                BitmapFactory.decodeStream(it, null, options)
            } ?: return null

            val scaledBitmap = scaleBitmap(originalBitmap)
            
            val compressedFile = File(context.cacheDir, "compressed_${System.currentTimeMillis()}.jpg")
            FileOutputStream(compressedFile).use { out ->
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out)
            }
            
            Uri.fromFile(compressedFile)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    private fun scaleBitmap(bitmap: Bitmap): Bitmap {
        val maxWidth = 1024
        val maxHeight = 1024
        val width = bitmap.width
        val height = bitmap.height
        
        if (width <= maxWidth && height <= maxHeight) return bitmap

        val ratio: Float = width.toFloat() / height.toFloat()
        val newWidth: Int
        val newHeight: Int

        if (ratio > 1) {
            newWidth = maxWidth
            newHeight = (maxWidth / ratio).toInt()
        } else {
            newHeight = maxHeight
            newWidth = (maxHeight * ratio).toInt()
        }

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }
}
