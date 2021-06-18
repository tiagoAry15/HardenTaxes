package com.example.harden_taxes.Adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.harden_taxes.model.Apartment
import com.example.todolist.adapter.ApartmentListener
import com.example.harden_taxes.R
import com.google.firebase.database.FirebaseDatabase

class ApartmentAdapter(val users: List<Apartment>): RecyclerView.Adapter<ApartmentAdapter.UserViewHolder>() {

    private var listener: ApartmentListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {

        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.user_item_list, parent, false)
        return UserViewHolder(view, listener)

    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.iName.text = (users[position].firstName + " " + users[position].lastName)

        holder.iFloor.text = users[position].floor.toString()


        FirebaseDatabase.getInstance().reference.child("/")
        if(users[position].monthTax == "0" || users[position].email == ""){
            holder.iOverdue.setText("sem valor")
        }
        else {
            if (users[position].regular) {
                holder.iOverdue.setText("Regular")
                holder.iOverdue.setTextColor(Color.GREEN)
            } else {
                holder.iOverdue.setText("Inadimplente")
                holder.iOverdue.setTextColor(Color.RED)
            }
        }

    }

    fun setOnUserItemClickListener(listener: ApartmentListener) {
        this.listener = listener
    }
    class UserViewHolder(view: View, val listener: ApartmentListener?) :
        RecyclerView.ViewHolder(view) {
        val iName:TextView = view.findViewById(R.id.user_textview_name)
        val iFloor:TextView = view.findViewById(R.id.user_textview_apart)
        val iOverdue:TextView = view.findViewById(R.id.user_textview_situation)
        init {
            view.setOnClickListener {
                listener?.onClick(it, adapterPosition)

            }
            view.setOnLongClickListener {
                listener?.onLongClick(it, adapterPosition)
                true
            }
        }
    }
}