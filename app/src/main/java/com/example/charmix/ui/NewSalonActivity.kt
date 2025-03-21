package com.example.charmix.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.charmix.database.DBHelper
import com.example.charmix.databinding.ActivityNewSalonBinding

class NewSalonActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewSalonBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewSalonBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = DBHelper(this)
        val i = intent

        binding.btnSalvar.setOnClickListener {
            val nome = binding.editNome.text.toString()
            val endereco = binding.editEndereco.text.toString()
            val telefone = binding.editTelefone.text.toString()
            val cnpj = binding.editCnpj.text.toString()
            val descricao = binding.editDescricao.text.toString()
            val url_image: String = "teste"
            val status = 1
            var idUsuario = -1

            //Recuperando o id do usuario logado
            val sharedPreferences = getSharedPreferences("login", MODE_PRIVATE)
            val usuarioLogado = sharedPreferences.getInt("usuarioLogado", -1)

            if (usuarioLogado != -1) {
                idUsuario = usuarioLogado
            } else {
                Toast.makeText(applicationContext, "Usuário não encontrado", Toast.LENGTH_SHORT)
                    .show()
            }

            if (nome.isNotEmpty() && endereco.isNotEmpty() && telefone.isNotEmpty() && cnpj.isNotEmpty() && descricao.isNotEmpty()) {
                if (cnpj.length == 14) {
                    if (db.verificarSalaoExiste(cnpj)) {
                        Toast.makeText(
                            applicationContext,
                            "CNPJ já cadastrado!",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    } else {
                        val res = db.insertSalao(
                            nome,
                            endereco,
                            telefone,
                            cnpj,
                            descricao,
                            url_image,
                            status,
                            idUsuario
                        )
                        if (res > 0) {
                            Toast.makeText(
                                applicationContext,
                                "Cadastro feito com sucesso!",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            setResult(1, i)
                            finish()
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Erro no cadastro!",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }
                } else {
                    Toast.makeText(applicationContext, "CNPJ inválido!", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(applicationContext, "Prencha todos os campos", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        binding.btnCancelar.setOnClickListener {
            setResult(0, i)
            finish()
        }

    }
}