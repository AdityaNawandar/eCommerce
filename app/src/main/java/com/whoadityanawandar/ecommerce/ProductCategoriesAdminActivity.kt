package com.whoadityanawandar.ecommerce

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView.OnItemClickListener
import android.widget.GridView
import android.widget.Toast

class ProductCategoriesAdminActivity : AppCompatActivity() {

    lateinit var gvCategories: GridView

    private var strarrCategories = arrayOf(
        "Shirts",
        "Tshirts",
        "Dresses",
        "Jackets",
        "Sunglasses",
        "Purses",
        "Hats",
        "Shoes",
        "Headphones",
        "Laptops",
        "Watches",
        "Phones"
    )
    private var intarrCategoryImages = intArrayOf(
        R.drawable.cat_shirt,
        R.drawable.cat_tshirt,
        R.drawable.cat_dress,
        R.drawable.cat_jacket,
        R.drawable.cat_sunglasses,
        R.drawable.cat_purse,
        R.drawable.cat_hat,
        R.drawable.cat_shoe,
        R.drawable.cat_headphones,
        R.drawable.cat_laptop,
        R.drawable.cat_watch,
        R.drawable.cat_phone
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_categories_admin)

        title = "KotlinApp"
        gvCategories = findViewById(R.id.grdvwCategories)
        val mainAdapter = ImageAdapter(this@ProductCategoriesAdminActivity, strarrCategories, intarrCategoryImages)
        gvCategories.adapter = mainAdapter
        gvCategories.onItemClickListener = OnItemClickListener { _, _, position, _ ->
            Toast.makeText(
                applicationContext, "You CLicked " + strarrCategories[+position],
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}