package com.abhirajsharma.theuniversitydatabase.others

import androidx.recyclerview.widget.DiffUtil
import com.abhirajsharma.theuniversitydatabase.model.UsersItemModel

class CardStackCallback(
    private val old: List<UsersItemModel>,
    private val aNew: List<UsersItemModel>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return old.size
    }

    override fun getNewListSize(): Int {
        return aNew.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return old[oldItemPosition].image === aNew[newItemPosition].image
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return old[oldItemPosition] === aNew[newItemPosition]
    }

}