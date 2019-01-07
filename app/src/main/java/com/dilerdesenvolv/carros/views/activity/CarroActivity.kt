package com.dilerdesenvolv.carros.views.activity

import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.view.Menu
import android.view.MenuItem
import com.dilerdesenvolv.carros.R
import com.dilerdesenvolv.carros.domain.event.FavoritoEvent
import com.dilerdesenvolv.carros.domain.event.SaveCarroEvent
import com.dilerdesenvolv.carros.domain.model.Carro
import com.dilerdesenvolv.carros.domain.service.CarroService
import com.dilerdesenvolv.carros.domain.service.FavoritosService
import com.dilerdesenvolv.carros.extensions.loadUrl
import com.dilerdesenvolv.carros.extensions.setupToolbar
import com.dilerdesenvolv.carros.utils.AndroidUtils
import com.dilerdesenvolv.carros.views.fragments.MapaFragment
import kotlinx.android.synthetic.main.activity_carro.*
import kotlinx.android.synthetic.main.activity_carro_contents.*
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.*

class CarroActivity : BaseActivity() {

    private val mCarro by lazy { intent.getParcelableExtra<Carro>("carro") }
    private val mIsFromPush by lazy { intent.getBooleanExtra("isPush", false) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carro)
        // Toolbar
        setupToolbar(R.id.toolbar, mCarro.nome, true)
        // Atualiza os dados na tela
        initViews()
        // FAB
        fab.setOnClickListener { onClickFavoritar(mCarro) }
        // setaCor favorite button
        setupFavoriteButton()
    }

    override fun onStart() {
        super.onStart()
        this.verifyLogged()
    }

    private fun initViews() {
        tDesc.text = mCarro.desc
        appBarImg.loadUrl(mCarro.urlFoto)

        // Foto do carro com transp
        img.loadUrl(mCarro.urlFoto)
        // Play video
        imgPlayVideo.setOnClickListener {
            if (mCarro.urlVideo != null && mCarro.urlVideo != "") {
                val youtubeId = AndroidUtils.getYouTubeId(mCarro.urlVideo.toString())
                if (youtubeId != null && youtubeId != "") {
                    AndroidUtils.openYoutubeLink(this, youtubeId)

                } else {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setDataAndType(Uri.parse(mCarro.urlVideo), "video/*")
                    startActivity(intent)
                }
            } else {
                toast(getString(R.string.nenhum_video))
            }
        }
        // Fragment do mapa
        val mapaFragment = MapaFragment()
        mapaFragment.arguments = intent.extras
        supportFragmentManager.beginTransaction()
                .replace(R.id.flMapaFragment, mapaFragment)
                .commit()
    }

    // add as opc de Salvar/Deletar no menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_carro, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if (!CarroService.isAdmin()) {
            menu?.getItem(0)?.isVisible = false // action_editar
            menu?.getItem(1)?.isVisible = false // action_deletar
        }

        return true
    }

    // Trata eventos do menu
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_editar -> {
                startActivity<CarroFormActivity>("carro" to mCarro)
                finish()
            }
            R.id.action_deletar -> {
                alert(R.string.msg_confirma_excluir_carro, R.string.app_name) {
                    positiveButton(R.string.sim) {
                        taskExcluir()
                    }
                    negativeButton(R.string.nao) { }
                }.show()
            }
            R.id.action_compartilhar -> {
                toast("TO BE IMPLEMENTED")
            }
            android.R.id.home -> {
                if (mIsFromPush) {
                    this.callMainAfterPush()
                }
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (mIsFromPush) {
            this.callMainAfterPush()
            finish()
        }
        super.onBackPressed()
    }

    private fun onClickFavoritar(carro: Carro) {
        doAsync {
            try {
                val favoritado = FavoritosService.save(carro)
                uiThread {
                    toast(if (favoritado) R.string.msg_carro_favoritado
                    else R.string.msg_carro_desfavoritado)
                    // mudar a cor, etc
                    setFavoriteColor(favoritado)
                    // Dispara evento para atualizar a lista de carros favoritos
                    EventBus.getDefault().post(FavoritoEvent(mCarro))
                }
            }  catch (e: Exception) {
                uiThread {
                    Snackbar.make(cardView, getString(R.string.falhout_tent_nova) + ": " + e.message, Snackbar.LENGTH_INDEFINITE).show()
                }
            }
        }
    }

    private fun taskExcluir() {
        if (!AndroidUtils.isNetworkAvailable(this)) {
            AndroidUtils.showNoNetwork(cardView)
            return
        }
        doAsync {
            try {
                val response = CarroService.delete(carro = mCarro)
                uiThread {
                    toast(response?.msg ?: "")
                    finish()
                    // Dispara evento para atualizar a lista de carros
                    EventBus.getDefault().post(SaveCarroEvent(mCarro))
                }
            } catch (e: Exception) {
                uiThread {
                    Snackbar.make(cardView, getString(R.string.falhout_tent_nova) + ": " + e.message, Snackbar.LENGTH_INDEFINITE).show()
                }
            }
        }
    }

    private fun setFavoriteColor(favorito: Boolean) {
        val fundo = ContextCompat.getColor(this,
                if (favorito) R.color.favorito_on
                else R.color.favorito_off)
        val cor = ContextCompat.getColor(this,
                if (favorito) R.color.yellow
                else R.color.favorito_on)
        fab.backgroundTintList = ColorStateList(arrayOf(intArrayOf(0)), intArrayOf(fundo))
        fab.setColorFilter(cor)
    }

    private fun setupFavoriteButton() {
        doAsync {
            val isFavo = FavoritosService.isFavorito(mCarro)
            uiThread {
                setFavoriteColor(isFavo)
            }
        }
    }

    private fun callMainAfterPush() {
        EventBus.getDefault().post(SaveCarroEvent(mCarro))
        startActivity<MainActivity>()
    }

}
