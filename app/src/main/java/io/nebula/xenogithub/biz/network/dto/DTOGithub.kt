package io.nebula.xenogithub.biz.network.dto

import com.squareup.moshi.Json


/**
 * Created by nebula on 2025/3/7
 */
data class DTORepoOwner(
    val login: String,
    @Json(name = "avatar_url")
    val avatarUrl: String
)

data class DTORepository(
    val id: Int,
    val name: String,
    @Json(name = "full_name")
    val fullName: String,
    val description: String?,
    val language: String?,
    @Json(name = "stargazers_count")
    val stargazersCount: Int,
    @Json(name = "forks_count")
    val forksCount: Int,
    @Json(name = "owner")
    val owner: DTORepoOwner,
    @Json(name = "updated_at")
    val updatedAt: String
)

data class DTOResult(
    @Json(name = "total_count")
    val size: Int,
    @Json(name = "incomplete_results")
    val incompleteResults: Boolean,
    val items: List<DTORepository>
)

data class DTOAuthToken(
    @Json(name = "access_token")
    val accessToken: String,
    @Json(name = "token_type")
    val tokenType: String,
    val scope: String
)

data class DTOUser(
    val login: String,
    @Json(name = "avatar_url")
    val avatarUrl: String,
    val name: String?,
    val followers: Int,
    val following: Int
)

data class DTOIssueUser(
    val login: String,
    @Json(name = "avatar_url")
    val avatarUrl: String,
    val name: String?,
)

data class DTOIssue(
    val id: Long,
    val number: Int,
    val title: String,
    val body: String?,
    val state: String,
    val user: DTOIssueUser,
)

