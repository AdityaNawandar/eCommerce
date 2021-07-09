package com.whoadityanawandar.ecommerce

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.ContentLoadingProgressBar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class LoginActivity : AppCompatActivity() {

    lateinit var edttxtPhoneNumber: EditText
    lateinit var edttxtPassword: EditText
    lateinit var chkbxRememberMe : CheckBox
    private lateinit var loadingProgressBar: ContentLoadingProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        edttxtPhoneNumber = findViewById(R.id.edttxtPhone)
        edttxtPassword = findViewById(R.id.edttxtPassword)
        loadingProgressBar = findViewById(R.id.progressbar)
        chkbxRememberMe = findViewById(R.id.chkbxRememberMe)
        loadingProgressBar.isActivated = true
        loadingProgressBar.hide()

    }

    fun hideKeyboard(view: View) {
        val inputMethodManager: InputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0)
    }

    fun login(view: View) {
        //loadingProgressBar.show()
        var strPhoneNumber = edttxtPhoneNumber.text.toString()
        var strPassword = edttxtPassword.text.toString()
        if (strPhoneNumber.isEmpty()) {
            Toast.makeText(this, "We will need your phone number", Toast.LENGTH_SHORT).show()
            return
        } else if (strPassword.isEmpty()) {
            Toast.makeText(this, "Please provide a password", Toast.LENGTH_SHORT).show()
            return
        } else {
            loadingProgressBar.show()
            validateUserCredentials(strPhoneNumber, strPassword)
        }


    }

    private fun validateUserCredentials(strPhoneNumber: String, strPassword: String) {
        val usersRef =
            FirebaseDatabase
                .getInstance("https://ecommerce-whoadityanawandar-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Users")

        try {
            usersRef.addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    loadingProgressBar.show()
                    if (snapshot.child(strPhoneNumber).exists()) {

                        var password =
                            snapshot.child(strPhoneNumber).child("password").value.toString()
                        if (password == strPassword) {

                            var phoneNumber =
                                snapshot.child(strPhoneNumber).child("phone").value.toString()
                            //let user in
                            loadingProgressBar.hide()

                            if(chkbxRememberMe.isChecked) {
                                //add user details to shared pref
                                val editor = getSharedPreferences("name", MODE_PRIVATE).edit()
                                editor.putString("phone", phoneNumber)
                                editor.putString("password", password)
                                editor.putBoolean("isLoggedIn", true)
                                editor.apply()
                            }
                            val intent = Intent(applicationContext, HomeActivity::class.java)
                            startActivity(intent)
                            finish()

                        } else {
                            Toast.makeText(
                                this@LoginActivity,
                                "Incorrect password!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Number is not registered",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    loadingProgressBar.hide()
                    Toast.makeText(this@LoginActivity, "Login cancelled!", Toast.LENGTH_SHORT)
                        .show()
                }
            })//usersRef.addValueEventListener(userListener)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}//class end