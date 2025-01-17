package eu.kanade.domain.category.anime.interactor

import eu.kanade.domain.category.anime.repository.AnimeCategoryRepository
import eu.kanade.domain.category.model.Category
import eu.kanade.domain.category.model.anyWithName
import eu.kanade.domain.library.service.LibraryPreferences
import eu.kanade.tachiyomi.util.lang.withNonCancellableContext
import eu.kanade.tachiyomi.util.system.logcat
import logcat.LogPriority

class CreateAnimeCategoryWithName(
    private val categoryRepository: AnimeCategoryRepository,
    private val preferences: LibraryPreferences,
) {

    private val initialFlags: Long
        get() {
            val sort = preferences.librarySortingMode().get()
            return preferences.libraryDisplayMode().get().flag or
                sort.type.flag or
                sort.direction.flag
        }

    suspend fun await(name: String): Result = withNonCancellableContext {
        val categories = categoryRepository.getAllAnimeCategories()
        if (categories.anyWithName(name)) {
            return@withNonCancellableContext Result.NameAlreadyExistsError
        }

        val nextOrder = categories.maxOfOrNull { it.order }?.plus(1) ?: 0
        val newCategory = Category(
            id = 0,
            name = name,
            order = nextOrder,
            flags = initialFlags,
        )

        try {
            categoryRepository.insertAnimeCategory(newCategory)
            Result.Success
        } catch (e: Exception) {
            logcat(LogPriority.ERROR, e)
            Result.InternalError(e)
        }
    }

    sealed class Result {
        object Success : Result()
        object NameAlreadyExistsError : Result()
        data class InternalError(val error: Throwable) : Result()
    }
}
