package org.lynx.client.http

import org.lynx.client.data.dto.Certificate
import org.lynx.client.data.dto.MessageDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LynxChatHttpClient {
    @POST("/api/message")
    suspend fun sendMessage(@Body message: MessageDto): Response<String>

    @POST("/api/cert")
    suspend fun sendCert(@Body publicKeyAsBase64: Certificate): Response<Certificate>
}