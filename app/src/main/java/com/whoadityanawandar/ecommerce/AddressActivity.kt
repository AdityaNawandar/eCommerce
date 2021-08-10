package com.whoadityanawandar.ecommerce

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.View.*
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
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.whoadityanawandar.ecommerce.adapters.AddressAdapter
import com.whoadityanawandar.ecommerce.model.Address
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


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
    lateinit var sharedPreferences: SharedPreferences
    lateinit var linlayAddNewAddress: LinearLayout
    lateinit var recvwAddresses: RecyclerView
    lateinit var btnAddNewAddress: Button
    var orderIdCounter = 0
    var addressIdCounter = 0
    lateinit var addressesAdapter: AddressAdapter
    private var pinCode: String? = null
    lateinit var products: Any
    var arrlstAddresses = ArrayList<Address>()

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
        linlayAddNewAddress = findViewById(R.id.linlayAddNewAddress)
        btnAddNewAddress = findViewById(R.id.btnAddNewAddress)
        recvwAddresses = findViewById(R.id.recvwAddresses)
        addressesAdapter = AddressAdapter(arrlstAddresses)
        // initializing our request que variable with request
        // queue and passing our context to it.
        mRequestQueue = Volley.newRequestQueue(this@AddressActivity)

        sharedPreferences =
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

        getShippingAddressesFromDB()

    }

    fun showFieldsForNewAddress(view: View) {
        linlayAddNewAddress.visibility = VISIBLE
        btnAddNewAddress.visibility = GONE
    }

    fun saveShippingAddress(view: View) {
        addAddressCounterToDB()
    }

    private fun addAddressToDB() {
        val userShippingAddress = getNewShippingAddress()

        var addressID = "Ad_$addressIdCounter"
        userShippingAddress["addressId"] = addressID

        val usersRef = FirebaseDatabase
            .getInstance("https://ecommerce-whoadityanawandar-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("Users")

        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                usersRef.child(currentUserPhone)
                    .child("ShippingAddresses")
                    .child(addressID)
                    .updateChildren(userShippingAddress)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            //loadingProgressBar.visibility = View.INVISIBLE
                            Toast.makeText(
                                this@AddressActivity,
                                "Address saved",
                                Toast.LENGTH_SHORT
                            ).show()

                            getShippingAddressesFromDB()
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
                    "Cancelled!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    fun createOrder(view: View) {
        getProductsFromCart()
    }

    private fun initializeRecyclerView() {

        try {
            addressesAdapter = AddressAdapter(arrlstAddresses)
            //recyclerView.setHasFixedSize(true)
            var layoutManager = LinearLayoutManager(this)
            recvwAddresses.layoutManager = layoutManager
            recvwAddresses.adapter = addressesAdapter

            //addressesAdapter.startListening()
        } catch (e: Exception) {
            e.printStackTrace()
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

    private fun getShippingAddressesFromDB() {
        var addressesRef = FirebaseDatabase
            .getInstance("https://ecommerce-whoadityanawandar-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .reference.child("Users")
            .child(currentUserPhone)
            .child("ShippingAddresses")

        addressesRef
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        var lstAddresses = ArrayList<Address>()
                            for (addressSnapshot in snapshot.children) {
                                val address = addressSnapshot.getValue<Address>()
                                lstAddresses.add(address!!)
                        }

                        arrlstAddresses = lstAddresses
                        initializeRecyclerView()

                        Toast.makeText(
                            this@AddressActivity,
                            "Addresses fetched!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@AddressActivity, "Cancelled!", Toast.LENGTH_SHORT).show()
                }


            })
    }

    private fun getNewShippingAddress(): MutableMap<String, Any> {
        var strAddressLine1 = edttxtAddressLine1.text.toString()
        var strAddressLine2 = edttxtAddressLine2.text.toString()
        var strCity = edttxtAddressCity.text.toString()
        var strState = txtvwAddressState.text.toString()
        var strPinCode = edttxtAddressPinCode.text.toString()

        var userShippingAddress = hashMapOf<String, Any>(
            "addressLine1" to strAddressLine1,
            "addressLine2" to strAddressLine2,
            "city" to strCity,
            "state" to strState,
            "pinCode" to strPinCode
        )

        return userShippingAddress
    }

    private fun getProductsFromCart() {
        var cartRef = FirebaseDatabase
            .getInstance("https://ecommerce-whoadityanawandar-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .reference.child("User View Cart")

        cartRef.child(currentUserPhone).get()
            .addOnCompleteListener {
                if (it.result!!.exists()) {

                    var productsMap = it.result!!.value as MutableMap<Any, Any>
                    products = productsMap["Products"]!!

                    Toast.makeText(this, "Products saved", Toast.LENGTH_SHORT).show()
                    addOrderCounterToDB(products)

                }
            }
    }

    private fun saveOrder(userShippingAddress: MutableMap<String, Any>) {
        var dbRef = FirebaseDatabase
            .getInstance("https://ecommerce-whoadityanawandar-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .reference

        val simpleDateFormat = SimpleDateFormat("MMMddyy'_'hh:mm:ss'_'aa")
        val currentDate = simpleDateFormat.format(Date())
        var orderID = "Or_$orderIdCounter"

        var order = hashMapOf<String, Any>(
            "orderID" to orderID,
            "orderDateTime" to currentDate,
            "products" to products!!,
            "orderStatus" to "Pending Payment",
            "shippingAddress" to userShippingAddress
        )

        dbRef.child("Orders")
            .child(currentUserPhone)
            .child(orderID)
            .updateChildren(order)
            .addOnCompleteListener {

            }
    }

    private fun addOrderCounterToDB(products: Any?) {
        val ordersRef = FirebaseDatabase
            .getInstance("https://ecommerce-whoadityanawandar-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("Counters")
            .child("orderIdCounter")

        ordersRef.runTransaction(object : Transaction.Handler {

            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                try {
                    val currentValue: Int? = mutableData.getValue(Int::class.java)
                    if (currentValue == null) {
                        mutableData.value = 100000
                    } else {
                        mutableData.value = currentValue + 1
                    }
                    orderIdCounter = Integer.parseInt(mutableData!!.value.toString())

                } catch (e: Exception) {

                    e.printStackTrace()
                }
                return Transaction.success(mutableData)
            }

            override fun onComplete(
                databaseError: DatabaseError?,
                committed: Boolean,
                dataSnapshot: DataSnapshot?
            ) {
                //getShippingAddressesFromDB()
                println("Transaction completed")
            }
        })
    }

    private fun addAddressCounterToDB() {
        val addressesRef = FirebaseDatabase
            .getInstance("https://ecommerce-whoadityanawandar-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("Counters")
            .child("addressIdCounter")


        addressesRef.runTransaction(object : Transaction.Handler {

            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                try {
                    val currentValue: Int? = mutableData.getValue(Int::class.java)
                    if (currentValue == null) {
                        mutableData.value = 100000
                    } else {
                        mutableData.value = currentValue + 1
                    }
                    addressIdCounter = Integer.parseInt(mutableData!!.value.toString())

                } catch (e: Exception) {

                    e.printStackTrace()
                }
                return Transaction.success(mutableData)
            }

            override fun onComplete(
                databaseError: DatabaseError?,
                committed: Boolean,
                dataSnapshot: DataSnapshot?
            ) {
                addAddressToDB()
                //println("Transaction completed")
            }
        })
    }


}//class end