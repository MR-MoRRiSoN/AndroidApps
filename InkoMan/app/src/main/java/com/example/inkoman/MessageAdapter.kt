package com.example.inkoman

import android.app.Notification
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter(val context: ChatActivity, private val messageList: ArrayList<Message>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val itemRecive = 1;
    private val itemSend = 2;
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 1) {
            //recive
            val view: View = LayoutInflater.from(context).inflate(R.layout.recive, parent, false)
            ReciveViewHolder(view)
        } else {
            //sent
            val view: View = LayoutInflater.from(context).inflate(R.layout.sent, parent, false)
            SentViewHolder(view)
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = messageList[position]
        if (holder.javaClass == SentViewHolder::class.java) {
            //send holder
            val viewHolder = holder as SentViewHolder
            holder.sentMessage.text = currentMessage.massage
        } else {
            //recive holder
            val viewHolder = holder as ReciveViewHolder
            holder.reciveMessage.text = currentMessage.massage
        }
    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList[
                position]
        if (FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.senderId)) {
            return itemSend
        } else {
            return itemRecive
        }
    }

    override fun getItemCount(): Int {
        return messageList.size

    }

    class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sentMessage: TextView = itemView.findViewById<TextView>(R.id.text_sent_message)
    }

    class ReciveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val reciveMessage: TextView = itemView.findViewById<TextView>(R.id.text_Recive_message)

    }



}