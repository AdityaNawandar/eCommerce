package com.whoadityanawandar.ecommerce.ui.home

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.whoadityanawandar.ecommerce.ui.home.HomeViewModel
import com.whoadityanawandar.ecommerce.databinding.FragmentHomeBinding
import com.whoadityanawandar.ecommerce.R
import com.whoadityanawandar.ecommerce.model.Product
import com.whoadityanawandar.ecommerce.viewholder.ProductViewHolder

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null
    private lateinit var productsRef: DatabaseReference

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root


        productsRef = FirebaseDatabase.getInstance().reference.child("Products")

        val recyclerView = root.findViewById<RecyclerView>(R.id.recvwProducts)
        var layoutManager = LinearLayoutManager(this.context)
        recyclerView.layoutManager = layoutManager

        val options: FirebaseRecyclerOptions<Product> = FirebaseRecyclerOptions.Builder<Product>()
            .setQuery(this.productsRef, Product::class.java)
            .build()

        val adapter: FirebaseRecyclerAdapter<Product, ProductViewHolder> =
            object : FirebaseRecyclerAdapter<Product, ProductViewHolder>(options) {
                override fun onBindViewHolder(
                    holder: ProductViewHolder,
                    position: Int,
                    model: Product
                ) {
                    holder.tvProductName.text = model.productName
                    holder.tvProductPrice.text = model.price
                    holder.tvProductDescription.text = model.description
                    Glide.with(holder.ivProductImage.context)
                        .load(model.imageUrl)
                        .into(holder.ivProductImage)
                    //holder.ivProductImage.setImageURI(Uri.parse(model.imageUrl))
                }

                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): ProductViewHolder {
                    val view: View = LayoutInflater.from(parent.context)
                        .inflate(R.layout.product_items_layout, parent, false)
                    return ProductViewHolder(view)
                }
            }





        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
        adapter.startListening()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}