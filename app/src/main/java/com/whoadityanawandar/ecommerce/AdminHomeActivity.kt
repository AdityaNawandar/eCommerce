package com.whoadityanawandar.ecommerce

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar

class AdminHomeActivity : AppCompatActivity() {

    lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_home)

        createMenu()
    }

    fun checkOrders(view: View){
        var intent = Intent(this, ReceivedOrdersAdminActivity::class.java)
        startActivity(intent)
    }

    fun addProduct(view: View){
        var intent = Intent(this, AddProductAdminActivity::class.java)
        startActivity(intent)
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