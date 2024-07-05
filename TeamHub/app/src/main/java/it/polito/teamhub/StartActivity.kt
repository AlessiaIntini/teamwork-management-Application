package it.polito.teamhub

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.auth.FirebaseAuth

class StartActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Install the splash screen
        installSplashScreen()

        super.onCreate(savedInstanceState)

        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            // User is signed in, launch MainActivity
            startActivity(Intent(this, MainActivity::class.java).apply {
                data = this@StartActivity.intent.data
            })
        } else {
            // User is not signed in, launch LoginActivity
            startActivity(Intent(this, LoginActivity::class.java).apply {
                data = this@StartActivity.intent.data
            })
        }
        finish() // Finish it.polito.teamhub.SplashActivity to remove it from the back stack
    }
}
