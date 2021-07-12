package com.whoadityanawandar.ecommerce

import com.whoadityanawandar.ecommerce.R
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity


class HomeActivity : AppCompatActivity() {

    lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        createMenu()

    }

    private fun createMenu() {
        toolbar = findViewById(R.id.toolbar)
        toolbar.title = "Home"
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
        editor.putBoolean("isLoggedIn", false)
        editor.apply()

    }
}