package io.nebula.xenogithub.biz.network

import io.nebula.xenogithub.biz.model.RaiseIssueRequest
import io.nebula.xenogithub.biz.model.Repository
import io.nebula.xenogithub.biz.model.User
import io.nebula.xenogithub.biz.model.parse
import io.nebula.xenogithub.biz.network.service.CLIENT_ID
import io.nebula.xenogithub.biz.network.service.CLIENT_SECRET
import java.time.LocalDate

/**
 * biz encapsulation
 * Created by nebula on 2025/3/7
 */
object XenoNet {
    suspend fun retrieveTrendingRepos(): Result<List<Repository>> {
        val query = buildString {
            append("created:>${LocalDate.now().minusWeeks(1)}")
        }
        return reposCommonSearch(query, null)
    }

    // https://docs.github.com/en/search-github/searching-on-github/searching-for-repositories
    suspend fun reposCommonSearch(
        query: String?,
        language: String?,
        sort: String = "starts"
    ): Result<List<Repository>> = kotlin.runCatching {
        val searchQ = buildString {
            append("stars:>0 ")
            query?.let { append("$query ") }
            language?.let { append("language:$it") }
        }
        ServiceHelper.githubAPIService.searchRepositories(searchQ, sort)
            .items.map { it.parse() }
    }

    suspend fun refresh(token: String) = kotlin.runCatching {
        ServiceHelper.githubAPIService.getAuthedUser(typedToken(token)).parse()
    }

    suspend fun loadReposFromUser(token: String) = kotlin.runCatching {
        ServiceHelper.githubAPIService.loadReposFromUser(typedToken(token)).map { it.parse() }
    }

    suspend fun loadIssuesForRepo(token: String, owner: String, repo: String) = kotlin.runCatching {
        ServiceHelper.githubAPIService.listIssues(typedToken(token), owner, repo).map { it.parse() }
    }

    suspend fun raiseIssue(
        token: String,
        owner: String,
        repo: String,
        title: String,
        body: String
    ) = kotlin.runCatching {
        ServiceHelper.githubAPIService.raiseIssue(
            typedToken(token),
            owner,
            repo,
            RaiseIssueRequest(title, body)
        ).parse()
    }

    suspend fun oauth(authCode: String): Result<User> = kotlin.runCatching {
        val token = ServiceHelper.githubService.getAccessToken(
            clientId = CLIENT_ID,
            clientSecret = CLIENT_SECRET,
            authCode = authCode
        ).parse()
        val user = ServiceHelper.githubAPIService
            .getAuthedUser("${token.tokenType} ${token.accessToken}")
            .parse()
        user.apply {
            user.updateToken(token.accessToken)
        }
    }

    private fun typedToken(token: String) = "Bearer $token"
}