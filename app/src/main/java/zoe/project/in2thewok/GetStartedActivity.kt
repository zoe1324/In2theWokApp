package zoe.project.in2thewok

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

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
                Intent(this, QuestionnaireActivity::class.java).also {
                    startActivity(it)
                }
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
}