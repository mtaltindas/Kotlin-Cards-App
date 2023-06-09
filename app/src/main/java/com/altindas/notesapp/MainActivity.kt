package com.altindas.notesapp

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.altindas.notesapp.Adapter.NoteCustomAdapter
import com.altindas.notesapp.Models.Note
import java.util.Locale


class MainActivity : AppCompatActivity() {


    lateinit var db: DBHandler
    lateinit var addNoteButton: Button
    lateinit var settingsButton: Button
    lateinit var empty: TextView
    lateinit var notesListView: ListView
    lateinit var customAdapter: NoteCustomAdapter
    var emptyListinBegin:Boolean = false

    override fun onResume() {
        super.onResume()
        emptyCheck();refreshList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadLocate()
        setContentView(R.layout.activity_main)
        //Initializations
        db = DBHandler(this)
        addNoteButton = findViewById(R.id.addNoteButton)
        notesListView = findViewById(R.id.notesListView)
        settingsButton = findViewById(R.id.settingsButton)
        empty = findViewById(R.id.empty)
        emptyCheck()
        //ListView Adapter
        customAdapter = NoteCustomAdapter(this,db.allNote())
        notesListView.adapter = customAdapter

        //Ayarlar butonu
        settingsButton.setOnClickListener { //To register the button with context menu.
            val popupMenu = PopupMenu(this, settingsButton)
            popupMenu.menuInflater.inflate(R.menu.setting_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.language -> {
                        showChangeLang()
                    }
                    R.id.unknown -> {
                        changeHiddenPreferences()
                    }
                    R.id.deleteDB -> {
                        deleteDB()
                    }
                    R.id.aboutus -> {
                        val intent = Intent(this, AboutusActivity::class.java)
                        intent.putExtra("description", getString(R.string.app_description))
                        startActivity(intent)
                    }
                }
                true
            }
            popupMenu.show()
        }

       //ListView Click
        notesListView.setOnItemClickListener{ _, _, position, _ ->
            startInsideNoteActivity(position)
        }
        notesListView.setOnItemLongClickListener { _, viewLong, positionLong, _ ->
            val notesList=refreshList()
            val popupMenu = PopupMenu(this,viewLong)
            popupMenu.menuInflater.inflate(R.menu.list_menu,popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.delete -> {
                        db.deleteNote(notesList[positionLong].nid)
                        emptyCheck();refreshList()
                    }
                    R.id.edit -> {
                        startInsideNoteActivity(positionLong)
                    }
                }
                false
            }
            popupMenu.show()
            true
        }

        //Not Ekleme Click
        addNoteButton.setOnClickListener {
            startInsideNoteActivity_default()
        }
    }//Oncreate ends here---------------------------------------------------------------------------

    private fun emptyCheck(){
        if(db.allNote().isEmpty()){
            emptyListinBegin=true
            notesListView.visibility = View.GONE
            empty.visibility = View.VISIBLE
        }
        else{
            notesListView.visibility = View.VISIBLE
            empty.visibility = View.GONE
        }
    }
    private fun startInsideNoteActivity(noteIndex:Int){
        val myNotes=refreshList()
        val note = (myNotes)[noteIndex]
        val intent = Intent(this, InsideNoteActivity::class.java)
        intent.putExtra("title", note.title)
        intent.putExtra("detail",note.detail)
        intent.putExtra("date", note.date)
        intent.putExtra("nid", note.nid)
        intent.putExtra("createNoteFlag", false)
        intent.putExtra("hidden", note.hidden)
        startActivity(intent)
    }
    private fun startInsideNoteActivity_default(){
        val intent = Intent(this, InsideNoteActivity::class.java)
        intent.putExtra("title", "") //getString(R.string.in_def_title))
        intent.putExtra("detail","")//getString(R.string.in_def_detail))
        intent.putExtra("date", getString(R.string.in_def_date))
        intent.putExtra("nid", "noNid")
        intent.putExtra("createNoteFlag", true)
        intent.putExtra("hidden", "public")

        startActivity(intent)
    }
    private fun refreshList():MutableList<Note>{
        val shared=getSharedPreferences("HiddenSettings", Activity.MODE_PRIVATE)
        val noteitems=mutableListOf<Note>()
        if(shared.getString("hidden","DEFAULT_VALUE")=="public"){
            for (item in db.allNote()){
                val note = Note(
                    item.nid,
                    item.title,
                    item.detail,
                    item.date,
                    item.hidden
                )
                noteitems.add(note)
            }
        }else{
            for (item in db.allNote()){
                if(item.hidden=="public"){
                    val note = Note(item.nid, item.title, item.detail, item.date, item.hidden)
                    noteitems.add(note)
                }
            }
        }
        notesListView.adapter = NoteCustomAdapter(this,noteitems)
        customAdapter.notifyDataSetChanged()
        return noteitems
    }

    private fun showChangeLang(){
        val listLang= arrayOf("Türkçe","English")
        val mBuilder=AlertDialog.Builder(this)
        mBuilder.setTitle("Choose Language/Dil Seçin")
        mBuilder.setSingleChoiceItems(listLang,-1){dialog,which->
            if(which==0){
                setLocate("tr")
                recreate()
            }else{
                setLocate("en")
                recreate()
            }
            dialog.dismiss()
        }
        val mDialog=mBuilder.create()
        mDialog.show()
    }

    private fun setLocate(Lang:String){
       val locale=Locale(Lang)
       Locale.setDefault(locale)
       val config=Configuration()
       config.locale=locale
       baseContext.resources.updateConfiguration(config,baseContext.resources.displayMetrics)
       val editor=getSharedPreferences("Settings",Context.MODE_PRIVATE).edit()
       editor.putString("My_Lang",Lang)
       editor.apply()
    }

    private fun loadLocate(){

        val sharedPreferences=getSharedPreferences("Settings", Activity.MODE_PRIVATE)
        val language=sharedPreferences.getString("My_Lang","")
        Log.d("dil",language.toString())
        if (language != null) {
            setLocate(language)
        }
    }

    private fun changeHiddenPreferences(){
        val shared=getSharedPreferences("HiddenSettings", Activity.MODE_PRIVATE).edit()

        val mBuilder=AlertDialog.Builder(this)
        mBuilder.setTitle(getString(R.string.m_hiddenText))

        mBuilder.setPositiveButton(getString(R.string.m_showButton)){ _,_ ->
            shared.putString("hidden","public")
            shared.apply()
            refreshList()
        }
        mBuilder.setNeutralButton(getString(R.string.m_noshowButton)){ _, _ ->
            shared.putString("hidden","private")
            shared.apply()
            refreshList()
        }

        val mDialog=mBuilder.create();mDialog.show()
    }

    private fun deleteDB(){
        val mBuilder=AlertDialog.Builder(this)
        mBuilder.setTitle(getString(R.string.m_deleteAll_title))

        mBuilder.setPositiveButton(getString(R.string.m_deleteAll_positiveButton)){_, _ ->
            for(item in db.allNote()){
                db.deleteNote(item.nid)
            }
            emptyCheck()
            refreshList()
        }
        mBuilder.setNeutralButton(getString(R.string.m_deleteAll_cancelButton)){_, _ ->
            refreshList()
        }
        val mDialog=mBuilder.create();mDialog.show()
    }
}


/*
    notları kategorilendirme?
    gizli notlar için passwd?
    pagination?
 */