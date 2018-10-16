package com.example.robi.cmhapp

import java.util.*

class MeetingEvent {
    private var Name:String =  ""
    private var Date:Date = Date()
    private var Location:String = ""
    private var Description:String = ""

    constructor()
    {}
    constructor(n:String, d:Date, l:String, des:String)
    {
        this.Name = n
        this.Date = d
        this.Location = l
        this.Description = des
    }

//    private fun writeNewMeetEvent()
//    {
//        MeetingEvent m = MeetingEvent(plaintext.text, date.date,);
//    }








}