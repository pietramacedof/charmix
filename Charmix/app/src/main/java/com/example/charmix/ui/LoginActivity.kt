package com.example.charmix.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.charmix.database.DBHelper
import com.example.charmix.databinding.ActivityLoginBinding
import com.example.charmix.model.UsuarioModel

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = DBHelper(this)
        sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE)

        binding.btnEntrar.setOnClickListener {
            val identificador = binding.editIdentificador.text.toString()
            val senha = binding.editSenha.text.toString()
            val logged = binding.checkboxConextado.isChecked

            if (identificador.isNotEmpty() && senha.isNotEmpty()) {
                val usuario: UsuarioModel = db.getUser(identificador)
                if (usuario.identificador == identificador) {
                    if (usuario.senha == senha) {
                        val editor = sharedPreferences.edit()
                        editor.putInt("usuarioLogado", usuario.id)
                        if(logged){
                            editor.putString("identificador", usuario.identificador)
                        }
                        editor.apply()
                        startActivity(Intent(this, MainActivity::class.java))
                    } else {
                        Toast.makeText(applicationContext, "Senha incorreta!", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(applicationContext, "Usuario não encontrado", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(applicationContext, "Preencha todos os campos", Toast.LENGTH_SHORT)
                    .show()
            }

        }

        binding.irCadastrar.setOnClickListener {
            val i = Intent(this, RegisterActivity::class.java)
            startActivity(i)
        }

    }
}