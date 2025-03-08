package io.nebula.xenogithub.biz.network.service

import io.nebula.xenogithub.biz.network.dto.DTOAuthToken
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

/**
 * Created by nebula on 2025/3/7
 */
const val CLIENT_ID = "Ov23lint3rKGlMy5llyg"
const val CLIENT_SECRET = "4b3e13b4933b584a17fcbc31b64a5f2204e29274" //should be saved in the back-end ..
const val CALL_URL = "xenode://auth/callback"

const val AUTH_URL =
    "https://github.com/login/oauth/authorize?client_id=$CLIENT_ID&redirect_uri=$CALL_URL&scope=user,repo"

interface GithubAuthService {
    @GET("login/oauth/access_token")
    suspend fun getAccessToken(
        @Query("client_id") clientId: String,
        @Query("client_secret") clientSecret: String,
        @Query("code") authCode: String,
        @Header("Content-Type") contentType: String = "application/x-www-form-urlencoded",
    ): DTOAuthToken
}