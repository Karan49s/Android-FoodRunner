package com.karan.foodrunner.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.karan.foodrunner.R


class Profile : Fragment() {

    private lateinit var txtUserName: TextView
    private lateinit var txtPhone: TextView
    private lateinit var txtEmail: TextView
    private lateinit var txtAddress: TextView
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        val view=inflater.inflate(R.layout.fragment_profile, container, false)

        sharedPreferences=(activity as FragmentActivity).getSharedPreferences(getString(R.string.prefrence_file_name),Context.MODE_PRIVATE)

        txtUserName=view.findViewById(R.id.txtUserName)
        txtPhone=view.findViewById(R.id.txtPhone)
        txtEmail=view.findViewById(R.id.txtEmail)
        txtAddress=view.findViewById(R.id.txtAddress)

        txtUserName.text=sharedPreferences.getString("user_name",null)
        val phoneText="+91-${sharedPreferences.getString("user_mobile_number",null)}"
        txtPhone.text=phoneText
        txtEmail.text=sharedPreferences.getString("user_email",null)
        txtAddress.text=sharedPreferences.getString("user_address",null)

        return view
    }

}