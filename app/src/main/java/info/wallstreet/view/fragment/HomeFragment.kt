package info.wallstreet.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import info.wallstreet.R
import info.wallstreet.model.User
import info.wallstreet.view.NavigationActivity

class HomeFragment : Fragment() {
  private lateinit var user:User
  private lateinit var btc:TextView
  private lateinit var ltc:TextView
  private lateinit var eth:TextView
  private lateinit var doge:TextView

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.fragment_home, container, false)

    user = User(activity as NavigationActivity)

    btc = view.findViewById(R.id.textViewBitcoinBalance)
    ltc = view.findViewById(R.id.textViewLitecoinBalance)
    eth = view.findViewById(R.id.textViewEthereumBalance)
    doge = view.findViewById(R.id.textViewDogecoinBalance)

    btc.text = user.getString("balance_btc")
    ltc.text = user.getString("balance_ltc")
    eth.text = user.getString("balance_eth")
    doge.text = user.getString("balance_doge")

    return view
  }
}