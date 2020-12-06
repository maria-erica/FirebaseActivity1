package com.example.firebaseactivity

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.example.firebaseactivity.adapter.arrayAdapter
import com.example.firebaseactivity.models.ytListTableHandler
import com.example.firebaseactivity.objectHandlers.ytList
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    lateinit var adapter : arrayAdapter
    var ytHandler = ytListTableHandler()
    var listArr = arrayListOf<ytList>()

    lateinit var listView : ListView
    lateinit var title : EditText
    lateinit var link : EditText
    lateinit var reason: EditText
    lateinit var rank : EditText
    lateinit var addBtn : Button
    var reasons = " "
    var ytID = " "
    lateinit var reasonText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listView = findViewById<ListView>(R.id.ytList)
        title = findViewById(R.id.title)
        reason = findViewById(R.id.reasonEditText)
        rank = findViewById(R.id.ranking)

        link = findViewById(R.id.link)
        addBtn = findViewById(R.id.addBtn)
        addBtn.setOnClickListener {data()}
        registerForContextMenu(listView)

        listView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, l ->
                val inflater = this.layoutInflater
                val view = inflater.inflate(R.layout.display_res_link,null)
                val ytSelected = listArr[position]
                val ytlink = ytSelected.link
                val ytReason = ytSelected.reason
                val resTxt = view.findViewById<TextView>(R.id.resView)
                val linkTxt = view.findViewById<TextView>(R.id.linkView)
                linkTxt.setText(ytlink)
                resTxt.setText(ytReason)
                val dialogBuilder = AlertDialog.Builder(this)
                    .setView(view)
                    .setNegativeButton("OK", DialogInterface.OnClickListener {
                            dailog, i ->
                    })
                dialogBuilder.create()
                dialogBuilder.show()
            }
    }
    fun data(){
        val titleTxt = title.text.toString()
        val linkTxt = link.text.toString()
        val rankTxt = rank.text.toString()
        val reasonTxt = reason.text.toString()

        if (addBtn.text.toString() == "Add"){
            try {
                var rankInt = rankTxt.toInt()
                val yt = ytList(title = titleTxt, link = linkTxt,reason = reasonTxt,rank = rankInt)
                if (ytHandler.create(yt)) {
                    Toast.makeText(this,"Youtube Channel Added", Toast.LENGTH_SHORT).show()
                    clearField()
                }
            }catch (e: Exception){
                Toast.makeText(this,"Rank must be a Number", Toast.LENGTH_LONG).show()
            }
        }
        else if (addBtn.text.toString() == "Update"){
            try {
                var rankInt = rankTxt.toInt()
                val yt = ytList(id = ytID, title = titleTxt, link = linkTxt, rank = rankInt, reason = reasonTxt)
                if (ytHandler.edit(yt)) {
                    Toast.makeText(this, "Youtube Channel Edited", Toast.LENGTH_LONG).show()
                    clearField()
                }
            }catch (e: Exception){
                Toast.makeText(this, "Rank must be a Number", Toast.LENGTH_SHORT).show()
            }
        }

    }
    fun clearField(){
        title.text.clear()
        link.text.clear()
        rank.text.clear()
        reason.text.clear()
        addBtn.setText("Add")
    }
    override fun onStart(){
        super.onStart()
        ytHandler.ytRef.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                // TODO("Not yet implemented")
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                listArr.clear()
                snapshot.children.forEach { it  ->
                    var yt = it.getValue(ytList::class.java)
                    listArr.add(yt!!)
                    listArr.sortBy { it.rank }
                }

                adapter = arrayAdapter(applicationContext, R.layout.list_layout,listArr)
                listView.adapter = adapter
            }

        })

    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        menuInflater.inflate(R.menu.option_del_edit,menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info =  item.menuInfo as AdapterView.AdapterContextMenuInfo
        val yt = listArr[info.position]
        ytID = yt.id.toString()
        when(item.itemId){
            R.id.deleteYT ->{
                ytHandler.delete(ytID)
                return true
            }
            R.id.editYT ->{
                title.setText(yt.title)
                link.setText(yt.link)
                reason.setText(yt.reason)
                rank.setText(yt.rank.toString())
                addBtn.setText("Update")
                addBtn.setOnClickListener {
                    data() }
                return true
            }
            else -> return super.onContextItemSelected(item)
        }
    }
}