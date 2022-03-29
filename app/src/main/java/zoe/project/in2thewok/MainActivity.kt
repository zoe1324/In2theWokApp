package zoe.project.in2thewok

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

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