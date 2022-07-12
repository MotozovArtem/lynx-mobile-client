package org.lynx.client.ui.activity.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import org.lynx.client.LynxApplication
import org.lynx.client.databinding.ActivityLoginBinding
import org.lynx.client.ui.activity.app.AppActivity
import org.lynx.client.ui.viewmodel.LoginViewModel

class LoginActivity : AppCompatActivity() {

    private val TAG = "LoginActivity"

    private lateinit var loginViewModel: LoginViewModel

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        loginViewModel = LoginViewModel((application as LynxApplication).appContainer)
        binding.loginButton.setOnClickListener { onLogin() }
        val authenticatedObserver = Observer<Boolean> { isAuthenticated ->
            if (isAuthenticated) {
                binding.loginButton.isEnabled = true
                val intent = Intent(this, AppActivity::class.java)
                startActivity(intent)
            }
        }
        loginViewModel.authenticated.observe(this, authenticatedObserver)
    }

    private fun onLogin() {
        binding.loginButton.isEnabled = false
        // TODO: Remove - only for test
//        (application as PeerStockClientApplication).appContainer.authorizedUser = "Ben"
//        val intent = Intent(thi   s, AppActivity::class.java)
//        intent.flags = intent.flags.or(Intent.FLAG_ACTIVITY_NO_HISTORY)
//        startActivity(intent)
        loginViewModel.initAndAuthenticate(
            binding.usernameEditText.text.toString(),
            binding.passwordEditText.text.toString(),
            binding.domainEditText.text.toString()
        )
    }
}