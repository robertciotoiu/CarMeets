package com.example.robi.cmhapp

import android.graphics.Point
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.view.View
import android.view.WindowManager
import android.graphics.Typeface
import android.support.v4.content.res.ResourcesCompat
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent




class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideStatusBar()
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(android.R.color.transparent))
            setFullScreenFlags()
            hasSoftNavigationBar()
            changeFonts()
        }

    }

    private fun changeFonts()
    {
        var typeFace: Typeface? = ResourcesCompat.getFont(this.applicationContext, R.font.freescpt)
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

}
