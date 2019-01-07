package com.dilerdesenvolv.carros.adapter

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.dilerdesenvolv.carros.domain.TipoCarro
import com.dilerdesenvolv.carros.views.fragments.CarrosFragment
import com.dilerdesenvolv.carros.views.fragments.FavoritosFragment

class TabsAdapter (private val mContext: Context, fm: FragmentManager) : FragmentPagerAdapter(fm) {

    // Qtd de tabs
    override fun getCount(): Int = 5

    override fun getPageTitle(position: Int): CharSequence {
        return mContext.getString(getTipoCarro(position).string)
    }

    // Fragment que mostra a lista de carros
    override fun getItem(position: Int): Fragment {
        if (position == 4) {
            return FavoritosFragment()
        }
        val f: Fragment = CarrosFragment()
        f.arguments = Bundle()
        f.arguments?.putSerializable("tipo", getTipoCarro(position))

        return f
    }

    private fun getTipoCarro(position: Int) = when (position) {
        0 -> TipoCarro.todos
        1 -> TipoCarro.classicos
        2 -> TipoCarro.esportivos
        3 -> TipoCarro.luxo
        else -> TipoCarro.favoritos
    }

}