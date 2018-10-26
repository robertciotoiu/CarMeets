package com.example.robi.cmhapp

import android.content.Intent
import android.graphics.Point
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.view.View
import android.view.WindowManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DatabaseError
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.Log
import android.widget.*
import io.opencensus.tags.Tag
import kotlinx.android.synthetic.main.activity_display_events.*
import kotlinx.android.synthetic.main.listview_meeting.view.*
import org.w3c.dom.Text


class DisplayEventsActivity : AppCompatActivity(),IScreenFormat {


    lateinit var ref:DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_events)
        hideStatusBar()
        getWindow().setNavigationBarColor(getResources().getColor(android.R.color.transparent))
        setFullScreenFlags()
        hasSoftNavigationBar()


        var listView:ListView = findViewById(R.id.listView)
        var meetingList:MutableList<MeetingEvent> = mutableListOf()

        ref = FirebaseDatabase.getInstance().getReference("meetings")
        ref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists())
                {
                    listView.adapter = null
                    for(e in p0.children)
                    {
                        val meeting = e.getValue(MeetingEvent::class.java)
                        meetingList.add(meeting!!)
                    }
                    val adapter = MeetingAdapter(this@DisplayEventsActivity,R.layout.listview_meeting,meetingList)
                    listView.adapter = adapter

                }
            }
        })

        listView.setOnItemClickListener { parent, view, position, id ->

            Toast.makeText(this, "Clicked item :"+" "+position,Toast.LENGTH_SHORT).show()
            val appInfo = Intent(this@DisplayEventsActivity, listViewItemExpand::class.java)


            var ref2:DatabaseReference = FirebaseDatabase.getInstance().getReference("meetings")
            ref2.addValueEventListener(object :ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    if(p0.exists())
                    {
                        var total = p0.getChildrenCount()
                        // let's say userB is actually 2nd in the list
                        var i = 0
                        // loop through dataSnapshot
                        for (c in p0.children)
                        {
                            Log.d("myTag", ""+i+" "+position);
                        if (i==position)
                        {
                                // the list is ascending, when finally reach userB, i = 3
                            var mting : MeetingEvent? = c.getValue(MeetingEvent::class.java)

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
                        else
                        {
                            i++
                        }
                    }
                    }
                }
            })
        }

        constraintLayout3.background = BitmapDrawable(blurBitmap(BitmapFactory.decodeResource(resources,R.drawable.testbackground2)))




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
        setContentView(R.layout.activity_display_events)
        // Hide the status bar.
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        actionBar?.hide()
    }
}
