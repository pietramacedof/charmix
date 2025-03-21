package com.example.charmix.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.charmix.R
import com.example.charmix.database.DBHelper
import com.example.charmix.databinding.ActivityMainBinding
import com.example.charmix.model.SalaoModel
import com.example.charmix.recyclerView.adapter.SalonsListAdapter


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var result: ActivityResultLauncher<Intent>
    private lateinit var saloesList: ArrayList<SalaoModel>
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar a Toolbar
        setSupportActionBar(binding.toolbar)

        sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE)
        val idUsuario = sharedPreferences.getInt("usuarioLogado", 0)

        val db = DBHelper(this)

        val salons = db.getAllSaloes(idUsuario)
        val adapter = SalonsListAdapter(
            salons = salons,
            onItemClickListener = { salon ->
                val i = Intent(this, SalonDashboardActivity::class.java)
                i.putExtra("idSalao", salon.id)
                i.putExtra("nome", salon.nome)
                result.launch(i)
            }
        )

        // Listar saloes
        binding.recyclerView.adapter = adapter

        binding.btnAdd.setOnClickListener {
            result.launch(Intent(this, NewSalonActivity::class.java))
        }

        result =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { resultData ->
                when (resultData.resultCode) {
                    1 -> {
                        // Ação quando um salão foi adicionado
                        saloesList = db.getAllSaloes(idUsuario)
                        adapter.updateSalons(saloesList)
                        Toast.makeText(
                            applicationContext,
                            "Salão adicionado com sucesso!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    2 -> {
                        // Ação quando um salão foi deletado
                        val idSalaoExcluido = resultData.data?.getIntExtra("idSalaoExcluido", -1)
                        if (idSalaoExcluido != null && idSalaoExcluido != -1) {
                            val indexSalaoRemovido =
                                saloesList.indexOfFirst { it.id == idSalaoExcluido }
                            if (indexSalaoRemovido != -1) {
                                saloesList.removeAt(indexSalaoRemovido)
                                adapter.updateSalons(saloesList)
                            }
                        }
                        saloesList = db.getAllSaloes(idUsuario)
                        adapter.updateSalons(saloesList)
                        Toast.makeText(
                            applicationContext,
                            "Salão deletado com sucesso!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
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
                editor.putString("identificador", "")
                editor.apply()
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }


}