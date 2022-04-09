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

/**
 * [GetStartedActivity], a username entry page for the user, presents a welcome message,
 * a username input field, and a button to navigate to questionnaire page,
 * changes to [QuestionnaireActivity] if username successfully updates on the database.
 *
 * @constructor Creates a new AppCompatActivity
 * @suppress EmptyFunctionBlock
 * @suppress TooGenericExceptionCaught
 */
@Suppress("EmptyFunctionBlock", "TooGenericExceptionCaught")

class GetStartedActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    /**
     * Creates the [GetStartedActivity], has button listener for
     * 'get started' button. Switches to [QuestionnaireActivity] upon 'get started' button click, and if
     * username successfully updates on the db.
     *
     * @param savedInstanceState contains any data passed to the activity via Bundle
     */
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

    /**
     * Attempts to register a displayName to the FirebaseAuth instance created from
     * registration, based on input on teUsername.
     * Uses CoRoutines to asynchronously update user profile.
     * Triggers startQuestionnaire() method upon success of updateProfile request.
     *
     * @throws Exception if db connection fails.
     */
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

    /**
     * Uses an Intent to launch the [QuestionnaireActivity] class.
     */
    private fun startQuestionnaire(){
        Intent(this, QuestionnaireActivity::class.java).also {
            startActivity(it)
            finish()
        }
    }

    /**
     * Suppresses back button, to prevent errors in account registration
     */
    override fun onBackPressed() {}
}
