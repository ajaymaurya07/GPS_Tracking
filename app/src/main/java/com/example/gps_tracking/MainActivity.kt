package com.example.gps_tracking

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.example.gps_tracking.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener

class MainActivity : AppCompatActivity() {

    lateinit var binding:ActivityMainBinding
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this,R.layout.activity_main)

        locationRequest=LocationRequest()
        locationRequest.setInterval(30000)
        locationRequest.setFastestInterval(5000)


        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationresult: LocationResult) {
                var location=locationresult.lastLocation
                if(location!=null){
                    uiupdate(location)
                }

            }
        }

        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)

        gps_click()
        gps_update()
        locationUpdate()

    }

    fun gps_click(){
        binding.idGps.setOnCheckedChangeListener { buttonView, isChecked ->
            // We are checking if the switch is checked or not.
            if (isChecked) {
                // If the switch is checked, set the text accordingly.
                binding.idSensor.text = " Using GPS"
            } else {
                // If the switch is unchecked, set the text accordingly.
                binding.idSensor.text = "Using cell tower + wifi"
            }
        }



    }

    fun gps_update(){

        fusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(this);
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.lastLocation.addOnSuccessListener(OnSuccessListener {

                if(it!=null){
                    uiupdate(it)
                }

            })
        }
        else{
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),10
            )
        }

    }


    fun locationUpdate(){
        binding.idLocationUpdate.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){

                startlocationupdate()
            }
            else{
                binding.idOnOff.text="off"
                stoplocationupdate()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun startlocationupdate() {

        binding.idOnOff.text="location being tracked"
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback,null)
        gps_update()


    }

    private fun stoplocationupdate() {
        binding.idOnOff.text="location being not tracked"

        binding.idLat.text="location being not tracked"
        binding.idLong.text="location being not tracked"
        binding.idAltitude.text="location being not tracked"
        binding.idAccuracy.text="location being not tracked"
        binding.idSpeed.text="location being not tracked"
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    private fun uiupdate(location:Location) {
        binding.idLat.text=location.latitude.toString()
        binding.idLong.text=location.longitude.toString()
        binding.idAltitude.text=location.altitude.toString()
        binding.idAccuracy.text=location.accuracy.toString()
        binding.idSpeed.text=location.speed.toString()

        var geocoder=Geocoder(this)
        var address=  geocoder.getFromLocation(location.latitude,location.longitude,1)
        try {
            if (address != null) {
                binding.idAddress.text= address.get(0)?.getAddressLine(0).toString()
            }
        }
        catch (e:Exception){
            binding.idAddress.text= "null"
        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode==10 && grantResults.size>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            gps_update()
        }
    }

}