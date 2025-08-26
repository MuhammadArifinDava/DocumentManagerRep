package com.epic.documentmanager.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.epic.documentmanager.R
import com.epic.documentmanager.utils.Constants
import com.epic.documentmanager.utils.ValidationUtils
import com.epic.documentmanager.viewmodels.AuthViewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var authViewModel: AuthViewModel
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvRegister: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initViews()
        setupViewModel()
        setupObservers()
        setupClickListeners()
    }

    private fun initViews() {
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvRegister = findViewById(R.id.tvRegister)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun setupViewModel() {
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
    }

    private fun setupObservers() {
        authViewModel.loginResult.observe(this) { result ->
            if (result.isSuccess) {
                val user = result.getOrNull()
                user?.let {
                    Toast.makeText(this, "Login berhasil! Selamat datang ${it.fullName}", Toast.LENGTH_SHORT).show()

                    // Navigate to dashboard
                    val intent = Intent(this, DashboardActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            } else {
                Toast.makeText(this, "Login gagal: ${result.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
            }
        }

        authViewModel.loading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            btnLogin.isEnabled = !isLoading
            btnLogin.text = if (isLoading) "Loading..." else "Login"
        }
    }

    private fun setupClickListeners() {
        btnLogin.setOnClickListener {
            login()
        }

        tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun login() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        val fields = mapOf(
            "email" to email,
            "password" to password
        )

        val errors = ValidationUtils.validateForm(fields)
        if (errors.isNotEmpty()) {
            Toast.makeText(this, errors.first(), Toast.LENGTH_SHORT).show()
            return
        }

        authViewModel.login(email, password)
    }
}