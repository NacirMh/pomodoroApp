package com.example.pomodoro

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.PersistableBundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AlertDialog
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {
    val TAG = "ActivityMain"

    private lateinit var taketv : TextView
    private lateinit var timetv : TextView
    private lateinit var startbtn : Button
    private lateinit var resettv : TextView
    private lateinit var progress : ProgressBar
    private lateinit var toolbar: Toolbar
    private lateinit var okbtn : Button
    private lateinit var cancelbtn :Button
    private lateinit var newtimetvv : TextInputEditText
    private lateinit var dialog : Dialog

    var start:Long =1000.times(60)*25
    var remainingtime:Long = start
    var mycountdown:CountDownTimer? = null
    var countdownstarted= false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        intializevars()

        toolbar.inflateMenu(R.menu.menu_p)
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.contactitem -> {
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.data= Uri.parse("mailto:")
                    intent.type="plain/text"
                    intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("nacir16mahfoudhi@gmail.com"))
                    if(intent.resolveActivity(packageManager) != null) {
                        startActivity(intent)
                    }
                    true
                }
                R.id.settingitem ->{
                    showsettingdialog()
                    okbtn.setOnClickListener {
                        if (newtimetvv.text.toString() != ""){
                            start = newtimetvv.text.toString().toLong().times(1000).times(60)
                            Toast.makeText(this, "time changed", Toast.LENGTH_SHORT).show()
                            resettimer()
                        }
                        dialog.dismiss()
                    }
                    cancelbtn.setOnClickListener{
                        dialog.dismiss()
                    }
                    true
                }
                else -> false
            }
        }

        startbtn.setOnClickListener {
            if (!countdownstarted){
                startcount(remainingtime)
                taketv.text = resources.getText(R.string.keep_pomodoro)
                startbtn.text="Pause"
            }else{
                mycountdown!!.cancel()
                countdownstarted=false
                startbtn.text="start"
            }
        }
        resettv.setOnClickListener {
            resettimer()

        }

    }

    private fun showsettingdialog() {
        dialog=Dialog(this)
        dialog.setContentView(R.layout.edit_time)
        dialog.show()
        okbtn = dialog.findViewById(R.id.okbtn)
        cancelbtn = dialog.findViewById(R.id.cancelbtn)
        newtimetvv = dialog.findViewById(R.id.newtime)
    }


    private fun intializevars(){
        taketv = findViewById(R.id.taketxt)
        timetv = findViewById(R.id.timetxt)
        startbtn = findViewById(R.id.startbtn)
        resettv = findViewById(R.id.resettxt)
        progress = findViewById(R.id.progressBar)
        toolbar = findViewById(R.id.toolbar)
    }


    private fun startcount(time:Long){
        mycountdown = object : CountDownTimer(time,1000){

            override fun onTick(timeleft: Long) {
                remainingtime=time
                remainingtime=timeleft
                settext()
                progress.progress=((remainingtime*100).div(start)).toInt()
            }

            override fun onFinish() {
                resettimer()
            }

        }.start()
        countdownstarted=true
    }

    private fun resettimer(){
        mycountdown?.cancel()
        remainingtime=start
        settext()
        taketv.text=resources.getText(R.string.take_pomodoro)
        progress.progress=100
        startbtn.text="Start"
        countdownstarted=false
    }

    private fun settext() {
        val minute = remainingtime.div(1000).div(60)
        val second = remainingtime.div(1000) % 60
        timetv.text = String.format("%02d:%02d", minute, second)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong("remainingtime",remainingtime)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle){
        super.onRestoreInstanceState(savedInstanceState)
        val savedtime = savedInstanceState.getLong("remainingtime")
        startcount(savedtime)
    }
}