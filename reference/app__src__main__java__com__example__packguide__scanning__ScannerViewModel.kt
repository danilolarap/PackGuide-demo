package com.example.packguide.scanning

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import com.example.packguide.ui.screens.LuggagePreset

data class ScanState(
    val loading: Boolean = false,
    val photo: Bitmap? = null,
    val photoUri: Uri? = null,
    val message: String? = null
)

class ScannerViewModel(application: Application) : AndroidViewModel(application) {
    private val states = mutableStateMapOf<LuggagePreset, ScanState>()
    private val jobs = mutableMapOf<LuggagePreset, Job>()
    fun stateFor(type: LuggagePreset): ScanState = states[type] ?: ScanState()
    private val scanner = PhotoScanner()

    fun message(text: String, type: LuggagePreset) { states[type] = stateFor(type).copy(message = text) }
    fun clearPhoto(type: LuggagePreset) {
        jobs.remove(type)?.cancel()
        states.remove(type)
    }
    fun clearPhotos() {
        jobs.values.forEach { it.cancel() }
        jobs.clear()
        states.clear()
    }
    fun selectPhoto(uri: Uri, type: LuggagePreset) {
        if (stateFor(type).loading) return
        states[type] = stateFor(type).copy(loading = true, message = null)
        jobs[type] = viewModelScope.launch {
            try {
                val photo = scanner.decode(getApplication(), uri)
                states[type] = ScanState(photo = photo, photoUri = uri)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: OutOfMemoryError) {
                states[type] = stateFor(type).copy(loading = false, message = "La foto es demasiado grande. Elige una imagen más pequeña.")
            } catch (error: Exception) {
                val message = when ((error as? PhotoReadException)?.problem) {
                    PhotoProblem.Unsupported -> "Ese archivo no es una foto compatible. Elige una imagen JPG, PNG o WebP."
                    PhotoProblem.TooLarge -> "La foto es demasiado grande. Elige una imagen más pequeña."
                    null -> "No se pudo abrir la foto. Elige otra imagen de la maleta."
                }
                states[type] = stateFor(type).copy(loading = false, message = message)
            } finally {
                if (jobs[type] == coroutineContext[Job]) jobs.remove(type)
            }
        }
    }

    fun selectPhoto(bitmap: Bitmap, type: LuggagePreset) {
        if (!stateFor(type).loading) states[type] = ScanState(photo = bitmap)
    }
}
