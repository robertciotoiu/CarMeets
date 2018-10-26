package com.example.robi.cmhapp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_form.*
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener



class MeetingsMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meetings_map)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // Add a marker in Sydney and move the camera
        //val sydney = LatLng(-34.0, 151.0)
        //mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        var mymarker:LatLng
        var stringCoordinates:String
        var ref2: DatabaseReference = FirebaseDatabase.getInstance().getReference("meetings")
        ref2.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists())
                {
                    for (c in p0.children)
                    {
                        var mting : MeetingEvent? = c.getValue(MeetingEvent::class.java)
                        stringCoordinates=""+mting?.Coordinates
                        var index = stringCoordinates.indexOf(",");  // Gets the first index where a space occours
                        var latitude = stringCoordinates.substring(0, index).toDouble(); // Gets the first part]
                        var longitude = stringCoordinates.substring(index + 1).toDouble()
                        mymarker = LatLng(latitude, longitude)
                        mMap.addMarker(MarkerOptions().position(mymarker).title(mting?.Name))
                        Log.d("myTag", "Am AJUNS AICI");
                    }
                }
            }
        })
        mMap.setOnMyLocationChangeListener { arg0 -> mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(arg0.latitude, arg0.longitude)))
        }

        mMap.setOnInfoWindowClickListener {
            val appInfo = Intent(this@MeetingsMapActivity, listViewItemExpand::class.java)
            var ref2:DatabaseReference = FirebaseDatabase.getInstance().getReference("meetings")
            ref2.addValueEventListener(object :ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                }
                override fun onDataChange(p0: DataSnapshot) {
                    if(p0.exists())
                    {
                        // loop through dataSnapshot
                        for (c in p0.children)
                        {
                            var mting : MeetingEvent? = c.getValue(MeetingEvent::class.java)
                            if (mting?.Name == it.title)
                            {
                                appInfo.putExtra("name",mting?.Name)
                                appInfo.putExtra("description",mting?.Description)
                                appInfo.putExtra("date",mting?.Date)
                                appInfo.putExtra("time",mting?.Time)
                                appInfo.putExtra("location",mting?.Location)
                                appInfo.putExtra("image",R.drawable.spbzoctq)
                                Log.d("intheif", ""+mting?.Name);
                                startActivity(appInfo)
                                break
                            }
                        }
                    }
                }
            })
        } //if click on the event goes to the event's page


    }
}
