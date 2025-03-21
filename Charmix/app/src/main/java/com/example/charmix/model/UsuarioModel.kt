package com.example.charmix.model

data class UsuarioModel(
    val id: Int=0,
    val nome: String="",
    val email: String="",
    val identificador: String="",
    val senha: String="",
    val telefone: String="",
    val tipo: Int=0
)
