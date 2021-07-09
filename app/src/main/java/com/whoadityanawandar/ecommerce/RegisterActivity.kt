package com.whoadityanawandar.ecommerce

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.widget.ContentLoadingProgressBar
import com.google.firebase.database.*
import java.lang.NullPointerException

class RegisterActivity : AppCompatActivity() {
    lateinit var edttxtFirstName: EditText
    lateinit var edttxtLastName: EditText
    lateinit var edttxtPhoneNumber: EditText
    lateinit var edttxtPassword: EditText

    private lateinit var loadingProgressBar: ContentLoadingProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        edttxtFirstName = findViewById(R.id.edttxtFirstName)
        edttxtLastName = findViewById(R.id.edttxtLastName)
        edttxtPhoneNumber = findViewById(R.id.edttxtPhone)
        edttxtPassword = findViewById(R.id.edttxtPassword)

        loadingProgressBar = findViewById(R.id.progressbar)
        loadingProgressBar.hide()
    }

   fun register(view: View) {
        var strFirstName = edttxtFirstName.text.toString()
        var strLastName = edttxtLastName.text.toString()
        var strPhoneNumber = edttxtPhoneNumber.text.toString()
        var strPassword = edttxtPassword.text.toString()

        if (strFirstName.isEmpty()) {
            Toast.makeText(this, "Please tell us your name", Toast.LENGTH_SHORT).show()
            return
        } else if (strPhoneNumber.isEmpty()) {
            Toast.makeText(this, "We will need your phone number", Toast.LENGTH_SHORT).show()
            return
        } else if (strPassword.isEmpty()) {
            Toast.makeText(this, "Please provide a password", Toast.LENGTH_SHORT).show()
            return
        } else {
            loadingProgressBar.show()
            //register account
            checkIfUserExists(strPhoneNumber, strFirstName, strLastName, strPassword)
        }

    }

    private fun checkIfUserExists(
        strPhoneNumber: String,
        strFirstName: String,
        strLastName: String,
        strPassword: String
    ) {
        try {
            val usersRef = FirebaseDatabase
                .getInstance("https://ecommerce-whoadityanawandar-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Users")

            usersRef.addListenerForSingleValueEvent( object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        if (!(snapshot.child(strPhoneNumber).exists())) {

                            var userDetails = hashMapOf<String, Any>(
                                "firstname" to strFirstName,
                                "lastname" to strLastName,
                                "phone" to strPhoneNumber,
                                "password" to strPassword
                            )

                            usersRef.child(strPhoneNumber)
                                .setValue(userDetails)
                                .addOnCompleteListener { task ->
                                    if(task.isSuccessful){
                                        //loadingProgressBar.visibility = View.INVISIBLE
                                        Toast.makeText(this@RegisterActivity, "Congratulations! Your account has been created.", Toast.LENGTH_SHORT).show()
                                        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                    else{
                                        Toast.makeText(this@RegisterActivity,"Something went wrong!", Toast.LENGTH_SHORT).show()
                                    }
                                }


                        }
                        else {
                            loadingProgressBar.hide()
                            Toast.makeText(
                                this@RegisterActivity,
                                "Number already exists. Please try again using another number.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                    catch (e: Exception) {
                        e.printStackTrace()
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    loadingProgressBar.hide()
                    Log.w(TAG, "loadPost:onCancelled", error.toException())

                }

            })

            //usersRef.addValueEventListener(userListener)

        } catch (e: Exception) {
            loadingProgressBar.hide()
            e.printStackTrace()
        }
    }


}