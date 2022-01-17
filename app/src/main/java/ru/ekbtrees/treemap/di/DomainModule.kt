package ru.ekbtrees.treemap.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import ru.ekbtrees.treemap.domain.interactors.CommentInteractor
import ru.ekbtrees.treemap.domain.interactors.CommentInteractorlmpl
import ru.ekbtrees.treemap.domain.interactors.TreesInteractor
import ru.ekbtrees.treemap.domain.interactors.TreesInteractorImpl
import ru.ekbtrees.treemap.domain.repositories.CommentsRepository
import ru.ekbtrees.treemap.domain.repositories.TreesRepository

/**
 * Dagger модуль бизнес слоя
 */
@Module
@InstallIn(ViewModelComponent::class)
object DomainModule {
    @Provides
    fun provideTreesInteractor(treesRepository: TreesRepository): TreesInteractor {
        return TreesInteractorImpl(treesRepository)
    }

    @Provides
    fun provideTreeCommentsInteractor(commentsRepository: CommentsRepository): CommentInteractor {
        return CommentInteractorlmpl(commentsRepository)
    }
}