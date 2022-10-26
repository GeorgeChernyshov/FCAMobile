package com.fca.fcamobile.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
class CoroutinesModule {

    @Provides
    fun provideCoroutineDispatcher() = Dispatchers.IO

    @Provides
    fun provideExternalScope(
        dispatcher: CoroutineDispatcher
    ): CoroutineScope = CoroutineScope(dispatcher)
}