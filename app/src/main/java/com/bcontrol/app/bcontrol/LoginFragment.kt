package com.bcontrol.app.bcontrol

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import androidx.navigation.fragment.findNavController

class LoginFragment : Fragment(R.layout.fragment_login) {
    lateinit var usernameTextInput: com.google.android.material.textfield.TextInputEditText
    lateinit var usernameContainer: com.google.android.material.textfield.TextInputLayout
    lateinit var passwordTextInput: com.google.android.material.textfield.TextInputEditText
    lateinit var passwordContainer: com.google.android.material.textfield.TextInputLayout
    lateinit var btnLogin: Button
    var alertDialog: AlertDialog? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnLogin = requireView().findViewById<Button>(R.id.loginButton)
        usernameTextInput =
            requireView().findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.usernameEditText)
        usernameContainer =
            requireView().findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.usernameContainer)
        passwordTextInput =
            requireView().findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.passwordEditText)
        passwordContainer =
            requireView().findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.passwordContainer)
        btnLogin.setOnClickListener {
            var myUsername: String = usernameTextInput.text.toString()
            var myPassword: String = passwordTextInput.text.toString()
            Log.d("DEBUG", "Username is $myUsername, $myPassword")
            if (myUsername == "") {
                usernameContainer.helperText = "Campo requerido"
            } else {
                if (myPassword == "") {
                    passwordContainer.helperText = "Campo requerido"
                } else {
                    usernameContainer.helperText = null
                    var answer: MyHttpResponse = postJson(
                        "https://7645-200-87-90-199.sa.ngrok.io/api/v1/token/",
                        """{"username": "$myUsername", "password": "$myPassword"}"""
                    )
                    Log.d("Answer is ${answer.statusCode}:, ${answer.response}")
                    if(answer.statusCode==200){
                        findNavController().navigate(R.id.action_loginFragment_to_monitoringFragment)
                    }else{
                        var myContext= requireContext()
                        val builder =
                            AlertDialog.Builder(myContext)
                        builder.setTitle("Error")
                        builder.setMessage("No se pudo identificar, por favor revise sus credenciales")
                        builder.setPositiveButton(android.R.string.ok, null)
                        alertDialog?.dismiss()
                        alertDialog = builder.create()
                        alertDialog?.show()
                    }

                }
            }
        }
    }
}