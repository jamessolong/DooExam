package com.example.user.dooexam


import android.app.AlertDialog
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.row.view.*


/////////////////////////////////////////////////////////////////////////////////////////////////MainActivit///

class MainActivity : AppCompatActivity() {

    var listExams = ArrayList<Exam>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        LoadQuery("%")
    }

//////////////////////////////////////////////////////////////////////////////////////////////////onResume///

    override fun onResume() {
        super.onResume()
        LoadQuery("%")
    }

////////////////////////////////////////////////////////////////////////////////////////////////////LoadQuery///

    private fun LoadQuery(title: String) {
        var dbManager = DbManager(this)
        val projections = arrayOf("ID", "Title", "Description")
        val selectionArgs = arrayOf(title)
        val cursor = dbManager.Query(projections, "Title like ?", selectionArgs, "Title")
        listExams.clear()
        if (cursor.moveToFirst()) {

            do {
                val ID = cursor.getInt(cursor.getColumnIndex("ID"))
                val Title = cursor.getString(cursor.getColumnIndex("Title"))
                val Description = cursor.getString(cursor.getColumnIndex("Description"))

                listExams.add(Exam(ID, Title, Description))

            } while (cursor.moveToNext())
        }

///////////////////////////////////////////////////////////////////////////////////////////////adapter & actionbar///

        var myExamAdapter = MyExamAdapter(this, listExams)
        //set adapter
        examList.adapter = myExamAdapter

        //get size ListView
        val total = examList.count
        //actionbar
        val mActionBar = supportActionBar
        if (mActionBar != null) {

            if(total == 1 || total == 0){
                mActionBar.subtitle = "You have $total Exam"
            }
            else{
                mActionBar.subtitle = "You have $total Exams"
            }

        }
    }

/////////////////////////////////////////////////////////////////////////////////////////onCreateOptionsMenu///
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        ///////////////////////////////////////////////////////////////////////////////////////////search/// StackOverflow

    val sv: SearchView = menu!!.findItem(R.id.app_bar_search).actionView as SearchView

        val sm = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        sv.setSearchableInfo(sm.getSearchableInfo(componentName))
        sv.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                LoadQuery("%" + query + "%")
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                LoadQuery("%" + newText + "%")
                return false
            }
        });

        return super.onCreateOptionsMenu(menu)
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////onOptionsItemSelected///

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item != null) {
            when (item.itemId) {
                R.id.addExam -> {
                    startActivity(Intent(this, AddExamActivity::class.java))
                }
                R.id.action_settings -> {
                    startActivity(Intent(this, AboutActivity::class.java))

                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////Adapter///


    inner class MyExamAdapter : BaseAdapter {
        var listExamAdapter = ArrayList<Exam>()
        var context: Context? = null

        constructor(context: Context, listNotesAdapter: ArrayList<Exam>) : super() {
            this.listExamAdapter = listNotesAdapter
            this.context = context
        }


        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            /////////////////////////////////////////////////////////////////inflate layout row.xml
            var myView = layoutInflater.inflate(R.layout.row, null)
            val myExam = listExamAdapter[position]
            //////////-----//////////
            myView.subjextTV.text = myExam.subName
            myView.descTV.text = myExam.subDes
            /////////////////////////////////////////////////////////////////delete button click
            myView.deleteBtn.setOnClickListener {
                var dbManager = DbManager(this.context!!)
                val selectionArgs = arrayOf(myExam.subID.toString())
                dbManager.delete("ID=?", selectionArgs)
                Toast.makeText(this@MainActivity, "Deleted", Toast.LENGTH_SHORT).show()
                LoadQuery("%")
            }
            ///////////////////////////////////////////////////////////////////update button
            myView.editBtn.setOnClickListener {
                GoToUpdateFun(myExam)
            }

            //share btn click
            myView.shareBtn.setOnClickListener {
                //get title
                val subject = myView.subjextTV.text.toString()
                //get description
                val desc = myView.descTV.text.toString()
                //concatenate
                val s = subject + "\n" + desc
                //share intent
                val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_TEXT, s)
                startActivity(Intent.createChooser(shareIntent, s))
            }

            return myView
        }

        override fun getItem(position: Int): Any {
            return listExamAdapter[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return listExamAdapter.size
        }

    }

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun GoToUpdateFun(myNote: Exam) {
        var intent = Intent(this, AddExamActivity::class.java)
        intent.putExtra("ID", myNote.subID)
        intent.putExtra("name", myNote.subName)
        intent.putExtra("des", myNote.subDes)
        startActivity(intent) //start activity
    }


}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////