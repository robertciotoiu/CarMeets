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
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import android.os.Build
import java.io.File
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
//        var imgBA:ByteArray = meeting.Image.toByteArray()
//        val bitmap = BitmapFactory.decodeByteArray(imgBA, 0, imgBA.size)
        val charset = Charsets.UTF_8
        val byteArray = meeting.Image.toByteArray(charset)
        //var theimg = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)

        val imageByteArray = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Base64.getDecoder().decode(meeting.Image)
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        val theimg = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)

        imageView.setImageBitmap(theimg)
        //theimg.recycle();

        //imageView.setImageBitmap(meeting.Image)//object.image retrieved and converted to bitmap from firebase
        textView_name.text = meeting.Name
        textView_date.text = meeting.Date
        textView_location.text = meeting.Location
        textView_time.text = meeting.Time

        return view
    }
}