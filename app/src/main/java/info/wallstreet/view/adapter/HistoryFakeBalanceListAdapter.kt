package info.wallstreet.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import info.wallstreet.R
import info.wallstreet.model.HistoryFakeBalance

class HistoryFakeBalanceListAdapter : RecyclerView.Adapter<HistoryFakeBalanceListAdapter.MyViewHolder>() {
  private val myDataset = ArrayList<HistoryFakeBalance>()

  init {
    myDataset.add(HistoryFakeBalance("", "", ""))
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
    val layout = LayoutInflater.from(parent.context).inflate(R.layout.list_fake_balance, parent, false)
    return MyViewHolder(layout)
  }

  override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
    holder.description.text = myDataset[position].description
    holder.balance.text = myDataset[position].balance
    holder.date.text = myDataset[position].date
  }

  override fun getItemCount() = myDataset.size

  fun addItem(item: HistoryFakeBalance) {
    myDataset.add(1, item)
    this.notifyDataSetChanged()
    this.notifyItemRangeInserted(0, myDataset.size)
  }

  fun clear() {
    myDataset.clear()
    myDataset.add(HistoryFakeBalance("", "", ""))
    this.notifyDataSetChanged()
    this.notifyItemRangeInserted(0, myDataset.size)
  }

  class MyViewHolder(layout: View) : RecyclerView.ViewHolder(layout) {
    val description: TextView = layout.findViewById(R.id.description)
    val balance: TextView = layout.findViewById(R.id.balance)
    val date: TextView = layout.findViewById(R.id.date)
  }
}