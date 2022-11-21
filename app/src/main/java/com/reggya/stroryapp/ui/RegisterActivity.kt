package com.reggya.stroryapp.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.view.KeyEvent
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.reggya.stroryapp.data.*
import com.reggya.stroryapp.utils.LoginPreference
import com.reggya.stroryapp.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding : ActivityRegisterBinding
    private lateinit var viewModel: StoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        playAnimation()
        viewModel = ViewModelProvider(this, ViewModelFactory.getInstance())[StoryViewModel::class.java]

        var state = false
        binding.name.visibility = View.GONE
        binding.register.setOnClickListener {
            playAnimation()
            state = !state
            val type : String = if (state) "Register" else "Login"
            binding.btLogin.text = type
            binding.question.text = if (state) "Sudah punya akun ?" else "Belum punya akun ?"
            binding.name.visibility = if (state) View.VISIBLE else View.GONE
            binding.tvHeader.text = type
            binding.register.text = if (state) "Login" else "Register"
        }
        binding.btLogin.setOnClickListener {
            setLoginRegister(state)
        }
        binding.password.setOnKeyListener { v, keyCode, event ->
            if (event.action== KeyEvent.ACTION_DOWN && keyCode== KeyEvent.KEYCODE_ENTER){
                setLoginRegister(state)
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }

    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun playAnimation() {
        binding.tvHeader.alpha = 0f
        binding.email.alpha = 0f
        binding.text.alpha = 0f
        binding.name.alpha = 0f
        binding.password.alpha = 0f
        binding.containerRegister.alpha = 0f
        binding.btLogin.alpha = 0f

        val login = ObjectAnimator.ofFloat(binding.tvHeader, View.ALPHA, 1f).setDuration(500)
        val title = ObjectAnimator.ofFloat(binding.text, View.ALPHA, 1f).setDuration(500)
        val name = ObjectAnimator.ofFloat(binding.name, View.ALPHA, 1f).setDuration(500)
        val email = ObjectAnimator.ofFloat(binding.email, View.ALPHA, 1f).setDuration(500)
        val password= ObjectAnimator.ofFloat(binding.password, View.ALPHA, 1f).setDuration(500)
        val container = ObjectAnimator.ofFloat(binding.containerRegister, View.ALPHA, 1f).setDuration(500)
        val btLogin = ObjectAnimator.ofFloat(binding.btLogin, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(login, title, name,  email, password, container, btLogin)
            startDelay = 500
        }.start()
    }

    private fun setLoginRegister(state: Boolean) {
        val name = binding.name.text.toString()
        val email = binding.email.text.toString()
        val password = binding.password.text.toString()

        if (!checkError(name, email, password,state)) getLoginRegister(name, email, password,state)
        else return
    }

    private fun getLoginRegister(
        name: String,
        email: String,
        password: String,
        type: Boolean
    ) {
        if (type){
            viewModel.register(RegisterResponse(password = password, name = name, email = email)).observe(this){
                when(it.type){
                    ApiResponseType.SUCCESS -> login(password, email)
                    ApiResponseType.ERROR ->{
                        Toast.makeText(this, it.data?.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                    ApiResponseType.EMPTY -> Toast.makeText(this, it.data?.message.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }else{
            login(password, email)
        }
    }

    private fun login(password: String, email: String) {
        val progressBar = ProgressDialog(this)
        progressBar.showProgressBar()
        viewModel.login(RegisterResponse(password = password, email = email)).observe(this){
            when(it.type){
                ApiResponseType.SUCCESS ->{
                    progressBar.dismissProgressBar()
                    val loginPreference = LoginPreference(this)
                    loginPreference.setLogin(it.data?.loginResult?.name.toString(),
                        it.data?.loginResult?.userId.toString(), it.data?.loginResult?.token.toString())
                    loginPreference.isLogin(true)
                    startActivity(Intent(this, StoriesActivity::class.java))
                    finish()
                }
                ApiResponseType.ERROR -> {
                    progressBar.dismissProgressBar()
                    Toast.makeText(this, it.data?.message.toString(), Toast.LENGTH_SHORT).show()
                }
                ApiResponseType.EMPTY -> {
                    progressBar.dismissProgressBar()
                    Toast.makeText(this, it.data?.message.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun checkError(name: String, email: String, password: String, type: Boolean): Boolean {

        var error = false
        if (type){
            if (name.isEmpty()){
                binding.name.error = "Nama tidak boleh kosong"
                error = true
            }
            if (email.isEmpty()){
                binding.email.error = "Email tidak boleh kosong"
                error = true
            }
            if (password.isEmpty()){
                binding.password.error = "Password tidak boleh kosong"
                error = true
            }
            if (email.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.email.error = "Email tidak valid"
            }
        }
        else{
            if (email.isEmpty()){
                binding.email.error = "Email tidak boleh kosong"
                error = true
            }
            if (password.isEmpty()){
                binding.password.error = "Password tidak boleh kosong"
                error = true
            }
            if (email.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.email.error = "Email tidak valid"
            }
        }
        return error
    }
}