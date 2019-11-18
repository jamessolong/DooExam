package com.example.user.dooexam

import android.content.ContentValues
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_add_exam.*

class AddExamActivity : AppCompatActivity() {

    var id = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_exam)

        try {
            val bundle:Bundle = intent.extras
            id = bundle.getInt("ID", 0)
            if (id!=0){
                subject_txt.setText(bundle.getString("name"))
                desc_txt.setText(bundle.getString("des"))
            }
        }catch (ex:Exception){}
    }

    fun addFunc(view:View){
        var dbManager = DbManager(this)

        var values = ContentValues()
        values.put("Title", subject_txt.text.toString())
        values.put("Description", desc_txt.text.toString())

        if (id ==0){
            val ID = dbManager.insert(values)
            if (ID>0){
                Toast.makeText(this, "Exam is added", Toast.LENGTH_SHORT).show()
                finish()
            }
            else{
                Toast.makeText(this, "Error to adding Exam", Toast.LENGTH_SHORT).show()
            }
        }
        else{
            var selectionArgs = arrayOf(id.toString())
            val ID = dbManager.update(values, "ID=?", selectionArgs)
            if (ID>0){
                Toast.makeText(this, "Exam is Updated", Toast.LENGTH_SHORT).show()
                finish()
            }
            else{
                Toast.makeText(this, "Error to Update Exam", Toast.LENGTH_SHORT).show()
            }
        }
    }
}