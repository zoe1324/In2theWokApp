package zoe.project.in2thewok

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class QuestionnaireActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_questionnaire)
        supportActionBar?.hide()
        auth = Firebase.auth
        val currentUser = auth.currentUser
        val submitBtn = findViewById<Button>(R.id.btnSubmit)
        val username = currentUser?.displayName.toString()
        submitBtn.setOnClickListener {
            Intent(this, HomeActivity::class.java).also {
                startActivity(it)
            }
        }

        val skipBtn = findViewById<Button>(R.id.btnSkip)

        skipBtn.setOnClickListener {
            Intent(this, HomeActivity::class.java).also {
                startActivity(it)
            }
        }
    }
}
