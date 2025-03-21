package com.example.charmix.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.charmix.database.DBHelper
import com.example.charmix.databinding.ActivityServiceDetailBinding
import com.example.charmix.model.ServicoModel

class ServiceDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityServiceDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityServiceDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val i = intent
        val idServico = intent.getIntExtra("idServico", -1)
        Log.d("ServiceDetailActivity", "idServico recebido: $idServico")

        val db = DBHelper(this)
        val servico = db.getServico(idServico)

        if (idServico != -1) {
            mudarEditText(false)
            editDadosServico(servico)
        }

        // Voltar
        binding.btnVoltar.setOnClickListener {
            finish()
        }

        // Editar servico
        binding.btnEditar.setOnClickListener {
            mudarEditText(true)
            alternarVisibilidadeBtns()
        }

        // Cancelar alteracoes em servicos
        binding.btnCancelar.setOnClickListener {
            mudarEditText(false)
            alternarVisibilidadeBtns()
        }

        // Salvar alteracoes do servico
        binding.btnSalvar.setOnClickListener {
            val nome = binding.editNome.text.toString()
            val descricao = binding.editDescricao.text.toString()
            val preco = binding.editPreco.text.toString().toDouble()
            val duracao = binding.editDuracao.text.toString().toInt()

            if (nome.isNotEmpty() && descricao.isNotEmpty()) {
                val res =
                    db.updateServico(servico.id, servico.idSalao, nome, descricao, preco, duracao)

                if (res > 0) {
                    Toast.makeText(
                        applicationContext,
                        "Serviço atualizado com sucesso!",
                        Toast.LENGTH_SHORT
                    ).show()
                    mudarEditText(false)
                    alternarVisibilidadeBtns()
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Erro ao atualizar serviço!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        // Deletar serviço
        binding.btnDelete.setOnClickListener{
            val res = db.deleteServico(idServico)

            if(res > 0){
                val intent = Intent()
                intent.putExtra("idServicoExcluido", idServico)
                setResult(2, intent)
                finish()
            }else{
                Toast.makeText(applicationContext, "Erro ao deletar serviço!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun editDadosServico(infoServico: ServicoModel) {
        binding.editNome.setText(infoServico.nome)
        binding.editDescricao.setText(infoServico.descricao)
        binding.editPreco.setText(infoServico.preco.toString())
        binding.editDuracao.setText(infoServico.duracao.toString())
    }

    private fun alternarVisibilidadeBtns() {
        if (binding.btnsInicial.visibility == View.VISIBLE) {
            binding.btnsInicial.visibility = View.GONE
            binding.btnsEditar.visibility = View.VISIBLE

        } else {
            binding.btnsInicial.visibility = View.VISIBLE
            binding.btnsEditar.visibility = View.GONE
        }
    }

    private fun mudarEditText(status: Boolean) {
        binding.editNome.isEnabled = status
        binding.editDescricao.isEnabled = status
        binding.editPreco.isEnabled = status
        binding.editDuracao.isEnabled = status
    }
}