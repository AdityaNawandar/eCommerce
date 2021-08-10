package com.whoadityanawandar.ecommerce

import com.whoadityanawandar.ecommerce.R
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.ContentLoadingProgressBar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class LoginActivity : AppCompatActivity() {

    lateinit var edttxtPhoneNumber: EditText
    lateinit var edttxtPassword: EditText
    lateinit var chkbxRememberMe: CheckBox
    lateinit var txtvwIAmAnAdminLink: TextView
    lateinit var txtvwIAmNotAnAdminLink: TextView
    lateinit var btnLogin: Button
    private lateinit var loadingProgressBar: ContentLoadingProgressBar
    var isAdmin = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        edttxtPhoneNumber = findViewById(R.id.edttxtPhone)
        edttxtPassword = findViewById(R.id.edttxtPassword)
        chkbxRememberMe = findViewById(R.id.chkbxRememberMe)
        txtvwIAmAnAdminLink = findViewById(R.id.txtvwIAmAnAdminLink)
        txtvwIAmNotAnAdminLink = findViewById(R.id.txtvwIAmNotAnAdminLink)
        btnLogin = findViewById(R.id.btnLogin)
        loadingProgressBar = findViewById(R.id.progressbar)
        loadingProgressBar.isActivated = true
        loadingProgressBar.hide()


        txtvwIAmAnAdminLink.setOnClickListener {
            it.visibility = View.INVISIBLE
            txtvwIAmNotAnAdminLink.visibility = View.VISIBLE
            btnLogin.text = "Admin Login"
            isAdmin = true
        }

        txtvwIAmNotAnAdminLink.setOnClickListener {
            it.visibility = View.INVISIBLE
            txtvwIAmAnAdminLink.visibility = View.VISIBLE
            btnLogin.text = "Login"
            isAdmin = false
        }

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

        val childToBeReferenced = if(isAdmin) "Admins" else "Users"
        val usersRef =
            FirebaseDatabase
                .getInstance("https://ecommerce-whoadityanawandar-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference(childToBeReferenced)

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
                            var firstName = snapshot.child(strPhoneNumber).child("firstname").value.toString()
                            var lastName = snapshot.child(strPhoneNumber).child("lastname").value.toString()
                            var profilePicUrl = snapshot.child(strPhoneNumber).child("imageUrl").value.toString()
                            var name = "$firstName $lastName"

                            //let user in
                            loadingProgressBar.hide()

                            if (chkbxRememberMe.isChecked) {
                                //add user details to shared pref
                                addUserDataToSharedPrefs(name, phoneNumber, password, profilePicUrl, isAdmin)
                            }

                            var intent: Intent?
                            if(isAdmin) {
                                intent = Intent(applicationContext, AdminHomeActivity::class.java)
                            }
                            else{
                                intent = Intent(applicationContext, HomeActivity::class.java)
                            }
                            startActivity(intent)
                            finish()

                        } else {
                            Toast.makeText(
                                this@LoginActivity,
                                "Incorrect password!",
                                Toast.LENGTH_SHORT
                            ).show()
                            loadingProgressBar.hide()
                        }
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            if(isAdmin)"You are not an Admin" else "Number is not registered",
                            Toast.LENGTH_LONG
                        ).show()
                        loadingProgressBar.hide()
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

    private fun addUserDataToSharedPrefs(name: String, phoneNumber: String, password: String, profilePicUrl:String, isAdmin: Boolean){
        //add user details to shared pref
        val editor = getSharedPreferences("name", MODE_PRIVATE).edit()
        editor.putString("name", name)
        editor.putString("phone", phoneNumber)
        editor.putString("password", password)
        editor.putString("profilePicUrl", profilePicUrl)
        editor.putBoolean("isAdmin", isAdmin)
        editor.putBoolean("isLoggedIn", true)
        editor.apply()
    }

}//class end