package org.lynx.client.http.server

import android.util.Log
import com.google.gson.Gson
import fi.iki.elonen.NanoHTTPD
import org.lynx.client.data.dto.Certificate
import org.lynx.client.data.dto.MessageDto
import org.lynx.client.data.dto.toDomainFromReceivedMessage
import org.lynx.client.service.CryptographyService
import org.lynx.client.service.MessageService
import org.lynx.client.service.UserService

class WebServer(
    port: Int,
    private val messageService: MessageService,
    private val cryptographyService: CryptographyService,
    private val userService: UserService
) : NanoHTTPD(port) {

    private val TAG = "WebServer"

    override fun serve(session: IHTTPSession?): Response {
        Log.d(TAG, "Received request with URI: ${session?.uri}")
        if (session == null) {
            return newFixedLengthResponse("Session is null. Strange behavior.")
        }
        if (session.method != Method.POST) {
            Log.d(
                TAG,
                "Received non-POST request from ${session.remoteHostName} with IP ${session.remoteIpAddress}"
            )
            return newFixedLengthResponse(
                Response.Status.METHOD_NOT_ALLOWED,
                "application/json",
                """{"status":"Method not allowed"}"""
            )
        }
        return routeRequests(session)
    }

    private fun routeRequests(session: IHTTPSession): Response {
        try {
            return when (session.uri) {
                "/api/file" -> onFile(session)
                "/api/message" -> onMessage(session)
                "/api/cert" -> onCert(session)
                else -> newFixedLengthResponse(
                    Response.Status.BAD_REQUEST,
                    "application/json",
                    """{"status":"Route not found"}"""
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception while processing request ${e.message}", e)
            return newFixedLengthResponse(
                Response.Status.INTERNAL_ERROR,
                "application/json",
                """{"status":"Internal error"}"""
            )
        }
    }

    private fun onFile(session: IHTTPSession): Response {
        val requestBody = mutableMapOf<String, String>()
        session.parseBody(requestBody)
        val receivedMessage = Gson().fromJson(requestBody["postData"], MessageDto::class.java)
        messageService.receiveFile(receivedMessage.toDomainFromReceivedMessage())
        return newFixedLengthResponse("OK")
    }

    private fun onMessage(session: IHTTPSession): Response {
        val requestBody = mutableMapOf<String, String>()
        session.parseBody(requestBody)
        val receivedMessage = Gson().fromJson(requestBody["postData"], MessageDto::class.java)
        messageService.receiveMessage(receivedMessage.toDomainFromReceivedMessage())
        return newFixedLengthResponse("OK")
    }

    private fun onCert(session: IHTTPSession): Response {
        val requestBody = mutableMapOf<String, String>()
        session.parseBody(requestBody)
        val receivedCert = Gson().fromJson(requestBody["postData"], Certificate::class.java)
        cryptographyService.generateSharedKeyForAbonent(
            receivedCert.username,
            receivedCert.publicKey
        )
        val certificate = Certificate(
            userService.authenticatedUser,
            cryptographyService.getX509PublicKeyAsBase64()
        )
        val certificateJson = Gson().toJson(certificate)
        return newFixedLengthResponse(
            Response.Status.OK,
            "application/json",
            certificateJson
        )
    }

}