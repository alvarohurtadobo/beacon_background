package com.bcontrol.app.bcontrol

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import org.altbeacon.beacon.*
import org.altbeacon.beacon.permissions.BeaconScanPermissionsActivity
import java.time.LocalDateTime

class MonitoringFragment : Fragment(R.layout.fragment_monitoring) {
    lateinit var beaconListView: ListView
    lateinit var beaconCountTextView: TextView
    lateinit var monitoringButton: Button
    lateinit var rangingButton: Button
    lateinit var beaconReferenceApplication: BeaconReferenceApplication
    lateinit var myContext:Context
    var alertDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myContext= requireContext()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // setContentView(R.layout.fragment_monitoring)  // Originally main here
        var application = requireActivity().application as BeaconReferenceApplication

        beaconReferenceApplication = application as BeaconReferenceApplication


        // Set up a Live Data observer for beacon data
        val regionViewModel = BeaconManager.getInstanceForApplication(myContext).getRegionViewModel(beaconReferenceApplication.region)
        // observer will be called each time the monitored regionState changes (inside vs. outside region)
        regionViewModel.regionState.observe(viewLifecycleOwner, monitoringObserver)
        // observer will be called each time a new list of beacons is ranged (typically ~1 second in the foreground)
        regionViewModel.rangedBeacons.observe(viewLifecycleOwner, rangingObserver)
        rangingButton = requireView().findViewById<Button>(R.id.rangingButton)
        rangingButton.setOnClickListener{
            rangingButtonTapped(view)
        }
        monitoringButton = requireView().findViewById<Button>(R.id.monitoringButton)
        monitoringButton.setOnClickListener{
            monitoringButtonTapped(view)
        }
        beaconListView = requireView().findViewById<ListView>(R.id.beaconList)
        beaconCountTextView = requireView().findViewById<TextView>(R.id.beaconCount)
        beaconCountTextView.text = "No beacons detectados"
        beaconListView.adapter =
            ArrayAdapter(myContext, android.R.layout.simple_list_item_1, arrayOf("--"))
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
            dialogMessage = "didExitRegionEvent has fired"
            stateString == "outside"
            beaconCountTextView.text = "Fuera de la zona de detección, no beacons detectados"
            beaconListView.adapter =
                ArrayAdapter(myContext, android.R.layout.simple_list_item_1, arrayOf("--"))
        }
        else {
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
        Log.d(TAG, "Ranged: ${beacons.count()} beacons")
        if (BeaconManager.getInstanceForApplication(myContext).rangedRegions.size > 0) {
            beaconCountTextView.text = "Beacons detectados: ${beacons.count()}"
            beaconListView.adapter = ArrayAdapter(myContext, android.R.layout.simple_list_item_1,
                beacons
                    .sortedBy { it.distance }
                    .map { "${it.id1}\nid2: ${it.id2} id3:  rssi: ${it.rssi}\nest. distance: ${it.distance} m" }
                    .toTypedArray()
            )
        }
    }

    fun rangingButtonTapped(view: View) {
        val beaconManager = BeaconManager.getInstanceForApplication(myContext)
        if (beaconManager.rangedRegions.size == 0) {
            beaconManager.startRangingBeacons(beaconReferenceApplication.region)
            rangingButton.text = "Detener escaneo"
            beaconCountTextView.text = "Detección habilitada, esperando primera detección"
        }
        else {
            beaconManager.stopRangingBeacons(beaconReferenceApplication.region)
            rangingButton.text = "Reanudar escaneo"
            beaconCountTextView.text = "Detección deshabilitada, no beacons detectados"
            beaconListView.adapter =
                ArrayAdapter(myContext, android.R.layout.simple_list_item_1, arrayOf("--"))
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

        }
        else {
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