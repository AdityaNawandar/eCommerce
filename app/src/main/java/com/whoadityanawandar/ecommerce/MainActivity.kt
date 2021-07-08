package com.whoadityanawandar.ecommerce

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View


class MainActivity : AppCompatActivity()
{

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun goToRegisterActivity(view: View){
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    fun goToLoginActivity(view: View){
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}