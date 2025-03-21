package com.example.charmix.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.charmix.database.DBHelper
import com.example.charmix.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Instanciar a classe do banco de dados
        val db = DBHelper(this)

        //Verifica a selacao do tipo de usuario
        verificarTipoUsuario()

        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            verificarTipoUsuario()
        }

        binding.btnCadastrar.setOnClickListener {
            val nome = binding.editNome.text.toString()
            var identificador = binding.editIdentificador.text.toString()
            val email = binding.editEmail.text.toString()
            val telefone = binding.editTelefone.text.toString()
            val senha = binding.editSenha.text.toString()
            val confirmarSenha = binding.editConfirmarSenha.text.toString()
            var tipoUsuario = -1

            if (binding.radioCLiente.isChecked && identificador.length == 11) {
                tipoUsuario = 0
            }else if(binding.radioProprietario.isChecked && identificador.length == 14){
                tipoUsuario = 1
            }

            //Vericações feitas:
            // Todos os campos preenchidos
            //Usuario com o mesmo cpf e cnpj no banco
            //Verificar se o cnpj e cpf corresponde ao tipo de usuario
            // a senha deve ter no minimo 6 caracteres
            // Senhas coincidem


            if (nome.isNotEmpty() && identificador.isNotEmpty() && email.isNotEmpty() && telefone.isNotEmpty() && senha.isNotEmpty()) {
                if(db.verificarUsuarioExiste(identificador)){
                    Toast.makeText(applicationContext, "O CPF ou CNPJ informado já está em uso.", Toast.LENGTH_SHORT)
                        .show()
                    binding.editIdentificador.setText("")
                }else{
                    if(tipoUsuario != -1){
                        if(senha.equals(confirmarSenha)){
                            if(senha.length >= 6){
                                val res = db.insertUser(nome, identificador, email, telefone, senha, tipoUsuario)
                                if(res > 0){
                                    Toast.makeText(applicationContext, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT)
                                        .show()
                                    startActivity(Intent(this, LoginActivity::class.java))
                                }else{
                                    Toast.makeText(applicationContext, "Erro ao realizar cadastro. Tente novamente!", Toast.LENGTH_SHORT)
                                        .show()
                                    binding.editNome.setText("")
                                    binding.editIdentificador.setText("")
                                    binding.editEmail.setText("")
                                    binding.editTelefone.setText("")
                                    binding.editSenha.setText("")
                                    binding.editConfirmarSenha.setText("")
                                }
                            }else{
                                Toast.makeText(applicationContext, "A senha deve ter no mínimo 6 caracteres", Toast.LENGTH_SHORT)
                                    .show()
                                binding.editSenha.setText("")
                                binding.editConfirmarSenha.setText("")
                            }
                        }else{
                            Toast.makeText(applicationContext, "Senhas não coincidem!", Toast.LENGTH_SHORT)
                                .show()
                            binding.editSenha.setText("")
                            binding.editConfirmarSenha.setText("")
                        }
                    }else{
                        Toast.makeText(applicationContext, "CPF ou CNPJ inválidos", Toast.LENGTH_SHORT)
                            .show()
                        binding.editIdentificador.setText("")
                    }
                }
            } else {
                Toast.makeText(applicationContext, "Preencha todos os campos", Toast.LENGTH_SHORT)
                    .show()
            }
        }


        binding.irLogin.setOnClickListener {
            val i = Intent(this, LoginActivity::class.java)
            startActivity(i)
        }

    }

    private fun verificarTipoUsuario() {
        val radioCliente = binding.radioCLiente.isChecked
        binding.editIdentificador.setText("")

        // se radioCliente o edit muda para cpf se nao muda para cnpj
        if (radioCliente) {
            binding.editIdentificador.setHint("CPF")
        } else {
            binding.editIdentificador.setHint("CNPJ")
        }
    }
}