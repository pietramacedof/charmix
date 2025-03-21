package com.example.charmix.model

data class SalaoModel(
    val id: Int=0,
    val nome: String="",
    val endereco: String="",
    val telefone: String ="",
    val cnpj: String="",
    val descricao: String="",
    val url_imagem:String="",
    val status: Int=0,
    val idUsuario: Int=0
)
