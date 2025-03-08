package io.nebula.xenogithub.common

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

/**
 * Created by nebula on 2025/3/8
 */
@Module
@InstallIn(SingletonComponent::class)
class CoroutineFactory {

    @Provides
    @Singleton
    fun instanceIODispatchers(): CoroutineDispatcher {
        return Dispatchers.IO
    }
}