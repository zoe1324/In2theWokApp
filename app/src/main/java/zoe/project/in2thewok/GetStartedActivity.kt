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

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_get_started)
            supportActionBar?.hide()
            auth = Firebase.auth
            val getStartedBtn = findViewById<Button>(R.id.btnGetStarted)

            getStartedBtn.setOnClickListener {
                registerUsername()
            }
        }

    public override fun onStart(){
        super.onStart()
    }

    private fun registerUsername(){

        val username = findViewById<EditText>(R.id.teUsername).text.toString().trim()

        if(username.isNotEmpty()){
            CoroutineScope(Dispatchers.IO).launch {
                val user = auth.currentUser
                val usernameSet = UserProfileChangeRequest.Builder()
                        .setDisplayName(username)
                        .build()
                try {
                    user?.updateProfile(usernameSet)?.addOnSuccessListener {
                        Toast.makeText(this@GetStartedActivity,
                            "Successfully registered username",
                            Toast.LENGTH_LONG
                        ).show()
                        startQuestionnaire()
                    }?.addOnFailureListener {
                        Toast.makeText(this@GetStartedActivity,
                            "Failed to register username, please try again",
                            Toast.LENGTH_LONG
                        ).show()
                    }?.await()

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@GetStartedActivity,
                            e.message,
                            Toast.LENGTH_LONG
                        ).show()
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
            finish()
        }
    }

    override fun onBackPressed() {}
}