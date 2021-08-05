package com.whoadityanawandar.ecommerce

import android.widget.Button;
import com.android.volley.Request;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class TestActivity : AppCompatActivity() {
/*    // creating variables for edi text,
    // button and our text views.
    private var pinCodeEdt: EditText? = null
    private var getDataBtn: Button? = null
    private var pinCodeDetailsTV: TextView? = null

    // creating a variable for our string.
    var pinCode: String? = null

    // creating a variable for request queue.
    private var mRequestQueue: RequestQueue? = null


protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // initializing our variables.
        pinCodeEdt = findViewById<EditText>(R.id.idedtPinCode)
        getDataBtn = findViewById<Button>(R.id.idBtnGetCityandState)
        pinCodeDetailsTV = findViewById<TextView>(R.id.idTVPinCodeDetails)

        // initializing our request que variable with request
        // queue and passing our context to it.
        mRequestQueue = Volley.newRequestQueue(this@TestActivity)

        // initialing on click listener for our button.
        getDataBtn!!.setOnClickListener {
            // getting string  from EditText.
            pinCode = pinCodeEdt!!.text.toString()

            // validating if the text is empty or not.
            if (TextUtils.isEmpty(pinCode)) {
                // displaying a toast message if the
                // text field is empty
                Toast.makeText(this@TestActivity, "Please enter valid pin code", Toast.LENGTH_SHORT)
                    .show()
            } else {
                // calling a method to display 
                // our pincode details.
                getDataFromPinCode(pinCode)
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
        val queue: RequestQueue = Volley.newRequestQueue(this@TestActivity)

        // in below line we are creating a 
        // object request using volley.
        val objectRequest =
            JsonObjectRequest(Request.Method.GET, url, null,
                { response ->
                    // inside this method we will get two methods
                    // such as on response method
                    // inside on response method we are extracting
                    // data from the json format.
                    try {
                        // we are getting data of post office
                        // in the form of JSON file.
                        val postOfficeArray: JSONArray = response.getJSONArray("PostOffice")
                        if (response?.getString("Status") == "Error") {
                            // validating if the response status is success or failure.
                            // in this method the response status is having error and
                            // we are setting text to TextView as invalid pincode.
                            pinCodeDetailsTV?.setText("Pin code is not valid.")
                        } else {
                            // if the status is success we are calling this method
                            // in which we are getting data from post office object
                            // here we are calling first object of our json array.
                            val obj: JSONObject = postOfficeArray.getJSONObject(0)

                            // inside our json array we are getting district name,
                            // state and country from our data.
                            val district: String = obj.getString("District")
                            val state: String = obj.getString("State")
                            val country: String = obj.getString("Country")

                            // after getting all data we are setting this data in
                            // our text view on below line.
                            pinCodeDetailsTV!!
                                .text = ("District is : $district, State : $state,  Country : $country.trimIndent()")

                        }
                    } catch (e: JSONException) {
                        // if we gets any error then it
                        // will be printed in log cat.
                        e.printStackTrace()
                        pinCodeDetailsTV?.setText("Pin code is not valid")
                    }
                }) { // below method is called if we get
                // any error while fetching data from API.
                // below line is use to display an error message.
                Toast.makeText(this@TestActivity, "Pin code is not valid.", Toast.LENGTH_SHORT)
                    .show()
                pinCodeDetailsTV?.setText("Pin code is not valid")
            }
        // below line is use for adding object 
        // request to our request queue.
        queue.add(objectRequest)
    }*/

}