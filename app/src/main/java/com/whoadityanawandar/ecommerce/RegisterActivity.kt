package com.whoadityanawandar.ecommerce

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class RegisterActivity : AppCompatActivity()
{
    lateinit var edttxtFirstName : EditText
    lateinit var edttxtLastName : EditText
    lateinit var edttxtPhoneNumber : EditText
    lateinit var edttxtPassword : EditText

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        edttxtFirstName = findViewById(R.id.edttxtFirstName)
        edttxtLastName = findViewById(R.id.edttxtLastName)
        edttxtPhoneNumber = findViewById(R.id.edttxtPhone)
        edttxtPassword = findViewById(R.id.edttxtPassword)
    }

    fun register(view: View){
        var strFirstName = edttxtFirstName.text.toString()
        var strLastName = edttxtLastName.text.toString()
        var strPhoneNumber = edttxtPhoneNumber.text.toString()
        var strPassword = edttxtPassword.text.toString()

        if(strFirstName.isEmpty()){
            Toast.makeText(this, "Please tell us your name", Toast.LENGTH_SHORT).show()
            return
        }
        else if(strPhoneNumber.isEmpty())
        {
            Toast.makeText(this, "We will need your phone number", Toast.LENGTH_SHORT).show()
            return
        }
        else if(strPassword.isEmpty())
        {
            Toast.makeText(this, "Please provide a password", Toast.LENGTH_SHORT).show()
            return
        }
        else{
            //register account
            checkIfUserExists(strPhoneNumber)
        }

    }

    private fun checkIfUserExists(strPhoneNumber: String)
    {

    }


}