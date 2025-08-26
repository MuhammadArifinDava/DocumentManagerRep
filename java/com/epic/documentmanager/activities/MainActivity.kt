package com.epic.documentmanager.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.epic.documentmanager.R
import com.epic.documentmanager.viewmodels.AuthViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        // Show splash screen for 2 seconds then check authentication
        Handler(Looper.getMainLooper()).postDelayed({
            checkAuthenticationStatus()
        }, 2000)
    }

    private fun checkAuthenticationStatus() {
        if (authViewModel.isUserLoggedIn()) {
            // User is logged in, go to dashboard
            authViewModel.getCurrentUser()
            authViewModel.currentUser.observe(this) { user ->
                if (user != null) {
                    val intent = Intent(this, DashboardActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Failed to get user data, go to login
                    goToLogin()
                }
            }
        } else {
            // User is not logged in, go to login
            goToLogin()
        }
    }

    private fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
