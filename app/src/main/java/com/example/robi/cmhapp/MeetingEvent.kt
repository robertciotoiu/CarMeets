package com.example.robi.cmhapp

import android.content.res.Resources
import android.widget.ImageView
import java.util.*
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import java.io.ByteArrayOutputStream


class MeetingEvent {
    var Name:String = ""
    var Date:String = ""
    var Time:String = ""
    var Coordinates:String = ""
    var Location:String = ""
    var Description:String = ""
    var Image:String=""

    constructor()
    {}
    constructor(n:String, d:String, t:String,c:String , l:String, des:String,img:String)
    {
        this.Name = n
        this.Date = d
        this.Time = t
        this.Coordinates = c
        this.Location = l
        this.Description = des
        this.Image = img

    }



//    private fun writeNewMeetEvent()
//    {
//        MeetingEvent m = MeetingEvent(plaintext.text, date.date,);
//    }








}