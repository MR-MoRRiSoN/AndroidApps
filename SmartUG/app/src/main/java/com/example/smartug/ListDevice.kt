package com.example.smartug

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartug.session.LoginPref
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ListDevice : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var list: ArrayList<ItemDatabase>
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var mAuth: FirebaseAuth
    private lateinit var adapter: RecyclerAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var session: LoginPref
    private lateinit var textView: LinearLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_device)


/*      val actionBar: ActionBar? = supportActionBar
        val colorDrawable = ColorDrawable(Color.parseColor("#ed1b24"))
        actionBar?.setBackgroundDrawable(colorDrawable)
*/

        recyclerView = findViewById(R.id.rv_recyclerView)
        list = ArrayList()
        layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        adapter = RecyclerAdapter(list, this)
        firebaseDatabase = FirebaseDatabase.getInstance()
        session = LoginPref(this)
        session.checkLogin()
        checkNetworkConnection()
        getAccesOnBase()
        arrayList(false)


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.costum_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.florOption -> {
                showAlertDialog()
                return true
            }
            R.id.exit -> {
                session.logOutUser()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun arrayList(check: Boolean): Array<String> {
        if (!check) {

            firebaseDatabase.reference.child("ListOfDevice")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        saveDate(snapshot.children.count(), 2)
                        //   Toast.makeText(this@ListDevice,snapshot.children.count().toString(),Toast.LENGTH_LONG).show()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
        } else {

            val sharedPreferences = getSharedPreferences("sharePrefs", Context.MODE_PRIVATE)
            val countedNumber = sharedPreferences.getInt("numberOfFloor", 0)
            val arrayList = Array(countedNumber) { "" }
            for (i in 0 until countedNumber) {
                arrayList[i] = "Floor_N$i"
            }
            return arrayList
        }
        return arrayOf("Error")
    }


    private fun showAlertDialog() {
        textView = findViewById(R.id.textView)
        val arrayList = arrayList(true)
        val mBuilder = AlertDialog.Builder(this@ListDevice)
        mBuilder.setTitle("Choose Your Flour")
        mBuilder.setSingleChoiceItems(arrayList, -1) { dialogInterface, i ->
            saveDate(i, 3)
            getListOfFloor(i)
            dialogInterface.dismiss()
            textView.visibility = View.GONE
        }

        val mDialog = mBuilder.create()
        mDialog.show()

    }

    private fun getListOfFloor(i: Int) {
        firebaseDatabase.reference.child("ListOfDevice").child("Floor_$i")
            .addValueEventListener(object : ValueEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    list.clear()
                    try {
                        for (dtsnapshot in snapshot.children) {

                            val data = dtsnapshot.getValue(ItemDatabase::class.java)
                            list.add(data!!)
                        }
                        //         saveDate(snapshot.children.count(), 1)
                        adapter.notifyDataSetChanged()
                        recyclerView.adapter = adapter
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@ListDevice, "ERROR IN DATABASE", Toast.LENGTH_SHORT
                        ).show()
                        println("###########################################")
                        println(e)
                        println("###########################################")
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ListDevice, "Error", Toast.LENGTH_SHORT).show()
                }
            })

    }

    private fun getAccesOnBase() {
        val user: HashMap<String, String> = session.getUserDetails()
        val email = user[LoginPref.KEY_EMAIL]
        val passwd = user[LoginPref.KEY_Passwd]
        if (haveNetwork()) {
            mAuth = FirebaseAuth.getInstance()
            mAuth.signInWithEmailAndPassword(email!!, passwd!!)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        //    var sayMyID = FirebaseAuth.getInstance().currentUser?.uid
                    } else {
                        Toast.makeText(this, "Passwd Was Changed In Database", Toast.LENGTH_LONG)
                            .show()
                        session.logOutUser()

                    }

                }
        }

    }

    @SuppressLint("CommitPrefEdits")
    private fun saveDate(countedNumber: Int, mode: Int) {
        val sharedPreferences = getSharedPreferences("sharePrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        when (mode) {
            1 -> {

                // editor.apply {
                //     putInt("numberOfRoom", countedNumber)
                //}.apply()
            }
            2 -> {
                editor.apply {
                    putInt("numberOfFloor", countedNumber)
                }.apply()
            }
            3 -> {
                editor.apply {
                    putInt("currentFloor", countedNumber)
                }.apply()
            }
        }

    }

    private fun haveNetwork(): Boolean {
        var haveWifi = false
        var haveMobile = false
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.allNetworkInfo
        for (info in networkInfo) {
            if (info.typeName.equals("WIFI", ignoreCase = true)) if (info.isConnected) haveWifi =
                true
            if (info.typeName.equals(
                    "MOBILE",
                    ignoreCase = true
                )
            ) if (info.isConnected) haveMobile = true
        }
        return haveMobile || haveWifi
    }

    private fun restartApp() {
        startActivity(Intent(applicationContext, ListDevice::class.java))
        finish()
    }

    private fun checkNetworkConnection() {

        if (haveNetwork()) {

        } else {
            val builder = AlertDialog.Builder(this@ListDevice)
            builder.setTitle("Smart UG")
            builder.setMessage("Internet Not Found")
            builder.setNegativeButton("Cancel") { _, _ -> finish() }
            builder.setPositiveButton("Try Again") { _, _ -> restartApp() }
            builder.show()
        }

    }

}
