package com.example.charmix.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.charmix.R
import com.example.charmix.database.DBHelper
import com.example.charmix.databinding.ActivitySalonDashboardBinding
import com.example.charmix.model.AgendamentoModel
import com.example.charmix.model.SalaoModel
import com.example.charmix.model.ServicoModel
import com.example.charmix.recyclerView.adapter.AgendaListAdapter
import com.example.charmix.recyclerView.adapter.ServicesListAdapter

class SalonDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySalonDashboardBinding
    private lateinit var result: ActivityResultLauncher<Intent>
    lateinit var servicosList: ArrayList<ServicoModel>
    lateinit var agendamentosList: ArrayList<AgendamentoModel>
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySalonDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar a Toolbar
        setSupportActionBar(binding.toolbar)

        val db = DBHelper(this)
        val i = intent
        val idSalao = i.getIntExtra("idSalao", -1)

        var infoSalao = SalaoModel()
        if (idSalao != -1) {
            //Mostrar os dados do salao em detalhes
            infoSalao = db.getSalao(idSalao)
            editDadosSalao(infoSalao)
            mudarEditText(false)
        }

        // Mostrar a lista de servicos
        servicosList = db.getAllServicos(idSalao)
        val intentEditService = Intent(this, ServiceDetailActivity::class.java)
        val adapter = ServicesListAdapter(
            services = servicosList,
            onItemClickListener = { service ->
                // Ver informacoes do servico
                Log.d("IntentDebug", "Intent: ${intentEditService}")
                intentEditService.putExtra("idServico", service.id)
                result.launch(intentEditService)

            }
        )

        // Mostrar lista de agendamentos
        agendamentosList = db.getAllAgendamentos(idSalao)
        val adapterAgenda = AgendaListAdapter(
            this,
            agendamentoModel = agendamentosList,
            onItemClickListener = { agendamento ->
                Toast.makeText(applicationContext, "Agenda clicada", Toast.LENGTH_SHORT).show()
            }
        )
        binding.recyclerViewAgenda.adapter = adapterAgenda
        // Lista dos servicos
        binding.recyclerView.adapter = adapter

        // Btn voltar
        binding.btnVoltar.setOnClickListener {
            finish()
        }

        // Editar salao
        binding.btnEditar.setOnClickListener {
            mudarEditText(true)
            alternarVisibilidadeBtns()
        }

        // Deletar salao
        binding.btnDeletar.setOnClickListener {
            val res = db.deleteSalao(idSalao)
            // intent de exclusao
            val intent = Intent()
            if (res > 0) {
                intent.putExtra("idSalaoExcluido", idSalao)
                setResult(2, intent)
                finish()
            } else {
                Toast.makeText(
                    applicationContext,
                    "Erro ao deletar salão. Tente novamente!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Salvar alteracoes do salao
        binding.btnSalvar.setOnClickListener {
            val nome = binding.editNome.text.toString()
            val endereco = binding.editEndereco.text.toString()
            val telefone = binding.editTelefone.text.toString()
            val cnpj = binding.editCnpj.text.toString()
            val descricao = binding.editDescricao.text.toString()

            val res = db.updateSalao(
                infoSalao.id,
                nome,
                endereco,
                telefone,
                cnpj,
                descricao,
                infoSalao.url_imagem,
                infoSalao.status,
                infoSalao.idUsuario
            )

            if (res > 0) {
                Toast.makeText(
                    applicationContext,
                    "Dados do salão atualizados com sucesso!",
                    Toast.LENGTH_SHORT
                ).show()
                val novosDados = db.getSalao(idSalao)
                editDadosSalao(novosDados)
                alternarVisibilidadeBtns()
                mudarEditText(false)
            } else {
                Toast.makeText(
                    applicationContext,
                    "Erro ao atualizar dados!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Cancelar alterações no salao
        binding.btnCancelar.setOnClickListener {
            alternarVisibilidadeBtns()
            mudarEditText(false)
        }

        // Adicionar servico
        binding.btnAdicionarServico.setOnClickListener {
            abrirActivity(NewServiceActivity::class.java, idSalao)
        }

        // Agendar
        binding.btnAgendar.setOnClickListener{
            abrirActivity(NewAgendaActivity::class.java, idSalao)
        }

        result =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { resultData ->
                // Se add um novo servico atualiza a lista de servicos
                if (resultData.resultCode == 1) {
                    servicosList = db.getAllServicos(idSalao)
                    adapter.updateServices(servicosList)
                    //Se deletar algum servico, remove da lista
                } else if (resultData.resultCode == 2) {
                    val idServicoExcluido = resultData.data?.getIntExtra("idServicoExcluido", -1)

                    if (idServicoExcluido != null && idServicoExcluido != -1) {
                        val indexServicoRemovido =
                            servicosList.indexOfFirst { it.id == idServicoExcluido }
                        if (indexServicoRemovido != -1) {
                            servicosList.removeAt(indexServicoRemovido)
                            adapter.updateServices(servicosList)
                            Toast.makeText(
                                applicationContext,
                                "Serviço excluido com sucesso!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
    }

    private fun abrirActivity(activityClass: Class<*>, idSalao: Int) {
        val intent = Intent(this, activityClass)
        intent.putExtra("idSalao", idSalao)
        result.launch(intent)
    }

    private fun editDadosSalao(infoSalao: SalaoModel) {
        binding.editNome.setText(infoSalao.nome)
        binding.editEndereco.setText(infoSalao.endereco)
        binding.editTelefone.setText(infoSalao.telefone)
        binding.editCnpj.setText(infoSalao.cnpj)
        binding.editDescricao.setText(infoSalao.descricao)
    }

    private fun alternarVisibilidadeBtns() {
        if (binding.btnInicial.visibility == View.VISIBLE) {
            binding.btnInicial.visibility = View.GONE
            binding.btnsEdit.visibility = View.VISIBLE

        } else {
            binding.btnInicial.visibility = View.VISIBLE
            binding.btnsEdit.visibility = View.GONE
        }
    }


    // Inflar o menu de logout
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(
            R.menu.menu_main, menu
        )
        return true
    }

    // Fazer com que deslogue quando clicar no botao de logout
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putString("username", "")
                editor.apply()
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun mudarEditText(status: Boolean) {
        binding.editNome.isEnabled = status
        binding.editEndereco.isEnabled = status
        binding.editTelefone.isEnabled = status
        binding.editCnpj.isEnabled = status
        binding.editDescricao.isEnabled = status
    }
}