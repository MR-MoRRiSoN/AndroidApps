package com.example.smartug

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecyclerAdapter(private var list: ArrayList<ItemDatabase>, private var context: Context) :
    RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var designTitle: TextView = itemView.findViewById(R.id.tv_title)
        var designTitle2: TextView = itemView.findViewById(R.id.tv_description)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = list[position]

        //      Glide.with(context).load(currentItem.image).into(holder.designImage); if want add pic
        holder.designTitle.text = currentItem.title
        holder.designTitle2.text = currentItem.disc

        //val currentUser = list[position]
        holder.itemView.setOnClickListener {
            val intent = Intent(context, Configuration::class.java)
            intent.putExtra("currentRoom", (position+1).toString())
            context.startActivity(intent)
        }


    }

    override fun getItemCount(): Int {
        return list.size
    }
}