package zoe.project.in2thewok

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HomeActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        supportActionBar?.title = "Home"
        supportActionBar?.show()


        //Initialise the FirebaseAuth instance
        auth = Firebase.auth
    }
    override fun onStart() {
        super.onStart()
        setContentView(R.layout.activity_home)
        supportActionBar?.title = "Home"
        supportActionBar?.show()
        //Initialise the FirebaseAuth instance
        auth = Firebase.auth
    }

}