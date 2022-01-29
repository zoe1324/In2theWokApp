package zoe.project.in2thewok

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    //Declaring an instance of FirebaseAuth
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()

        //Initialise the FirebaseAuth instance
        auth = Firebase.auth

        val loginBtn = findViewById<Button>(R.id.btnLogin)

        loginBtn.setOnClickListener {
                loginUser()
        }
    }

    public override fun onStart(){
        super.onStart()

        //Check if user is signed in (non-null) and update UI accordingly
        val currentUser = auth.currentUser

        if(currentUser != null) {
            switchToHome()
        }
    }

    private fun loginUser(){
        val email = findViewById<EditText>(R.id.teLoginEmail).text.toString()
        val password = findViewById<EditText>(R.id.teLoginPassword).text.toString()
        if (email.isNotEmpty() && password.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch { //learn what this means??
                try { //to register the user
                    auth.signInWithEmailAndPassword(email, password).await()
                    switchToHome()
                } catch (e: Exception) { //if something goes wrong in login
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        } else {
            Toast.makeText(
                this@LoginActivity,
                "Please enter email and password",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun switchToHome() {
        Intent(this, HomeActivity::class.java).also {
                    startActivity(it)
        }
    }
}