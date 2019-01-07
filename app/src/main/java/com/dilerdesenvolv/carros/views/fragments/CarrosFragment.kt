package com.dilerdesenvolv.carros.views.fragments

import android.app.Activity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.dilerdesenvolv.carros.R
import com.dilerdesenvolv.carros.R.id.*
import com.dilerdesenvolv.carros.adapter.CarroAdapter
import com.dilerdesenvolv.carros.domain.TipoCarro
import com.dilerdesenvolv.carros.domain.event.SaveCarroEvent
import com.dilerdesenvolv.carros.domain.model.Carro
import com.dilerdesenvolv.carros.domain.service.CarroService
import com.dilerdesenvolv.carros.utils.AndroidUtils
import com.dilerdesenvolv.carros.views.activity.CarroActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_carros.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.uiThread

open class CarrosFragment : BaseFragment() {

    private var mTipo: TipoCarro = TipoCarro.todos
    protected var mCarros: MutableList<Carro> = mutableListOf<Carro>()
    private var mPagina = 1
    private var mIsLastItem = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // le o tipo enviado
        if (arguments != null) {
            mTipo = arguments?.getSerializable("tipo") as TipoCarro
        }
        // Registra os eventos do bus
        EventBus.getDefault().register(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_carros, container, false)
        val textView = view?.findViewById<TextView>(R.id.text)
        // converte o R.string.xxx em texto
        val tipoString = getString(mTipo.string)
        textView?.text = "Carros $tipoString"

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Views
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.setHasFixedSize(true)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView_onScro: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView_onScro, dx, dy)

                if (dy > 0 && viewPager?.currentItem != 4) { // se for != Favoritos (q vem do DB)
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        taskCarros()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Cancela os eventos bus
        EventBus.getDefault().unregister(this)
    }

    @Subscribe
    fun onReceiveEventBus(event: SaveCarroEvent) {
        // Recebe os eventos do bus
        mPagina = 1
        taskCarros(true)
    }

    protected open fun taskCarros(isNewList: Boolean = false) {
        if (!AndroidUtils.isNetworkAvailable(activity as Activity)) {
            AndroidUtils.showNoNetwork(recyclerView)
            return
        }
        progress?.visibility = if (swipeToRefresh?.isRefreshing == true) View.INVISIBLE else View.VISIBLE
        // Abre uma thread
        doAsync {
            var listCarros: MutableList<Carro>?
            try {
                // Busca os carros
                listCarros = CarroService.getCarros(tipoCarro = mTipo, pagina = mPagina)
                if (listCarros == null || listCarros.isEmpty() || listCarros.size == 0) {
                    mIsLastItem = true
                } else {
                    mIsLastItem = false
                    if (mPagina > 1) {
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
                    if ((recyclerView != null && recyclerView.adapter == null) || isNewList) { // to keep scroll position
                        recyclerView.adapter = CarroAdapter(mCarros) { onClickCarro(it) }
                    }

                    progress?.visibility = View.INVISIBLE
                    swipeToRefresh?.isRefreshing = false
                    tv_msg?.visibility = if (mCarros.size == 0) View.VISIBLE else View.INVISIBLE
                }
            } catch (e: Exception) {
                uiThread {
                    view?.let { it1 -> Snackbar.make(it1, getString(R.string.falhout_tent_nova) + ": " + e.message, Snackbar.LENGTH_INDEFINITE).show() }
                    progress?.visibility = View.INVISIBLE
                    swipeToRefresh?.isRefreshing = false
                }
            }
        }
    }

    protected open fun onClickCarro(carro: Carro) {
        activity?.startActivity<CarroActivity>("carro" to carro)
    }

}
