package com.bcontrol.app.bcontrol

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    lateinit var firstName: EditText
    lateinit var lastName: EditText
    lateinit var email: EditText
    lateinit var role: EditText
    lateinit var client: EditText
    lateinit var branch: EditText
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firstName =requireView().findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.firstname_edit_text)
        lastName =requireView().findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.lastname_edit_text)
        email =requireView().findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.email_edit_text)
        role =requireView().findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.role_edit_text)
        client =requireView().findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.company_edit_text)
        branch =requireView().findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.branch_edit_text)
        firstName.setText(myUser.firstName)
        lastName.setText(myUser.lastName)
        email.setText(myUser.email)
        role.setText(myUser.roleName)
        client.setText(myUser.clientName)
        branch.setText(myUser.branchName)
    }
}