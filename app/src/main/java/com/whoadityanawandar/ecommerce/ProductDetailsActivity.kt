package com.whoadityanawandar.ecommerce

import com.whoadityanawandar.ecommerce.R
import android.os.Bundle
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


class ProductDetailsActivity : AppCompatActivity() {

    private lateinit var btnMinus: ImageButton
    private lateinit var btnPlus: ImageButton
    private lateinit var txtvwOrderQuantity: TextView
    private lateinit var txtvwProductDetailsName: TextView
    private lateinit var txtvwProductDetailsDescription: TextView
    private lateinit var txtvwProductDetailsPrice: TextView
    private lateinit var imgvwProductDetailsImage:ImageView
    //private lateinit var txtvwOrderQuantity: TextView
    private lateinit var fabCart: FloatingActionButton
    private var intQuantityCount = 0
    private var productID = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)

/*        btnMinus = findViewById(R.id.imgbtnMinus)
        btnPlus = findViewById(R.id.imgbtnPlus)*/
        txtvwProductDetailsName = findViewById(R.id.txtvwProductDetailsName)
        txtvwProductDetailsDescription = findViewById(R.id.txtvwProductDetailsDescription)
        txtvwProductDetailsPrice = findViewById(R.id.txtvwProductDetailsPrice)
        txtvwOrderQuantity = findViewById(R.id.txtvwOrderQuantity)
        imgvwProductDetailsImage = findViewById(R.id.imgvwProductDetailsImage)
        fabCart = findViewById(R.id.fabProductDetailsCart)
        productID = intent.getStringExtra("productID").toString()

        getProductDetails(productID)

/*        btnMinus.setOnClickListener {
            if (intQuantityCount > 0) {
                intQuantityCount--
                txtvwOrderQuantity.text = intQuantityCount.toString()
            }
        }
        btnMinus.setOnClickListener {
            if (intQuantityCount < 20) {
                intQuantityCount++
                txtvwOrderQuantity.text = intQuantityCount.toString()
            } else {
                Toast.makeText(
                    applicationContext,
                    "Sorry, you may only order 20 items",
                    Toast.LENGTH_LONG
                ).show()
            }
        }*/

    }

    private fun getProductDetails(productID: String) {

        try {
            var productsRef = FirebaseDatabase.getInstance("https://ecommerce-whoadityanawandar-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Products")

            productsRef.child(productID).addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        //var product =  snapshot.value as Product
                        txtvwProductDetailsName.text = snapshot.child("productName").value.toString()
                        txtvwProductDetailsDescription.text = snapshot.child("description").value.toString()
                        txtvwProductDetailsPrice.text = "\u20B9 " + snapshot.child("price").value.toString()
                        Glide.with(applicationContext)
                            .load(snapshot.child("imageUrl").value.toString())
                            .into(imgvwProductDetailsImage)
                    }
                    else{
                        Toast.makeText(applicationContext, "Error fetching details...", Toast.LENGTH_SHORT).show()
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

    fun decreaseQuantity(view: View) {
        if (intQuantityCount > 0) {
            intQuantityCount--
            txtvwOrderQuantity.text = intQuantityCount.toString()
        }
    }

    fun increaseQuantity(view: View) {
        if (intQuantityCount < 20) {
            intQuantityCount++
            txtvwOrderQuantity.text = intQuantityCount.toString()
        } else {
            Toast.makeText(
                applicationContext,
                "Sorry, you may order a maximum of 20 items only",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun goToCart(view: View) {

    }

    fun addToCart(view: View){

    }

}//class end
