package com.example.charmix.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.charmix.database.DBHelper
import com.example.charmix.databinding.ActivityNewServiceBinding

class NewServiceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewServiceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewServiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = DBHelper(this)
        val i = intent

        binding.btnCancelar.setOnClickListener{
            setResult(0, i)
            finish()
        }

        binding.btnSalvar.setOnClickListener{
            val nome = binding.editNome.text.toString()
            val descricao = binding.editDescricao.text.toString()
            val preco = binding.editPreco.text.toString().takeIf { it.isNotBlank() }?.toDoubleOrNull() ?: 0.0
            val duracao = binding.editDuracao.text.toString().takeIf { it.isNotBlank() }?.toIntOrNull() ?: 0
            val idSalao = intent.getIntExtra("idSalao", -1)

            if(preco != 0.0 && duracao != 0){
                if(nome.isNotEmpty() && descricao.isNotEmpty()){
                    val res = db.insertServico(idSalao, nome, descricao, preco, duracao)
                    setResult(1, i)
                    finish()
                }else{
                    Toast.makeText(applicationContext, "Nome ou Descrição não podem ser vazio", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(applicationContext, "Preco ou duração não podem ser vazios ou inválidos", Toast.LENGTH_SHORT).show()
            }


        }
    }
}