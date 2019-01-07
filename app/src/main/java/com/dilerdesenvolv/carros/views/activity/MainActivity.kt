package com.dilerdesenvolv.carros.views.activity

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.view.Menu
import android.view.MenuItem
import com.dilerdesenvolv.carros.R
import com.dilerdesenvolv.carros.adapter.TabsAdapter
import com.dilerdesenvolv.carros.domain.TipoCarro
import com.dilerdesenvolv.carros.domain.model.Carro
import com.dilerdesenvolv.carros.domain.service.FirebaseGoogleAuthService
import com.dilerdesenvolv.carros.extensions.setupToolbar
import com.dilerdesenvolv.carros.utils.Prefs
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.messaging.FirebaseMessaging
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_drawer_header.view.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.startActivity
import android.support.annotation.Nullable
import android.support.v4.content.ContextCompat.startActivity
import com.dilerdesenvolv.carros.R.id.*
import com.dilerdesenvolv.carros.domain.service.CarroService

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    // Google API Client object.
    private var mGoogleApiClient: GoogleApiClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupToolbar(R.id.toolbar)
        setupNavDrawer()
        setupViewPagerTabs()

        if (!CarroService.isAdmin()) {
            fab.hide()
        }
        fab.setOnClickListener {
            startActivity<CarroFormActivity>()
        }

        FirebaseMessaging.getInstance().subscribeToTopic("news")

        mGoogleApiClient = FirebaseGoogleAuthService.googleApiClient(this)

    }

    override fun onNewIntent(intent: Intent?) {
        this.verifyLogged()
        super.onNewIntent(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        createOptionsMenuBusca(menu)

        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_item_carros_todos -> {
                startActivity<CarrosActivity>("tipo" to TipoCarro.todos)
            }
            R.id.nav_item_carros_classicos -> {
                startActivity<CarrosActivity>("tipo" to TipoCarro.classicos)
            }
            R.id.nav_item_carros_esportivos -> {
                startActivity<CarrosActivity>("tipo" to TipoCarro.esportivos)
            }
            R.id.nav_item_carros_luxo -> {
                startActivity<CarrosActivity>("tipo" to TipoCarro.luxo)
            }
            R.id.nav_item_site_sobre -> {
                startActivity<SiteLivroActivity>()
            }
            R.id.nav_item_logout -> {
                // Logout FIrebase
                mFirebaseAuth.signOut()
                // Logout Facebook
                LoginManager.getInstance().logOut()
                // Logout Google
                logoutGoogle()

                startActivity<LoginActivity>()
                finish()
            }
        }
        // fecha o menu
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)

        return true
    }

    private fun setupNavDrawer() {
        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)

        val header = nav_view.getHeaderView(0)
        header.tvProfileName.text = mUser?.displayName
        header.tvProfileEmail.text = mUser?.email

        Picasso.with(this).load(mUser?.photoUrl).into(header.ivProfile)
    }

    private fun setupViewPagerTabs() {
        viewPager.offscreenPageLimit = 3
        viewPager.adapter  = TabsAdapter(mContext, supportFragmentManager)
        tabLayout.setupWithViewPager(viewPager)
        val cor = ContextCompat.getColor(mContext, R.color.white)
        tabLayout.setTabTextColors(cor, cor)

        // salva e recupera a ultima tab acessada
        val tabIdx = Prefs.getInt("tabIdx")
        viewPager.currentItem = tabIdx
        viewPager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) { }
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) { }
            override fun onPageSelected(position: Int) {
                Prefs.setInt("tabIdx", position) // Salva indice nas Prefs
            }
        })
    }

    private fun logoutGoogle() : Boolean {
        var retur = false
        mGoogleApiClient?.connect()
        mGoogleApiClient?.registerConnectionCallbacks(object : GoogleApiClient.ConnectionCallbacks {
            override fun onConnected(@Nullable bundle: Bundle?) {
                if (mGoogleApiClient != null && mGoogleApiClient!!.isConnected) {
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback { status ->
                        if (status.isSuccess) {
//                            startActivity<LoginActivity>()
//                            finish()
                            retur = true
                        }
                    }
                }
            }
            override fun onConnectionSuspended(i: Int) {
                retur = false
            }
        })

        return retur
    }

}
