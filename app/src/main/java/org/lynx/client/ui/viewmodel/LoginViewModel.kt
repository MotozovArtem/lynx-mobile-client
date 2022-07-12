package org.lynx.client.ui.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.Request
import org.lynx.client.di.AppContainer
import org.lynx.client.data.dto.AuthTokenResponse
import org.lynx.client.http.PeerStockServerHttpClient
import org.lynx.client.service.AuthenticationServiceImpl
import retrofit2.Response

class LoginViewModel(var appContainer: AppContainer) : ViewModel() {
    private val TAG = "LoginViewModel"

    val authenticated: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }

    fun initAndAuthenticate(username: String, password: String, domain: String) {
        viewModelScope.launch {
            initializeAuthenticationService(domain)
            authenticate(username, password)
        }
    }

    suspend fun initializeAuthenticationService(domain: String) {
        appContainer.authenticationService = AuthenticationServiceImpl(appContainer)
        appContainer.authenticationService!!.initializeNetworkServices(domain)
    }

    suspend fun authenticate(username: String, password: String) {
        val result: Response<AuthTokenResponse>? =
            appContainer.authenticationService?.authentication(username, password)
        when (result) {
            is Response<AuthTokenResponse> -> {
                Log.i(TAG, "Authenticated: ${result.body()?.username} ${result.body()?.token}")
                appContainer.httpClientBuilder.addInterceptor { chain: Interceptor.Chain ->
                    val original: Request = chain.request()
                    val request: Request =
                        original.newBuilder()
                            .header("Authorization", "Bearer ${result.body()?.token}")
                            .method(original.method, original.body).build()
                    chain.proceed(request)
                }
                appContainer.httpClient = appContainer.httpClientBuilder.build()
                appContainer.retrofit =
                    appContainer.retrofitBuilder.client(appContainer.httpClient!!).build()
                appContainer.peerStockServerHttpClient =
                    appContainer.retrofit?.create(PeerStockServerHttpClient::class.java)
                appContainer.authenticationService!!.authenticated.set(true)
                authenticated.value = true
                appContainer.userService.authenticatedUser = result.body()!!.username
            }
            else -> Log.e(TAG, "Authentication failed")
        }
    }
}