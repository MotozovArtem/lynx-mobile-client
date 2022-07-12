package org.lynx.client.http

import org.lynx.client.data.dto.AuthCredentialsRequest
import org.lynx.client.data.dto.AuthTokenResponse
import org.lynx.client.data.dto.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PeerStockServerHttpClient {
    @POST("/token/generate-token")
    suspend fun authentication(@Body credentials: AuthCredentialsRequest): Response<AuthTokenResponse>

    @GET("/user/{id}")
    suspend fun getUserById(@Path("id") userId: String): Response<UserResponse>

    @GET("/user")
    suspend fun getUsers(): Response<List<UserResponse>>
}