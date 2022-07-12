package org.lynx.client

import android.app.Application
import com.google.crypto.tink.hybrid.HybridConfig
import info.guardianproject.netcipher.proxy.OrbotHelper
import org.lynx.client.di.AppContainer
import org.lynx.client.di.AppContainerImpl

class LynxApplication : Application() {

    lateinit var appContainer: AppContainer

    override fun onCreate() {
        super.onCreate()

        // TODO: Do Orbot installed check
        OrbotHelper.get(this).init()
        // Google Tink lib registration
        HybridConfig.register()
        appContainer = AppContainerImpl(this)
        appContainer.applicationServer.start()
    }
}