package com.dilerdesenvolv.carros.views.activity.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.widget.TextView
import com.dilerdesenvolv.carros.R

/**
 * Created by dilerdesenvolv on 21/08/2017.
 */
class AboutDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // cria html com texto sobre
        val aboutBody = SpannableStringBuilder()
        // versao do APP
        val versionName = getAppVersionName()
        // converte o texto do strings.xml para HTML
        val html: Spanned
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            html = Html.fromHtml(getString(R.string.about_dialog_text, versionName), Html.FROM_HTML_MODE_LEGACY)
        } else {
            html = Html.fromHtml(getString(R.string.about_dialog_text, versionName))
        }
        aboutBody.append(html)
        // infla o layout
        val inflater = LayoutInflater.from(activity)
        val view = inflater.inflate(R.layout.dialog_about, null) as TextView
        view.text = aboutBody
        view.movementMethod = LinkMovementMethod()
        // Cria o Dialog Customizado
        return AlertDialog.Builder(activity)
                .setTitle(R.string.about_dialog_title)
                .setView(view)
                .setPositiveButton(R.string.ok)
                { dialog, _ -> dialog.dismiss() }
                .create()
    }

    fun getAppVersionName(): String {
        val pm = activity?.packageManager
        val packageName = activity?.packageName
        var versionName = ""
        try {
            val info = pm?.getPackageInfo(packageName, 0)
            if (info != null) {
                versionName = info.versionName
            }
        } catch (ex: PackageManager.NameNotFoundException) {
            versionName = "N/A"
        }

        return versionName
    }

    companion object {
        // metodo util para mostrar o dialog
        fun showAbout(fm: android.support.v4.app.FragmentManager) {
            val ft = fm.beginTransaction()
            val prev = fm.findFragmentByTag("about_dialog")
            if (prev != null) {
                ft.remove(prev)
            }
            ft.addToBackStack(null)
            AboutDialog().show(ft, "about_dialog")
        }
    }

}