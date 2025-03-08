package io.nebula.xenogithub.biz.manager

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.nebula.xenogithub.biz.model.User
import javax.inject.Singleton

/**
 * Created by nebula on 2025/3/8
 */

@Module
@InstallIn(SingletonComponent::class)
class AccountManagerImpl : IAccountManager {
    private var current: User? = null

    @Provides
    @Singleton
    fun instance(): IAccountManager {
        return AccountManagerImpl()
    }

    override fun currentAccount(): User? {
        return current
    }

    override fun update(user: User?) {
        current = user
    }

}