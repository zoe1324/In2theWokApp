package zoe.project.in2thewok

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
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
    private lateinit var auth: FirebaseAuth
    private val articleCollectionRef = Firebase.firestore.collection("articles").document("fun").collection("fun_facts")
    private val healthArticleCollectionRef = Firebase.firestore.collection("articles").document("health").collection("health_articles")
    private val postCollectionRef = Firebase.firestore.collection("posts")
    private val personCollectionRef = Firebase.firestore.collection("people")
    val articles = arrayListOf<String>()
    val healthArticles = arrayListOf<String>()
    val posts = arrayListOf<Post>()
    var recPosts = arrayListOf<Post>()
    var recs = arrayListOf<String>()
    var bookmarked : ArrayList<String>? = arrayListOf()
    var bookmarkedPosts = arrayListOf<Post>()
    var person: Person? = null
    var q1: String? = null
    var q2: String? = null
    var q3: String? = null
    var q4: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        homeFragment = HomeFragment()
        profileFragment = ProfileFragment()
        addFragment = AddFragment()
        articleFragment = ArticleFragment()
        infoFragment = InfoFragment()

        //Hardcoded ids for functionality
        recs = arrayListOf("1lA1Z8C9xmUm8eqK7Abh","cZ96WPzc85g6ZuoPhg0j","iY8j4vktKX9MrnABO0lx")

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        retrieveArticles()
        retrievePosts()
        retrievePersonAnswers()


        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId){
                R.id.home -> {
                    replaceFragment(R.id.frag_layout, homeFragment)
                }
                R.id.profile -> {
                    replaceFragment(R.id.frag_layout, profileFragment)
                }
                R.id.add -> {
                    replaceFragment(R.id.frag_layout, addFragment)
                }
                R.id.articles -> {
                    replaceFragment(R.id.frag_layout, articleFragment)
                }
                R.id.info -> {
                    replaceFragment(R.id.frag_layout, infoFragment)
                }
            }
            true
        }
    }
    private fun retrievePersonAnswers() = CoroutineScope(Dispatchers.IO).launch {
        try{
            var querySnapshot = personCollectionRef
                .whereEqualTo("userID", auth.currentUser?.uid.toString())
                .get()
                .await()
            for(document in querySnapshot!!.documents){
                person = document.toObject<Person>()
                q1 = person?.q1.toString()
                q2 = person?.q2.toString()
                q3 = person?.q3.toString()
                q4 = person?.q4.toString()
            }
            initRecsAndBookmarkPosts()
        } catch(e: Exception){
            withContext(Dispatchers.Main) {
                Toast.makeText(this@HomeActivity, e.message, Toast.LENGTH_LONG).show()
            }
        }

    }
    private fun retrieveArticles() = CoroutineScope(Dispatchers.IO).launch{
        try {
            var querySnapshot = articleCollectionRef
                .get()
                .await()
            for (document in querySnapshot.documents) {
                val url = "${document["url"]}"
                articles.add(url)
            }
            querySnapshot = healthArticleCollectionRef
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
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@HomeActivity, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun initRecsAndBookmarkPosts() = CoroutineScope(Dispatchers.IO).launch {
        try{
            recPosts.clear()
                var querySnapshot = postCollectionRef
                    .whereEqualTo("cuisineType", q4.toString().lowercase())
                    .get()
                    .await()
                for (document in querySnapshot.documents) {
                    document.toObject<Post>()?.let { recPosts.add(it) }
                }
            if(recPosts.isEmpty()){
                for(rec in recs){
                    querySnapshot = postCollectionRef
                        .whereEqualTo("postID", rec)
                        .get()
                        .await()
                    for(document in querySnapshot.documents){
                        document.toObject<Post>()?.let{recPosts.add(it)}
                    }

                }
            }
            bookmarkedPosts.clear()
            querySnapshot = personCollectionRef
                .whereEqualTo("userID", auth.currentUser?.uid.toString())
                .get()
                .await()
            for(document in querySnapshot!!.documents){
                person = document.toObject<Person>()
                bookmarked = person?.bookmarks
            }
            for(bookmark in bookmarked!!){
                querySnapshot = postCollectionRef
                    .whereEqualTo("postID", bookmark)
                    .get()
                    .await()
                for (document in querySnapshot.documents) {
                    document.toObject<Post>()?.let { bookmarkedPosts.add(it) }
                }
            }
            replaceFragment(R.id.frag_layout, homeFragment)
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@HomeActivity, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun retrieveBookmarkedPosts() = CoroutineScope(Dispatchers.IO).launch {
        try{
            bookmarkedPosts.clear()
            var querySnapshot = personCollectionRef
                .whereEqualTo("userID", auth.currentUser?.uid.toString())
                .get()
                .await()
            for(document in querySnapshot!!.documents){
                val person = document.toObject<Person>()
                bookmarked = person?.bookmarks
            }
            for(bookmark in bookmarked!!){
                querySnapshot = postCollectionRef
                    .whereEqualTo("postID", bookmark)
                    .get()
                    .await()
                for (document in querySnapshot.documents) {
                    document.toObject<Post>()?.let { bookmarkedPosts.add(it) }
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@HomeActivity, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun AppCompatActivity.replaceFragment(frameId: Int, fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frag_layout, fragment)
        transaction.commit()
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
        bundle.putStringArrayList("comments", post.comments)
        recipeFragment = RecipeFragment()
        recipeFragment.arguments = bundle
        replaceFragment(R.id.frag_layout, recipeFragment)
    }

    override fun updatePostList() {
        retrievePosts()
    }

    override fun updateBookmarkList(bookmarksList: ArrayList<String>?) {
        bookmarked = bookmarksList
        retrieveBookmarkedPosts()
    }

    override fun signOut() {
        Intent(this, MainActivity::class.java).also {
            startActivity(it)
            finish()
        }
    }
}