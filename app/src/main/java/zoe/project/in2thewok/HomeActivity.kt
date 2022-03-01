package zoe.project.in2thewok

//import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import zoe.project.in2thewok.databinding.ActivityHomeBinding
import java.lang.Exception

class HomeActivity : AppCompatActivity(), Communicator{

    private lateinit var binding: ActivityHomeBinding
    private lateinit var homeFragment: HomeFragment
    private lateinit var profileFragment: ProfileFragment
    private lateinit var addFragment: AddFragment
    private lateinit var articleFragment: ArticleFragment
    private lateinit var infoFragment: InfoFragment
    private lateinit var recipeFragment: RecipeFragment
    private val articleCollectionRef = Firebase.firestore.collection("articles").document("fun").collection("fun_facts")
    private val healthArticleCollectionRef = Firebase.firestore.collection("articles").document("health").collection("health_articles")
    private val postCollectionRef = Firebase.firestore.collection("posts")
    val articles = arrayListOf<String>()
    val healthArticles = arrayListOf<String>()
    val posts = arrayListOf<Post>()
    private var auth = Firebase.auth
//    private val listener = ProfileFragment.FragmentProfileListener

//    override fun listener.onRecipeSent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        supportActionBar?.title = "Hello" + Firebase.auth.currentUser?.displayName
//        supportActionBar?.show()
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
        retrieveFunArticles()
        retrieveHealthArticles()
        retrievePosts()

        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId){
                R.id.home -> {
                    replaceFragment(R.id.frag_layout, homeFragment)
//                    supportActionBar?.title = "Home"
                }
                R.id.profile -> {
                    replaceFragment(R.id.frag_layout, profileFragment)
//                    supportActionBar?.title = "Profile"
                }
                R.id.add -> {
                    replaceFragment(R.id.frag_layout, addFragment)
//                    supportActionBar?.title = "Create New Post"
                }
                R.id.articles -> {
                    replaceFragment(R.id.frag_layout, articleFragment)
//                    supportActionBar?.title = "Fun Food Articles/Reminders to Eat Healthily"
                }
                R.id.info -> {
                    replaceFragment(R.id.frag_layout, infoFragment)
//                    supportActionBar?.title = "Nutritional Information"
                }
            }
            true
        }
    }

    private fun retrieveFunArticles() = CoroutineScope(Dispatchers.IO).launch{
        try {
            val querySnapshot = articleCollectionRef
                .get()
                .await()
            for (document in querySnapshot.documents) {
                val url = "${document["url"]}"
                articles.add(url)
            }

        } catch (e: Exception){
            withContext(Dispatchers.Main) {
                Toast.makeText(this@HomeActivity, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun retrieveHealthArticles() = CoroutineScope(Dispatchers.IO).launch{
        try {
            val querySnapshot = healthArticleCollectionRef
                .get()
                .await()
            for (document in querySnapshot.documents) {
                val url = "${document["url"]}"
                healthArticles.add(url)
            }
        } catch (e: Exception){
            withContext(Dispatchers.Main) {
                Toast.makeText(this@HomeActivity, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun retrievePosts() = CoroutineScope(Dispatchers.IO).launch {

        try {
            posts.clear()
            val querySnapshot = postCollectionRef
                .whereEqualTo("userID", auth.currentUser?.uid)
                .get()
                .await()
            for (document in querySnapshot.documents) {
                document.toObject<Post>()?.let { posts.add(it) }
            }
//  TODO: Fix how this is styled, and navigate to the recipe 'fragment' that doesn't exist yet.

        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@HomeActivity, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
//        supportActionBar?.title = "Home"
//        supportActionBar?.title = "Hello " + Firebase.auth.currentUser?.displayName
        supportActionBar?.hide()
        //Initialise the FirebaseAuth instance
        auth = Firebase.auth
    }



    inline fun FragmentManager.doTransaction(func: FragmentTransaction.() ->
    FragmentTransaction
    ) {
        beginTransaction().func().commit()
    }

//    fun AppCompatActivity.addFragment(frameId: Int, fragment: Fragment){
//        supportFragmentManager.doTransaction { add(frameId, fragment) }
//    }


    fun AppCompatActivity.replaceFragment(frameId: Int, fragment: Fragment) {
        supportFragmentManager.doTransaction{replace(frameId, fragment)}
    }

    override fun recipePassComm(post: Post) {
        val bundle = Bundle()
        bundle.putString("postID", post.postID)
        bundle.putString("userID", post.userID)
        bundle.putString("username", post.username)
        bundle.putString("imageURI", post.imageURI)
        bundle.putString("title", post.title)
        bundle.putStringArrayList("ingredients", post.ingredients)
        bundle.putStringArrayList("steps", post.steps)
        bundle.putString("cuisineType", post.cuisineType)
        bundle.putString("story", post.story)
        bundle.putStringArrayList("comments", arrayListOf())
//        val transaction = this.supportFragmentManager.beginTransaction()
        recipeFragment = RecipeFragment()
        recipeFragment.arguments = bundle
        replaceFragment(R.id.frag_layout, recipeFragment)
    }

    override fun updatePostList() {
        retrievePosts()
    }
//    fun AppCompatActivity.removeFragment(fragment: Fragment) {
//        supportFragmentManager.doTransaction{remove(fragment)}
//    }

}