package com.bcontrol.app.bcontrol

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import org.altbeacon.beacon.*
import org.altbeacon.beacon.permissions.BeaconScanPermissionsActivity
import java.time.LocalDateTime

class MonitoringFragment : Fragment(R.layout.fragment_monitoring) {
//    lateinit var beaconListView: ListView
    lateinit var resultText: TextView
    lateinit var beaconCountTextView: TextView
    lateinit var monitoringButton: Button
    lateinit var goToLoginButton: Button
    lateinit var logoutButton: Button

    lateinit var rangingButton: Button
    lateinit var beaconReferenceApplication: BeaconReferenceApplication
    lateinit var myContext: Context
    lateinit var sharedPreferences: SharedPreferences
    var alertDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myContext = requireContext()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // setContentView(R.layout.fragment_monitoring)  // Originally main here
        beaconReferenceApplication = requireActivity().application as BeaconReferenceApplication
        beaconReferenceApplication.initBeaconService()

        // Set up a Live Data observer for beacon data
        val regionViewModel = BeaconManager.getInstanceForApplication(myContext)
            .getRegionViewModel(beaconReferenceApplication.region)
        // observer will be called each time the monitored regionState changes (inside vs. outside region)
        regionViewModel.regionState.observe(viewLifecycleOwner, monitoringObserver)
        // observer will be called each time a new list of beacons is ranged (typically ~1 second in the foreground)
        regionViewModel.rangedBeacons.observe(viewLifecycleOwner, rangingObserver)
        rangingButton = requireView().findViewById<Button>(R.id.rangingButton)
        rangingButton.setOnClickListener {
            rangingButtonTapped(view)
        }
        monitoringButton = requireView().findViewById<Button>(R.id.monitoringButton)
        monitoringButton.setOnClickListener {
            monitoringButtonTapped(view)
        }
        goToLoginButton = requireView().findViewById<Button>(R.id.goToProfileButton)
        goToLoginButton.setOnClickListener {
            findNavController().navigate(R.id.action_monitoringFragment_to_profileFragment)
        }
        logoutButton = requireView().findViewById<Button>(R.id.logoutButton)
        logoutButton.setOnClickListener {
            Log.d("DEBUG", "pressed logout")
//            Toast.makeText(this, "Hasta pronto", Toast.LENGTH_SHORT).show()
            sharedPreferences =
                context?.getSharedPreferences("SESION", Context.MODE_PRIVATE)!!
            sharedPreferences.edit().clear().commit()
            findNavController().navigate(R.id.action_monitoringFragment_to_loginFragment)
        }
//        beaconListView = requireView().findViewById<ListView>(R.id.beaconList)
        resultText= requireView().findViewById<TextView>(R.id.resultText)
        beaconCountTextView = requireView().findViewById<TextView>(R.id.beaconCount)
        beaconCountTextView.text = "No beacons detectados"
//        beaconListView.adapter =
//            ArrayAdapter(myContext, android.R.layout.simple_list_item_1, arrayOf("--"))
        Log.d("DEBUG", "Arrived to this")
    }

    override fun onPause() {
        Log.d(TAG, "onPause")
        super.onPause()
    }

    override fun onResume() {
        Log.d(TAG, "onResume")
        super.onResume()
        // You MUST make sure the following dynamic permissions are granted by the user to detect beacons
        //
        //    Manifest.permission.BLUETOOTH_SCAN
        //    Manifest.permission.BLUETOOTH_CONNECT
        //    Manifest.permission.ACCESS_FINE_LOCATION
        //    Manifest.permission.ACCESS_BACKGROUND_LOCATION // only needed to detect in background
        //
        // The code needed to get these permissions has become increasingly complex, so it is in
        // its own file so as not to clutter this file focussed on how to use the library.

        if (!BeaconScanPermissionsActivity.allPermissionsGranted(
                myContext,
                true
            )
        ) {
            val intent = Intent(myContext, BeaconScanPermissionsActivity::class.java)
            intent.putExtra("backgroundAccessRequested", true)
            startActivity(intent)
        }
    }

    val monitoringObserver = Observer<Int> { state ->
        var dialogTitle = "Beacons detectados"
        var dialogMessage = "Un beacon a entrado en la región"
        var stateString = "inside"
        if (state == MonitorNotifier.OUTSIDE) {
            dialogTitle = "No beacons detectados"
            dialogMessage = "Ya no se detectó actividad, en dos minutos se cerrará la el evento en curso"
            stateString == "outside"
            beaconCountTextView.text = "Fuera de la zona de detección, no beacons detectados"
            resultText.setText("Fuera del área ${currentDetectedBeacon.area_name}, se desconectará automáticamente si no se reingresa")
//            beaconListView.adapter =
//                ArrayAdapter(myContext, android.R.layout.simple_list_item_1, arrayOf("--"))
        } else {
            beaconCountTextView.text = "Dentro de la zona de detección"
        }
        Log.d(TAG, "monitoring state changed to : $stateString")
        val builder =
            AlertDialog.Builder(myContext)
        builder.setTitle(dialogTitle)
        builder.setMessage(dialogMessage)
        builder.setPositiveButton(android.R.string.ok, null)
        alertDialog?.dismiss()
        alertDialog = builder.create()
        alertDialog?.show()
    }

    val rangingObserver = Observer<Collection<Beacon>> { beacons ->
        Log.d(TAG, "Ranged: ${beacons.count()} beacons in Monitor ${BeaconManager.getInstanceForApplication(myContext).rangedRegions.size}")
        if (BeaconManager.getInstanceForApplication(myContext).rangedRegions.size > 0) {
            beaconCountTextView.text = "Beacons detectados: ${beacons.count()}"
            if(beacons.count()>0) {
                var newDetectedBeacon = findBeacon(beacons)

                if(newDetectedBeacon.id==0){
                    resultText.setText("Beacon no registrado: ${newDetectedBeacon.uuid}")
                }else{
                    if(currentDetectedBeacon.id!=newDetectedBeacon.id){
                        val currentTime = LocalDateTime.now()
                        Log.d("DEBUG", "Detectado ${newDetectedBeacon.id}, anterior: ${currentDetectedBeacon.id}, Current event: ${currentEvent.id}")
                        // A new non 0 beacon detected, this is different than prev
                        if(currentDetectedBeacon.id!=0 && currentEvent.id !=0){
                            // If prev has a non 0 id and we have an ongoing event we have to close current event
//                            currentEvent.end_hour = currentTime.hour
//                            currentEvent.end_minute = currentTime.minute
                            val updteEventResponse = putJson("$myUrl/api/v1/business/event/${currentEvent.id}",
                                """{"end_hour": ${currentTime.hour}, "end_minute": ${currentTime.minute}, "is_closed": true}""")
                            if(updteEventResponse.statusCode==200) {
                                Log.d("DEBUG","Update event success")
                                currentEvent = EventModel(0,0,0,0,0,0,0,false)
                            }else{
                                Log.d("DEBUG","Update event failure ${updteEventResponse.statusCode}:${updteEventResponse.response}")
                            }
                        }
                        currentDetectedBeacon = newDetectedBeacon

                        currentEvent = EventModel(0,currentDetectedBeacon.id, myUser.id,currentTime.hour, currentTime.minute,0,0,false)
                        Log.d("DEBUG","Creating event $currentEvent")
                        val createEventResponse = postJson("$myUrl/api/v1/business/event",
                            """{"beacon": ${currentEvent.beacon}, "user": ${currentEvent.user}, "start_hour": ${currentEvent.start_hour}, "start_minute": ${currentEvent.start_minute}}""")
                        if(createEventResponse.statusCode==201) {
                            Log.d("DEBUG","Create event success")
                            currentEvent = EventModel.fromJsonString(createEventResponse.response)
                            Log.d("DEBUG","Created event $currentEvent")
                        }else{
                            Log.d("DEBUG","Create event failure ${createEventResponse.statusCode}:${createEventResponse.response}")
                        }
                    }else{
                        //If new beacon id is not different from prev we update the timer
                        lastDetection = LocalDateTime.now()
                        Log.d("DEBUG", "Last detection updated to $lastDetection")
                    }
                    resultText.setText("Beacon: ${newDetectedBeacon.id}\nNombre: ${newDetectedBeacon.name}\nModelo: ${newDetectedBeacon.model}\nUUID: ${newDetectedBeacon.uuid}")
                }
            }
//            beaconListView.adapter = ArrayAdapter(myContext, android.R.layout.simple_list_item_1,
//                beacons
//                    .sortedBy { it.distance }
//                    .map { "${it.id1}\nid2: ${it.id2} id3: ${it.id3} rssi: ${it.rssi}\nest. distance: ${it.distance} m.\n Código: (${it.beaconTypeCode}), Identifiers: ${it.identifiers}, Campos de datos: ${it.dataFields}, Campos de datos extra: ${it.extraDataFields}, Fabricante: ${it.manufacturer}" }
//                    .toTypedArray()
//            )
        }
    }

    fun rangingButtonTapped(view: View) {
        val beaconManager = BeaconManager.getInstanceForApplication(myContext)
        if (beaconManager.rangedRegions.size == 0) {
            beaconManager.startRangingBeacons(beaconReferenceApplication.region)
            rangingButton.text = "Detener escaneo"
            beaconCountTextView.text = "Detección habilitada, esperando primera detección"
        } else {
            beaconManager.stopRangingBeacons(beaconReferenceApplication.region)
            rangingButton.text = "Reanudar escaneo"
            beaconCountTextView.text = "Detección deshabilitada, no beacons detectados"
//            beaconListView.adapter =
//                ArrayAdapter(myContext, android.R.layout.simple_list_item_1, arrayOf("--"))
        }
    }

    fun monitoringButtonTapped(view: View) {
        var dialogTitle = ""
        var dialogMessage = ""
        val beaconManager = BeaconManager.getInstanceForApplication(myContext)
        if (beaconManager.monitoredRegions.size == 0) {
            beaconManager.startMonitoring(beaconReferenceApplication.region)
            dialogTitle = "Monitoreo de beacons iniciado."
            dialogMessage = "Verás una ventana cuando un beacon es o deja de ser detectado"
            monitoringButton.text = "Desactivar ventanas"

        } else {
            beaconManager.stopMonitoring(beaconReferenceApplication.region)
            dialogTitle = "Beacon monitoring stopped."
            dialogMessage = "Las ventanas de dialogo están deshabilitadas"
            monitoringButton.text = "Activar ventanas"
        }
        val builder =
            AlertDialog.Builder(myContext)
        builder.setTitle(dialogTitle)
        builder.setMessage(dialogMessage)
        builder.setPositiveButton(android.R.string.ok, null)
        alertDialog?.dismiss()
        alertDialog = builder.create()
        alertDialog?.show()

    }

    companion object {
        val TAG = "MainActivity"
        val PERMISSION_REQUEST_BACKGROUND_LOCATION = 0
        val PERMISSION_REQUEST_BLUETOOTH_SCAN = 1
        val PERMISSION_REQUEST_BLUETOOTH_CONNECT = 2
        val PERMISSION_REQUEST_FINE_LOCATION = 3
    }

}