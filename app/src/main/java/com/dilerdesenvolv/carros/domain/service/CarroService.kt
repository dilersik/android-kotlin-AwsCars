package com.dilerdesenvolv.carros.domain.service

import android.util.Base64
import android.util.Log
import com.dilerdesenvolv.carros.R
import com.dilerdesenvolv.carros.domain.Response
import com.dilerdesenvolv.carros.domain.TipoCarro
import com.dilerdesenvolv.carros.domain.dao.DBm
import com.dilerdesenvolv.carros.domain.model.Carro
import com.dilerdesenvolv.carros.extensions.getText
import com.dilerdesenvolv.carros.extensions.getXML
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import org.json.JSONArray
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

object CarroService {

    private const val TAG = "LOG_CarroService"
    private const val BASE_URL = ""
    private const val API_KEY: String = "ASD"
    private const val API_KEY_ADMIN: String = "ASD"
    private const val API_EMAIL_ADM = "dilermandosikora@gmail.com"
    private val mService: CarrosREST
    private val mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    init {
        val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        mService = retrofit.create(CarrosREST::class.java)
    }

    private fun getAPI_KEY() : String {
        return if (isAdmin()) API_KEY_ADMIN else API_KEY
    }

    fun isAdmin() : Boolean {
        return mFirebaseAuth.currentUser?.email == API_EMAIL_ADM
    }

    fun getCarros(tipoCarro: TipoCarro, pagina: Int): MutableList<Carro>? {
        val call = mService.getCarros(tipoCarro.name, pagina, getAPI_KEY())

        return call.execute().body()
        // OkHTTP
//        val json = HttpHelper.get("${BASE_URL}tipo/${tipoCarro.name}")
//        return fromJson<List<Carro>>(json)
    }
    // // from dir /raw - searchable os carros por tipo
//    fun getCarros(mContext: Context, tipoCarro: TipoCarro): List<Carro> {
//        val raw = getArquivoRaw(tipoCarro)
//        // Abre o arquivo para leitura
//        val inputStream = mContext.resources.openRawResource(raw)
//        inputStream.bufferedReader().use {
//            return fromJson<List<Carro>>(json = it.readText())
////            return parserJson(it.readText())
//        }
//    }

    fun searchCarros(busca: String, pagina: Int): MutableList<Carro>? {
        val call = mService.searchCarros(busca, pagina, getAPI_KEY())

        return call.execute().body()
    }

    fun save(carro: Carro): Response? {
        val call = mService.save(Gson().toJson(carro), getAPI_KEY())
        return call.execute().body()
        // OkHTTP
//        val json = HttpHelper.post(BASE_URL, carro.toJson())
//        return fromJson<Response>(json)
    }

    fun delete(carro: Carro): Response? {
        val call = mService.delete(carro.id ?: 0, getAPI_KEY())
        val response = call.execute().body()

        // se removeu do servidor, remove dos favoritos
        if (response != null && response.isOk()) {
            DBm.getCarroDAO().delete(carro)
        }
        return response
        // OkHTTP
//        val json = HttpHelper.delete(BASE_URL + carro.id)
//        return fromJson<Response>(json)
    }

    fun postFoto(carro: Carro, file: File): Response? {
        val base64 = Base64.encodeToString(file.readBytes(), Base64.NO_WRAP)
        val call = mService.postFoto(carro.id, file.name, base64, getAPI_KEY())
        return call.execute().body()
        // okHTTP
//        val params = mapOf("api_key" to API_KEY, "fileName" to file.name, "base64" to base64)
//        return fromJson<Response>(json = HttpHelper.postForm(BASE_URL + "postFotoBase64", params))
    }

    // Retorna o arquivo a ser lido
    private fun getArquivoRaw(tipoCarro: TipoCarro) = when(tipoCarro) {
//        TipoCarro.todos -> R.raw.carros_todos
        TipoCarro.classicos -> R.raw.carros_classicos
        TipoCarro.esportivos -> R.raw.carros_esportivos
        else -> R.raw.carros_luxo
    }

    // Le o JSON e cria lista de carros
    private fun parserJson(json: String): List<Carro> {
        val carros = mutableListOf<Carro>()
        val array = JSONArray(json)
        for (i in 0..array.length() - 1) {
            val jsonCarro = array.getJSONObject(i)
            val c = Carro()
            c.nome = jsonCarro.optString("nome")
            c.desc = jsonCarro.optString("desc")
            c.urlFoto = jsonCarro.optString("url_foto")
            carros.add(c)
        }
        Log.d(TAG, "${carros.size} carros encontrados")
        return carros
    }

    // Le o XML e cria lista de carros
    private fun parserXML(xmlString: String): List<Carro> {
        val carros = mutableListOf<Carro>()
        for (node in xmlString.getXML().getChildren("carro")) {
            val c = Carro()
            c.nome = node.getText("nome")
            c.desc = node.getText("desc")
            c.urlFoto = node.getText("url_foto")
            carros.add(c)
        }
        Log.d(TAG, "${carros.size} carros encontrados")
        return carros
    }

}