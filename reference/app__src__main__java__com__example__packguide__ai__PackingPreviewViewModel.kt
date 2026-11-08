package com.example.packguide.ai

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.packguide.scanning.PhotoScanner
import com.example.packguide.scanning.PhotoReadException
import com.example.packguide.scanning.PhotoProblem
import com.example.packguide.settings.AppLanguage
import com.example.packguide.ui.screens.LuggagePreset
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import java.io.IOException

enum class PreviewProblem { Photo, UnsupportedPhoto, LargePhoto, Generation, Connection, Cancelled }
enum class PreviewWork { Photos, Guide, Review }

data class PackingPreviewState(
    val work: PreviewWork? = null,
    val guide: Bitmap? = null,
    val guidePlan: PackingPlan? = null,
    val guideSource: Bitmap? = null,
    val photos: List<Bitmap> = emptyList(),
    val review: Bitmap? = null,
    val reviewPlan: PackingPlan? = null,
    val problem: PreviewProblem? = null
)

class PackingPreviewViewModel(application: Application, private val service: PackingImageService) : AndroidViewModel(application) {
    constructor(application: Application) : this(application, OfflinePackingImageService())

    private val states = mutableStateMapOf<LuggagePreset, PackingPreviewState>()
    private val jobs = mutableMapOf<LuggagePreset, Job>()
    fun stateFor(type: LuggagePreset) = states[type] ?: PackingPreviewState()

    fun retain(types: List<LuggagePreset>) {
        (states.keys.toList() - types.toSet()).forEach { type ->
            jobs.remove(type)?.cancel()
            states.remove(type)
        }
    }

    fun clear() {
        jobs.values.forEach { it.cancel() }
        jobs.clear()
        states.clear()
    }

    fun cancel(type: LuggagePreset) {
        jobs.remove(type)?.cancel()
        states[type] = stateFor(type).copy(work = null, problem = PreviewProblem.Cancelled)
    }

    fun photoError(type: LuggagePreset) { states[type] = stateFor(type).copy(problem = PreviewProblem.Photo) }

    fun removePhoto(type: LuggagePreset, index: Int) {
        val state = stateFor(type)
        if (state.work != null) return
        states[type] = state.copy(photos = state.photos.filterIndexed { i, _ -> i != index },
            review = null, reviewPlan = null, problem = null)
    }

    fun addPhotos(type: LuggagePreset, uris: List<Uri>) {
        val slots = 4 - stateFor(type).photos.size
        if (slots <= 0 || uris.isEmpty()) return // Closing the picker preserves the previous selection.
        launch(type, PreviewWork.Photos) {
            val photos = mutableListOf<Bitmap>()
            try {
                for (uri in uris.take(slots)) photos += PhotoScanner().decode(getApplication(), uri)
                currentCoroutineContext().ensureActive()
                states[type] = stateFor(type).copy(photos = stateFor(type).photos + photos, review = null, reviewPlan = null)
            } catch (error: Throwable) {
                photos.forEach { it.recycle() }
                throw error
            }
        }
    }

    fun addPhoto(type: LuggagePreset, photo: Bitmap) {
        val state = stateFor(type)
        if (state.work != null || state.photos.size >= 4 || photo.isRecycled) return
        states[type] = state.copy(photos = state.photos + photo, review = null, reviewPlan = null, problem = null)
    }

    fun generate(type: LuggagePreset, plan: PackingPlan, source: Bitmap, language: AppLanguage, review: Boolean = false) {
        if (plan.contents.isEmpty()) return
        launch(type, if (review) PreviewWork.Review else PreviewWork.Guide) {
            val image = service.generate(plan, source, language)
            currentCoroutineContext().ensureActive()
            states[type] = if (review) stateFor(type).copy(review = image, reviewPlan = plan)
                else stateFor(type).copy(guide = image, guidePlan = plan, guideSource = source)
        }
    }

    private fun launch(type: LuggagePreset, work: PreviewWork, block: suspend () -> Unit) {
        if (stateFor(type).work != null) return
        states[type] = stateFor(type).copy(work = work, problem = null)
        val job = viewModelScope.launch(start = CoroutineStart.LAZY) {
            try { block() }
            catch (cancelled: CancellationException) { throw cancelled }
            catch (_: OutOfMemoryError) { states[type] = stateFor(type).copy(problem = PreviewProblem.LargePhoto) }
            catch (error: Exception) {
                val problem = when {
                    error is PhotoReadException && error.problem == PhotoProblem.TooLarge -> PreviewProblem.LargePhoto
                    error is PhotoReadException && error.problem == PhotoProblem.Unsupported -> PreviewProblem.UnsupportedPhoto
                    work == PreviewWork.Photos -> PreviewProblem.Photo
                    error is IOException -> PreviewProblem.Connection
                    else -> PreviewProblem.Generation
                }
                states[type] = stateFor(type).copy(problem = problem)
            } finally {
                if (jobs[type] == coroutineContext[Job]) {
                    states[type] = stateFor(type).copy(work = null)
                    jobs.remove(type)
                }
            }
        }
        jobs[type] = job
        job.start()
    }
}
