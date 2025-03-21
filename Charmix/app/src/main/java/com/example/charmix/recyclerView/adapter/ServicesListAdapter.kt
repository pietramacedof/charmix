package com.example.charmix.recyclerView.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.charmix.databinding.ServiceItemBinding
import com.example.charmix.model.ServicoModel

class ServicesListAdapter(
    private val services: ArrayList<ServicoModel>,
    private val onItemClickListener: (ServicoModel) -> Unit
) : RecyclerView.Adapter<ServicesListAdapter.ViewHolder>() {

    inner class ViewHolder(
        val binding: ServiceItemBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        // Removido o lateinit, já que a variável será inicializada diretamente no bind
        fun bind(service: ServicoModel) {
            binding.textNomeServico.text = service.nome
            binding.textDescricao.text = "Descrição: ${service.descricao}"
            binding.textPreco.text = "Preço: R$ ${service.preco.toString()}"
            var hora = 0
            var minutos = 0
            var extensao = "minutos"
            if(service.duracao > 60){
                hora = service.duracao/60
                minutos = service.duracao%60
                binding.textDuracao.text = "Duração do serviço: ${hora} hora(s) e ${minutos} minutos"
            }else{
                binding.textDuracao.text = "Duração do serviço: ${service.duracao} minutos"
            }


            // Configura o listener de clique diretamente aqui
            binding.root.setOnClickListener {
                onItemClickListener(service)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ServiceItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = services.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(services[position])
    }

    fun updateServices(newServices: ArrayList<ServicoModel>) {
        services.clear()
        services.addAll(newServices)
        notifyDataSetChanged()
    }


}
