package com.whoadityanawandar.ecommerce

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.common.ChangeEventType
import com.firebase.ui.database.ChangeEventListener
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import com.whoadityanawandar.ecommerce.model.Cart
import com.whoadityanawandar.ecommerce.model.Product
import com.whoadityanawandar.ecommerce.viewholder.CartViewHolder
import com.google.firebase.firestore.model.Values


class CartActivity : AppCompatActivity() {

    lateinit var cartRef: DatabaseReference
    lateinit var txtvwCartTotalAmount: TextView
    lateinit var imgbtnCartMinus: ImageButton
    lateinit var imgbtnCartPlus: ImageButton
    lateinit var adapter: FirebaseRecyclerAdapter<Cart, CartViewHolder>
    private var currentUserPhone = ""
    var cartTotal = 0
    lateinit var recyclerView: RecyclerView
    var isIncrease = false
    var isDecrease = false
    lateinit var options: FirebaseRecyclerOptions<Cart>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        try {
            cartRef = FirebaseDatabase
                .getInstance("https://ecommerce-whoadityanawandar-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .reference.child("User View Cart")

            var sharedPreferences =
                getSharedPreferences("name", MODE_PRIVATE)
            currentUserPhone = sharedPreferences.getString("phone", "")!!

            recyclerView = findViewById(R.id.recvwCartList)

            txtvwCartTotalAmount = findViewById(R.id.txtvwCartTotalAmount)

            options = FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(cartRef.child(currentUserPhone).child("Products"), Cart::class.java)
                .build()

            // to fetch price and quantity to update total cart value before setting the adapter
            options.snapshots.addChangeEventListener(object : ChangeEventListener {
                override fun onChildChanged(
                    type: ChangeEventType,
                    snapshot: DataSnapshot,
                    newIndex: Int,
                    oldIndex: Int
                ) {
                    var price = 0
                    var quantity = 0
                    var amount = 0
                    var productName = ""

                    var dataMap = snapshot.value as MutableMap<Any, Any>

                    var productNameKV = dataMap["productName"]
                    productName = productNameKV.toString()

                    var priceKV = dataMap["price"]
                    price = Integer.parseInt(priceKV.toString())

                    var quantityKV = dataMap["quantity"]
                    quantity = Integer.parseInt(quantityKV.toString())

                    amount = (price * quantity)

                    if (isIncrease) {
                        cartTotal += price
                    } else if (isDecrease) {
                        cartTotal -= price
                    } else {
                        cartTotal += amount
                    }

                    txtvwCartTotalAmount.text =
                        resources.getString(R.string.rupee_symbol, cartTotal.toString())

                    isIncrease = false
                    isDecrease = false

                }

                override fun onDataChanged() {
                    // A full update has been received and processed.
                    // Here's where you would use `snapshots` and process stuff
                }

                override fun onError(e: DatabaseError) {
                    Toast.makeText(applicationContext, e.toString(), Toast.LENGTH_SHORT).show()
                }
            })

            adapter =
                object : FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {

                    override fun onBindViewHolder(
                        holder: CartViewHolder,
                        position: Int,
                        model: Cart
                    ) {
                        //initializations
                        imgbtnCartMinus = holder.ibCartMinus
                        imgbtnCartPlus = holder.ibCartPlus


                        //assigning values
                        holder.tvProductName.text = model.productName
                        holder.tvProductPrice.text =
                            resources.getString(R.string.rupee_symbol, model.price)

                        holder.tvCartProductQuantity.text = model.quantity

                        var productAmount =
                            Integer.parseInt(model.price) * Integer.parseInt(holder.tvCartProductQuantity.text.toString())

                        holder.tvCartProductAmount.text = productAmount.toString()
                        Glide.with(holder.ivCartProductImage.context)
                            .load(model.imageUrl)
                            .into(holder.ivCartProductImage)


                        //redirect to product details page on item click
                        holder.itemView.setOnClickListener {
                            var productID = model.productID
                            val intent =
                                Intent(this@CartActivity, ProductDetailsActivity::class.java)
                            intent.putExtra("productID", productID)

                            startActivity(intent)
                        }

                        holder.ibCartPlus.setOnClickListener {
                            increaseQuantityInCart(
                                model.productID,
                                position,
                                holder.tvCartProductQuantity.text.toString()
                            )
                        }

                        holder.ibCartMinus.setOnClickListener {
                            decreaseQuantityInCart(
                                model.productID,
                                position,
                                holder.tvCartProductQuantity.text.toString()
                            )
                        }

                        holder.tvRemoveProduct.setOnClickListener {
                            removeProductFromCart(position, model.productID)
                        }

                    }

                    override fun onCreateViewHolder(
                        parent: ViewGroup,
                        viewType: Int
                    ): CartViewHolder {
                        val view: View = LayoutInflater.from(parent.context)
                            .inflate(R.layout.cart_product_layout, parent, false)
                        return CartViewHolder(view)
                    }

                }

            initializeRecyclerView()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initializeRecyclerView(){

        //recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
        var layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        adapter.startListening()
    }

    private fun removeProductFromCart(position: Int, productID: String) {
        val itemView = recyclerView.findViewHolderForAdapterPosition(position)!!.itemView

        //get current (clicked) item amount
        var tvAmount = itemView.findViewById<TextView>(R.id.txtvwCartProductAmount)
        var amount = tvAmount.text.toString()
        var intAmount = Integer.parseInt(amount)


        cartTotal -= intAmount
        var strCartTotal = cartTotal.toString()
        txtvwCartTotalAmount.text = resources.getString(R.string.rupee_symbol, strCartTotal)

        cartRef
            .child(currentUserPhone)
            .child("Products")
            .child(productID)
            .removeValue()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Removed!", Toast.LENGTH_SHORT).show()

                }
            }
    }

    fun increaseQuantityInCart(productID: String, position: Int, quantity: String) {
        isIncrease = true
        changeQuantityInDB(productID, position, quantity)
    }

    fun decreaseQuantityInCart(productID: String, position: Int, quantity: String) {
        isDecrease = true
        changeQuantityInDB(productID, position, quantity)

    }

    private fun changeQuantityInDB(productID: String, position: Int, quantity: String) {
        val itemView =
            recyclerView.findViewHolderForAdapterPosition(position)!!.itemView

        //get current (clicked) item quantity
        var tvQuantity = itemView.findViewById<TextView>(R.id.txtvwCartProductQuantity)
        var quantity = tvQuantity.text.toString()
        var intQuantity = Integer.parseInt(quantity)


        //get current (clicked) item amount
        var tvAmount = itemView.findViewById<TextView>(R.id.txtvwCartProductAmount)
        var amount = tvAmount.text.toString()
        var intAmount = Integer.parseInt(amount)

        //get current (clicked) item price
        var tvPrice = itemView.findViewById<TextView>(R.id.txtvwCartProductPrice)
        var price = tvPrice.text.toString()
        var delimiter = "\u20B9"
        val parts = price.split(delimiter)
        price = parts[1]
        var intPrice = Integer.parseInt(price)

        if (isIncrease) {

            if (intQuantity < 10) {

                intQuantity++
                //cartTotal += intPrice
                intAmount = intPrice * intQuantity
                tvAmount.text = intAmount.toString()
                tvQuantity.text = intQuantity.toString()

            } else {
                Toast.makeText(
                    applicationContext,
                    "Sorry, you may order a maximum of 10 items only",
                    Toast.LENGTH_LONG
                ).show()
            }
        } else if (isDecrease) {

            if (intQuantity > 1) {
                intQuantity--
                //cartTotal -= intPrice
                intAmount = intPrice * intQuantity
                tvAmount.text = intAmount.toString()
                tvQuantity.text = intQuantity.toString()
            }

        }

        cartRef
            .child(currentUserPhone)
            .child("Products")
            .child(productID)
            .child("quantity")
            .setValue(intQuantity.toString())
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Changed!", Toast.LENGTH_SHORT).show()

                }
            }
    }

}//class end