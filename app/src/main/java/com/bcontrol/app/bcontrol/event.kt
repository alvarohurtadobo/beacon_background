package com.bcontrol.app.bcontrol

import com.google.gson.Gson

data class EventModel(val id: Int,
                      val beacon: Int,
                      val user: Int,
                      val start_hour:Int,
                      val start_minute: Int,
                      val end_hour:Int,
                      val end_minute: Int
                      , val is_closed:Boolean) {
    override fun toString(): String = "[Event]: $id, $start_hour:$start_minute"

    companion object{
        fun fromJsonString(json: String): EventModel {
            val gson = Gson()
            return gson.fromJson(json, EventModel::class.java)
        }
    }
}

var currentEvent :EventModel = EventModel(0,0,0,0,0,0,0,false)