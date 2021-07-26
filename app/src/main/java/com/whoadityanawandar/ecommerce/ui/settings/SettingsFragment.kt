package com.whoadityanawandar.ecommerce.ui.settings

import android.R.attr.data
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.ContentLoadingProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.whoadityanawandar.ecommerce.R
import com.whoadityanawandar.ecommerce.databinding.FragmentSettingsBinding


class SettingsFragment : Fragment() {

    private lateinit var settingsViewModel: SettingsViewModel
    private var _binding: FragmentSettingsBinding? = null
    private lateinit var loadingProgressBar: ContentLoadingProgressBar
    private var storage = Firebase.storage
    private var strImageDownloadUrl = ""
    private var galleryPicRequestCode = 1
    private var imageUri: Uri? = null
    private var isImageChanged = false
    private var currentUserPhone = ""
    private var strFirstName = ""
    private var strLastName = ""
    private var strPhoneNumber = ""
    private var strAddress = ""
    lateinit var tvFirstNameSettings: EditText
    lateinit var tvLastNameSettings: EditText
    lateinit var tvPhoneNumberSettings: EditText
    lateinit var tvAddressSettings: EditText
    lateinit var ivProfilePic: ImageView

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        settingsViewModel =
            ViewModelProvider(this).get(SettingsViewModel::class.java)

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        ivProfilePic = binding.imgvwProfilePicSettings
        tvFirstNameSettings = binding.edttxtFirstNameSettings
        tvLastNameSettings = binding.edttxtLastNameSettings
        tvPhoneNumberSettings = binding.edttxtPhoneNumberSettings
        tvAddressSettings = binding.edttxtAddressSettings
        val btnUpdate: Button = binding.btnUpdateSettings
        val btnClose: Button = binding.btnCloseSettings
        val btnChooseImage: Button = binding.btnChooseImageSettings
        loadingProgressBar = root.findViewById(R.id.progressbar)
        loadingProgressBar.isActivated = true
        loadingProgressBar.hide()

        this.displayUserInfo()

        var sharedPreferences =
            this.requireActivity().getSharedPreferences("name", AppCompatActivity.MODE_PRIVATE)
        currentUserPhone = sharedPreferences.getString("phone", "")!!

        btnUpdate.setOnClickListener {
            if (isImageChanged) {
                uploadProfilePic(currentUserPhone)
            }
            else{
                updateUserDetails(strFirstName, strLastName, strPhoneNumber, strAddress)
            }

        }

        btnClose.setOnClickListener {
            requireActivity().onBackPressed()
        }

        btnChooseImage.setOnClickListener {
            try {
                isImageChanged = true
                var galleryIntent = Intent()
                galleryIntent.action = Intent.ACTION_GET_CONTENT
                galleryIntent.type = "image/*"
                this.requireActivity().setResult(Activity.RESULT_OK, this.requireActivity().intent)
                startActivityForResult(galleryIntent, galleryPicRequestCode)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return root
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == galleryPicRequestCode && resultCode == AppCompatActivity.RESULT_OK && data != null) {
            imageUri = data.data
            ivProfilePic.setImageURI(imageUri)
        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun updateUserDetails(
        strFirstName: String,
        strLastName: String,
        strPhoneNumber: String,
        strAddress: String
    ) {

        try {
            var user = hashMapOf(
                "firstname" to strFirstName,
                "lastname" to strLastName,
                "phone" to strPhoneNumber,
                "address" to strAddress,
                "imageUrl" to strImageDownloadUrl
            )

            var usersRef =
                FirebaseDatabase.getInstance("https://ecommerce-whoadityanawandar-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference("Users")

            usersRef.child(currentUserPhone)
                .updateChildren(user as Map<String, Any>)
                .addOnCompleteListener {
                    loadingProgressBar.hide()
                    if (it.isSuccessful) {
                        Toast.makeText(context, "Information saved successfully!", Toast.LENGTH_LONG)
                            .show()
/*                        val intent = Intent(context, SettingsFragment::class.java)
                        startActivity(intent)
                        requireActivity().finish()*/
                    } else {
                        Toast.makeText(context, "Error saving information", Toast.LENGTH_LONG).show()
                    }
                }
                .addOnFailureListener{
                    it.printStackTrace()
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun uploadProfilePic(currentUserPhone: String) {

        try {
            loadingProgressBar.show()
            val userImageRef = storage.reference.child("User Images")
            val imageID = currentUserPhone
            val filePath = userImageRef.child("$imageID.jpg")

            val uploadTask = filePath.putFile(imageUri!!)

            val urlTask = uploadTask.continueWithTask<Uri> { task ->
                if (!task.isSuccessful) {
                    loadingProgressBar.hide()
                    task.exception?.let {
                        throw it
                        Toast.makeText(context, "Error {$it}", Toast.LENGTH_SHORT).show()
                    }
                }
                filePath.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Image uploaded, saving info...", Toast.LENGTH_SHORT)
                        .show()
                    loadingProgressBar.hide()
                    strImageDownloadUrl = task.result.toString()
                    updateUserDetails(strFirstName, strLastName, strPhoneNumber, strAddress)

                    return@addOnCompleteListener
                } else {
                    Toast.makeText(context, "Image could not be uploaded", Toast.LENGTH_SHORT).show()
                    loadingProgressBar.hide()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun displayUserInfo(

    ) {
        var usersRef =
            FirebaseDatabase.getInstance("https://ecommerce-whoadityanawandar-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("Users")
        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    var firstName = snapshot.child(currentUserPhone).child("firstname").value.toString()
                    var lastName = snapshot.child(currentUserPhone).child("lastname").value.toString()
                    var phone = snapshot.child(currentUserPhone).child("phone").value.toString()
                    if (snapshot.child(currentUserPhone).child("address").exists()) {
                        var address = snapshot.child(currentUserPhone).child("address").value.toString()
                        tvAddressSettings.setText(address)
                    }
                    tvFirstNameSettings.setText(firstName)
                    tvLastNameSettings.setText(lastName)
                    tvPhoneNumberSettings.setText(phone)
                    if (snapshot.child(currentUserPhone).child("imageUrl").exists()) {
                        var imageUrl = snapshot.child(currentUserPhone).child("imageUrl").value.toString()
                        Glide.with(context!!).load(imageUrl).into(ivProfilePic)
                    }

                    strFirstName = tvFirstNameSettings.text.toString()
                    strLastName = tvLastNameSettings.text.toString()
                    strPhoneNumber = tvPhoneNumberSettings.text.toString()
                    strAddress = tvAddressSettings.text.toString()

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Cancelled!", Toast.LENGTH_SHORT).show()
            }

        })
    }

}