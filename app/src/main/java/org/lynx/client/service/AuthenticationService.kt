package org.lynx.client.service

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.lynx.client.di.AppContainer
import org.lynx.client.data.dto.AuthCredentialsRequest
import org.lynx.client.data.dto.AuthTokenResponse
import org.lynx.client.http.PeerStockServerHttpClient
import retrofit2.Response
import java.net.InetSocketAddress
import java.net.Proxy

interface AuthenticationService {
    val authenticated: ObservableBoolean
    val initialized: ObservableBoolean
    suspend fun initializeNetworkServices(domain: String)
    suspend fun authentication(username: String, password: String): Response<AuthTokenResponse>
}

class AuthenticationServiceImpl(private val appContainer: AppContainer) : ViewModel(), AuthenticationService {

    override val initialized = ObservableBoolean(false)

    override val authenticated = ObservableBoolean(false)

    override suspend fun initializeNetworkServices(domain: String) {
        withContext(Dispatchers.IO) {
            val proxy = Proxy(Proxy.Type.SOCKS, InetSocketAddress("localhost", 9050))
            appContainer.httpClient = appContainer.httpClientBuilder.proxy(proxy).build()
            appContainer.isHttpClientInitialized = true
            appContainer.retrofitBuilder.baseUrl("http://$domain").client(appContainer.httpClient!!)
            appContainer.retrofit = appContainer.retrofitBuilder.build()
            appContainer.isRetrofitClientInitialized = true
            appContainer.peerStockServerHttpClient = appContainer.retrofit?.create(PeerStockServerHttpClient::class.java)!!
            initialized.set(true)
        }
    }

    override suspend fun authentication(username: String, password: String): Response<AuthTokenResponse> {
        return withContext(Dispatchers.IO) {
            val credentials = AuthCredentialsRequest(username, password)
            appContainer.peerStockServerHttpClient!!.authentication(credentials)
        }
    }


}