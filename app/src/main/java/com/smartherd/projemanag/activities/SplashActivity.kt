package com.smartherd.projemanag.activities

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.smartherd.projemanag.databinding.ActivitySplashBinding
import com.smartherd.projemanag.firebase.FireStoreClass

class SplashActivity : AppCompatActivity() {
    lateinit var binding : ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // TODO Setting up splash screen,using custom fonts  (Step 2: Add the full screen flags here.)
        // This is used to hide the status bar and make the splash screen as a full screen activity.
        // START
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        // END

        // TODO Setting up splash screen,using custom fonts  (Step 3: Create a folder where the font will be located.)
        // In the app folder create this folder by right clicking at the res folder
        // Select the new choice and within this choice choose the folder option and within the folder option choose the asset folder option
        // Drag and drop the font file with .ttf extension to the created asset folder
        // Right click on the "app" package and GO TO ==> New ==> Folder ==> Assets Folder ==> Finish.

        // TODO Setting up splash screen,using custom fonts (Step 4: Use the below line of code to apply it to the title TextView.)
        val typeface: Typeface =
            Typeface.createFromAsset(assets, "backoutwebwebfont.ttf")
        binding.tvAppName.typeface = typeface

        // TODO Setting the intro screen and moving to it after 2 seconds (Step 6: Here we will launch the Intro Screen after the splash screen using the handler. As using handler the splash screen will disappear after what we give to the handler.)
        // Adding the handler to after the a task after some delay.
        Handler().postDelayed({
            // Start the Intro Activity
            startActivity(Intent(this@SplashActivity, IntroActivity::class.java))
            finish() // Call this when your activity is done and should be closed and prevent the user from coming back to it when the back button is pressed
        }, 2000) // Here we pass the delay time in milliSeconds after which the splash activity will disappear.
    }

}


