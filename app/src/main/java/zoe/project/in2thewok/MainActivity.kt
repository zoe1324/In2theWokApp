package zoe.project.in2thewok

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

/**
 * [MainActivity], a welcome page for the user, presents the application name
 * and buttons to navigate to login or register page, changes to [HomeActivity]
 * if Firebase Authentication detects a user instance. Transitions to [LoginActivity] upon
 * login button tap, transitions to [RegisterActivity] if register button is tapped.
 *
 * @constructor Creates a new AppCompatActivity
 * */


class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    /**
     * Creates the [MainActivity], has button listeners for
     * login and register buttons. Switches to [LoginActivity] upon login button click,
     * and [RegisterActivity] upon register button click.
     *
     * @param savedInstanceState contains any data passed to the activity via Bundle
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        supportActionBar?.hide()
        auth = Firebase.auth
        val loginBtnMain = findViewById<Button>(R.id.btnLoginMain)
        val registerBtnMain = findViewById<Button>(R.id.btnRegisterMain)

        loginBtnMain.setOnClickListener {
            Intent(this, LoginActivity::class.java).also {
                startActivity(it)
            }
        }
        registerBtnMain.setOnClickListener {
            Intent(this, RegisterActivity::class.java).also {
                startActivity(it)
            }
        }

    }

    /**
     * Starts [MainActivity], checks if user is already signed in
     * and navigates to [HomeActivity] if FirebaseAuth instance is not null.
     */
    public override fun onStart(){
        super.onStart()
        val currentUser = auth.currentUser

        if(currentUser != null) {
            Intent(this, HomeActivity::class.java).also {
                startActivity(it)
            }
        }
    }
}
