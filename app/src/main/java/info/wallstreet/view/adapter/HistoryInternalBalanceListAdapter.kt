package info.wallstreet.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import info.wallstreet.R
import info.wallstreet.model.HistoryInternalBalance

class HistoryInternalBalanceListAdapter : RecyclerView.Adapter<HistoryInternalBalanceListAdapter.MyViewHolder>() {
  private val myDataset = ArrayList<HistoryInternalBalance>()

  init {
    myDataset.add(HistoryInternalBalance("", "", ""))
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
    val layout = LayoutInflater.from(parent.context).inflate(R.layout.list_internal_balance, parent, false)
    return MyViewHolder(layout)
  }

  override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
    holder.address.text = myDataset[position].address
    holder.balance.text = myDataset[position].balance
    holder.date.text = myDataset[position].date
  }

  override fun getItemCount() = myDataset.size

  fun addItem(item: HistoryInternalBalance) {
    myDataset.add(1, item)
    this.notifyDataSetChanged()
    this.notifyItemRangeInserted(0, myDataset.size)
  }

  fun clear() {
    myDataset.clear()
    myDataset.add(HistoryInternalBalance("", "", ""))
    this.notifyDataSetChanged()
    this.notifyItemRangeInserted(0, myDataset.size)
  }

  class MyViewHolder(layout: View) : RecyclerView.ViewHolder(layout) {
    val address: TextView = layout.findViewById(R.id.address)
    val balance: TextView = layout.findViewById(R.id.balance)
    val date: TextView = layout.findViewById(R.id.date)
  }
}