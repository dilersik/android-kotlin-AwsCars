package com.dilerdesenvolv.carros.views.activity

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.os.Build
import android.support.v4.view.MenuItemCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.view.Menu
import com.dilerdesenvolv.carros.R
import com.google.firebase.auth.FirebaseAuth
import org.jetbrains.anko.startActivity

@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {

    // propriedade para acessar o mContext de qqr lugar
    protected val mContext: Context get() = this

    protected lateinit var mToolbar: android.support.v7.app.ActionBar

    protected val mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    protected val mUser = mFirebaseAuth.currentUser

    // metodos comuns para activities
    protected fun createOptionsMenuBusca(menu: Menu?) {
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView: SearchView
        val item = menu?.findItem(R.id.action_busca)

        searchView = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            item?.actionView as SearchView
        } else {
            MenuItemCompat.getActionView(item) as SearchView
        }

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = resources.getString(R.string.buscar_carros)
    }

    protected open fun verifyLogged() {
        if (mFirebaseAuth.currentUser == null) {
            this.callLoginActivity()
        }
    }

    protected open fun callLoginActivity() {
        startActivity<LoginActivity>()
        finish()
    }

}