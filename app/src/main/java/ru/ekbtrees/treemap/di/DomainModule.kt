package ru.ekbtrees.treemap.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import ru.ekbtrees.treemap.domain.interactors.TreesInteractor
import ru.ekbtrees.treemap.domain.interactors.TreesInteractorImpl
import ru.ekbtrees.treemap.domain.interactors.file.FilesInteractor
import ru.ekbtrees.treemap.domain.interactors.file.FilesInteractorImpl
import ru.ekbtrees.treemap.domain.repositories.FileRepository
import ru.ekbtrees.treemap.domain.repositories.TreesRepository

/**
 * Dagger модуль бизнес слоя
 */
@Module
@InstallIn(ViewModelComponent::class)
object DomainModule {
    @Provides
    fun provideTreesInteractor(
        treesRepository: TreesRepository
    ): TreesInteractor {
        return TreesInteractorImpl(treesRepository = treesRepository)
    }

    @Provides
    fun provideFilesInteractor(fileRepository: FileRepository): FilesInteractor {
        return FilesInteractorImpl(fileRepository = fileRepository)
    }
}