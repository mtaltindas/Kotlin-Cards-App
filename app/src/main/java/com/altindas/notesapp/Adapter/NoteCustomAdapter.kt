package com.altindas.notesapp.Adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.altindas.notesapp.Models.Note
import com.altindas.notesapp.R

class NoteCustomAdapter(private val context: Activity, private val list: List<Note>) : ArrayAdapter<Note>(context,R.layout.note_list_item, list ) {

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val rootView = context.layoutInflater.inflate(R.layout.note_list_item, null, true)

        val title1 = rootView.findViewById<TextView>(R.id.textTitle)
        val detail1 = rootView.findViewById<TextView>(R.id.textDetail)
        val date = rootView.findViewById<TextView>(R.id.dateView)
    //   val detail2 = rootView.findViewById<TextView>(R.id.detail2)

        val note = list[position]
        Log.d("list",note.toString())

        title1.text=note.title
        detail1.text=note.detail
        date.text=note.date
        Log.d("adapter",date.text.toString())

    //    title2.text=nextNote.title
    //    detail2.text=nextNote.detail

        return rootView
    }


}