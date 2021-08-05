package com.whoadityanawandar.ecommerce

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import android.view.View.OnFocusChangeListener
import android.widget.*
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class AddressActivity : AppCompatActivity() {

    // creating variables for edi text,
    // button and our text views.
    lateinit var edttxtAddressCity: EditText
    lateinit var txtvwAddressState: AutoCompleteTextView
    lateinit var txtinlayAddressState: TextInputLayout
    lateinit var edttxtAddressPinCode: EditText
    lateinit var edttxtAddressLine1: EditText
    lateinit var edttxtAddressLine2: EditText
    private var currentUserPhone: String = ""

    // creating a variable for our string.
    var pinCode: String? = null

    // creating a variable for request queue.
    private var mRequestQueue: RequestQueue? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address)

        // initializing our variables.
        edttxtAddressCity = findViewById(R.id.txtvwAddressCity)
        txtvwAddressState = findViewById(R.id.txtvwAddressState)
        txtinlayAddressState = findViewById(R.id.txtinlayAddressState)
        edttxtAddressPinCode = findViewById(R.id.txtvwAddressPinCode)
        edttxtAddressLine1 = findViewById(R.id.edttxtAddressLine1)
        edttxtAddressLine2 = findViewById(R.id.edttxtAddressLine2)
        // initializing our request que variable with request
        // queue and passing our context to it.
        mRequestQueue = Volley.newRequestQueue(this@AddressActivity)

        var sharedPreferences =
            getSharedPreferences("name", MODE_PRIVATE)
        currentUserPhone = sharedPreferences.getString("phone", "")!!

        edttxtAddressPinCode.onFocusChangeListener = OnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                //Toast.makeText(applicationContext, "Got the focus", Toast.LENGTH_LONG).show()
            } else {
                // getting string  from EditText.
                pinCode = edttxtAddressPinCode.text.toString()

                // validating if the text is empty or not.
                if (TextUtils.isEmpty(pinCode)) {
                    Toast.makeText(
                        this@AddressActivity, "Please enter valid pin code",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    getDataFromPinCode(pinCode)
                }
                //Toast.makeText(applicationContext, "Lost the focus", Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun getDataFromPinCode(pinCode: String?) {

        // clearing our cache of request queue.
        mRequestQueue?.cache!!.clear()

        // below is the url from where we will be getting
        // our response in the json format.
        val url = "http://www.postalpincode.in/api/pincode/$pinCode"

        // below line is use to initialize our request queue.
        val queue: RequestQueue = Volley.newRequestQueue(this@AddressActivity)

        // creating object request using volley.
        val objectRequest =
            JsonObjectRequest(Request.Method.GET, url, null,
                { response ->
                    // inside this method we will get two methods
                    // such as on response method
                    // inside on response method we are extracting
                    // data from the json format.
                    try {
                        // getting data of post office in the form of JSON file.
                        val postOfficeArray: JSONArray = response.getJSONArray("PostOffice")
                        if (response?.getString("Status") == "Error") {
                            //if error in response
                            Toast.makeText(
                                this@AddressActivity,
                                "Pin code is not valid.",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        } else {

                            //calling first object of our json array.
                            val obj: JSONObject = postOfficeArray.getJSONObject(0)

                            // state and country from our data.
                            val district: String = obj.getString("District")
                            val state: String = obj.getString("State")
                            val country: String = obj.getString("Country")

                            // after getting all data we are setting this data in
                            // our text view on below line.
                            txtvwAddressState.setText(state)
                            edttxtAddressCity.setText(district)


                        }
                    } catch (e: JSONException) {
                        // if we gets any error then it
                        // will be printed in log cat.
                        e.printStackTrace()
                        Toast.makeText(
                            this@AddressActivity,
                            "Pin code is not valid.",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                })
            {
                // error fetching data from API.
                Toast.makeText(this@AddressActivity, "Pin code is not valid.", Toast.LENGTH_SHORT)
                    .show()
            }
        // adding request to request queue.
        queue.add(objectRequest)
    }

    fun saveShippingAddress(view: View) {

        var strAddressLine1 = edttxtAddressLine1.text.toString()
        var strAddressLine2 = edttxtAddressLine2.text.toString()
        var strCity = edttxtAddressCity.text.toString()
        var strState = txtvwAddressState.text.toString()
        var strPinCode = edttxtAddressPinCode.text.toString()

        val usersRef = FirebaseDatabase
            .getInstance("https://ecommerce-whoadityanawandar-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("Users")

        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                //try {
                //if (!(snapshot.child(strPhoneNumber).exists())) {

                var userShippingAddress = hashMapOf<String, Any>(
                    "addressLine1" to strAddressLine1,
                    "addressLine2" to strAddressLine2,
                    "city" to strCity,
                    "state" to strState,
                    "pinCode" to strPinCode
                )

                usersRef.child(currentUserPhone)
                    .child("shippingAddress")
                    .setValue(userShippingAddress)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            //loadingProgressBar.visibility = View.INVISIBLE
                            Toast.makeText(
                                this@AddressActivity,
                                "Address saved",
                                Toast.LENGTH_SHORT
                            ).show()
/*                                    val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                                    startActivity(intent)
                                    finish()*/
                        } else {
                            Toast.makeText(
                                this@AddressActivity,
                                "Something went wrong!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }


            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@AddressActivity,
                    "Candelled!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

}//class end