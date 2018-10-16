package com.example.robi.cmhapp

import android.graphics.Point
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.view.View
import android.view.WindowManager


import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.widget.Button
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException

import com.google.android.gms.location.places.ui.PlacePicker
import android.widget.Toast
import android.support.annotation.NonNull
import com.google.android.gms.location.places.ui.PlacePicker.getAttributions
import com.google.android.gms.location.places.Place
import android.content.Intent
import kotlinx.android.synthetic.main.activity_form.*


class FormActivity : AppCompatActivity(),IScreenFormat {

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun setFullScreenFlags() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR)
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
        if (!hasSoftNavigationBar()) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
    }
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun hasSoftNavigationBar(): Boolean {
        val display = windowManager.defaultDisplay
        val realSize = Point()
        val displaySize = Point()

        display.getRealSize(realSize)
        display.getSize(displaySize)
        return !realSize.equals(displaySize)
    }
    override fun hideStatusBar() {
        setContentView(R.layout.activity_main)
        // Hide the status bar.
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        actionBar?.hide()
    }

    private val MY_PERMISSION_FINE_LOCATION = 101
    private val PLACE_PICKER_REQUEST = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(android.R.color.transparent))
            setFullScreenFlags()
            hasSoftNavigationBar()
        }

        requestPermission()
        setContentView(R.layout.activity_form)
        val button = findViewById<Button>(R.id.button4)
        button.setOnClickListener(object:View.OnClickListener {
            override fun onClick(v:View) {
                val builder = PlacePicker.IntentBuilder()
                try {
                    val intent = builder.build(this@FormActivity)
                    startActivityForResult(intent, PLACE_PICKER_REQUEST)
                } catch (e: GooglePlayServicesRepairableException) {
                    e.printStackTrace()
                } catch (e: GooglePlayServicesNotAvailableException) {
                    e.printStackTrace()
                }

                // Code here executes on main thread after user presses button
            }
        })
    }

    private fun requestPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), MY_PERMISSION_FINE_LOCATION)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            MY_PERMISSION_FINE_LOCATION -> if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(
                    applicationContext,
                    "This app requires location permissions to be granted",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                val place = PlacePicker.getPlace(this@FormActivity, data!!)
                button4.setText(place.name)// + place.address)
//                if (place.attributions == null) {
//                    attributionText.loadData("no attribution", "text/html; charset=utf-8", "UFT-8")
//                } else {
//                    attributionText.loadData(place.attributions!!.toString(), "text/html; charset=utf-8", "UFT-8")
//                }
            }
        }
    }



}


//    private fun locationSelector()
//    {
//
//        val PLACE_PICKER_REQUEST = 1
//        val builder = PlacePicker.IntentBuilder()
//        startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST)
//    }


