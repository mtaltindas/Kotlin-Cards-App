package com.altindas.notesapp

import android.app.DatePickerDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import java.util.Calendar


class InsideNoteActivity : AppCompatActivity() {
    lateinit var in_titleEditView:TextView
    lateinit var in_detailEditView:TextView
    lateinit var in_dateTextView:TextView
    lateinit var in_deleteButton: Button
    lateinit var in_saveButton: Button
    lateinit var in_hideSwitch: Switch

    lateinit var db: DBHandler
    var selectDate = ""
    var date_flag:Boolean=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inside_note)
        //Initializations---------------------------------------------
        var title = intent.getStringExtra("title")
        var detail = intent.getStringExtra("detail")
        var date = intent.getStringExtra("date")
        var nid = intent.getIntExtra("nid",-1)
        var createNoteFlag = intent.getBooleanExtra("createNoteFlag",false)
        var hidden = intent.getStringExtra("hidden")
        val calender = Calendar.getInstance()
        db = DBHandler(this)
        in_titleEditView = findViewById(R.id.in_TitleEditView)
        in_detailEditView = findViewById(R.id.in_detailEditView)
        in_dateTextView = findViewById(R.id.in_dateTextView)
        in_deleteButton = findViewById(R.id.in_deleteButton)
        in_saveButton = findViewById(R.id.in_saveButton)
        in_hideSwitch = findViewById(R.id.in_hideSwitch)
        //--------------------------------------------------------
        in_titleEditView.hint = getString(R.string.in_def_hintTitle)
        in_detailEditView.hint = getString(R.string.in_def_hintDetail)
        in_titleEditView.text=title
        in_detailEditView.text=detail
        in_dateTextView.text=date
        selectDate=in_dateTextView.text.toString()
        date_flag=checkDate()
        //Not silme------------------------------------------------------------------------------
        in_deleteButton.setOnClickListener(){
            db.deleteNote(nid)
            finish()
        }
        //Not kayıt------------------------------------------------------------------------------
        in_saveButton.setOnClickListener(){
            if (date_flag) {
                if (createNoteFlag) {//ilk defa oluşturulan notlar SQL-INSERT
                    db.addNote(
                        in_titleEditView.text.toString(),
                        in_detailEditView.text.toString(),
                        selectDate,
                        hidden!!
                    )
                }
                else {//varolan notları  SQL-UPDATE
                    db.updateNote(
                        in_titleEditView.text.toString(),
                        in_detailEditView.text.toString(),
                        selectDate,
                        hidden!!,
                        nid
                    )
                }
                finish()
            }
            else{
                Toast.makeText(this, getString(R.string.in_toast_date), Toast.LENGTH_LONG).show()
            }
        }
        //Hidden Switch------------------------------------------------------------------------------
        in_hideSwitch.isChecked = (hidden=="private")

        in_hideSwitch.setOnCheckedChangeListener { _, isChecked ->
            hidden = if(isChecked){
                "private"
            }else{
                "public"
            }
        }
        //Tarih düzenleme------------------------------------------------------------------------------
        in_dateTextView.setOnClickListener(){
            val datePickerDialog = DatePickerDialog(
                this,
                { _, i, i2, i3 ->
                    Log.d("i", i.toString()) // yıl
                    Log.d("i2", (i2 + 1).toString()) // ay
                    Log.d("i3", i3.toString()) // gün
                    var ay = "${i2+1}"
                    if ( i2+1 < 10 ) {
                        ay = "0${i2+1}"
                    }
                    selectDate = "$i3.$ay.$i"
                    in_dateTextView.text=selectDate
                    date_flag=checkDate()
                },
                calender.get(Calendar.YEAR),
                calender.get(Calendar.MONTH),
                calender.get(Calendar.DAY_OF_MONTH),
            )
            datePickerDialog.show()
        }

    }
    //oncreate ends here------------------------------------------------------------------------------
    private fun checkDate(): Boolean {
        return in_dateTextView.text.toString() != getString(R.string.in_def_date)
    }

}