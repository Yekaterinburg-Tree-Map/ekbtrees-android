package ru.ekbtrees.treemap.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.ekbtrees.treemap.data.TreesRepositoryImpl
import ru.ekbtrees.treemap.domain.repositories.TreesRepository

/**
 * Dagger модуль слоя данных
 */
@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    fun provideTreesRepository(@ApplicationContext context: Context): TreesRepository {
        return TreesRepositoryImpl(context)
    }
}