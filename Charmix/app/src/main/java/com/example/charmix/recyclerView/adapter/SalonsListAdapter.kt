package com.example.charmix.recyclerView.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.charmix.databinding.SalonItemBinding
import com.example.charmix.model.SalaoModel

class SalonsListAdapter(
    private val salons: ArrayList<SalaoModel>,
    private val onItemClickListener: (SalaoModel) ->Unit
) : RecyclerView.Adapter<SalonsListAdapter.ViewHolder>() {

    inner class ViewHolder(
        val binding: SalonItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private lateinit var salon: SalaoModel

        fun init(){
            binding.root.setOnClickListener{
                if(::salon.isInitialized) {
                    onItemClickListener(salon)
                }
            }
        }
        fun bind(salon: SalaoModel) {
            this.salon = salon
            binding.textNomeSalao.text = salon.nome
            binding.textEndereco.text = salon.endereco
            binding.textTelefone.text = salon.telefone
            init()
        }
    }

    // Metodo usado para inflar o layout (Cria o item de salao na tela)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SalonItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    // Faz a contagem da quantidade de itens que deve ser criado
    override fun getItemCount(): Int = salons.size

    // Essa função é chamada sempre que precisa que um item apareça na tela
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(salons[position])
    }

    // Metodo para atualizar  a lista de saloes quando um novo salao for criado
    fun updateSalons(newSalons: ArrayList<SalaoModel>){
        salons.clear()
        salons.addAll(newSalons)
        notifyDataSetChanged()
    }
}
