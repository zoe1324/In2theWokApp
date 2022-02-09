package zoe.project.in2thewok

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class GetStartedActivity : AppCompatActivity() {
    //Declaring an instance of FirebaseAuth
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_get_started)
            supportActionBar?.hide()

            //Initialise the FirebaseAuth instance
            auth = Firebase.auth

            val getStartedBtn = findViewById<Button>(R.id.btnGetStarted)

            getStartedBtn.setOnClickListener {
                registerUsername()
            }
        }

    public override fun onStart(){
        super.onStart()
        //Check if user is signed in (non-null) and update UI accordingly
//        val currentUser = auth.currentUser
//        if(currentUser != null) {
//            Intent(this, HomeActivity::class.java).also {
//                startActivity(it)
//            }
//        }
//
    }

    private fun registerUsername(){
        val username = findViewById<EditText>(R.id.teUsername).text.toString()
        if(username.isNotEmpty()){

            CoroutineScope(Dispatchers.IO).launch { //learn what this means??
                val user = auth.currentUser
                val usernameSet = UserProfileChangeRequest.Builder()
                        .setDisplayName(username)
                        .build()
                try {
                    if(user != null) {
                        user.updateProfile(usernameSet).await()
                        startQuestionnaire()
                    }

                } catch (e: Exception) { //if something goes wrong in registration
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@GetStartedActivity, e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        } else {
            Toast.makeText(
                this@GetStartedActivity,
                "Please enter email and password",
                Toast.LENGTH_LONG
            ).show()
        }

    }

    private fun startQuestionnaire(){
        Intent(this, QuestionnaireActivity::class.java).also {
            startActivity(it)
        }
    }

//    private fun regUsername(){
//        auth = FirebaseAuth.getInstance()
//        val user = auth.currentUser
//        val userSetUp = UserProfileChangeRequest.Builder()
//        userSetUp.displayName = username
//        userSetUp.build()
//    }
}