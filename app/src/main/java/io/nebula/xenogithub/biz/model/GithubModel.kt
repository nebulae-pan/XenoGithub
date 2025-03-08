package io.nebula.xenogithub.biz.model

import io.nebula.xenogithub.biz.network.dto.DTOAuthToken
import io.nebula.xenogithub.biz.network.dto.DTOIssue
import io.nebula.xenogithub.biz.network.dto.DTOIssueUser
import io.nebula.xenogithub.biz.network.dto.DTORepoOwner
import io.nebula.xenogithub.biz.network.dto.DTORepository
import io.nebula.xenogithub.biz.network.dto.DTOUser

/**
 * for field mapping and initial process
 *
 * Created by nebula on 2025/3/7
 */
data class RepoOwner(
    val name: String,
    val avatarUrl: String
)

data class Repository(
    val id: Int,
    val name: String,
    val fullName: String,
    val description: String,
    val language: String?,
    val stargazersCount: Int,
    val forksCount: Int,
    val owner: RepoOwner,
    val updatedAt: String
)

data class User(
    val username: String,
    val name: String?,
    val avatarUrl: String,
    val followers: Int,
    val following: Int,
    private var _token: String? = null
) {
    val token: String?
        get() = _token

    internal fun updateToken(token: String) {
        _token = token
    }
}

data class IssueUser(
    val username: String,
    val name: String?,
    val avatarUrl: String,
)

data class AuthToken(
    val accessToken: String,
    val tokenType: String,
    val scope: String
)

data class Issue(
    val id: Long,
    val number: Int,
    val title: String,
    val body: String?,
    val user: IssueUser
)

data class RaiseIssueRequest(
    val title: String,
    val body: String,
)

fun DTORepoOwner.parse() = RepoOwner(
    name = login,
    avatarUrl = avatarUrl
)

fun DTORepository.parse() = Repository(
    id = id,
    name = name,
    fullName = fullName,
    owner = owner.parse(),
    description = description ?: "",
    language = language,
    updatedAt = updatedAt,
    forksCount = forksCount,
    stargazersCount = stargazersCount
)

fun DTOAuthToken.parse() = AuthToken(
    accessToken = accessToken,
    tokenType = tokenType,
    scope = scope
)

fun DTOUser.parse() = User(
    name = name,
    username = login,
    avatarUrl = avatarUrl,
    followers = followers,
    following = following
)

fun DTOIssue.parse() = Issue(
    id = id,
    number = number,
    title = title,
    body = body,
    user = user.parse()
)

fun DTOIssueUser.parse() = IssueUser(
    name = name,
    username = login,
    avatarUrl = avatarUrl,
)