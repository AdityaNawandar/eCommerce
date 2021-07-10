package com.whoadityanawandar.ecommerce

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkIfUserIsLoggedIn()

    }

    private fun checkIfUserIsLoggedIn() {
        //check if user is logged in
        try {
            var sharedPreferences = getSharedPreferences("name", MODE_PRIVATE);
            var isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
            var isAdmin = sharedPreferences.getBoolean("isAdmin", false);
            if (isLoggedIn) {
                var intent: Intent?
                if(isAdmin) {
                    intent = Intent(applicationContext, ProductCategoriesAdminActivity::class.java)
                }
                else{
                    intent = Intent(applicationContext, HomeActivity::class.java)
                }
                startActivity(intent)
                finish()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun goToRegisterActivity(view: View) {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    fun goToLoginActivity(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}