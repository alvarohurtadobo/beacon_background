package com.bcontrol.app.bcontrol

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    lateinit var first_name_input: EditText
    lateinit var last_name_input: EditText
    lateinit var email_input: EditText
    lateinit var role_input: EditText
    lateinit var client_input: EditText
    lateinit var branch_input: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
     }

//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_profile, container, false)
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        first_name_input = requireView().findViewById<EditText>(R.id.first_name_edit_text)
        first_name_input.setText(myUser.firstName)
        last_name_input = requireView().findViewById<EditText>(R.id.last_name_edit_text)
        last_name_input.setText(myUser.lastName)
        email_input = requireView().findViewById<EditText>(R.id.email_edit_text)
        email_input.setText(myUser.email)
        role_input = requireView().findViewById<EditText>(R.id.role_edit_text)
        role_input.setText(myUser.roleName)
        client_input = requireView().findViewById<EditText>(R.id.company_edit_text)
        client_input.setText(myUser.clientName)
        branch_input = requireView().findViewById<EditText>(R.id.branch_edit_text)
        branch_input.setText(myUser.branchName)
    }
}