package zoe.project.in2thewok

//import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import zoe.project.in2thewok.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var homeFragment: HomeFragment
    private lateinit var profileFragment: ProfileFragment
    private lateinit var addFragment: AddFragment
    private lateinit var articleFragment: ArticleFragment
    private lateinit var infoFragment: InfoFragment


    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_home)
        supportActionBar?.title = "Home"
        supportActionBar?.show()
        homeFragment = HomeFragment()
        profileFragment = ProfileFragment()
        addFragment = AddFragment()
        articleFragment = ArticleFragment()
        infoFragment = InfoFragment()

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(R.id.frag_layout, homeFragment)
        //Initialise the FirebaseAuth instance
        auth = Firebase.auth

        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId){
                R.id.home -> {replaceFragment(R.id.frag_layout, homeFragment)}
                R.id.profile -> {replaceFragment(R.id.frag_layout, profileFragment)}
                R.id.add -> {replaceFragment(R.id.frag_layout, addFragment)}
                R.id.articles -> {replaceFragment(R.id.frag_layout, articleFragment)}
                R.id.info -> {replaceFragment(R.id.frag_layout, infoFragment)}
            }
            true
        }
    }
    override fun onStart() {
        super.onStart()
//        setContentView(R.layout.activity_home)
        supportActionBar?.title = "Home"
        supportActionBar?.show()
        //Initialise the FirebaseAuth instance
        auth = Firebase.auth
    }

    inline fun FragmentManager.doTransaction(func: FragmentTransaction.() ->
    FragmentTransaction
    ) {
        beginTransaction().func().commit()
    }

    fun AppCompatActivity.addFragment(frameId: Int, fragment: Fragment){
        supportFragmentManager.doTransaction { add(frameId, fragment) }
    }


    fun AppCompatActivity.replaceFragment(frameId: Int, fragment: Fragment) {
        supportFragmentManager.doTransaction{replace(frameId, fragment)}
    }

    fun AppCompatActivity.removeFragment(fragment: Fragment) {
        supportFragmentManager.doTransaction{remove(fragment)}
    }

}