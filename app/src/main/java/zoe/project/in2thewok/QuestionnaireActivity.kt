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

/**
 * [QuestionnaireActivity], a questionnaire page for the user, presents
 * four questions and input fields, and a button to navigate to [HomeActivity],
 * changes to [HomeActivity] if the user profile details successfully update on the db.
 *
 * @constructor Creates a new AppCompatActivity
 * @suppress EmptyFunctionBlock
 * @suppress TooGenericExceptionCaught
 */
@Suppress("EmptyFunctionBlock", "TooGenericExceptionCaught")

class QuestionnaireActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val personCollectionRef = Firebase.firestore.collection("people")

    /**
     * Creates the [QuestionnaireActivity], has button listener for
     * submit button. Switches to [HomeActivity] upon submit button click.
     * Triggers savePerson() upon submit button click. Creates new Person
     * data class object using user's displayName, four question answers,
     * their Firebase Auth userID and an empty arrayList for user bookmarks.
     *
     * @param savedInstanceState contains any data passed to the activity via Bundle
     */
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

    /**
     * Attempts to save a new Person to 'people' collection on Firebase Firestore db.
     * Uses CoRoutines to asynchronously save the Person object using the Firestore Collection Reference.
     * Triggers switchToHome() method upon success of saving new Person.
     *
     * @param person contains current Person object to be saved to Firestore db.
     * @throws Exception if user input isn't accepted or db connection fails.
     */
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

    /**
     * Uses an Intent to launch the [HomeActivity] class.
     */
    private fun switchToHome() {
        Intent(this, HomeActivity::class.java).also {
            startActivity(it)
        }
    }

    /**
     * Suppresses back button, to prevent errors in account registration
     */
    override fun onBackPressed() {
    }
}
