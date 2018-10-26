package com.example.robi.cmhapp

import android.content.Context
import android.graphics.Point
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_list_view_item_expand.*
import android.content.Intent
import android.net.Uri
import android.renderscript.Allocation
import android.renderscript.Element.U8_4
import android.renderscript.ScriptIntrinsicBlur
import android.renderscript.RenderScript
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.renderscript.Element
import android.support.constraint.ConstraintLayout
import android.widget.LinearLayout
import com.example.robi.cmhapp.R.layout.activity_list_view_item_expand
import android.graphics.drawable.BitmapDrawable
import android.support.v4.content.res.ResourcesCompat


class listViewItemExpand : AppCompatActivity(),IScreenFormat {

//    lateinit var context:Context
//
//    fun listViewItemExpand(context: Context) {
//        this.context = context
//    }

    lateinit var t1:TextView
    lateinit var t2:TextView
    lateinit var t3:TextView
    lateinit var t4:TextView
    lateinit var t5:TextView
    lateinit var image1:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_view_item_expand)
        setContentView(R.layout.activity_display_events)
        hideStatusBar()
        getWindow().setNavigationBarColor(getResources().getColor(android.R.color.transparent))
        setFullScreenFlags()
        hasSoftNavigationBar()


        t1 = findViewById(R.id.itemName)
        t2 = findViewById(R.id.itemDescription)
        t3 = findViewById(R.id.itemDate)
        t4 = findViewById(R.id.itemTime)
        t5 = findViewById(R.id.itemLocation)
        image1 = findViewById(R.id.itemImage)

        t1.setText(intent.getStringExtra("name"))
        t2.setText(intent.getStringExtra("description"))
        t3.setText(intent.getStringExtra("date"))
        t4.setText(intent.getStringExtra("time"))
        t5.setText(intent.getStringExtra("location"))
        image1.setImageBitmap(intent.getParcelableExtra("image"))



        t5.setOnClickListener {
            val map = "http://maps.google.co.in/maps?q=${itemLocation.text}"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(map))
            startActivity(intent)
        }
        image1.setOnClickListener {
            val map = "http://maps.google.co.in/maps?q=${itemLocation.text}"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(map))
            startActivity(intent)
        }

        changeFonts()//maybe will crash here(it may need to move it down) /now it should be ok
        constraintLayout4.background = BitmapDrawable(blurBitmap(BitmapFactory.decodeResource(resources,R.drawable.testbackground2)))

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
        setContentView(R.layout.activity_list_view_item_expand)
        // Hide the status bar.
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        actionBar?.hide()
    }

    private fun changeFonts() {
        var typeFace: Typeface? = ResourcesCompat.getFont(this.applicationContext, R.font.rubik_medium)
        this.t1.setTypeface(typeFace, Typeface.NORMAL)
        this.t2.setTypeface(typeFace, Typeface.NORMAL)
        this.t3.setTypeface(typeFace, Typeface.NORMAL)
        this.t4.setTypeface(typeFace, Typeface.NORMAL)
        this.t5.setTypeface(typeFace, Typeface.NORMAL)
        this.descriptionTextView.setTypeface(typeFace,Typeface.BOLD)
        this.descriptionTextView2.setTypeface(typeFace,Typeface.BOLD)
        this.descriptionTextView3.setTypeface(typeFace,Typeface.BOLD)
    }
}
