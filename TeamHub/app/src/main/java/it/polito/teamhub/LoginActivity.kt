package it.polito.teamhub

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import it.polito.teamhub.ui.theme.CustomFontFamily
import it.polito.teamhub.ui.theme.TeamHubTheme
import it.polito.teamhub.ui.theme.linearGradient
import it.polito.teamhub.ui.view.component.Walkthrough
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : ComponentActivity() {
    private lateinit var credentialManager: CredentialManager
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        credentialManager = CredentialManager.create(this)

        setContent {
            val navController = rememberNavController()
            TeamHubTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(navController, startDestination = "home") {
                        composable("home") {
                            Box(modifier = Modifier.fillMaxSize()) {
                                // Background image
                                Image(
                                    painter = painterResource(id = R.drawable.login_background_image),
                                    contentDescription = "Background Image",
                                    contentScale = ContentScale.Crop, // crop the image to fill the screen
                                    modifier = Modifier.fillMaxSize()
                                )

                                // App name and proposition value
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 100.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Top
                                ) {
                                    Text(
                                        text = "TeamHUB",
                                        style = TextStyle(
                                            brush = linearGradient,
                                            fontFamily = CustomFontFamily,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 36.sp,
                                            lineHeight = 36.sp,
                                            letterSpacing = 0.sp
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 16.dp),
                                        textAlign = TextAlign.Center

                                    )
                                    Text(
                                        text = "Cultivate Collaboration, Cultivate Success",
                                        style = TextStyle(
                                            color = Color.Black,
                                            fontFamily = CustomFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 28.sp,
                                            lineHeight = 32.sp,
                                            letterSpacing = 0.sp,
                                        ),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(
                                            bottom = 32.dp,
                                            start = 32.dp,
                                            end = 32.dp
                                        )
                                    )
                                }

                                // Sign-in button
                                Button(
                                    onClick = { navController.navigate("walkthrough") },
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .padding(horizontal = 20.dp, vertical = 30.dp)
                                        .fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.Black,
                                        contentColor = Color.White
                                    ),
                                    contentPadding = PaddingValues(16.dp)
                                ) {

                                    Text(
                                        text = "Get Started",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                        composable("walkthrough") {
                            Walkthrough(signIn = { signInWithGoogle() })
                        }
                    }
                }
            }
        }
    }

    private fun signInWithGoogle() {
        val signInWithGoogleOption: GetSignInWithGoogleOption = GetSignInWithGoogleOption
            .Builder("933198624284-aipvk8i781vph1at93tbj1jb4s0kdeuo.apps.googleusercontent.com")
            .setNonce("<nonce string to use when generating a Google ID token>")
            .build()

        val request: GetCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(signInWithGoogleOption)
            .build()

        coroutineScope.launch {
            try {
                val result = credentialManager.getCredential(
                    request = request,
                    context = this@LoginActivity,
                )
                handleSignIn(result)
            } catch (e: GetCredentialException) {
                handleFailure(e)
            }
        }
    }

    private fun handleSignIn(result: GetCredentialResponse) {
        // Handle the successfully returned credential.
        when (val credential = result.credential) {
            // GoogleIdToken credential
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        // Use googleIdTokenCredential and extract id to validate and
                        // authenticate on your server.
                        val googleIdTokenCredential = GoogleIdTokenCredential
                            .createFrom(credential.data)

                        // Authenticate with Firebase
                        firebaseAuthWithGoogle(googleIdTokenCredential.idToken)

                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e(TAG, "Received an invalid google id token response", e)
                    }
                } else {
                    // Catch any unrecognized custom credential type here.
                    Log.e(TAG, "Unexpected type of credential")
                }
            }

            else -> {
                // Catch any unrecognized credential type here.
                Log.e(TAG, "Unexpected type of credential")
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(this, "Authentication succeeded!", Toast.LENGTH_SHORT).show()
                    val user = FirebaseAuth.getInstance().currentUser
                    if (user != null) {
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.data = this@LoginActivity.intent.data
                        startActivity(intent)
                        finish()
                    }

                } else {
                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                }
            }
    }

    private fun handleFailure(e: GetCredentialException) {
        // Handle the failure here
        Log.e(TAG, "Error getting credential", e)
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}