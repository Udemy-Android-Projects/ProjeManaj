package com.smartherd.projemanag.activities

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.smartherd.projemanag.databinding.ActivityIntroBinding

class IntroActivity : BaseActivity() {
    var binding : ActivityIntroBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        // TODO Setting the intro screen and moving to it after 2 seconds (Step 5: Add the flag settings.)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        val typeface: Typeface =
            Typeface.createFromAsset(assets, "backoutwebwebfont.ttf")
        binding?.tvAppNameIntro?.typeface = typeface

        // TODO Designing The SignUp Activity (Step 5: Add a click event for Sign Up btn and launch the Sign Up Screen.)
        // START
        binding?.btnSignUpIntro?.setOnClickListener {
            // Launch the sign up screen.
            startActivity(Intent(this@IntroActivity, SignUpActivity::class.java))
        }
        // END
        binding?.btnSignInIntro?.setOnClickListener {
            startActivity(Intent(this@IntroActivity, SignInActivity::class.java))
        }

    }
    override fun onBackPressed() {
        doubleBackToExit()
    }
}