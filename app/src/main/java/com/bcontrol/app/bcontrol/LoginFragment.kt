package com.bcontrol.app.bcontrol

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

class LoginFragment : Fragment(R.layout.fragment_login) {
    lateinit var usernameTextInput:com.google.android.material.textfield.TextInputEditText
    lateinit var usernameContainer:com.google.android.material.textfield.TextInputLayout
    lateinit var passwordTextInput:com.google.android.material.textfield.TextInputEditText
    lateinit var passwordContainer:com.google.android.material.textfield.TextInputLayout
    lateinit var btnLogin: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnLogin = requireView().findViewById<Button>(R.id.loginButton)
        usernameTextInput = requireView().findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.usernameEditText)
        usernameContainer = requireView().findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.usernameContainer)
        btnLogin.setOnClickListener{
            var myUsername:String = usernameTextInput.text.toString()
            Log.d("DEBUG", "Username is $myUsername")
            if(myUsername == ""){
                usernameContainer.helperText = "Campo requerido"
                postJson("https://7645-200-87-90-199.sa.ngrok.io/api/v1/token/","""{"username": "alvaro001", "password": "12345678"}""")
            }else{
                usernameContainer.helperText = null
            }
        }
    }
}