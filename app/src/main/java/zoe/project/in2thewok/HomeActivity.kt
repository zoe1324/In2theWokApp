package zoe.project.in2thewok

//import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
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
// TODO: Fix some back button functionality that lets you press back before completing registration properly
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        supportActionBar?.title = "Hello" + Firebase.auth.currentUser?.displayName
//        supportActionBar?.show()
        homeFragment = HomeFragment()
        profileFragment = ProfileFragment()
        addFragment = AddFragment()
        articleFragment = ArticleFragment()
        infoFragment = InfoFragment()

        //Hardcoded ids for functionality
        recs = arrayListOf("DvI17nLSULQhmKrbsQmA","NYBaiKg4nkosY6PYBx4x","ZUFGJjKZy8fZQz6KuPrq", "vjX6h3jA0QHO48GAgFz4", "wfJvMMKGTqOh9sduTcQq", "wpMnkDNc661Zj23EgfaQ")
//        bookmarked = arrayListOf("NYBaiKg4nkosY6PYBx4x","DvI17nLSULQhmKrbsQmA","vjX6h3jA0QHO48GAgFz4", "ZUFGJjKZy8fZQz6KuPrq", "wfJvMMKGTqOh9sduTcQq", "wpMnkDNc661Zj23EgfaQ")

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //Initialise the FirebaseAuth instance
        auth = Firebase.auth
        retrieveFunArticles()
        retrieveHealthArticles()
        retrievePosts()
        initRecsAndBookmarkPosts()


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

//    private fun refreshRecommendedPosts() = CoroutineScope(Dispatchers.IO).launch{
//        recs.clear()
//    }

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
            for(rec in recs){
                val querySnapshot = postCollectionRef
                    .whereEqualTo("postID", rec)
                    .get()
                    .await()
                for (document in querySnapshot.documents) {
                    document.toObject<Post>()?.let { recPosts.add(it) }
                }
            }
            bookmarkedPosts.clear()
//            var bookmarksList: ArrayList<String>?
            var docRef: DocumentReference?
            var querySnapshot = personCollectionRef
                .whereEqualTo("userID", auth.currentUser?.uid.toString())
                .get()
                .await()
            for(document in querySnapshot!!.documents){
                val person = document.toObject<Person>()
                bookmarked = person?.bookmarks
//                bookmarksList?.add(postID)
//                docRef = document.getDocumentReference("bookmarks")
//                docRef?.update("bookmarks", bookmarksList)
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

    override fun onStart() {
        super.onStart()
        supportActionBar?.hide()

        //Initialise the FirebaseAuth instance
        auth = Firebase.auth
    }



    inline fun FragmentManager.doTransaction(func: FragmentTransaction.() ->
    FragmentTransaction
    ) {
        beginTransaction().func().commit()
    }


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

    override fun updateRecList() {
//        retrieveRecPosts()
    }

//    fun AppCompatActivity.removeFragment(fragment: Fragment) {
//        supportFragmentManager.doTransaction{remove(fragment)}
//    }

}