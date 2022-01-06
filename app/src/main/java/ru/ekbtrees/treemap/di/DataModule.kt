package ru.ekbtrees.treemap.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import ru.ekbtrees.treemap.data.TreesRepositoryImpl
import ru.ekbtrees.treemap.data.api.TreesApiService
import ru.ekbtrees.treemap.data.files.FilesRepositoryImpl
import ru.ekbtrees.treemap.data.files.api.FilesApiService
import ru.ekbtrees.treemap.domain.repositories.FilesRepository
import ru.ekbtrees.treemap.domain.repositories.TreesRepository
import javax.inject.Singleton

/**
 * Dagger модуль слоя данных
 */
@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    fun provideTreesRepository(treesApiService: TreesApiService): TreesRepository {
        return TreesRepositoryImpl(treesApiService = treesApiService)
    }

    @Singleton
    @Provides
    fun provideCoroutineScope(): CoroutineScope {
        return CoroutineScope(Dispatchers.IO)
    }

    @Provides
    fun provideFilesRepository(
        @ApplicationContext context: Context,
        filesApiService: FilesApiService
    ): FilesRepository {
        return FilesRepositoryImpl(
            context = context as Application,
            apiService = filesApiService
        )
    }
}