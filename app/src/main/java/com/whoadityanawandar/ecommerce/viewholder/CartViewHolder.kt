package com.whoadityanawandar.ecommerce.viewholder

import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.whoadityanawandar.ecommerce.R
import com.whoadityanawandar.ecommerce.`interface`.ItemClickListener

class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    var tvProductName: TextView = itemView.findViewById(R.id.txtvwCartProductName)
    var tvProductPrice: TextView = itemView.findViewById(R.id.txtvwCartProductPrice)
    var tvCartProductQuantity: TextView = itemView.findViewById(R.id.txtvwCartProductQuantity)
    var tvProductDiscount: TextView = itemView.findViewById(R.id.txtvwCartProductDiscount)
    var ivCartProductImage: ImageView = itemView.findViewById(R.id.imgvwCartProductImage)
    var tvCartProductAmount: TextView = itemView.findViewById(R.id.txtvwCartProductAmount)
    var ibCartMinus: ImageButton = itemView.findViewById(R.id.imgbtnCartMinus)
    var ibCartPlus: ImageButton = itemView.findViewById(R.id.imgbtnCartPlus)
    var tvRemoveProduct: TextView = itemView.findViewById(R.id.txtvwCartRemoveProduct)
    var tvAddToWishList: TextView = itemView.findViewById(R.id.txtvwCartWishlist)
    private lateinit var listener: ItemClickListener


    fun setItemClickListener(listener: ItemClickListener) {
        this.listener = listener
    }

    override fun onClick(v: View?) {
        listener.onClick(v!!, adapterPosition, false)
    }


}