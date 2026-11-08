package com.example.packguide.scanning

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FilterInputStream
import java.io.InputStream

enum class PhotoProblem { Unsupported, TooLarge }
class PhotoReadException(val problem: PhotoProblem) : Exception()

class PhotoScanner {
    suspend fun decode(context: Context, uri: Uri): Bitmap {
        var decoded: Bitmap? = null
        try {
            return withContext(Dispatchers.IO) {
                val resolver = context.contentResolver
                val mime = resolver.getType(uri)
                if (mime != null && !mime.startsWith("image/")) throw PhotoReadException(PhotoProblem.Unsupported)
                fun open(): InputStream = LimitedPhotoStream(resolver.openInputStream(uri) ?: error("Cannot open photo"))
                val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                open().use { BitmapFactory.decodeStream(it, null, bounds) }
                if (bounds.outWidth <= 0 || bounds.outHeight <= 0) throw PhotoReadException(PhotoProblem.Unsupported)
                if (bounds.outWidth.toLong() * bounds.outHeight > 100_000_000L) throw PhotoReadException(PhotoProblem.TooLarge)
                var sample = 1
                while (maxOf(bounds.outWidth, bounds.outHeight) / sample > 1600) sample *= 2
                val options = BitmapFactory.Options().apply { inSampleSize = sample }
                val bitmap = open().use { BitmapFactory.decodeStream(it, null, options) }
                    ?: error("Invalid image")
                try {
                    val exif = open().use { ExifInterface(it) }
                    val matrix = Matrix().apply {
                        if (exif?.isFlipped == true) postScale(-1f, 1f)
                        postRotate((exif?.rotationDegrees ?: 0).toFloat())
                    }
                    val rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                    if (rotated !== bitmap) bitmap.recycle()
                    decoded = rotated
                    rotated
                } catch (error: Throwable) {
                    bitmap.recycle()
                    throw error
                }
            }
        } catch (error: Throwable) {
            // withContext can discard its result when the owning ViewModel is canceled.
            decoded?.recycle()
            throw error
        }
    }

}

/** Applies to content streams too, including providers with no filesystem path or known size. */
private class LimitedPhotoStream(input: InputStream) : FilterInputStream(input) {
    private var consumed = 0L
    private fun count(amount: Long) {
        consumed += amount.coerceAtLeast(0)
        if (consumed > 50L * 1024 * 1024) throw PhotoReadException(PhotoProblem.TooLarge)
    }
    override fun read(): Int = super.read().also { if (it >= 0) count(1) }
    override fun read(buffer: ByteArray, offset: Int, length: Int): Int =
        `in`.read(buffer, offset, length).also { count(it.toLong()) }
    override fun skip(amount: Long): Long = `in`.skip(amount).also(::count)
}
