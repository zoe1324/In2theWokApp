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

    private lateinit var auth: FirebaseAuth

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

    private fun switchToGetStarted() {
        Intent(this, GetStartedActivity::class.java).also {
            startActivity(it)
        }
    }

}