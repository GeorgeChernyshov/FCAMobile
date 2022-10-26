package com.fca.fcamobile.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
class FileProviderModule {

    @Provides
    fun provideGraphFile(
        app: Application
    ) = File(app.filesDir.path + GRAPH_FILENAME)

    companion object {
        private const val GRAPH_FILENAME = "/Graph"
    }
}