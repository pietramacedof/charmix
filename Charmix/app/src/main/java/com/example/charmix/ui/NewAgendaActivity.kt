package com.example.charmix.ui

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.charmix.database.DBHelper
import com.example.charmix.databinding.ActivityNewAgendaBinding
import com.example.charmix.model.ServicoModel
import com.example.charmix.model.UsuarioModel

class NewAgendaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewAgendaBinding
    private lateinit var spinnerServicos: Spinner
    private lateinit var spinnerClientes: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewAgendaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        spinnerServicos = binding.spinnerServicos

        val db = DBHelper(this)
        val i = intent
        val idSalao = i.getIntExtra("idSalao", -1)
        // Obtém os serviços do banco de dados
        val servicos = db.getAllServicos(idSalao)
        val servicosComSelecao = mutableListOf("Selecione um serviço").apply {
            addAll(servicos.map { it.nome }) // Adiciona os nomes dos servicos dentro de servicos com selecao
        }
        var selectedServico = ServicoModel()

        // Mostra a lista de serviços
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, servicosComSelecao)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerServicos.adapter = adapter

        spinnerServicos.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position != 0) {
                    selectedServico = servicos[position - 1]
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        spinnerClientes = binding.spinnerClientes
        //Obtem todos os usuarios clientes
        val clientes = db.getClientes()
        val clienteComSelecao = mutableListOf("Selecione um cliente").apply {
            addAll(clientes.map { it.nome })
        }// Adiciona os nomes dos clientes dentro de servicos com selecao
        var selectedCliente = UsuarioModel()

        // Mostra a lista de clientes
        val adapterCliente =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, clienteComSelecao)
        adapterCliente.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerClientes.adapter = adapterCliente

        spinnerClientes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position != 0) {
                    selectedCliente = clientes[position - 1]
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        // Cancelar agendamento
        binding.btnCancelar.setOnClickListener {
            finish()
        }

        binding.btnSalvar.setOnClickListener{

            // Padronizar data e hora

            val cliente = selectedCliente
            val servico = selectedServico
            val data = binding.editData.text.toString()
            val horarioEdit = binding.editHorario.text.toString()

            // Convertendo a string para minutos para ser possivel add no bd
            val parts = horarioEdit.split(':')
            val horas = parts[0].toInt()
            val minutos = parts[1].toInt()
            val horario = (horas * 60) + minutos

            val status = 0 // pendente, confimado, realizado
            val observacao = binding.editObservacao.text.toString()

            if (cliente.nome.isNotEmpty() && servico.nome.isNotEmpty() && data.isNotEmpty() && horarioEdit.isNotEmpty()){
                val res = db.insertAgendamento(idSalao, cliente.id, servico.id, data, horario,status, observacao)
                if(res > 0){
                    setResult(3, i)
                    finish()
                }else{
                    Toast.makeText(applicationContext, "Erro ao salvar agendamento", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(applicationContext, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
            }

        }

    }
}