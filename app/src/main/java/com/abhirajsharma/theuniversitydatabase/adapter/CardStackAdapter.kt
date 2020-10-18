package com.abhirajsharma.theuniversitydatabase.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.abhirajsharma.theuniversitydatabase.R
import com.abhirajsharma.theuniversitydatabase.model.UsersItemModel
import com.squareup.picasso.Picasso


class CardStackAdapter(private var usersItems: List<UsersItemModel>) :
    RecyclerView.Adapter<CardStackAdapter.ViewHolder>() {

    fun getItems(): List<UsersItemModel> {
        return usersItems
    }

    fun setItems(usersItems: List<UsersItemModel>) {
        this.usersItems = usersItems
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.item_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(usersItems[position])
    }

    override fun getItemCount(): Int {
        return usersItems.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var image: ImageView = itemView.findViewById(R.id.item_image)
        private var nama: TextView = itemView.findViewById(R.id.item_name)
        private var usia: TextView = itemView.findViewById(R.id.item_age)
        private var kota: TextView = itemView.findViewById(R.id.item_city)
        fun setData(data: UsersItemModel) {
            Picasso.get()
                .load(data.image)
                .fit()
                .centerCrop()
                .into(image)
            nama.text = data.name
            usia.text = data.rollNo.toString()
            kota.text = data.branch
        }

    }

}