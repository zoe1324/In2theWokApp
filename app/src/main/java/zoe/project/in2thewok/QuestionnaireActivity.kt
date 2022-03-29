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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

@Suppress("EmptyFunctionBlock", "TooGenericExceptionCaught")

class QuestionnaireActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val personCollectionRef = Firebase.firestore.collection("people")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_questionnaire)
        supportActionBar?.hide()
        auth = Firebase.auth
        val currentUser = auth.currentUser
        val submitBtn = findViewById<Button>(R.id.btnSubmit)
        val username = currentUser?.displayName.toString().trim()

        submitBtn.setOnClickListener{
            val q1 = findViewById<EditText>(R.id.a1).text.toString().trim()
            val q2 = findViewById<EditText>(R.id.a2).text.toString().trim()
            val q3 = findViewById<EditText>(R.id.a3).text.toString().trim()
            val q4 = findViewById<EditText>(R.id.a4).text.toString().trim()
            val person = Person(username, q1, q2, q3, q4, currentUser?.uid.toString(), arrayListOf())
            savePerson(person)
        }
    }

    private fun savePerson(person: Person) = CoroutineScope(Dispatchers.IO).launch{
        try {
            personCollectionRef.add(person).addOnSuccessListener {
                switchToHome()
                Toast.makeText(this@QuestionnaireActivity,
                    "Successfully saved data.",
                    Toast.LENGTH_LONG
                ).show()
            }.await()
        } catch(e: Exception){
            withContext(Dispatchers.Main) {
                Toast.makeText(this@QuestionnaireActivity, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun switchToHome() {
        Intent(this, HomeActivity::class.java).also {
            startActivity(it)
        }
    }

    override fun onBackPressed() {
    }
}
