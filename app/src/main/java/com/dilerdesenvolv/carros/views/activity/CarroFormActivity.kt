package com.dilerdesenvolv.carros.views.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.dilerdesenvolv.carros.R
import com.dilerdesenvolv.carros.R.id.*
import com.dilerdesenvolv.carros.domain.Response
import com.dilerdesenvolv.carros.domain.TipoCarro
import com.dilerdesenvolv.carros.domain.event.SaveCarroEvent
import com.dilerdesenvolv.carros.domain.model.Carro
import com.dilerdesenvolv.carros.domain.service.CarroService
import com.dilerdesenvolv.carros.extensions.*
import com.dilerdesenvolv.carros.utils.AndroidUtils
import com.dilerdesenvolv.carros.utils.CameraHelper
import kotlinx.android.synthetic.main.activity_carro_form.*
import kotlinx.android.synthetic.main.activity_carro_form_contents.*
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread

class CarroFormActivity : BaseActivity() {

    companion object { private const val TAG = "LOG_CarroFormActivity" }
    private val mCarro: Carro? by lazy { intent.getParcelableExtra<Carro>("carro") }
    private val mCamera = CameraHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carro_form)

        setupToolbar(R.id.toolbar, mCarro?.nome ?: getString(R.string.novo_carro), true)
        initViews()
        if (savedInstanceState != null) {
            mCamera.init(savedInstanceState)
        }
    }

    override fun onStart() {
        super.onStart()
        this.verifyLogged()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Salva se girar a tela
        mCamera.onSaveInstanceState(outState)
    }

    // Adiciona opc de Salvar e Del no menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_form_carro, menu)
        return true
    }

    // Trata eventos do menu
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_salvar -> taskSalvar()
        }
        return super.onOptionsItemSelected(item)
    }

    // Le a foto quando a mCamera retornar
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            // Resize da img
            val bitmap = mCamera.getBitmap(600, 600)
            if (bitmap != null) {
                // salva neste tam
                mCamera.saveCompress(bitmap)
                // mostra a foto
                Log.d(TAG, "W " + bitmap.width + " H " + bitmap.height)
                appBarImg.setImageBitmap(bitmap)
            }
        }
    }

    private fun initViews() {
        // Ao clicar no header da foto abre a mCamera
        appBarImg.onClick { onClickAppBarImg() }
        // fun apply somente se o obj nao for null
        mCarro?.apply {
            appBarImg.loadUrl(mCarro?.urlFoto)

            etNome.string = nome ?: ""
            etDescricao.string = desc ?: ""

            when (tipo) {
                "classicos" -> rgTipo.check(R.id.rbTipoClassico)
                "esportivos" -> rgTipo.check(R.id.rbTipoEsportivo)
                "luxo" -> rgTipo.check(R.id.rbTipoLuxo)
            }
        }
    }

    private fun taskSalvar() {
        if (!AndroidUtils.isNetworkAvailable(this)) {
            AndroidUtils.showNoNetwork(linearLayout)
            return
        }
        if (etNome.isEmpty()) {
            etNome.error = getString(R.string.msg_error_form_nome)
            return
        }
        if (etDescricao.isEmpty()) {
            etDescricao.error = getString(R.string.msg_error_form_desc)
            return
        }

        progress.visibility = View.VISIBLE
        doAsync {
            val c = mCarro ?: Carro()
            c.id = mCarro?.id ?: 0
            c.nome = etNome.string
            c.desc = etDescricao.string
            c.tipo = when (rgTipo.checkedRadioButtonId) {
                R.id.rbTipoClassico -> TipoCarro.classicos.name
                R.id.rbTipoEsportivo -> TipoCarro.esportivos.name
                else -> TipoCarro.luxo.name
            }
            // Se tiver foto faz upload
            try {
                var responseFoto : Response? = null
                if (mCamera.file != null && mCamera.file!!.exists()) {
                    responseFoto = CarroService.postFoto(c, mCamera.file!!)
                    Log.d(TAG, "postFoto: " + mCamera.file!!.length().toString() + " " + mCamera.file!!.absoluteFile)
                    if (responseFoto != null && responseFoto.isOk()) {
                        // Atualiza a URL da foto no carro
                        c.urlFoto = responseFoto.url
                    }
                }
                // Salva carro no server
                val response = CarroService.save(c)
                uiThread {
                    toast((responseFoto?.msg ?: "") + " " + (response?.msg ?: ""))
                    progress.visibility = View.INVISIBLE
                    finish()
                    // Dispara um evento para atualizar a lista de carros
                    EventBus.getDefault().post(SaveCarroEvent(c))
                }
            } catch (e: Exception) {
                uiThread {
                    Snackbar.make(linearLayout, getString(R.string.falhout_tent_nova) + ": " + e.message, Snackbar.LENGTH_INDEFINITE).show()
                }
            }
        }
    }

    // Ao clicar na img do AppHeaer abre a mCamera
    private fun onClickAppBarImg() {
        val filename = if (mCarro != null) "foto_carro_${mCarro?.id}.jpg" else "foto_carro_${System.currentTimeMillis()}.jpg"
        // abre a mCamera
        val intent = mCamera.open(this, filename)
        startActivityForResult(intent, 0)
    }

}
