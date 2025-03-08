package io.nebula.xenogithub.biz.manager

import io.nebula.xenogithub.biz.model.User

/**
 * Created by nebula on 2025/3/8
 */
interface IAccountManager {
    fun currentAccount(): User?
    fun update(user: User?)
}