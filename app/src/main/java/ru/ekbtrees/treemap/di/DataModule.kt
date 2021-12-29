package ru.ekbtrees.treemap.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.ekbtrees.treemap.data.CommentsRepositorylmpl
import ru.ekbtrees.treemap.data.TreesRepositoryImpl
import ru.ekbtrees.treemap.data.api.CommentApiService
import ru.ekbtrees.treemap.data.api.TreesApiService
import ru.ekbtrees.treemap.domain.interactors.CommentInteractor
import ru.ekbtrees.treemap.domain.repositories.CommentsRepository
import ru.ekbtrees.treemap.domain.repositories.TreesRepository

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

    @Provides
    fun provideCommentsRepository(commentApiService: CommentApiService): CommentsRepository{
        return CommentsRepositorylmpl(commentApiService = commentApiService)
    }
}