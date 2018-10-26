package com.example.robi.cmhapp

import android.content.Context
import android.support.design.widget.CoordinatorLayout.Behavior.setTag
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.support.v4.content.ContextCompat.getSystemService
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.R.attr.name
import android.annotation.SuppressLint
import android.support.annotation.LayoutRes
import android.widget.*
import com.example.robi.cmhapp.R.drawable.meetingicon
import kotlinx.android.synthetic.main.activity_display_events.view.*
import kotlinx.android.synthetic.main.listview_meeting.view.*
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.location.Address
import android.location.Geocoder
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import android.os.Build
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlacePicker
import java.io.File
import java.lang.Exception
import java.util.*


class MeetingAdapter(val mCtx : Context , val layoutId:Int , val meetingList:List<MeetingEvent>):ArrayAdapter<MeetingEvent>(mCtx,layoutId,meetingList) {

    fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
        return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
    }




    @SuppressLint("ViewHolder")
    override fun getView(p0: Int, convertview: View?, p2: ViewGroup?): View {
        val layoutInflater : LayoutInflater = LayoutInflater.from(mCtx)
        val view:View = layoutInflater.inflate(
            layoutId,
            null
        )

        var imageView:ImageView = view.findViewById(R.id.ImgMeeting)
        var textView_name:TextView  =  view.findViewById(R.id.NameMeeting)
        var textView_time:TextView  =  view.findViewById(R.id.TimeMeeting)
        var textView_date:TextView  =  view.findViewById(R.id.DateMeeting)
        var textView_location:TextView  =  view.findViewById(R.id.LocationMeeting)

        val meeting = meetingList[p0]
        val imageByteArray = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Base64.getDecoder().decode(meeting.Image)
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        val theimg = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)

        imageView.setImageBitmap(theimg)

        textView_name.text = meeting.Name
        textView_date.text = meeting.Date
        textView_location.text = try{get_locality_and_country(meeting)}catch(e:Exception){"Not Available"}
        textView_time.text = meeting.Time

        return view
    }
    private fun get_locality_and_country(meeting:MeetingEvent):String{//to show only city and country in the listview :
        var someString = meeting.Coordinates
        var index = someString.indexOf(",");  // Gets the first index where a space occours
        var latitude = someString.substring(0, index); // Gets the first part]
        var longitude = someString.substring(index + 1);  // Gets the text part

        var geocoder:Geocoder = Geocoder(this.context)
        var addresses: List<Address>  = geocoder.getFromLocation(latitude.toDouble(),longitude.toDouble(), 1);

        var city_and_country:String = addresses[0].locality.toString()+","+addresses[0].countryName.toString()
        return city_and_country
    }

//    fun resizeBitmap(bitmap:Bitmap):Bitmap{
//        var currentBitmapWidth = bitmap.getWidth();
//        var currentBitmapHeight = bitmap.getHeight();
//
//        var ivWidth = 109;
//        var ivHeight = 113;
//        var newWidth = ivWidth;
//
//        var newHeight = Math.floor(currentBitmapHeight *( ivHeight / currentBitmapWidth).toDouble());
//
//        var nbitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight.toInt(), true);
//
//        return nbitmap
//    }

}