package com.example.firebaseactivity.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.firebaseactivity.R
import com.example.firebaseactivity.objectHandlers.ytList

class arrayAdapter (var mcont: Context, var res: Int, var list: List<ytList>): ArrayAdapter<ytList>(mcont,res, list ){
    override fun getCount(): Int {
        return list.size
    }
    override fun getItem(position: Int) : ytList? {
        return list[position]
    }
    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(mcont)
        val view = inflater.inflate(res, null)
        val title = view.findViewById<TextView>(R.id.title)
        val rank = view.findViewById<TextView>(R.id.ranking)
        title.text = list[position].title
        rank.text = list[position].rank.toString()
        return view
    }
}