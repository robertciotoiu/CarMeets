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
import android.app.DatePickerDialog
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
import android.text.format.Time
import android.util.Log
import android.widget.DatePicker
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.R.id.async
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_form.*
import kotlinx.android.synthetic.main.activity_form.view.*
import java.util.*


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
                locationPicker.setText("Location selected")// + place.address)
                coordinates.setText(place.getName())
                locationInfo.setText(place.getAddress())
//                if (place.attributions == null) {
//                    attributionText.loadData("no attribution", "text/html; charset=utf-8", "UFT-8")
//                } else {
//                    attributionText.loadData(place.attributions!!.toString(), "text/html; charset=utf-8", "UFT-8")
//                }
            }
        }
    }
    fun screenAdjuster(){
        getWindow().setNavigationBarColor(getResources().getColor(android.R.color.transparent))
        setFullScreenFlags()
        hasSoftNavigationBar()
    }

    private val mAuth = FirebaseAuth.getInstance()
    private var mDatabase : DatabaseReference = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)

        val currentUser = mAuth.currentUser




        //if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) // Unnecessary because I have API min level 23 in the gradle
        screenAdjuster()//set the navBar invisible, etc(design stuff)


        run{
            requestPermission()
            setContentView(R.layout.activity_form)
            val locationPickerButton = findViewById<Button>(R.id.locationPicker)
            locationPickerButton.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View) {
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

            coordinates.visibility = View.GONE
            locationInfo.visibility = View.GONE


            dateSelection.setOnClickListener {
                //hide all the the things from the activity
                meetingName.visibility = View.GONE
                dateSelection.visibility = View.GONE
                timeSelection.visibility = View.GONE
                locationPicker.visibility = View.GONE
                description.visibility = View.GONE
                submitButton.visibility = View.GONE
                datePicker.visibility = View.VISIBLE
                confirmDate.visibility = View.VISIBLE

                var c = Calendar.getInstance()
                var year = c.get(Calendar.YEAR)
                var month = c.get(Calendar.MONTH)
                var day = c.get(Calendar.DAY_OF_MONTH)

                var s: String = "" + day + "/" + month + "/" + year + ""
                dateSelection.text = s
                //make the date selector appears


            }
            confirmDate.setOnClickListener {

                var year = datePicker.year
                var month = datePicker.month
                var day = datePicker.dayOfMonth

                var s: String = "" + day + "/" + month + "/" + year + ""
                dateSelection.text = s

                meetingName.visibility = View.VISIBLE
                dateSelection.visibility = View.VISIBLE
                timeSelection.visibility = View.VISIBLE
                locationPicker.visibility = View.VISIBLE
                description.visibility = View.VISIBLE
                submitButton.visibility = View.VISIBLE
                datePicker.visibility = View.GONE
                confirmDate.visibility = View.GONE
            }

            timeSelection.setOnClickListener {
                meetingName.visibility = View.GONE
                dateSelection.visibility = View.GONE
                timeSelection.visibility = View.GONE
                locationPicker.visibility = View.GONE
                description.visibility = View.GONE
                submitButton.visibility = View.GONE
                timePicker.visibility = View.VISIBLE
                confirmTime.visibility = View.VISIBLE

                var hour = Time.HOUR
                var minute = Time.MINUTE

                var s: String = "" + hour + ":" + minute + ""
                timeSelection.text = s

            }

            confirmTime.setOnClickListener {
                var hour = timePicker.hour
                var minute = timePicker.minute

                var s: String = "" + hour + ":" + minute + ""
                timeSelection.text = s

                meetingName.visibility = View.VISIBLE
                dateSelection.visibility = View.VISIBLE
                timeSelection.visibility = View.VISIBLE
                locationPicker.visibility = View.VISIBLE
                description.visibility = View.VISIBLE
                submitButton.visibility = View.VISIBLE
                timePicker.visibility = View.GONE
                confirmTime.visibility = View.GONE

            }
            //submit meeting button

        }//location, time and date pickers

        submitButton.setOnClickListener {
            val meetingRef = mDatabase.child("meetings")
            //newMeetingRef.setValue(MeetingEvent(meetingName.text.toString(), dateSelection.text.toString(),timeSelection.text.toString(),coordinates.text.toString(),locationInfo.text.toString(),description.text.toString()),async)
            meetingRef.push().setValue(MeetingEvent(meetingName.text.toString(), dateSelection.text.toString(),timeSelection.text.toString(),coordinates.text.toString(),locationInfo.text.toString(),description.text.toString()),async)

// We can also chain the two calls together
            //mDatabase.child("meetings").child("name").setValue(meetingName.text.toString())
            //mDatabase.child("meetings").child("date").setValue(dateSelection.text.toString())
            //mDatabase.child("meetings").child("time").setValue(timeSelection.text.toString())
            //mDatabase.child("meetings").child("coordinates").setValue(coordinates.text.toString())
            //mDatabase.child("meetings").child("location").setValue(locationInfo.text.toString())
            //mDatabase.child("meetings").child("description").setValue(description.text.toString())
            //.add(meetingEvent)
            //.addOnSuccessListener(object : OnSuccessListener<DocumentReference> {
             //   override fun onSuccess(documentReference : DocumentReference ) {
             //   Log.d("Success", "DocumentSnapshot added with ID: " + documentReference.id)
              //  }
            //})
            //.addOnFailureListener(object : OnFailureListener {
              //  override fun onFailure(ex: Exception) {
               // Log.w("Failed", "Error adding document", ex)
                //}
            //})
            //have to make a class to handle the date&time
            //to hide open the date and to hide the rest and after select the date to
            //hide the date introduce info in a text label(handled by the class date handler and auto open the time select
            //same thing for the time as for the date
            //fun loadDatabase(firebaseData: DatabaseReference) {
//                val meetingEvent = MeetingEvent(meetingName.text.toString(), dateSelection.text.toString(),timeSelection.text.toString(),coordinates.text.toString(),locationInfo.text.toString(),description.text.toString())
//                val key = firebaseData.child("meetings").push().key
//                meetingEvent.uuid = key
//                firebaseData.child("meetings").child(key).setValue(it)
//            }
        }//the submit button

    }

}


//    private fun locationSelector()
//    {
//
//        val PLACE_PICKER_REQUEST = 1
//        val builder = PlacePicker.IntentBuilder()
//        startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST)
//    }


