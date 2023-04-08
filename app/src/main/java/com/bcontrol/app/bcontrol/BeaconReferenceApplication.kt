import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bcontrol.app.bcontrol.R

//package com.bcontrol.app.bcontrol
//
//import android.app.*
//import android.content.Context
//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import android.view.View
//import android.widget.ArrayAdapter
//import android.widget.Button
//import android.widget.ListView
//import android.widget.TextView
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.app.NotificationCompat
//import androidx.lifecycle.Observer
//import org.altbeacon.beacon.*
//import org.altbeacon.beacon.permissions.BeaconScanPermissionsActivity
//import java.time.LocalDateTime
//import java.time.ZoneOffset
//import java.time.format.DateTimeFormatter
//
//var lastDetection = LocalDateTime.now();
//
//class BeaconReferenceApplication: Application() {
//    lateinit var region: Region
//
//    override fun onCreate() {
//        super.onCreate()
//
//        val beaconManager = BeaconManager.getInstanceForApplication(this)
//        BeaconManager.setDebug(true)
//
//        // By default the AndroidBeaconLibrary will only find AltBeacons.  If you wish to make it
//        // find a different type of beacon, you must specify the byte layout for that beacon's
//        // advertisement with a line like below.  The example shows how to find a beacon with the
//        // same byte layout as AltBeacon but with a beaconTypeCode of 0xaabb.  To find the proper
//        // layout expression for other beacon types, do a web search for "setBeaconLayout"
//        // including the quotes.
//        //
//        //beaconManager.getBeaconParsers().clear();
//        //beaconManager.getBeaconParsers().add(new BeaconParser().
//        //        setBeaconLayout("m:0-1=4c00,i:2-24v,p:24-24"));
//
//
//        // By default the AndroidBeaconLibrary will only find AltBeacons.  If you wish to make it
//        // find a different type of beacon like Eddystone or iBeacon, you must specify the byte layout
//        // for that beacon's advertisement with a line like below.
//        //
//        // If you don't care about AltBeacon, you can clear it from the defaults:
//        beaconManager.getBeaconParsers().clear()
//
//        // The example shows how to find iBeacon.
//        beaconManager.getBeaconParsers().add(
//            BeaconParser().
//                setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"))
//
//        // enabling debugging will send lots of verbose debug information from the library to Logcat
//        // this is useful for troubleshooting problmes
//        // BeaconManager.setDebug(true)
//
//
//        // The BluetoothMedic code here, if included, will watch for problems with the bluetooth
//        // stack and optionally:
//        // - power cycle bluetooth to recover on bluetooth problems
//        // - periodically do a proactive scan or transmission to verify the bluetooth stack is OK
//        // BluetoothMedic.getInstance().enablePowerCycleOnFailures(this)
//        // BluetoothMedic.getInstance().enablePeriodicTests(this, BluetoothMedic.SCAN_TEST + BluetoothMedic.TRANSMIT_TEST)
//
//        // By default, the library will scan in the background every 5 minutes on Android 4-7,
//        // which will be limited to scan jobs scheduled every ~15 minutes on Android 8+
//        // If you want more frequent scanning (requires a foreground service on Android 8+),
//        // configure that here.
//        // If you want to continuously range beacons in the background more often than every 15 mintues,
//        // you can use the library's built-in foreground service to unlock this behavior on Android
//        // 8+.   the method below shows how you set that up.
//        setupForegroundService()
//        beaconManager.setEnableScheduledScanJobs(false);
//        beaconManager.setBackgroundBetweenScanPeriod(5000);
//        beaconManager.setBackgroundScanPeriod(5000);
//
//        // Ranging callbacks will drop out if no beacons are detected
//        // Monitoring callbacks will be delayed by up to 25 minutes on region exit
//        // beaconManager.setIntentScanningStrategyEnabled(true)
//
//        // The code below will start "monitoring" for beacons matching the region definition below
//        // the region definition is a wildcard that matches all beacons regardless of identifiers.
//        // if you only want to detect beacons with a specific UUID, change the id1 paremeter to
//        // a UUID like Identifier.parse("2F234454-CF6D-4A0F-ADF2-F4911BA9FFA6")
//        region = Region("all-beacons", null, null, null)
//        beaconManager.startMonitoring(region)
//        beaconManager.startRangingBeacons(region)
//        // These two lines set up a Live Data observer so this Activity can get beacon data from the Application class
//        val regionViewModel = BeaconManager.getInstanceForApplication(this).getRegionViewModel(region)
//        // observer will be called each time the monitored regionState changes (inside vs. outside region)
//        regionViewModel.regionState.observeForever( centralMonitoringObserver)
//        // observer will be called each time a new list of beacons is ranged (typically ~1 second in the foreground)
//        regionViewModel.rangedBeacons.observeForever( centralRangingObserver)
//    }
//
//    fun setupForegroundService() {
//        val builder = Notification.Builder(this, "BeaconReferenceApp")
//        builder.setSmallIcon(R.drawable.ic_launcher_background)
//        builder.setContentTitle("Buscando Beacons.")
//        val intent = Intent(this, MainActivity::class.java)
//        val pendingIntent = PendingIntent.getActivity(
//                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT + PendingIntent.FLAG_IMMUTABLE
//        )
//        builder.setContentIntent(pendingIntent);
//        val channel =  NotificationChannel("beacon-ref-notification-id",
//            "Detección de beacon", NotificationManager.IMPORTANCE_DEFAULT)
//        channel.setDescription("Esta notificación se lanza cuando un beacon es detectado.")
//        val notificationManager =  getSystemService(
//                Context.NOTIFICATION_SERVICE) as NotificationManager
//        notificationManager.createNotificationChannel(channel);
//        builder.setChannelId(channel.getId());
//        BeaconManager.getInstanceForApplication(this).enableForegroundServiceScanning(builder.build(), 456);
//    }
//
//    val centralMonitoringObserver = Observer<Int> { state ->
//        if (state == MonitorNotifier.OUTSIDE) {
//            Log.d(TAG, "outside beacon region: "+region)
//        }
//        else {
//            Log.d(TAG, "inside beacon region: "+region)
////            sendNotification()
//        }
//    }
//
//    val centralRangingObserver = Observer<Collection<Beacon>> { beacons ->
//        Log.d(MainActivity.TAG, "Ranged: ${beacons.count()} beacons")
//        for (beacon: Beacon in beacons) {
//            Log.d(TAG, "$beacon about ${beacon.distance} meters away")
//            var currentTime = LocalDateTime.now();
//            if((currentTime.toEpochSecond(ZoneOffset.UTC)- lastDetection.toEpochSecond(ZoneOffset.UTC))>6) {
//                sendNotification((beacon.distance * 100).toInt())
//                lastDetection = currentTime
//            }
//        }
//
//    }
//
//    private fun sendNotification(distance: Int) {
//        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
//        val current = LocalDateTime.now().format(formatter)
//        val builder = NotificationCompat.Builder(this, "beacon-ref-notification-id")
//            .setContentTitle("Beacon detectado")
//            .setContentText("Distancia de $distance [cm] (Horas: $current)")
//            .setSmallIcon(R.drawable.ic_launcher_background)
//        val stackBuilder = TaskStackBuilder.create(this)
//        stackBuilder.addNextIntent(Intent(this, MainActivity::class.java))
//        val resultPendingIntent = stackBuilder.getPendingIntent(
//            0,
//            PendingIntent.FLAG_UPDATE_CURRENT + PendingIntent.FLAG_IMMUTABLE
//        )
//        builder.setContentIntent(resultPendingIntent)
//        val notificationManager =
//            this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        notificationManager.notify(1, builder.build())
//    }
//
//    companion object {
//        val TAG = "BeaconReference"
//    }
//
//}
//
//class MainActivity : AppCompatActivity() {
//    lateinit var beaconListView: ListView
//    lateinit var beaconCountTextView: TextView
//    lateinit var monitoringButton: Button
//    lateinit var rangingButton: Button
//    lateinit var beaconReferenceApplication: BeaconReferenceApplication
//    var alertDialog: AlertDialog? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//        beaconReferenceApplication = application as BeaconReferenceApplication
//
//        // Set up a Live Data observer for beacon data
//        val regionViewModel = BeaconManager.getInstanceForApplication(this).getRegionViewModel(beaconReferenceApplication.region)
//        // observer will be called each time the monitored regionState changes (inside vs. outside region)
//        regionViewModel.regionState.observe(this, monitoringObserver)
//        // observer will be called each time a new list of beacons is ranged (typically ~1 second in the foreground)
//        regionViewModel.rangedBeacons.observe(this, rangingObserver)
//        rangingButton = findViewById<Button>(R.id.rangingButton)
//        monitoringButton = findViewById<Button>(R.id.monitoringButton)
//        beaconListView = findViewById<ListView>(R.id.beaconList)
//        beaconCountTextView = findViewById<TextView>(R.id.beaconCount)
//        beaconCountTextView.text = "No beacons detectados"
//        beaconListView.adapter =
//            ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayOf("--"))
//    }
//
//    override fun onPause() {
//        Log.d(TAG, "onPause")
//        super.onPause()
//    }
//    override fun onResume() {
//        Log.d(TAG, "onResume")
//        super.onResume()
//        // You MUST make sure the following dynamic permissions are granted by the user to detect beacons
//        //
//        //    Manifest.permission.BLUETOOTH_SCAN
//        //    Manifest.permission.BLUETOOTH_CONNECT
//        //    Manifest.permission.ACCESS_FINE_LOCATION
//        //    Manifest.permission.ACCESS_BACKGROUND_LOCATION // only needed to detect in background
//        //
//        // The code needed to get these permissions has become increasingly complex, so it is in
//        // its own file so as not to clutter this file focussed on how to use the library.
//
//        if (!BeaconScanPermissionsActivity.allPermissionsGranted(
//                this,
//                true
//            )
//        ) {
//            val intent = Intent(this, BeaconScanPermissionsActivity::class.java)
//            intent.putExtra("backgroundAccessRequested", true)
//            startActivity(intent)
//        }
//    }
//
//    val monitoringObserver = Observer<Int> { state ->
//        var dialogTitle = "Beacons detectados"
//        var dialogMessage = "Un beacon a entrado en la región"
//        var stateString = "inside"
//        if (state == MonitorNotifier.OUTSIDE) {
//            dialogTitle = "No beacons detectados"
//            dialogMessage = "didExitRegionEvent has fired"
//            stateString == "outside"
//            beaconCountTextView.text = "Fuera de la zona de detección, no beacons detectados"
//            beaconListView.adapter =
//                ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayOf("--"))
//        }
//        else {
//            beaconCountTextView.text = "Dentro de la zona de detección"
//        }
//        Log.d(TAG, "monitoring state changed to : $stateString")
//        val builder =
//            AlertDialog.Builder(this)
//        builder.setTitle(dialogTitle)
//        builder.setMessage(dialogMessage)
//        builder.setPositiveButton(android.R.string.ok, null)
//        alertDialog?.dismiss()
//        alertDialog = builder.create()
//        alertDialog?.show()
//    }
//
//    val rangingObserver = Observer<Collection<Beacon>> { beacons ->
//        Log.d(TAG, "Ranged: ${beacons.count()} beacons")
//        if (BeaconManager.getInstanceForApplication(this).rangedRegions.size > 0) {
//            beaconCountTextView.text = "Beacons detectados: ${beacons.count()}"
//            beaconListView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1,
//                beacons
//                    .sortedBy { it.distance }
//                    .map { "${it.id1}\nid2: ${it.id2} id3:  rssi: ${it.rssi}\nest. distance: ${it.distance} m" }
//                    .toTypedArray()
//            )
//        }
//    }
//
//    fun rangingButtonTapped(view: View) {
//        val beaconManager = BeaconManager.getInstanceForApplication(this)
//        if (beaconManager.rangedRegions.size == 0) {
//            beaconManager.startRangingBeacons(beaconReferenceApplication.region)
//            rangingButton.text = "Detener escaneo"
//            beaconCountTextView.text = "Detección habilitada, esperando primera detección"
//        }
//        else {
//            beaconManager.stopRangingBeacons(beaconReferenceApplication.region)
//            rangingButton.text = "Reanudar escaneo"
//            beaconCountTextView.text = "Detección deshabilitada, no beacons detectados"
//            beaconListView.adapter =
//                ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayOf("--"))
//        }
//    }
//
//    fun monitoringButtonTapped(view: View) {
//        var dialogTitle = ""
//        var dialogMessage = ""
//        val beaconManager = BeaconManager.getInstanceForApplication(this)
//        if (beaconManager.monitoredRegions.size == 0) {
//            beaconManager.startMonitoring(beaconReferenceApplication.region)
//            dialogTitle = "Monitoreo de beacons iniciado."
//            dialogMessage = "Verás una ventana cuando un beacon es o deja de ser detectado"
//            monitoringButton.text = "Desactivar ventanas"
//
//        }
//        else {
//            beaconManager.stopMonitoring(beaconReferenceApplication.region)
//            dialogTitle = "Beacon monitoring stopped."
//            dialogMessage = "Las ventanas de dialogo están deshabilitadas"
//            monitoringButton.text = "Activar ventanas"
//        }
//        val builder =
//            AlertDialog.Builder(this)
//        builder.setTitle(dialogTitle)
//        builder.setMessage(dialogMessage)
//        builder.setPositiveButton(android.R.string.ok, null)
//        alertDialog?.dismiss()
//        alertDialog = builder.create()
//        alertDialog?.show()
//
//    }
//
//    companion object {
//        val TAG = "MainActivity"
//        val PERMISSION_REQUEST_BACKGROUND_LOCATION = 0
//        val PERMISSION_REQUEST_BLUETOOTH_SCAN = 1
//        val PERMISSION_REQUEST_BLUETOOTH_CONNECT = 2
//        val PERMISSION_REQUEST_FINE_LOCATION = 3
//    }
//}

class MainActivity : AppCompatActivity() {
    // val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        Log.d("DEBUG","Main activity created")
        setContentView(R.layout.activity_main)
    }
}