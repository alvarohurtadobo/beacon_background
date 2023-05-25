package com.bcontrol.app.bcontrol

import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.reflect.TypeToken

val myUrl: String = "http://165.22.2.81"

class LoginFragment : Fragment(R.layout.fragment_login) {
    lateinit var usernameTextInput: com.google.android.material.textfield.TextInputEditText
    lateinit var usernameContainer: com.google.android.material.textfield.TextInputLayout
    lateinit var passwordTextInput: com.google.android.material.textfield.TextInputEditText
    lateinit var passwordContainer: com.google.android.material.textfield.TextInputLayout
    lateinit var btnLogin: Button
    var alertDialog: AlertDialog? = null

    var myUsername: String = ""
    var myPassword: String = ""
    lateinit var sharedPreferences: SharedPreferences

    fun isBluetoothEnabled(): Boolean {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled
    }

    private fun enableBluetooth() {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            // Device does not support Bluetooth
            // Handle the case accordingly
            return
        }

        if (!bluetoothAdapter.isEnabled) {
            val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivity(enableBluetoothIntent)
        } else {
            // Bluetooth is already enabled
            // Handle the case accordingly
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences =
            context?.getSharedPreferences("SESION", Context.MODE_PRIVATE)!!

        myUsername = sharedPreferences?.getString("username", "")!!
        myPassword = sharedPreferences?.getString("password", "")!!
        var beaconAnswer: MyHttpResponse = getJson(
            "$myUrl/api/v1/business/beacon/byClient/4"
        )
        val gson = Gson()
        val jsonArray = gson.fromJson(beaconAnswer.response, JsonArray::class.java)
        listOfBeacons = jsonArray.map { gson.fromJson(it, BeaconModel::class.java) }.toMutableList()
        Log.d("DEBUG", "listOfBeacons: ${listOfBeacons}")

        if (myUsername != "" && myPassword != "") {
            Log.d("DEBUG", "Registered data is $myUsername, $myPassword")
            var answer: MyHttpResponse = postJson(
                "$myUrl/api/v1/token/",
                """{"username": "$myUsername", "password": "$myPassword"}"""
            )
            Log.d("DEBUG", "Answer is ${answer.statusCode}:, ${answer.response}")
            if (answer.statusCode == 200) {
                myUser = UserModel.fromJsonString(answer.response)
                Log.d("DEBUG","Direct user print $myUser")
                Log.d("DEBUG","To String user print ${myUser.toString()}")
                findNavController().navigate(R.id.action_loginFragment_to_monitoringFragment)
            } else {
                val editor = sharedPreferences?.edit()
                editor?.putString("username", "")
                editor?.putString("password", "")
                editor?.commit()
            }
        }
        btnLogin = requireView().findViewById<Button>(R.id.loginButton)
        usernameTextInput =
            requireView().findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.usernameEditText)
        usernameContainer =
            requireView().findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.usernameContainer)
        passwordTextInput =
            requireView().findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.passwordEditText)
        passwordContainer =
            requireView().findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.passwordContainer)
//        if(!isBluetoothEnabled()){
//            btnLogin.setText("Encender bluetooth e Ingresar")
//        }
        btnLogin.setOnClickListener {
            if(!isBluetoothEnabled()){
                var myContext = requireContext()
                val builder =
                    AlertDialog.Builder(myContext)
                builder.setTitle("Bluetooth apagado")
                builder.setMessage("Por favor asegúrese de mantener el bluetooth encendido en todo momento incluso cuando el aplicativo está en segundo plano o minimizado")
                builder.setPositiveButton(android.R.string.ok, null)
                alertDialog?.dismiss()
                alertDialog = builder.create()
                alertDialog?.show()
            }
            else {
//                enableBluetooth()
                myUsername = usernameTextInput.text.toString()
                myPassword = passwordTextInput.text.toString()
                Log.d("DEBUG", "Username is $myUsername, $myPassword")
                if (myUsername == "") {
                    usernameContainer.helperText = "Campo requerido"
                } else {
                    if (myPassword == "") {
                        passwordContainer.helperText = "Campo requerido"
                    } else {
                        usernameContainer.helperText = null
                        var answer: MyHttpResponse = postJson(
                            "$myUrl/api/v1/token/",
                            """{"username": "$myUsername", "password": "$myPassword"}"""
                        )
                        Log.d("DEBUG", "Answer is ${answer.statusCode}:, ${answer.response}")
                        if (answer.statusCode == 200) {
                            myUser = UserModel.fromJsonString(answer.response)
                            Log.d("DEBUG", "Direct user print $myUser")
                            Log.d("DEBUG", "To String user print ${myUser.toString()}")
                            findNavController().navigate(R.id.action_loginFragment_to_monitoringFragment)
                            val editor = sharedPreferences?.edit()
                            editor?.putString("username", myUsername)
                            editor?.putString("password", myPassword)
                            editor?.commit()

                        } else {
                            var myContext = requireContext()
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
}