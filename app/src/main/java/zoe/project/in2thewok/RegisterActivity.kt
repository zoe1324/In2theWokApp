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

class RegisterActivity : AppCompatActivity() {
    //Declaring an instance of FirebaseAuth
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportActionBar?.hide()

        //Initialise the FirebaseAuth instance
        auth = Firebase.auth

        val registerBtn = findViewById<Button>(R.id.btnRegister)

        registerBtn.setOnClickListener {
            registerUser()
        }
    }

    public override fun onStart(){
        super.onStart()

        //Check if user is signed in (non-null) and update UI accordingly
        val currentUser = auth.currentUser

        if(currentUser != null) {
            Intent(this, HomeActivity::class.java).also {
                startActivity(it)
            }
        }

    }

    private fun registerUser() {
        val email = findViewById<EditText>(R.id.teEmail).text.toString()
        val password = findViewById<EditText>(R.id.tePassword).text.toString()
        if(email.isNotEmpty() && password.isNotEmpty()){
            if (email.isNotEmpty() && password.isNotEmpty()) {
                CoroutineScope(Dispatchers.IO).launch { //learn what this means??
                    try { //to register the user
                        auth.createUserWithEmailAndPassword(email, password).await()
                        switchToGetStarted()
                    } catch (e: Exception) { //if something goes wrong in registration
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@RegisterActivity, e.message, Toast.LENGTH_LONG).show()
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
    }
    private fun switchToGetStarted() {
        Intent(this, GetStartedActivity::class.java).also {
            startActivity(it)
        }
    }

}