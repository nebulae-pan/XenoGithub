package io.nebula.xenogithub.biz.network.service

import io.nebula.xenogithub.biz.model.RaiseIssueRequest
import io.nebula.xenogithub.biz.network.dto.DTOIssue
import io.nebula.xenogithub.biz.network.dto.DTORepository
import io.nebula.xenogithub.biz.network.dto.DTOResult
import io.nebula.xenogithub.biz.network.dto.DTOUser
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by nebula on 2025/3/7
 */
interface IGithubAPIService {
    @GET("search/repositories")
    suspend fun searchRepositories(
        @Query("q") query: String,
        @Query("sort") sort: String = "stars",
        @Query("order") order: String = "desc",
        @Query("per_page") perPage: Int = 30,
        @Query("page") page: Int = 1,
    ): DTOResult

    // https://docs.github.com/en/rest/repos/repos?apiVersion=2022-11-28#list-repositories-for-the-authenticated-user
    @GET("user")
    suspend fun getAuthedUser(
        @Header("Authorization") auth: String?
    ): DTOUser

    @GET("user/repos")
    suspend fun loadReposFromUser(
        @Header("Authorization") auth: String?,
        @Query("sort") sort: String = "stared",
    ): List<DTORepository>

    //https://docs.github.com/en/rest/issues/issues?apiVersion=2022-11-28#create-an-issue
    @GET("repos/{owner}/{repo}/issues")
    suspend fun listIssues(
        @Header("Authorization") auth: String?,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
    ): List<DTOIssue>

    @POST("repos/{owner}/{repo}/issues")
    suspend fun raiseIssue(
        @Header("Authorization") auth: String?,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Body issue: RaiseIssueRequest
    ): DTOIssue
}