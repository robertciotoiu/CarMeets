package com.example.robi.cmhapp

import android.graphics.Point
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.view.View
import android.view.WindowManager


import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.app.DatePickerDialog
import android.content.ContentResolver
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
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.media.Image
import android.net.Uri
import android.provider.MediaStore
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.text.format.Time
import android.util.Log
import android.widget.DatePicker
import android.widget.ImageView
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
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.URI
import java.nio.charset.Charset
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
        setContentView(R.layout.activity_form)
        // Hide the status bar.
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        actionBar?.hide()
    }

    private val MY_PERMISSION_FINE_LOCATION = 101
    private val PLACE_PICKER_REQUEST = 2

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

    val PICK_IMAGE = 1


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                val place = PlacePicker.getPlace(this@FormActivity, data!!)
                locationPicker.setText("Location selected")// + place.address)
                coordinates.setText(""+place.latLng.latitude+","+place.latLng.longitude)
                locationInfo.setText(place.address)
            }
        }
        if (PICK_IMAGE==requestCode && resultCode == Activity.RESULT_OK && data!=null && data.data!=null) {
            val uri : Uri? = data.data
            try {
                var imageView:ImageView =  findViewById(R.id.imagePicker)
                var bitmap: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                imageView.setImageBitmap(bitmap)
            }catch (e:IOException)
            {
                e.printStackTrace()
            }
        }
    }

    private fun getRealPathFromURI(contentUri: Uri): String {

        // can post image
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = managedQuery(contentUri,proj, // WHERE clause selection arguments (none)
            null, null, null
        )// Which columns to return
        // WHERE clause; which rows to return (all rows)
        // Order-by clause (ascending by name)
        val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()

        return cursor.getString(column_index)
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
                textView.visibility = View.GONE
                imagePicker.visibility = View.GONE
                imageDatePicker.visibility = View.GONE
                imageTimePicker.visibility = View.GONE
                imageLocationPicker.visibility = View.GONE
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
                textView.visibility = View.VISIBLE
                imagePicker.visibility = View.VISIBLE
                imageDatePicker.visibility = View.VISIBLE
                imageTimePicker.visibility = View.VISIBLE
                imageLocationPicker.visibility = View.VISIBLE
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
                textView.visibility = View.GONE
                imagePicker.visibility = View.GONE
                imageDatePicker.visibility = View.GONE
                imageTimePicker.visibility = View.GONE
                imageLocationPicker.visibility = View.GONE
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
                textView.visibility = View.VISIBLE
                imagePicker.visibility = View.VISIBLE
                imageDatePicker.visibility = View.VISIBLE
                imageTimePicker.visibility = View.VISIBLE
                imageLocationPicker.visibility = View.VISIBLE
                timePicker.visibility = View.GONE
                confirmTime.visibility = View.GONE

            }

            //submit meeting button
            submitButton.setOnClickListener {


                val meetingRef = mDatabase.child("meetings")
                if(meetingName.text!=null && dateSelection.text!=null && timeSelection.text!=null&&coordinates.text!=null&&locationInfo.text!=null&&description.text!=null&&imagePicker!=null)
                    meetingRef.push().setValue(MeetingEvent(meetingName.text.toString(), dateSelection.text.toString(),timeSelection.text.toString(),coordinates.text.toString(),locationInfo.text.toString(),description.text.toString(),compress_file(imagePicker)),async)
            }//the submit button
        }//location, time and date pickers and the submit button

        imagePicker.setOnClickListener{
            val getIntent = Intent(Intent.ACTION_GET_CONTENT)
            getIntent.type = "image/*"

            val pickIntent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickIntent.type = "image/*"

            val chooserIntent = Intent.createChooser(getIntent, "Select Image")
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))

            startActivityForResult(chooserIntent, PICK_IMAGE)


        }

        //background +blur effect
        constraintLayout2.background = BitmapDrawable(blurBitmap(BitmapFactory.decodeResource(resources,R.drawable.testbackground2)))

    }
    fun blurBitmap(bitmap: Bitmap): Bitmap {

        //Let's create an empty bitmap with the same size of the bitmap we want to blur
        val outBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)

        //Instantiate a new Renderscript
        val rs = RenderScript.create(applicationContext)

        //Create an Intrinsic Blur Script using the Renderscript
        val blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))

        //Create the in/out Allocations with the Renderscript and the in/out bitmaps
        val allIn = Allocation.createFromBitmap(rs, bitmap)
        val allOut = Allocation.createFromBitmap(rs, outBitmap)

        //Set the radius of the blur
        blurScript.setRadius(25f)

        //Perform the Renderscript
        blurScript.setInput(allIn)
        blurScript.forEach(allOut)

        //Copy the final bitmap created by the out Allocation to the outBitmap
        allOut.copyTo(outBitmap)

        //recycle the original bitmap
        bitmap.recycle()

        //After finishing everything, we destroy the Renderscript.
        rs.destroy()

        return outBitmap
    }
    private fun compress_file(imagePicker: ImageView):String
    {
        imagePicker.isDrawingCacheEnabled = true
        imagePicker.buildDrawingCache()
        val bitmap = (imagePicker.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val bytes = data
        val base64 = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Base64.getEncoder().encodeToString(bytes)
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        return base64
    }


}


