package com.dilerdesenvolv.carros.views.fragments

import android.support.design.widget.Snackbar
import android.view.View
import com.dilerdesenvolv.carros.R
import com.dilerdesenvolv.carros.adapter.CarroAdapter
import com.dilerdesenvolv.carros.domain.event.FavoritoEvent
import com.dilerdesenvolv.carros.domain.model.Carro
import com.dilerdesenvolv.carros.domain.service.FavoritosService
import com.dilerdesenvolv.carros.views.activity.CarroActivity
import kotlinx.android.synthetic.main.fragment_carros.*
import org.greenrobot.eventbus.Subscribe
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.uiThread

class FavoritosFragment : CarrosFragment() {

    override fun taskCarros(isNewList: Boolean) {
        progress?.visibility = if (swipeToRefresh.isRefreshing) View.INVISIBLE else View.VISIBLE
        doAsync {
            try {
                mCarros = FavoritosService.getCarros()
                uiThread {
                    recyclerView?.adapter = CarroAdapter(carros = mCarros) { onClickCarro(it) }

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

    override fun onClickCarro(carro: Carro) {
        activity?.startActivity<CarroActivity>("carro" to carro)
    }

    @Subscribe
    fun onReceiveEventBus(event: FavoritoEvent) {
        taskCarros()
    }

}