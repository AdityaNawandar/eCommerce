package com.whoadityanawandar.ecommerce.ui.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.whoadityanawandar.ecommerce.databinding.FragmentOrdersBinding
import com.whoadityanawandar.ecommerce.ui.orders.OrdersViewModel

class OrdersFragment : Fragment() {

    private lateinit var ordersViewModel: OrdersViewModel
    private var _binding: FragmentOrdersBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var root: View? = null
        try {
            ordersViewModel =
                ViewModelProvider(this).get(OrdersViewModel::class.java)

            _binding = FragmentOrdersBinding.inflate(inflater, container, false)
            root = binding.root

            val textView: TextView = binding.textOrders
            ordersViewModel.text.observe(viewLifecycleOwner, Observer {
                textView.text = it
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}