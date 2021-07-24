package com.whoadityanawandar.ecommerce.ui.logout

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.whoadityanawandar.ecommerce.MainActivity
import com.whoadityanawandar.ecommerce.databinding.FragmentLogoutBinding



class LogoutFragment : Fragment() {

    private lateinit var logoutViewModel: LogoutViewModel
    private var _binding: FragmentLogoutBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        logoutViewModel =
            ViewModelProvider(this).get(LogoutViewModel::class.java)

        _binding = FragmentLogoutBinding.inflate(inflater, container, false)
        val root: View = binding.root

        clearSharedPreferences()
        val intent = Intent(this.context, MainActivity::class.java)
        startActivity(intent)

/*        val textView: TextView = binding.textLogout
        logoutViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })*/
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun clearSharedPreferences() {
        val editor = this.requireActivity().getSharedPreferences("name", AppCompatActivity.MODE_PRIVATE)!!.edit()
        editor.putString("name", "")
        editor.putString("password", "")
        editor.putString("phone", "")
        editor.putBoolean("isAdmin", false)
        editor.putBoolean("isLoggedIn", false)
        editor.apply()

    }
}