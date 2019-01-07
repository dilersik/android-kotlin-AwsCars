package com.dilerdesenvolv.carros.views.activity

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.provider.SearchRecentSuggestions
import android.support.design.widget.Snackbar
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.dilerdesenvolv.carros.R
import com.dilerdesenvolv.carros.R.id.*
import com.dilerdesenvolv.carros.adapter.CarroAdapter
import com.dilerdesenvolv.carros.domain.model.Carro
import com.dilerdesenvolv.carros.domain.service.CarroService
import com.dilerdesenvolv.carros.extensions.setupToolbar
import com.dilerdesenvolv.carros.provider.BuscaProvider
import com.dilerdesenvolv.carros.utils.AndroidUtils
import kotlinx.android.synthetic.main.activity_busca.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread

class BuscaActivity : BaseActivity() {

    private var mCarros: MutableList<Carro> = mutableListOf()
    private var mBusca = ""
    private var mPagina = 1
    private var mIsLastItem = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_busca)

        mToolbar = setupToolbar(R.id.toolbar, null, true)
        handleSearch(intent)
        taskCarros()
        setupList()
    }

    override fun onStart() {
        super.onStart()
        this.verifyLogged()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_busca_activity, menu)

        createOptionsMenuBusca(menu)

        return true
    }

    override fun onNewIntent(intent: Intent) {
        setIntent(intent)
        handleSearch(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> finish()
            R.id.action_delete -> {
                val searchRecentSuggestions = SearchRecentSuggestions(this, BuscaProvider.AUTHORITY, BuscaProvider.MODE)
                searchRecentSuggestions.clearHistory()

                toast(R.string.historico_exc)
            }
        }

        return true
    }

    private fun handleSearch(intent: Intent) {
        if (Intent.ACTION_SEARCH.equals(intent.action!!, ignoreCase = true)) {
            mBusca = intent.getStringExtra(SearchManager.QUERY)

            mToolbar.title = mBusca
//            filterCars(q)

            val searchRecentSuggestions = SearchRecentSuggestions(this, BuscaProvider.AUTHORITY, BuscaProvider.MODE)
            searchRecentSuggestions.saveRecentQuery(mBusca, null)
        }
    }

    private fun setupList() {
        // Views
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.setHasFixedSize(true)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView_onScro: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView_onScro, dx, dy)

                if (dy > 0) {
                    val llm = recyclerView?.layoutManager as LinearLayoutManager
                    if (!mIsLastItem && mCarros.size == llm.findLastCompletelyVisibleItemPosition() + 1
                            && !swipeToRefresh.isRefreshing) {
                        mPagina += 1
                        taskCarros()
                    }
                }
            }
        })

        // Swipe to Refresh
        swipeToRefresh.setOnRefreshListener {
            mPagina = 1
            taskCarros(true)
            tv_msg.visibility = View.INVISIBLE
        }
        swipeToRefresh.setColorSchemeResources(
                R.color.refresh_progress_1,
                R.color.refresh_progress_2,
                R.color.refresh_progress_3)
    }

    private fun taskCarros(isNewList: Boolean = false) {
        if (!AndroidUtils.isNetworkAvailable(this)) {
            AndroidUtils.showNoNetwork(recyclerView)
            return
        }
        progress?.visibility = if (swipeToRefresh.isRefreshing) View.INVISIBLE else View.VISIBLE
        // Abre uma thread
        doAsync {
            var listCarros: MutableList<Carro>?
            try {
                // Busca os carros
                listCarros = CarroService.searchCarros(mBusca, pagina = mPagina)
                if (listCarros == null || listCarros.isEmpty() || listCarros.size == 0) {
                    mIsLastItem = true
                } else {
                    mIsLastItem = false
                    if (mPagina > 1 && listCarros.size > 0) {
                        val adapter = recyclerView?.adapter as CarroAdapter
                        for (carro in listCarros) {
                            adapter.addItemToList(carro, mCarros.size)
                        }
                    }
                }
                if (listCarros != null && (mCarros.size == 0 || isNewList)) {
                    mCarros = listCarros
                }

                // Atualiza a lista na UI Thread
                uiThread {
                    if (recyclerView != null && recyclerView.adapter == null || (isNewList)) { // to keep scroll position
                        recyclerView.adapter = CarroAdapter(mCarros) { onClickCarro(it) }
                    }

                    progress?.visibility = View.INVISIBLE
                    swipeToRefresh?.isRefreshing = false
                    tv_msg?.visibility = if (mCarros.size == 0) View.VISIBLE else View.INVISIBLE
                }
            } catch (e: Exception) {
                uiThread {
                    recyclerView?.let { it1 -> Snackbar.make(it1, getString(R.string.falhout_tent_nova) + ": " + e.message, Snackbar.LENGTH_INDEFINITE).show() }
                    progress?.visibility = View.INVISIBLE
                    swipeToRefresh?.isRefreshing = false
                }
            }
        }
    }

    private fun onClickCarro(carro: Carro) {
        startActivity<CarroActivity>("carro" to carro)
    }

}
