package com.example.charmix.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.charmix.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("login", MODE_PRIVATE)
        val identificador = sharedPreferences.getString("identificador", "")
        if (identificador != null) {
            if (identificador.isNotEmpty()){
                startActivity(Intent(this, MainActivity::class.java))
            }
        }

        binding.btnLogin.setOnClickListener{
            val i = Intent(this, LoginActivity::class.java)
            startActivity(i)
        }

        binding.btnCadastrar.setOnClickListener{
            val i = Intent(this, RegisterActivity::class.java)
            startActivity(i)
        }

    }
}