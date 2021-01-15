package info.wallstreet.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import info.wallstreet.R
import info.wallstreet.model.UpgradeList

class HistoryUpgradeListAdapter : RecyclerView.Adapter<HistoryUpgradeListAdapter.MyViewHolder>() {
  private val myDataset = ArrayList<UpgradeList>()

  init {
    myDataset.add(UpgradeList("Balance", "Type", "Date"))
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
    val layout = LayoutInflater.from(parent.context).inflate(R.layout.list_upgrade, parent, false)
    return MyViewHolder(layout)
  }

  override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
    holder.balance.text = myDataset[position].balance
    holder.type.text = myDataset[position].type
    holder.date.text = myDataset[position].date
  }

  override fun getItemCount() = myDataset.size

  fun addItem(item: UpgradeList) {
    myDataset.add(1, item)
    this.notifyDataSetChanged()
    this.notifyItemRangeInserted(0, myDataset.size)
  }

  fun clear() {
    myDataset.clear()
    myDataset.add(UpgradeList("Balance", "Type", "Date"))
    this.notifyDataSetChanged()
    this.notifyItemRangeInserted(0, myDataset.size)
  }

  class MyViewHolder(layout: View) : RecyclerView.ViewHolder(layout) {
    val balance: TextView = layout.findViewById(R.id.balance)
    val type: TextView = layout.findViewById(R.id.type)
    val date: TextView = layout.findViewById(R.id.date)
  }
}