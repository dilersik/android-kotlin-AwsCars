package com.dilerdesenvolv.carros.extensions

import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View

// findViewById + setOnClickListener
fun AppCompatActivity.onClick(@IdRes viewId: Int, onClick: (v: android.view.View) -> Unit) {
    findViewById<View>(viewId).setOnClickListener { onClick(it) }
}

// show toast
//fun Activity.toast(message: CharSequence, length: Int = Toast.LENGTH_SHORT) {
//    Toast.makeText(this, message, length).show()
//}
//fun Activity.toast(@StringRes message: Int, length: Int = Toast.LENGTH_SHORT) {
//    Toast.makeText(this, message, length).show()
//}

// config Toolbar
fun AppCompatActivity.setupToolbar(@IdRes id: Int, title: String? = null, upNavigation: Boolean = false) : ActionBar {
    val toolbar = findViewById<Toolbar>(id)
    setSupportActionBar(toolbar)
    if (title != null) {
        supportActionBar?.title = title
    }
    supportActionBar?.setDisplayHomeAsUpEnabled(upNavigation)
    return supportActionBar!!
}

// adiciona fragment no layout
fun AppCompatActivity.addFragment(@IdRes layoutId: Int, fragment: Fragment) {
    fragment.arguments = intent.extras
    val ft = supportFragmentManager.beginTransaction()
    ft.replace(layoutId, fragment) // replace ao inves de add, para substituir
    ft.commit()
}