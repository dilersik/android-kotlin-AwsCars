package com.dilerdesenvolv.carros.views.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.dilerdesenvolv.carros.R
import com.dilerdesenvolv.carros.domain.TipoCarro
import com.dilerdesenvolv.carros.extensions.addFragment
import com.dilerdesenvolv.carros.extensions.setupToolbar
import com.dilerdesenvolv.carros.views.fragments.CarrosFragment
import org.jetbrains.anko.startActivity

class CarrosActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carros)

        // Argumentos: Tipo carro
        val tipo = intent.getSerializableExtra("tipo") as TipoCarro
        val title = getString(tipo.string)
        // Toolbar: configra o título e o "up nav"
        setupToolbar(R.id.toolbar, title, true)
        if (savedInstanceState == null) {
            // Add fragment no layout marcado
            // Dentre os args passados para Activity, está o tipo carro
            addFragment(R.id.container, CarrosFragment())
        }
    }

    override fun onStart() {
        super.onStart()
        this.verifyLogged()
    }

}
