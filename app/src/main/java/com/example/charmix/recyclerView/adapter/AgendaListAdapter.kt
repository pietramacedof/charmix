package com.example.charmix.recyclerView.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.charmix.databinding.AgendaItemBinding
import com.example.charmix.model.AgendamentoModel

class AgendaListAdapter(
    private val context: Context,
    private val agendamentoModel: List<AgendamentoModel>,
    private val onItemClickListener: (AgendamentoModel) -> Unit
) : RecyclerView.Adapter<AgendaListAdapter.AgendaViewHolder>() {

    private val expandedItems = mutableSetOf<Int>() // Guarda os IDs expandidos

    inner class AgendaViewHolder(val binding: AgendaItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AgendaViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AgendaItemBinding.inflate(inflater, parent, false)
        return AgendaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AgendaViewHolder, position: Int) {
        val agendamento = agendamentoModel[position]

        with(holder.binding) {
            itemTitle.text = "${agendamento.idCliente} + ${agendamento.idServico}"
            agendaCliente.text = "Cliente: ${agendamento.idCliente}"

        // Verifica se o item está no Set de expandidos
            val isExpanded = expandedItems.contains(agendamento.id)

            // Define a rotação do ícone de expansão/recolhimento
            itemIcon.rotation = if (isExpanded) 180f else 0f

        // Define a visibilidade do conteúdo expandido
            agendaDetalhes.visibility = if (isExpanded) View.VISIBLE else View.GONE

            root.setOnClickListener {
                if (isExpanded) {
                    expandedItems.remove(agendamento.id)
                } else {
                    expandedItems.add(agendamento.id)
                }
                // Forçar atualização completa do RecyclerView
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemCount(): Int = agendamentoModel.size
}
