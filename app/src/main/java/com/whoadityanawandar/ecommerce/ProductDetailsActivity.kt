package com.whoadityanawandar.ecommerce

import android.app.PendingIntent.getActivity
import android.content.ContentValues.TAG
import android.content.Intent
import com.whoadityanawandar.ecommerce.R
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*

import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.whoadityanawandar.ecommerce.model.Product
import java.text.SimpleDateFormat
import java.util.*
import android.widget.ArrayAdapter

import android.widget.Spinner
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener

import android.widget.AutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.ktx.getValue


class ProductDetailsActivity : AppCompatActivity() {

    private lateinit var btnMinus: ImageButton
    private lateinit var btnPlus: ImageButton
    private lateinit var txtvwProductDetailsName: TextView
    private lateinit var txtvwProductDetailsDescription: TextView
    private lateinit var txtvwProductDetailsPrice: TextView
    private lateinit var imgvwProductDetailsImage: ImageView
    private lateinit var spinnerQuantity: AutoCompleteTextView
    private lateinit var spinnerLayout: TextInputLayout
    private lateinit var spinAdapter: ArrayAdapter<String>

    private lateinit var fabCart: FloatingActionButton
    private var intQuantityCount = 0
    private var strProductID = ""
    private var strProductPrice = ""
    private var isRedirectedFromCart = false
    private var cartQuantity = 0
    private var strCurrentUserPhone = ""
    private var strImageUrl = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)

        try {
            txtvwProductDetailsName = findViewById(R.id.txtvwProductDetailsName)
            txtvwProductDetailsDescription = findViewById(R.id.txtvwProductDetailsDescription)
            txtvwProductDetailsPrice = findViewById(R.id.txtvwProductDetailsPrice)
            spinnerQuantity = findViewById(R.id.spinnerQuantity)
            spinnerLayout = findViewById(R.id.spinnerLayout)
            imgvwProductDetailsImage = findViewById(R.id.imgvwProductDetailsImage)
            fabCart = findViewById(R.id.fabProductDetailsCart)

            //get from intent
            strProductID = intent.getStringExtra("productID").toString()
            isRedirectedFromCart = intent.getBooleanExtra("isRedirectedFromCart", false)
            if (isRedirectedFromCart) {
                cartQuantity = Integer.parseInt(intent.getStringExtra("quantity"))
            }

            getProductQuantityInCart()


            //get from shared prefs
            var sharedPreferences =
                getSharedPreferences("name", MODE_PRIVATE)
            strCurrentUserPhone = sharedPreferences.getString("phone", "")!!

            getProductDetails(strProductID)

            fabCart.setOnClickListener { view ->
                var intent = Intent(this, CartActivity::class.java)
                startActivity(intent)
            }

            //setting the quantity dropdown

            var arrQuantity = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
            spinAdapter =
                ArrayAdapter<String>(this, R.layout.spinner_text, arrQuantity)
            //var spinAdapter = ArrayAdapter<CharSequence>(this, R.layout.spinner_text, arrQuantity)
            //spinAdapter.setDropDownViewResource(R.layout.spinner_dropdown)
            spinnerQuantity.setAdapter(spinAdapter)

            (spinnerLayout.editText as AutoCompleteTextView).setOnItemClickListener { adapterView, view, position, id ->
                val strQuantity: String = spinAdapter.getItem(position)!!
                intQuantityCount = Integer.parseInt(strQuantity)
            }

/*            if (isRedirectedFromCart) {
                spinnerQuantity.setText(cartQuantity.toString(), false)
            }*/
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onResume() {
        super.onResume()
        getProductQuantityInCart()
    }

    private fun getProductQuantityInCart() {

        var cartProductsRef = FirebaseDatabase
            .getInstance("https://ecommerce-whoadityanawandar-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("User View Cart")

        cartProductsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                val product = dataSnapshot
                    .child(strCurrentUserPhone)
                    .child("Products")
                    .child(strProductID)
                if (product.exists()) {
                    val cartQuantity = product.child("quantity").value
                    spinnerQuantity.setText(cartQuantity.toString(), false)
                }
                else {
                    spinnerQuantity.clearListSelection()
                    spinAdapter.notifyDataSetChanged()
                    spinnerLayout.editText!!.setText("")
                }
                // ...
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "Cancelled!", databaseError.toException())
            }
        })

    }

    private fun getProductDetails(productID: String) {

        try {
            var productsRef =
                FirebaseDatabase.getInstance("https://ecommerce-whoadityanawandar-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference("Products")

            productsRef.child(productID)
                .addListenerForSingleValueEvent(object : ValueEventListener {

                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            //var product =  snapshot.value as Product
                            txtvwProductDetailsName.text =
                                snapshot.child("productName").value.toString()
                            txtvwProductDetailsDescription.text =
                                snapshot.child("description").value.toString()
                            strProductPrice = snapshot.child("price").value.toString()
                            txtvwProductDetailsPrice.text = resources.getString(R.string.rupee_symbol, strProductPrice)

                            strImageUrl = snapshot.child("imageUrl").value.toString()
                            Glide.with(applicationContext)
                                .load(strImageUrl)
                                .into(imgvwProductDetailsImage)
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Error fetching details...",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(applicationContext, "Cancelled", Toast.LENGTH_SHORT).show()
                    }

                })
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun addToCart(view: View) {

        val simpleDateFormat = SimpleDateFormat("MMMddyy'_'hh:mm:ss aa")
        val currentDate = simpleDateFormat.format(Date())

        val quantity: String = spinnerLayout.editText!!.text.toString()
        if (quantity == "" && intQuantityCount == 0) {
            intQuantityCount++
        } else {
            intQuantityCount = Integer.parseInt(quantity)
        }


        val amount = Integer.parseInt(strProductPrice) * intQuantityCount
        var cartRef = FirebaseDatabase
            .getInstance("https://ecommerce-whoadityanawandar-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .reference

        var cart = hashMapOf<String, Any>(
            "productID" to strProductID,
            "productName" to txtvwProductDetailsName.text.toString(),
            "price" to strProductPrice,
            "datetime" to currentDate,
            "quantity" to intQuantityCount.toString(),
            "imageUrl" to strImageUrl,
            "amount" to amount.toString(),
            "discount" to "null"
        )

        cartRef.child("User View Cart")
            .child(strCurrentUserPhone)
            .child("Products")
            .child(strProductID)
            .updateChildren(cart)
            .addOnCompleteListener { it ->
                if (it.isSuccessful) {
                    cartRef.child("Admin View Cart")
                        .child(strCurrentUserPhone)
                        .child("Products")
                        .child(strProductID)
                        .updateChildren(cart)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(this, "Added to Cart!", Toast.LENGTH_LONG).show()
                            }
                        }
                }
            }

    }

}//class end
