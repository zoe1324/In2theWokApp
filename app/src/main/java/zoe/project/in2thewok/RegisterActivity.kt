package zoe.project.in2thewok

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * [RegisterActivity], a registration page for the user, presents the application name,
 * an email input field, password input field, to navigate to the get started page,
 * changes to [GetStartedActivity] if user email and password are accepted by
 * FirebaseAuth validation.
 *
 * @constructor Creates a new AppCompatActivity
 * @suppress TooGenericExceptionCaught
 */
@Suppress("TooGenericExceptionCaught")

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    /**
     * Creates the [RegisterActivity], has button listener for
     * register button. Switches to [GetStartedActivity] upon register button click, if user inputs are
     * accepted via FirebaseAuth validation.
     *
     * @param savedInstanceState contains any data passed to the activity via Bundle
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportActionBar?.hide()
        auth = Firebase.auth
        val registerBtn = findViewById<Button>(R.id.btnRegister)

        registerBtn.setOnClickListener {
            registerUser()
        }
    }

    public override fun onStart(){
        super.onStart()
    }

    /**
     * Attempts to register a new user based on their inputs found in
     * teEmail and tePassword. Uses CoRoutines to asynchronously register the user.
     * Triggers switchToGetStarted() method upon success of createUserWithEmailAndPassword.
     *
     * @throws Exception if user input isn't accepted or db connection fails.
     */
    private fun registerUser() {
        val email = findViewById<EditText>(R.id.teEmail).text.toString().trim()
        val password = findViewById<EditText>(R.id.tePassword).text.toString().trim()
        if (email.isNotEmpty() && password.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
                        Toast.makeText(this@RegisterActivity,
                            "Account successfully registered",
                            Toast.LENGTH_LONG
                        ).show()
                        switchToGetStarted()
                    }.addOnFailureListener{
                        Toast.makeText(this@RegisterActivity,
                            "Failed to register, please try again",
                            Toast.LENGTH_LONG
                        ).show()
                    }.await()
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@RegisterActivity,
                            e.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        } else {
            Toast.makeText(
                this@RegisterActivity,
                "Please enter email and password",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    /**
     * Uses an Intent to launch the [GetStartedActivity] class.
     */
    private fun switchToGetStarted() {
        Intent(this, GetStartedActivity::class.java).also {
            startActivity(it)
        }
    }
}
