package com.example.mobicomp_project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.UiThread
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item.view.*


class RecyclerviewAdapter(private val recyclerList: List<CaloricIntake>, private val clickListener: (CaloricIntake) -> Unit) : RecyclerView.Adapter<RecyclerviewAdapter.RecyclerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerviewAdapter.RecyclerViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)

        return RecyclerViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerviewAdapter.RecyclerViewHolder, position: Int) {
        val currentItem = recyclerList[position]


        holder.foodView.text = currentItem.foodName
        holder.caloriesView.text = currentItem.calories.toString()
        holder.timeView.text = currentItem.timestamp


        holder.itemView.setOnClickListener{
            clickListener(currentItem)
        }
    }

    override fun getItemCount() = recyclerList.size

    class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val foodView: TextView = itemView.food_field
        val caloriesView: TextView = itemView.calories_field
        val timeView: TextView = itemView.time_field


    }

}