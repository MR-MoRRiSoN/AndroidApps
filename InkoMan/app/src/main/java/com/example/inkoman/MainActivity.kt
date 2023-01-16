package com.example.inkoman

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import java.lang.Exception
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userList: ArrayList<User>
    private lateinit var adapter: UserAdapter
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private var sayMyID = FirebaseAuth.getInstance().currentUser?.uid
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        userList = ArrayList()
        mDbRef = FirebaseDatabase.getInstance().reference
        adapter = UserAdapter(this, userList)
        userRecyclerView = findViewById(R.id.userRecyclerView)
        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.adapter = adapter




/*
        if (ActivityCompat.checkSelfPermission((this,android.Manifest.permission.RECORD_AUDIO)!=PackageManager.PERMISSION_GRANTED))
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.RECORD_AUDIO,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)111)*/
        detectActivist()
  //      chekNotif()
        getUser()
        Toast.makeText(this,"ok${sayMyID}",Toast.LENGTH_SHORT).show()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.logout) {
            mAuth.signOut()
            intent = Intent(this@MainActivity, Login::class.java)
            finish()
            startActivity(intent)
            return true
        }
        return true
    }

    private fun showNotification(name: String, message: String) {
        val channelID = "1"


        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(applicationContext, channelID)
            .setSmallIcon(R.drawable.incologo)
            .setContentTitle("Sent Message")
            .setContentText("message")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelID,
                "@ Notification",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.enableVibration(true)
            notificationManager.createNotificationChannel(channel)
            builder.setChannelId(channelID)
            val notification = builder.build()

            notificationManager.notify(2, notification)


        }

    }
    private fun detectActivist(){
        if (sayMyID == null) {
            intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
    }
    private fun chekNotif(){
        mDbRef.child("history").child(sayMyID!!).addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    adapter.notifyDataSetChanged()

                    mDbRef.child("history").child(sayMyID!!).child("")
                        .addValueEventListener(
                            object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {

                                    val nameAndMessage =
                                        snapshot.value.toString()

                                   showNotification(nameAndMessage," nameAndMessage[1]")


                                }

                                override fun onCancelled(error: DatabaseError) {

                                }
                            }
                        )

                }

                override fun onCancelled(error: DatabaseError) {

                }
            }
        )
    }
    private fun getUser(){
        mDbRef.child("user").addValueEventListener(
            object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    userList.clear()
                    for (postSnapshot in snapshot.children) {
                        val currentUser = postSnapshot.getValue((User::class.java))
                        userList.add(currentUser!!)
                    }
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }
    private fun sayMyName() {
        var sayMyName:Any
        sayMyName=  mDbRef.child("user").child(sayMyID!!).child("name").database.reference




    }
}


