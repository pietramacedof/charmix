package com.example.charmix.model

data class AgendamentoModel(
    val id: Int=0,
    val idSalao: Int=0,
    val idCliente: Int=0,
    val idServico: Int=0,
    val data: String="",
    val horario: Int=0,
    val status: Int=0,
    val observacao: String=""
)
