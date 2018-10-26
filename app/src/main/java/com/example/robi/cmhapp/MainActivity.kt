package com.example.robi.cmhapp

import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.view.View
import android.view.WindowManager
import android.support.v4.content.res.ResourcesCompat
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.widget.ImageView


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideStatusBar()
        getWindow().setNavigationBarColor(getResources().getColor(android.R.color.transparent))
        setFullScreenFlags()
        hasSoftNavigationBar()
        changeFonts()

        constraintLayout1.background = BitmapDrawable(blurBitmap(BitmapFactory.decodeResource(resources,R.drawable.testbackground2)))
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
    private fun changeFonts() {
        var typeFace: Typeface? = ResourcesCompat.getFont(this.applicationContext, R.font.rubik_medium)
        this.button!!.setTypeface(typeFace, Typeface.NORMAL)
        this.button2!!.setTypeface(typeFace, Typeface.NORMAL)
        this.button3!!.setTypeface(typeFace, Typeface.NORMAL)
    }




    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
     fun setFullScreenFlags() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR)
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
        if (!hasSoftNavigationBar()) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
    }
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
     fun hasSoftNavigationBar(): Boolean {
        val display = windowManager.defaultDisplay
        val realSize = Point()
        val displaySize = Point()

        display.getRealSize(realSize)
        display.getSize(displaySize)
        return !realSize.equals(displaySize)
    }
    fun hideStatusBar()
    {
        setContentView(R.layout.activity_main)
        // Hide the status bar.
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        actionBar?.hide()
    }

    fun sendMessage(view: View) {
        val intent = Intent(this@MainActivity, FormActivity::class.java)
        startActivity(intent)
    }

    fun sendMessage2(view: View) {
        val intent = Intent(this@MainActivity, DisplayEventsActivity::class.java)
        startActivity(intent)
    }

    fun sendMessage3(view: View) {
        val intent = Intent(this@MainActivity, MeetingsMapActivity::class.java)
        startActivity(intent)
    }

}
