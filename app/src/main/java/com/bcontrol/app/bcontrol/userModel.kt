package com.bcontrol.app.bcontrol

import com.google.gson.Gson

data class UserModel(
    val id: Int,
    val username: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val clientId: Int,
    val clientName: String,
    val branchId: Int,
    val branchName: String,
    val roleId: Int,
    val roleName: String,
    val access: String,
    val refresh: String
){
    override fun toString(): String = "[User]: $id, $username, $email"

    companion object{
        fun fromJsonString(json: String): UserModel {
            val gson = Gson()
            return gson.fromJson(json, UserModel::class.java)
        }
    }
}

var myUser = UserModel(0, "", "", "", "", 0, "", 0, "", 0, "", "", "")