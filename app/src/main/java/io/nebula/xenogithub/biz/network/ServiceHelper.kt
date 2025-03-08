package io.nebula.xenogithub.biz.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.nebula.xenogithub.biz.network.service.GithubAuthService
import io.nebula.xenogithub.biz.network.service.IGithubAPIService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * Created by nebula on 2025/3/7
 */
object ServiceHelper {
    private const val GITHUB_API_URL = "https://api.github.com"
    private const val GITHUB_URL = "https://github.com"
    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request()
                .newBuilder()
                .addHeader("accept", "application/vnd.github+json")
                .build()
            chain.proceed(request)
        }.build()

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val apiRetrofit = Retrofit.Builder()
        .baseUrl(GITHUB_API_URL)
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(GITHUB_URL)
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val githubAPIService: IGithubAPIService = apiRetrofit.create(IGithubAPIService::class.java)
    val githubService: GithubAuthService = retrofit.create(GithubAuthService::class.java)

}