package com.example.newsapp.ui.settings

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentSettingsBinding
import com.example.newsapp.resources.user
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class SettingsFragment : Fragment() {
    val db = FirebaseFirestore.getInstance()
    private var _binding: FragmentSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val slideshowViewModel =
            ViewModelProvider(this).get(SettingsViewModel::class.java)

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val nameTxt=root.findViewById<TextView>(R.id.et_name)
        val emailTxt=root.findViewById<TextView>(R.id.tv_email)
        val btnUPdate=root.findViewById<Button>(R.id.btn_update)
        btnUPdate.setOnClickListener(View.OnClickListener {
            db.collection("users").document(Firebase.auth.currentUser!!.uid)
                .update("name", nameTxt.text.toString())
            Toast.makeText(requireContext(),"Name updated",Toast.LENGTH_SHORT).show()
        })
        Firebase.auth.currentUser?.let {
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(it.uid)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val product: user? = task.result!!.toObject(user::class.java)
//                        Toast.makeText(this,"onComplete: $product",Toast.LENGTH_SHORT).show()
                        Log.d(ContentValues.TAG, "onComplete: $product")
                        if (product != null) {
                            nameTxt.text = product.name.toString()
                            emailTxt.text = product.email.toString()
                        }

                    } else {
//                        Toast.makeText(this,"not successful",Toast.LENGTH_SHORT).show()
                        Log.e(ContentValues.TAG, "onComplete: ", task.exception)
                    }
                }
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}