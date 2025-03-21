package com.example.charmix.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.charmix.model.AgendamentoModel
import com.example.charmix.model.SalaoModel
import com.example.charmix.model.ServicoModel
import com.example.charmix.model.UsuarioModel

class DBHelper(context: Context) : SQLiteOpenHelper(context, "database.db", null, 1) {

    val sql = arrayOf(
        "CREATE TABLE usuarios (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nome TEXT, " +
                "identificador TEXT UNIQUE, " +
                "email TEXT, " +
                "telefone TEXT, " +
                "senha TEXT, " +
                "tipo INTEGER )",
        "INSERT INTO usuarios (nome, identificador, email, telefone, senha, tipo) " +
                "VALUES ('Jamile', '05896712344', 'jamile@gmail.com', '77981067766', '1234', 0)",

        "CREATE TABLE saloes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nome TEXT NOT NULL, " +
                "endereco TEXT, " +
                "telefone TEXT, " +
                "cnpj TEXT, " +
                "descricao TEXT, " +
                "url_imagem TEXT, " +
                "status INTEGER, " +
                "idUsuario INTEGER, " +
                "FOREIGN KEY (idUsuario) REFERENCES usuarios(id) ON DELETE CASCADE)",

        "INSERT INTO saloes (nome, endereco, telefone, cnpj, descricao, url_imagem, status, idUsuario) " +
                "VALUES ('Charmix', 'Guanambi', '77986234532', '66666666666666', 'Salão especializado em cabelos cacheados', 'wwwwwwww', 1, 1)",

        "CREATE TABLE servicos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "idSalao INTEGER NOT NULL, " +
                "nome TEXT NOT NULL, " +
                "preco REAL NOT NULL, " +
                "descricao TEXT, " +
                "duracao INTEGER, " +
                "FOREIGN KEY (idSalao) REFERENCES saloes(id) ON DELETE CASCADE)",

        "INSERT INTO servicos (idSalao, nome, preco, descricao, duracao) " +
                "VALUES (1, 'Corte', 45.00, 'Corte V', 20)",

        "CREATE TABLE agendamentos(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "idSalao INTEGER NOT NULL," +
                "idCliente INTEGER NOT NULL, " +
                "idServico INTEGER NOT NULL," +
                "data TEXT NOT NULL, " +
                "horario INTEGER NOT NULL, " +
                "status INTEGER, " +
                "observacao TEXT," +
                "UNIQUE (IdCliente, horario)," +
                "FOREIGN KEY (IdSalao) REFERENCES saloes(id) ON DELETE CASCADE," +
                "FOREIGN KEY (idCliente) REFERENCES usuarios(id) ON DELETE CASCADE, " +
                "FOREIGN KEY (idServico) REFERENCES servicos(id) ON DELETE CASCADE)",
        "INSERT INTO agendamentos(idSalao, idCliente, idServico, data, horario, status, observacao) " +
                "VALUES(1, 1, 1, '2025-03-16', 14, 1, 'Tudo certo')",
    )


    override fun onCreate(db: SQLiteDatabase) {
        sql.forEach {
            db.execSQL(it)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    /*--------------------------------------------------------------------------------------------
                                       CRUD USUARIO
     --------------------------------------------------------------------------------------------*/
    fun insertUser(
        nome: String,
        identificador: String,
        email: String,
        telefone: String,
        senha: String,
        tipo: Int
    ): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("nome", nome)
        contentValues.put("identificador", identificador)
        contentValues.put("email", email)
        contentValues.put("telefone", telefone)
        contentValues.put("senha", senha)
        contentValues.put("tipo", tipo)

        val res = db.insert("usuarios", null, contentValues)
        db.close()
        return res
    }

    fun updateUser(
        id: Int,
        nome: String,
        identificador: String,
        email: String,
        telefone: String,
        senha: String,
        tipo: Int
    ): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("nome", nome)
        contentValues.put("identificador", identificador)
        contentValues.put("email", email)
        contentValues.put("telefone", telefone)
        contentValues.put("senha", senha)
        contentValues.put("tipo", tipo)

        val res = db.update("usuarios", contentValues, "id=?", arrayOf(id.toString()))
        db.close()
        return res
    }

    fun deleteUser(id: Int): Int {
        val db = this.writableDatabase

        val res = db.delete("usuarios", "id=?", arrayOf(id.toString()))
        db.close()
        return res
    }

    fun getUser(identificador: String): UsuarioModel {
        val db = this.readableDatabase
        val c = db.rawQuery(
            "SELECT * FROM usuarios WHERE identificador=? ", arrayOf(identificador)
        )
        var usuarioModel = UsuarioModel()

        if (c.count == 1) {
            c.moveToFirst()
            val idIndex = c.getColumnIndex("id")
            val nomeIndex = c.getColumnIndex("nome")
            val identificadorIndex = c.getColumnIndex("identificador")
            val emailIndex = c.getColumnIndex("email")
            val telefoneIndex = c.getColumnIndex("telefone")
            val senhaIndex = c.getColumnIndex("senha")
            val tipoIndex = c.getColumnIndex("tipo")

            usuarioModel = UsuarioModel(
                id = c.getInt(idIndex),
                nome = c.getString(nomeIndex),
                identificador = c.getString(identificadorIndex),
                email = c.getString(emailIndex),
                telefone = c.getString(telefoneIndex),
                senha = c.getString(senhaIndex),
                tipo = c.getInt(tipoIndex)
            )
        }

        db.close()
        return usuarioModel
    }

    fun verificarUsuarioExiste(identificador: String): Boolean {
        val db = this.readableDatabase
        val c = db.rawQuery(
            "SELECT * FROM usuarios WHERE identificador=?",
            arrayOf(identificador)
        )

        return if (c.moveToFirst()) {
            db.close()
            true
        } else {
            db.close()
            false
        }
    }

    fun getAllUsers(): ArrayList<UsuarioModel> {
        val db = this.readableDatabase
        val c = db.rawQuery(
            "SELECT * FROM usuarios", null
        )

        var usuarios = ArrayList<UsuarioModel>()

        if (c.count > 0) {
            c.moveToFirst()
            do {
                val idIndex = c.getColumnIndex("id")
                val nomeIndex = c.getColumnIndex("nome")
                val identificadorIndex = c.getColumnIndex("identificador")
                val emailIndex = c.getColumnIndex("email")
                val telefoneIndex = c.getColumnIndex("telefone")
                val senhaIndex = c.getColumnIndex("senha")
                val tipoIndex = c.getColumnIndex("tipo")

                var usuarioModel = UsuarioModel()

                usuarioModel = UsuarioModel(
                    id = c.getInt(idIndex),
                    nome = c.getString(nomeIndex),
                    identificador = c.getString(identificadorIndex),
                    email = c.getString(emailIndex),
                    telefone = c.getString(telefoneIndex),
                    senha = c.getString(senhaIndex),
                    tipo = c.getInt(tipoIndex)
                )

                usuarios.add(usuarioModel)
            } while (c.moveToNext())
        }

        db.close()
        return usuarios
    }

    fun getClientes(): ArrayList<UsuarioModel> {
        val db = this.readableDatabase
        val c = db.rawQuery(
            "SELECT * FROM usuarios WHERE tipo=0", null
        )

        var usuarios = ArrayList<UsuarioModel>()

        if (c.count > 0) {
            c.moveToFirst()
            do {
                val idIndex = c.getColumnIndex("id")
                val nomeIndex = c.getColumnIndex("nome")
                val identificadorIndex = c.getColumnIndex("identificador")
                val emailIndex = c.getColumnIndex("email")
                val telefoneIndex = c.getColumnIndex("telefone")
                val senhaIndex = c.getColumnIndex("senha")
                val tipoIndex = c.getColumnIndex("tipo")

                var usuarioModel = UsuarioModel()

                usuarioModel = UsuarioModel(
                    id = c.getInt(idIndex),
                    nome = c.getString(nomeIndex),
                    identificador = c.getString(identificadorIndex),
                    email = c.getString(emailIndex),
                    telefone = c.getString(telefoneIndex),
                    senha = c.getString(senhaIndex),
                    tipo = c.getInt(tipoIndex)
                )

                usuarios.add(usuarioModel)
            } while (c.moveToNext())
        }

        db.close()
        return usuarios
    }

    fun getClienteNameById(clienteId: Int): String {
        val db = readableDatabase
        var nomeCliente = ""
        val c = db.rawQuery("SELECT nome FROM usuarios WHERE id = ?", arrayOf(clienteId.toString()))

        if (c.moveToFirst()) {
            val nomeIndex = c.getColumnIndex("nome")
            nomeCliente = c.getString(nomeIndex)
        }
        c.close()
        return nomeCliente
    }


    /*--------------------------------------------------------------------------------------------
                                   CRUD SALAO
 --------------------------------------------------------------------------------------------*/

    // nome, endereco, telefone, cnpj, descricao, url_imagem, status
    fun insertSalao(
        nome: String,
        endereco: String,
        telefone: String,
        cnpj: String,
        descricao: String,
        url_image: String,
        status: Int,
        idUsuario: Int
    ): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("nome", nome)
        contentValues.put("endereco", endereco)
        contentValues.put("telefone", telefone)
        contentValues.put("cnpj", cnpj)
        contentValues.put("descricao", descricao)
        contentValues.put("url_imagem", url_image)
        contentValues.put("status", status)
        contentValues.put("idUsuario", idUsuario)

        val res = db.insert("saloes", null, contentValues)
        db.close()
        return res
    }

    fun updateSalao(
        id: Int,
        nome: String,
        endereco: String,
        telefone: String,
        cnpj: String,
        descricao: String,
        url_image: String,
        status: Int,
        idUsuario: Int
    ): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("nome", nome)
        contentValues.put("endereco", endereco)
        contentValues.put("telefone", telefone)
        contentValues.put("cnpj", cnpj)
        contentValues.put("descricao", descricao)
        contentValues.put("url_imagem", url_image)
        contentValues.put("status", status)
        contentValues.put("idUsuario", idUsuario)

        val res = db.update("saloes", contentValues, "id=?", arrayOf(id.toString()))
        db.close()
        return res
    }

    fun deleteSalao(id: Int): Int {
        val db = this.writableDatabase

        val res = db.delete("saloes", "id=?", arrayOf(id.toString()))
        db.close()
        return res
    }

    fun getAllSaloes(idUsuario: Int): ArrayList<SalaoModel> {
        val db = this.readableDatabase
        val c = db.rawQuery(
            "SELECT * FROM saloes WHERE idUsuario=?", arrayOf(idUsuario.toString())
        )

        var saloes = ArrayList<SalaoModel>()

        if (c.count > 0) {
            c.moveToFirst()
            do {
                val idIndex = c.getColumnIndex("id")
                val nomeIndex = c.getColumnIndex("nome")
                val enderecoIndex = c.getColumnIndex("endereco")
                val telefoneIndex = c.getColumnIndex("telefone")
                val cnpjIndex = c.getColumnIndex("cnpj")
                val descricaoIndex = c.getColumnIndex("descricao")
                val urlImagemIndex = c.getColumnIndex("url_imagem")
                val statusIndex = c.getColumnIndex("status")
                val idUsuarioIndex = c.getColumnIndex("idUsuario")

                var salaoModel = SalaoModel()

                salaoModel = SalaoModel(
                    id = c.getInt(idIndex),
                    nome = c.getString(nomeIndex),
                    endereco = c.getString(enderecoIndex),
                    telefone = c.getString(telefoneIndex),
                    cnpj = c.getString(cnpjIndex),
                    descricao = c.getString(descricaoIndex),
                    url_imagem = c.getString(urlImagemIndex),
                    status = c.getInt(statusIndex),
                    idUsuario = c.getInt(idUsuarioIndex)
                )

                saloes.add(salaoModel)
            } while (c.moveToNext())
        }

        db.close()
        return saloes
    }

    fun verificarSalaoExiste(cnpj: String): Boolean {
        val db = this.readableDatabase
        val c = db.rawQuery(
            "SELECT * FROM saloes WHERE cnpj=?",
            arrayOf(cnpj)
        )

        return if (c.moveToFirst()) {
            db.close()
            true
        } else {
            db.close()
            false
        }
    }

    fun getSalao(idSalao: Int): SalaoModel {
        val db = this.readableDatabase
        val c = db.rawQuery(
            "SELECT * FROM saloes WHERE id=?", arrayOf(idSalao.toString())
        )

        var salaoModel = SalaoModel()

        if (c.count > 0) {
            c.moveToFirst()
            do {
                val idIndex = c.getColumnIndex("id")
                val nomeIndex = c.getColumnIndex("nome")
                val enderecoIndex = c.getColumnIndex("endereco")
                val telefoneIndex = c.getColumnIndex("telefone")
                val cnpjIndex = c.getColumnIndex("cnpj")
                val descricaoIndex = c.getColumnIndex("descricao")
                val urlImagemIndex = c.getColumnIndex("url_imagem")
                val statusIndex = c.getColumnIndex("status")
                val idUsuarioIndex = c.getColumnIndex("idUsuario")

                salaoModel = SalaoModel(
                    id = c.getInt(idIndex),
                    nome = c.getString(nomeIndex),
                    endereco = c.getString(enderecoIndex),
                    telefone = c.getString(telefoneIndex),
                    cnpj = c.getString(cnpjIndex),
                    descricao = c.getString(descricaoIndex),
                    url_imagem = c.getString(urlImagemIndex),
                    status = c.getInt(statusIndex),
                    idUsuario = c.getInt(idUsuarioIndex)
                )

            } while (c.moveToNext())
        }

        db.close()
        return salaoModel
    }

    /*--------------------------------------------------------------------------------------------
                               CRUD SERVICO
--------------------------------------------------------------------------------------------*/
    // nome, endereco, telefone, cnpj, descricao, url_imagem, status
    fun insertServico(
        idSalao: Int,
        nome: String,
        descricao: String,
        preco: Double,
        duracao: Int
    ): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("idSalao", idSalao)
        contentValues.put("nome", nome)
        contentValues.put("descricao", descricao)
        contentValues.put("preco", preco)
        contentValues.put("duracao", duracao)

        val res = db.insert("servicos", null, contentValues)
        db.close()
        return res
    }

    fun updateServico(
        id: Int,
        idSalao: Int,
        nome: String,
        descricao: String,
        preco: Double,
        duracao: Int
    ): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("idSalao", idSalao)
        contentValues.put("nome", nome)
        contentValues.put("descricao", descricao)
        contentValues.put("preco", preco)
        contentValues.put("duracao", duracao)

        val res = db.update("servicos", contentValues, "id=?", arrayOf(id.toString()))
        db.close()
        return res
    }

    fun deleteServico(id: Int): Int {
        val db = this.writableDatabase

        val res = db.delete("servicos", "id=?", arrayOf(id.toString()))
        db.close()
        return res
    }

    fun getAllServicos(idSalao: Int): ArrayList<ServicoModel> {
        val db = this.readableDatabase
        val c = db.rawQuery(
            "SELECT * FROM servicos WHERE idSalao=?", arrayOf(idSalao.toString())
        )

        var servicos = ArrayList<ServicoModel>()

        if (c.count > 0) {
            c.moveToFirst()
            do {
                val idIndex = c.getColumnIndex("id")
                val idSalaoIndex = c.getColumnIndex("idSalao")
                val nomeIndex = c.getColumnIndex("nome")
                val descricaoIndex = c.getColumnIndex("descricao")
                val precoIndex = c.getColumnIndex("preco")
                val duracaoIndex = c.getColumnIndex("duracao")


                var servicoModel = ServicoModel()

                servicoModel = ServicoModel(
                    id = c.getInt(idIndex),
                    idSalao = c.getInt(idSalaoIndex),
                    nome = c.getString(nomeIndex),
                    descricao = c.getString(descricaoIndex),
                    preco = c.getDouble(precoIndex),
                    duracao = c.getInt(duracaoIndex),
                )

                servicos.add(servicoModel)
            } while (c.moveToNext())
        }
        c.close()
        db.close()
        return servicos
    }

    fun getServico(id: Int): ServicoModel {
        val db = this.readableDatabase
        val c = db.rawQuery(
            "SELECT * FROM servicos WHERE id=?", arrayOf(id.toString())
        )

        var servicoModel = ServicoModel()

        if (c.count > 0) {
            c.moveToFirst()
            do {
                val idIndex = c.getColumnIndex("id")
                val idSalaoIndex = c.getColumnIndex("idSalao")
                val nomeIndex = c.getColumnIndex("nome")
                val descricaoIndex = c.getColumnIndex("descricao")
                val precoIndex = c.getColumnIndex("preco")
                val duracaoIndex = c.getColumnIndex("duracao")

                servicoModel = ServicoModel(
                    id = c.getInt(idIndex),
                    idSalao = c.getInt(idSalaoIndex),
                    nome = c.getString(nomeIndex),
                    descricao = c.getString(descricaoIndex),
                    preco = c.getDouble(precoIndex),
                    duracao = c.getInt(duracaoIndex),
                )

            } while (c.moveToNext())
        }
        c.close()
        db.close()
        return servicoModel
    }

    fun getServicoNameById(idServico: Int): String {
        val db = readableDatabase
        var nomeServico = ""
        val c = db.rawQuery("SELECT nome FROM servicos WHERE id = ?", arrayOf(idServico.toString()))

        if (c.moveToFirst()) {
            val nomeIndex = c.getColumnIndex("nome")
            nomeServico = c.getString(nomeIndex)
        }
        c.close()
        return nomeServico
    }

    /*--------------------------------------------------------------------------------------------
                                   CRUD AGENDAMENTO
 --------------------------------------------------------------------------------------------*/
// idSalao, idCliente, idServico, horario, status, data, observacao
    fun insertAgendamento(
        idSalao: Int,
        idCliente: Int,
        idServico: Int,
        data: String,
        horario: Int,
        status: Int,
        observacao: String
    ): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("idSalao", idSalao)
        contentValues.put("idCliente", idCliente)
        contentValues.put("idServico", idServico)
        contentValues.put("data", data)
        contentValues.put("horario", horario)
        contentValues.put("status", status)
        contentValues.put("observacao", observacao)

        val res = db.insert("agendamentos", null, contentValues)
        db.close()
        return res
    }

    fun updateAgendamento(
        id: Int,
        idSalao: Int,
        idCliente: Int,
        idServico: Int,
        data: String,
        horario: Int,
        status: Int,
        observacao: String
    ): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("idSalao", idSalao)
        contentValues.put("idCliente", idCliente)
        contentValues.put("idServico", idServico)
        contentValues.put("data", data)
        contentValues.put("horario", horario)
        contentValues.put("status", status)
        contentValues.put("observacao", observacao)

        val res = db.update("agendamentos", contentValues, "id=?", arrayOf(id.toString()))
        db.close()
        return res
    }

    fun deleteAgendamento(id: Int): Int {
        val db = this.writableDatabase

        val res = db.delete("agendamentos", "id=?", arrayOf(id.toString()))
        db.close()
        return res
    }

    fun getAllAgendamentos(idSalao: Int): ArrayList<AgendamentoModel> {
        val db = this.readableDatabase
        val c = db.rawQuery(
            "SELECT * FROM agendamentos WHERE idSalao=?", arrayOf(idSalao.toString())
        )

        var agendamentos = ArrayList<AgendamentoModel>()

        if (c.count > 0) {
            c.moveToFirst()
            do {
                val idIndex = c.getColumnIndex("id")
                val idSalaoIndex = c.getColumnIndex("idSalao")
                val idClienteIndex = c.getColumnIndex("idCliente")
                val idServicoIndex = c.getColumnIndex("idServico")
                val dataIndex = c.getColumnIndex("data")
                val horarioIndex = c.getColumnIndex("horario")
                val statusIndex = c.getColumnIndex("status")
                val observacaoIndex = c.getColumnIndex("observacao")


                var agendamentoModel = AgendamentoModel()

                agendamentoModel = AgendamentoModel(
                    id = c.getInt(idIndex),
                    idSalao = c.getInt(idSalaoIndex),
                    idCliente = c.getInt(idClienteIndex),
                    idServico = c.getInt(idServicoIndex),
                    data = c.getString(dataIndex),
                    horario = c.getInt(horarioIndex),
                    status = c.getInt(statusIndex),
                    observacao = c.getString(observacaoIndex)

                )

                agendamentos.add(agendamentoModel)
            } while (c.moveToNext())
        }
        c.close()
        db.close()
        return agendamentos
    }

    fun getAgendamento(id: Int): AgendamentoModel {
        val db = this.readableDatabase
        val c = db.rawQuery(
            "SELECT * FROM agendamentos WHERE id=?", arrayOf(id.toString())
        )

        var agendamentoModel = AgendamentoModel()

        if (c.count > 0) {
            c.moveToFirst()
            do {
                val idIndex = c.getColumnIndex("id")
                val idSalaoIndex = c.getColumnIndex("idSalao")
                val idClienteIndex = c.getColumnIndex("idCliente")
                val idServicoIndex = c.getColumnIndex("idServico")
                val dataIndex = c.getColumnIndex("data")
                val horarioIndex = c.getColumnIndex("horario")
                val statusIndex = c.getColumnIndex("status")
                val observacaoIndex = c.getColumnIndex("observacao")

                agendamentoModel = AgendamentoModel(
                    id = c.getInt(idIndex),
                    idSalao = c.getInt(idSalaoIndex),
                    idCliente = c.getInt(idClienteIndex),
                    idServico = c.getInt(idServicoIndex),
                    data = c.getString(dataIndex),
                    horario = c.getInt(horarioIndex),
                    status = c.getInt(statusIndex),
                    observacao = c.getString(observacaoIndex)

                )

            } while (c.moveToNext())
        }
        c.close()
        db.close()
        return agendamentoModel
    }
}