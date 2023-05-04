package com.bcontrol.app.bcontrol

import android.util.Log
import com.google.gson.Gson
import org.altbeacon.beacon.Beacon

data class BeaconModel(
    val id: Int,
    val name: String,
    val uuid: String,
    val model: String,
    val area: Int,
    val area_name: String
){
    override fun toString(): String = "[Beacons]: $id, $name, $uuid"

    companion object{
        fun fromJsonString(json: String): BeaconModel {
            val gson = Gson()
            return gson.fromJson(json, BeaconModel::class.java)
        }
    }
}

var listOfBeacons: MutableList<BeaconModel> = mutableListOf()

var currentDetectedBeacon: BeaconModel = BeaconModel(0,"","","",0,"")

fun findBeacon(beacons: Collection<Beacon>):BeaconModel{
    if(beacons.isEmpty()){
        return BeaconModel(0,"","","",0,"")
    }
    var myAuxBeacon: Beacon = beacons.sortedBy { it.distance }[0]

    var myBeacon = BeaconModel(0,"",myAuxBeacon.id1.toString(),"",0,"")
    try {
        myBeacon =
            listOfBeacons.first { it.uuid == myAuxBeacon.id1.toString() }
        Log.d("DEBUG", "Beacon found ${myBeacon.uuid}")
        return myBeacon;
    }catch (error: java.lang.Exception){
        Log.d("DEBUG", "Beacon not found ${myBeacon.uuid}")
        return myBeacon;
    }
    return myBeacon;
}

