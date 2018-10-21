package com.example.robi.cmhapp

import java.util.*

class MeetingEvent {
    var Name:String = ""
    var Date:String = ""
    var Time:String = ""
    var Coordinates:String = ""
    var Location:String = ""
    var Description:String = ""

    constructor()
    {}
    constructor(n:String, d:String, t:String,c:String , l:String, des:String)
    {
        this.Name = n
        this.Date = d
        this.Time = t
        this.Coordinates = c
        this.Location = l
        this.Description = des
    }

//    private fun writeNewMeetEvent()
//    {
//        MeetingEvent m = MeetingEvent(plaintext.text, date.date,);
//    }








}