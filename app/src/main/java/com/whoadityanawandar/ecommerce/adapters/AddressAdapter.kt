package com.whoadityanawandar.ecommerce.adapters

import com.whoadityanawandar.ecommerce.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.whoadityanawandar.ecommerce.model.Address

class AddressAdapter
    (private val addressList: ArrayList<Address>) : RecyclerView.Adapter<AddressAdapter.ViewHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.address_layout, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val model = addressList[position]

        // sets the image to the imageview from our itemHolder class
        //holder.imageView.setImageResource(ItemsViewModel.image)

        // sets the text to the textview from our itemHolder class
        holder.tvAddressLine1.text = model.addressLine1
        holder.tvAddressLine2.text = model.addressLine2
        holder.tvAddressCity.text = model.city
        holder.tvAddressState.text = model.state
        holder.tvAddressPincode.text = model.pinCode

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return addressList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        //val imageView: ImageView = itemView.findViewById(R.id.imageview)
        val tvAddressLine1: TextView = itemView.findViewById(R.id.txtvwAddressLine1)
        val tvAddressLine2: TextView = itemView.findViewById(R.id.txtvwAddressLine2)
        val tvAddressCity: TextView = itemView.findViewById(R.id.txtvwAddressCity)
        val tvAddressState: TextView = itemView.findViewById(R.id.txtvwAddressState)
        val tvAddressPincode: TextView = itemView.findViewById(R.id.txtvwAddressPincode)

    }
}