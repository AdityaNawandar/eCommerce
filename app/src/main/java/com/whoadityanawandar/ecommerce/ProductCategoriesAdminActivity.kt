package com.whoadityanawandar.ecommerce

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView.OnItemClickListener
import android.widget.GridView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar

class ProductCategoriesAdminActivity : AppCompatActivity() {

    lateinit var gvCategories: GridView
    lateinit var toolbar: Toolbar

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

        gvCategories = findViewById(R.id.grdvwCategories)
        createMenu()
        val mainAdapter = ImageAdapter(
            this@ProductCategoriesAdminActivity,
            strarrCategories,
            intarrCategoryImages
        )
        gvCategories.adapter = mainAdapter
        gvCategories.onItemClickListener = OnItemClickListener { _, _, position, _ ->
            var category = strarrCategories[+position]
            val intent = Intent(this, AddProductAdminActivity::class.java)
            intent.putExtra("category", category)
            startActivity(intent)
            finish()
        }
    }

    private fun createMenu() {
        toolbar = findViewById(R.id.toolbar)
        toolbar.title = "Product Categories"
        toolbar.inflateMenu(R.menu.menu)
        toolbar.setOnMenuItemClickListener { item ->
            if (item!!.itemId == R.id.logout) {
                clearSharedPreferences()
                val intent = Intent(applicationContext, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            false
        }
    }


    private fun clearSharedPreferences() {
        val editor = getSharedPreferences("name", MODE_PRIVATE).edit()
        editor.putString("password", "")
        editor.putString("phone", "")
        editor.putBoolean("isAdmin", false)
        editor.putBoolean("isLoggedIn", false)
        editor.apply()

    }
}