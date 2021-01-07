package info.wallstreet.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import info.wallstreet.R
import info.wallstreet.model.UpgradeHistory
import java.util.*
import kotlin.collections.ArrayList

class UpgradeListAdapter(private val context: Context) : RecyclerView.Adapter<UpgradeListAdapter.MyViewHolder>() {
  private val myDataset = ArrayList<UpgradeHistory>()

  init {
    myDataset.add(UpgradeHistory("", "", Date().toString(), ""))
  }

  override fun onCreateViewHolder(
    parent: ViewGroup, viewType: Int
  ): MyViewHolder {
    val layout = LayoutInflater.from(parent.context).inflate(R.layout.list_upgrade_history, parent, false)
    return MyViewHolder(layout)
  }

  override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
    if (position == 0) return
    holder.balance.text = myDataset[position].balance
    if (myDataset[position].color == "in" && position != 0) {
      holder.balance.setTextColor(ContextCompat.getColor(context, R.color.Success))
      holder.balance.text = "+$${myDataset[position].balance}"
    } else if (myDataset[position].color == "out" && position != 0) {
      holder.balance.setTextColor(ContextCompat.getColor(context, R.color.Danger))
      holder.balance.text = "-$${myDataset[position].balance}"
    } else {
      holder.balance.text = myDataset[position].balance
    }
    holder.description.text = myDataset[position].description
    holder.date.text = myDataset[position].date
  }

  override fun getItemCount() = myDataset.size

  fun addItem(item: UpgradeHistory) {
    myDataset.add(1, item)
    this.notifyDataSetChanged()
    this.notifyItemRangeInserted(0, myDataset.size)
  }

  fun clear() {
    myDataset.clear()
    myDataset.add(UpgradeHistory("", "", Date().toString(), ""))
    this.notifyDataSetChanged()
    this.notifyItemRangeInserted(0, myDataset.size)
  }

  class MyViewHolder(layout: View) : RecyclerView.ViewHolder(layout) {
    val balance: TextView = layout.findViewById(R.id.balance)
    val description: TextView = layout.findViewById(R.id.description)
    val date: TextView = layout.findViewById(R.id.date)
  }
}