package com.whoadityanawandar.ecommerce

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.widget.ContentLoadingProgressBar
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.text.SimpleDateFormat
import java.util.*

class AddProductAdminActivity : AppCompatActivity() {

    private lateinit var imgvwAddProductImage: ImageView
    private var strProductName = ""
    var strProductDescription = ""
    var strProductPrice = ""
    var categoryName = ""
    lateinit var edttxtProductName: EditText
    lateinit var edttxtProductDescription: EditText
    lateinit var edttxtProductPrice: EditText
    private lateinit var loadingProgressBar: ContentLoadingProgressBar
    var storage = Firebase.storage
    var strImageDownloadUrl = ""
    private var galleryPicRequestCode = 1
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product_admin)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        try {
            categoryName = intent.getStringExtra("category").toString()
            edttxtProductName = findViewById(R.id.edttxtProductName)
            edttxtProductDescription = findViewById(R.id.edttxtProductDescription)
            edttxtProductPrice = findViewById(R.id.edttxtProductPrice)
            imgvwAddProductImage = findViewById(R.id.imgvwSelectProductImage)
            loadingProgressBar = findViewById(R.id.progressbar)
            loadingProgressBar.isActivated = true
            loadingProgressBar.hide()

            Toast.makeText(this, "You need to add $categoryName", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun hideKeyboard(view: View) {
        val inputMethodManager: InputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.applicationWindowToken, 0)
    }

    fun chooseProductImage(view: View) {
        try {
            var galleryIntent = Intent()
            galleryIntent.action = Intent.ACTION_GET_CONTENT
            galleryIntent.type = "image/*"
            setResult(Activity.RESULT_OK, intent)
            startActivityForResult(galleryIntent, galleryPicRequestCode)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == galleryPicRequestCode && resultCode == RESULT_OK && data != null) {
            imageUri = data.data
            imgvwAddProductImage.setImageURI(imageUri)
        }

    }

    fun validateProductData(view: View) {
        strProductName = edttxtProductName.text.toString()
        strProductDescription = edttxtProductDescription.text.toString()
        strProductPrice = edttxtProductPrice.text.toString()

        if (strProductName == "") {
            Toast.makeText(this, "Please add Product Name", Toast.LENGTH_SHORT).show()
        } else if (strProductDescription == "") {
            Toast.makeText(this, "Please add Description", Toast.LENGTH_SHORT).show()
        } else if (strProductPrice == "") {
            Toast.makeText(this, "Please add Price", Toast.LENGTH_SHORT).show()
        } else {
            saveProduct()
        }
    }

    private fun saveProduct() {

        try {
            loadingProgressBar.show()
            val simpleDateFormat = SimpleDateFormat("ddMMMyy'_'hh:mm:ss aa")
            val currentDate = simpleDateFormat.format(Date())

                val productImageRef = storage.reference.child("Product Images")
                val productID = strProductName + "_" + currentDate
                val filePath = productImageRef.child("$productID.jpg")

                val uploadTask = filePath.putFile(imageUri!!)

                val urlTask = uploadTask.continueWithTask<Uri> { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                            Toast.makeText(this, "Error {$it}", Toast.LENGTH_SHORT).show()
                        }
                    }
                    filePath.downloadUrl
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Image uploaded, adding details...", Toast.LENGTH_SHORT).show()
                        loadingProgressBar.hide()
                        strImageDownloadUrl = task.result.toString()
                        saveProductDetails(productID, currentDate)

                        return@addOnCompleteListener
                    } else {
                        Toast.makeText(this, "Image could not be uploaded", Toast.LENGTH_SHORT).show()
                        loadingProgressBar.hide()
                    }
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    private fun saveProductDetails(productID: String, currentDate: String) {

        try {
            var product = hashMapOf(
                "productID" to productID,
                "productName" to strProductName,
                "description" to strProductDescription,
                "imageUrl" to strImageDownloadUrl,
                "category" to categoryName,
                "dateTime" to currentDate,
                "price" to strProductPrice
            )
            loadingProgressBar.show()
            val productRef =
                FirebaseDatabase
                    .getInstance("https://ecommerce-whoadityanawandar-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference("Products")

            productRef.child(productID).updateChildren(product as Map<String, Any>)
                .addOnCompleteListener {
                    loadingProgressBar.hide()
                    if (it.isSuccessful) {
                        Toast.makeText(this, "Product added successfully!", Toast.LENGTH_LONG).show()
                        val intent = Intent(this, ProductCategoriesAdminActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Error adding product", Toast.LENGTH_LONG).show()
                    }
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



}//class end