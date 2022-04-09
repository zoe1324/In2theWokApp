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

/**
 * [HomeActivity], handles several Fragments: [HomeFragment], [ProfileFragment],
 * [AddFragment], [ArticleFragment], [InfoFragment] and [RecipeFragment]. Holds navigation bar,
 * Holds a fragment container in which the screen displays a Fragment,
 * depending on which navigation item is selected, swaps to RecipeFragment based on
 * user selection of recipes.
 *
 * @constructor Creates a new AppCompatActivity, implements Communicator interface
 * @suppress TooManyFunctions
 * @suppress TooGenericExceptionCaught
 * @suppress EmptyFunctionBlock
 */
@Suppress("TooManyFunctions", "TooGenericExceptionCaught", "EmptyFunctionBlock")

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
    private val healthArticleCollectionRef = Firebase.firestore.collection("articles")
        .document("health").collection("health_articles")
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


    /**
     * Creates the [HomeActivity], has onItemSelectedListener for
     * Navigation bar. Initially displays [HomeFragment] in the fragment container.
     * Displays different fragments according to which item is selected by user.
     * Contains a hardcoded list of recommendation IDs to use if system
     * finds no recommendations for the user based on their questionnaire.
     *
     * @param savedInstanceState contains any data passed to the activity via Bundle
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        homeFragment = HomeFragment()
        profileFragment = ProfileFragment()
        addFragment = AddFragment()
        articleFragment = ArticleFragment()
        infoFragment = InfoFragment()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Hardcoded ids for functionality
        recs = arrayListOf("1lA1Z8C9xmUm8eqK7Abh","cZ96WPzc85g6ZuoPhg0j","iY8j4vktKX9MrnABO0lx")
        auth = Firebase.auth
        retrieveArticles()
        retrievePosts()
        retrievePersonAnswers()


        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId){
                R.id.home -> {
                    replaceFragment(homeFragment)
                }
                R.id.profile -> {
                    replaceFragment(profileFragment)
                }
                R.id.add -> {
                    replaceFragment(addFragment)
                }
                R.id.articles -> {
                    replaceFragment(articleFragment)
                }
                R.id.info -> {
                    replaceFragment(infoFragment)
                }
            }
            true
        }
    }

    /**
     * Retrieves the user's questionnaire answers for display on user profile,
     * will retrieve one person document matching userID to current FirebaseAuth ID.
     * Triggers initRecsAndBookmarkPosts() function after db retrieval.
     *
     * @throws Exception if db retrieval fails
     */
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

    /**
     * Retrieves the fun fact and health article URLs from the article collection.
     * These articles will be used in [ArticleFragment] and [InfoFragment].
     *
     * @throws Exception if db retrieval fails
     */
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

    /**
     * Retrieves the user's posts for display on user profile,
     * will retrieve post documents with matching userID to current FirebaseAuth ID.
     * Converts each post document to Post object.
     *
     * @throws Exception if db retrieval fails
     */
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

    /**
     * Initialises the user's recommendations and bookmarks for display on home page,
     * will retrieve post documents to match the user's favourite cuisine type, and
     * if the system finds none, will use hardcoded IDs instead. Then, the user's bookmarks
     * are retrieved, with the postIDs being used to retrieve full Post objects.
     * Updates the screen with [HomeFragment] after db retrieval complete by calling
     * replaceFragment.
     *
     * @throws Exception if db retrieval fails
     */
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
            replaceFragment(homeFragment)
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@HomeActivity, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Retrieves user's bookmarked posts, updates bookmarked posts
     *
     * @throws Exception if db retrieval fails
     */
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

    /**
     * Uses FragmentManager to switch fragment to the passed in [Fragment]
     *
     * @param fragment The fragment to be swithced to
     */
    fun AppCompatActivity.replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frag_layout, fragment)
        transaction.commit()
    }

    /**
     * Bundles up all of the passed in recipe Post attributes to be passed to
     * the [RecipeFragment]. Triggers replaceFragment() function with [RecipeFragment].
     *
     * @param post The Post object instance of the recipe tapped on by the user, in either [ProfileFragment] or [HomeFragment].
     */
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
        replaceFragment(recipeFragment)
    }

    /**
     * Called by [AddFragment], updates the list of user posts
     */
    override fun updatePostList() {
        retrievePosts()
    }

    /**
     * Called by [RecipeFragment], updates user bookmarks when user bookmarks a post
     */
    override fun updateBookmarkList(bookmarksList: ArrayList<String>?) {
        bookmarked = bookmarksList
        retrieveBookmarkedPosts()
    }

    /**
     * Called by [HomeFragment], when the user taps the 'Log Out' button.
     */
    override fun signOut() {
        Intent(this, MainActivity::class.java).also {
            startActivity(it)
            finish()
        }
    }

    /**
     * Suppresses back button, to prevent errors in account registration
     */
    override fun onBackPressed() {
    }
}
