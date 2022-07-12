package org.lynx.client.di

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import okhttp3.OkHttpClient
import okhttp3.Request
import org.lynx.client.data.LynxDatabase
import org.lynx.client.data.repository.AbonentKeyRepository
import org.lynx.client.data.repository.KeyRepository
import org.lynx.client.data.repository.MessageRepository
import org.lynx.client.data.repository.UserRepository
import org.lynx.client.http.LynxChatHttpClient
import org.lynx.client.http.PeerStockServerHttpClient
import org.lynx.client.http.server.WebServer
import org.lynx.client.service.AuthenticationService
import org.lynx.client.service.CryptographyService
import org.lynx.client.service.CryptographyServiceImpl
import org.lynx.client.service.MessageService
import org.lynx.client.service.MessageServiceImpl
import org.lynx.client.service.UserService
import org.lynx.client.service.UserServiceImpl
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.net.Proxy
import java.util.concurrent.TimeUnit

interface AppContainer {
    var authenticationService: AuthenticationService?
    var httpClientBuilder: OkHttpClient.Builder
    var httpClient: OkHttpClient?
    var peerStockServerHttpClient: PeerStockServerHttpClient?
    var lynxChatHttpClient: LynxChatHttpClient?
    var proxy: Proxy?
    var retrofit: Retrofit?
    var retrofitBuilder: Retrofit.Builder
    var isHttpClientInitialized: Boolean
    var isRetrofitClientInitialized: Boolean
    val userService: UserService
    val messageRepository: MessageRepository
    val userRepository: UserRepository
    val keyRepository: KeyRepository
    val abonentKeyRepository: AbonentKeyRepository
    val applicationScope: CoroutineScope
    val cryptographyService: CryptographyService
    val messageService: MessageService
    val applicationServer: WebServer
}

class AppContainerImpl(private val applicationContext: Context) : AppContainer {

    override val applicationScope = CoroutineScope(SupervisorJob())

    override var proxy: Proxy? = null

    override var httpClientBuilder: OkHttpClient.Builder = OkHttpClient()
        .newBuilder()
        .readTimeout(3, TimeUnit.MINUTES)
        .connectTimeout(3, TimeUnit.MINUTES)
        .addInterceptor { chain ->
            val original: Request = chain.request()
            val request: Request = original.newBuilder()
                .header("Accept", "application/json")
                .method(original.method, original.body)
                .build()
            chain.proceed(request)
        }

    override var httpClient: OkHttpClient? = null

    override var retrofitBuilder: Retrofit.Builder = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())

    override var retrofit: Retrofit? = null

    override var peerStockServerHttpClient: PeerStockServerHttpClient? = null

    override var lynxChatHttpClient: LynxChatHttpClient? = null

    private val db: LynxDatabase =
        LynxDatabase.getDatabase(applicationContext, applicationScope)

    override val userService: UserService = UserServiceImpl("")

    override val messageRepository: MessageRepository by lazy {
        MessageRepository(
            db.messageDao(),
            userService
        )
    }

    override val userRepository: UserRepository by lazy {
        UserRepository(
            peerStockServerHttpClient!!,
            db.userDao()
        )
    }

    override val abonentKeyRepository: AbonentKeyRepository by lazy { AbonentKeyRepository(db.abonentKeyDao()) }

    override val keyRepository: KeyRepository by lazy { KeyRepository(db.keyDao()) }

    override var authenticationService: AuthenticationService? = null

    override var isHttpClientInitialized: Boolean = false

    override var isRetrofitClientInitialized: Boolean = false

    override val cryptographyService: CryptographyService by lazy {
        CryptographyServiceImpl(
            applicationContext,
            keyRepository,
            abonentKeyRepository,
            applicationScope
        )
    }

    override val messageService: MessageService by lazy {
        MessageServiceImpl(messageRepository, applicationScope, this, cryptographyService)
    }

    override val applicationServer: WebServer =
        WebServer(8888, messageService, cryptographyService, userService)

}