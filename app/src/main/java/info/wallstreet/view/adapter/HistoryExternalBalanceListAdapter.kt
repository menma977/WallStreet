package info.wallstreet.view.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import info.wallstreet.R
import info.wallstreet.model.HistoryExternalBalance

class HistoryExternalBalanceListAdapter(private val context: Context) : RecyclerView.Adapter<HistoryExternalBalanceListAdapter.MyViewHolder>() {
  private val myDataset = ArrayList<HistoryExternalBalance>()

  init {
    myDataset.add(HistoryExternalBalance("", "", "", ""))
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
    val layout = LayoutInflater.from(parent.context).inflate(R.layout.list_external_balance, parent, false)
    return MyViewHolder(layout)
  }

  override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
    holder.address.text = myDataset[position].address
    holder.hash.text = myDataset[position].hash
    holder.hash.setOnClickListener {
      val uri = "https://dogechain.info/tx/${myDataset[position].hash}"
      val move = Intent(Intent.ACTION_VIEW)
      move.data = Uri.parse(uri)
      context.startActivity(move)
    }
    holder.balance.text = myDataset[position].balance
    holder.date.text = myDataset[position].date
  }

  override fun getItemCount() = myDataset.size

  fun addItem(item: HistoryExternalBalance) {
    myDataset.add(1, item)
    this.notifyDataSetChanged()
    this.notifyItemRangeInserted(0, myDataset.size)
  }

  fun clear() {
    myDataset.clear()
    myDataset.add(HistoryExternalBalance("", "", "", ""))
    this.notifyDataSetChanged()
    this.notifyItemRangeInserted(0, myDataset.size)
  }

  class MyViewHolder(layout: View) : RecyclerView.ViewHolder(layout) {
    val address: TextView = layout.findViewById(R.id.address)
    val hash: TextView = layout.findViewById(R.id.hash)
    val balance: TextView = layout.findViewById(R.id.balance)
    val date: TextView = layout.findViewById(R.id.date)
  }
}