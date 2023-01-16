package com.example.inkoman

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ChatActivity : AppCompatActivity() {
    private lateinit var chatRecycleView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sendButton: ImageView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var mDbRef: DatabaseReference
    private lateinit var sayMyName: String
    var sendOrRecive: Boolean = true


    var reciverRoom: String? = null
    var senderRoom: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val name = intent.getStringExtra("name")
        val receiverUid = intent.getStringExtra("uid")
        val senderUid = FirebaseAuth.getInstance().currentUser?.uid

        mDbRef = FirebaseDatabase.getInstance().reference
        senderRoom = receiverUid + senderUid
        reciverRoom = senderUid + receiverUid

        supportActionBar?.title = name

        chatRecycleView = findViewById(R.id.chatRecyclerView)
        messageBox = findViewById(R.id.messageBox)
        sendButton = findViewById(R.id.sentButton)
        messageList = ArrayList()
        messageAdapter = MessageAdapter(this, messageList)

        chatRecycleView.layoutManager = LinearLayoutManager(this)
        chatRecycleView.adapter = messageAdapter


        getCurrentName(senderUid!!)
        getMessage()
        sendMessageAndNotification(senderUid, receiverUid!!, name!!)


    }

    private fun getCurrentName(senderUid: String) {

        mDbRef.child("user").child(senderUid).child("name").addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    sayMyName = snapshot.value.toString()
                }

                override fun onCancelled(error: DatabaseError) {

                }

            }
        )
    }

    private fun getMessage() {
        //recipe view from database
        mDbRef.child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    for (postSnapshot in snapshot.children) {
                        val message = postSnapshot.getValue(Message::class.java)
                        messageList.add(message!!)
                        //
                    }

                    chatRecycleView.smoothScrollToPosition((chatRecycleView.adapter as MessageAdapter).itemCount)
                    messageAdapter.notifyDataSetChanged()

                }

                override fun onCancelled(error: DatabaseError) {


                }
            })
    }

    private fun sendMessageAndNotification(senderUid: String, receiverUid: String, name: String) {
        sendButton.setOnClickListener {
            //adding massage database
            val message = messageBox.text.toString()
            val messageObject = Message(message, senderUid)
            sendOrRecive = true
            mDbRef.child("chats").child(senderRoom!!).child("messages").push()
                .setValue(messageObject).addOnSuccessListener {

                    mDbRef.child("chats").child(reciverRoom!!).child("messages").push()
                        .setValue(messageObject)

                    mDbRef.child("history").child(receiverUid.toString()).child(name.toString())
                        .setValue("nameOfReciver")
                    mDbRef.child("history").child(receiverUid.toString()).child("nameAndMessage")
                        .setValue("$sayMyName::$message")

                    mDbRef.child("history").child(senderUid).child(sayMyName).removeValue()
                        .addOnSuccessListener {
                            mDbRef.child("history").child(senderUid).removeValue()
                        }

                }
            messageBox.setText("")


        }
    }
}