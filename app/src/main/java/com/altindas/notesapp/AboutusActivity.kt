package com.altindas.notesapp

//noinspection SuspiciousImport
import android.R
import android.icu.util.Calendar
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import mehdi.sakout.aboutpage.AboutPage
import mehdi.sakout.aboutpage.Element


class AboutusActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_aboutus)
        val description = intent.getStringExtra("description")
        val aboutPage: View = AboutPage(this)
            .setDescription(description)
            .isRTL(false)
            .addItem(Element().setTitle("Version 0.1"))
            .addGroup("Connect with us")
            .addEmail("mtaltindas@gmail.com")
            .addWebsite("https://mtaltindas.com/")
            .addTwitter("medyo80")
            .addYoutube("UCdPQtdWIsg7_pi4mrRu46vA")
            .addPlayStore("com.ideashower.readitlater.pro")
            .addInstagram("medyo80")
            .addGitHub("medyo")
            .addItem(getCopyRightsElement())
            .create()
        setContentView(aboutPage)
    }
    private fun getCopyRightsElement():Element {
        val copyright=Element()
        val year=Calendar.getInstance().get(Calendar.YEAR)
        val copyrightString=String.format("Copyright %d",year)
        copyright.title = copyrightString
        copyright.iconDrawable = R.drawable.sym_def_app_icon
        copyright.gravity = Gravity.CENTER
        return copyright
    }
}