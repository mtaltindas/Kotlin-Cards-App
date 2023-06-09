package com.altindas.notesapp
import android.annotation.SuppressLint
import com.altindas.notesapp.Models.Note


import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DBHandler(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION ){

    override fun onCreate(db: SQLiteDatabase?) {
        val noteTable = "CREATE TABLE note (\n" +
                "$COL_ID     INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COL_TITLE TEXT,"  +
                "$COL_DETAIL TEXT," +
                "$COL_DATE TEXT,"+
                "$COL_HIDDEN TEXT);"
        db?.execSQL(noteTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val noteTableDrop = "DROP TABLE IF EXISTS note"
        db?.execSQL(noteTableDrop)
        onCreate(db)
    }

//#####################################################################################

    fun addNote( title:String, detail: String, date: String,hidden:String) : Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_TITLE, title)
        values.put(COL_DETAIL, detail)
        values.put(COL_HIDDEN, hidden)
        values.put(COL_DATE, date)

        val effectRow = db.insert("note", null, values)
        db.close()
        return effectRow
    }

    fun deleteNote(nid: Int) : Int {
        val db = this.writableDatabase
        val status = db.delete("note", "nid = $nid", null )
        db.close()
        return status
    }

    fun  updateNote(title:String, detail: String,date:String,hidden:String, nid: Int) : Int {
        val db = this.writableDatabase
        val content = ContentValues()
        content.put(COL_TITLE, title)
        content.put(COL_DETAIL, detail)
        content.put(COL_HIDDEN, hidden)
        content.put(COL_DATE,date)

        val status = db.update("note", content, "nid = $nid", null)
        db.close()
        return status
    }

    @SuppressLint("Recycle")
    fun allNote() : List<Note> {
        val db = this.readableDatabase
        val arr = mutableListOf<Note>()
        val cursor = db.query("note",null, null, null, null, null, null)
        while (cursor.moveToNext()) {
            val nid = cursor.getInt(0)
            val title = cursor.getString(1)
            val detail = cursor.getString(2)
            val date = cursor.getString(4)
            val hidden = cursor.getString(3)

            val note = Note(nid, title, detail, hidden,date)
            Log.d("db",date.toString())
            arr.add(note)
        }
        db.close()
        return arr
    }




}