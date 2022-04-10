package zoe.project.in2thewok

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import zoe.project.in2thewok.InfoFragment.RecyclerAdapter
import zoe.project.in2thewok.databinding.FragmentProfileBinding
import zoe.project.in2thewok.databinding.FragmentRecipeBinding

/**
 * [RecipeFragment], A [Fragment] class displayed by [HomeActivity],
 * linked via [FragmentRecipeBinding].
 *
 * @constructor Creates a new Fragment
 * @suppress TooGenericExceptionCaught
 */
@Suppress("TooGenericExceptionCaught")

class RecipeFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var communicator: Communicator
    private var _binding: FragmentRecipeBinding? = null
    private val binding get() = _binding!!
    private var ingredients: ArrayList<String> = arrayListOf()
    private var steps: ArrayList<String> = arrayListOf()
    private var comments: ArrayList<String> = arrayListOf()
    private val personCollectionRef = Firebase.firestore.collection("people")
    private val postCollectionRef = Firebase.firestore.collection("posts")
    var recipeTitle: String? = null
    var imageURI: String? = null
    var story: String? = null
    var postID: String? = null
    var userID: String? = null
    var username: String? = null
    var cuisineType: String? = null

    /**
     * Creates [RecipeFragment], receives Post object data via Bundle, then
     * retrieves each attribute and sets the corresponding variables.
     *
     * @param savedInstanceState contains any data passed to the fragment via Bundle
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        recipeTitle = arguments?.getString("title")
        ingredients = arguments?.getStringArrayList("ingredients") as ArrayList<String>
        steps = arguments?.getStringArrayList("steps") as ArrayList<String>
        comments = arguments?.getStringArrayList("comments") as ArrayList<String>
        story = arguments?.getString("story")
        imageURI = arguments?.getString("imageURI")
        postID = arguments?.getString("postID")
        userID = arguments?.getString("userID")
        username = arguments?.getString("username")
        cuisineType = arguments?.getString("cuisineType")
    }

    /**
     * Creates View for [RecipeFragment], has click listeners for commenting and bookmarking,
     * sets RecyclerView Adapters
     *
     * @param inflater Layout inflater for [Fragment]
     * @param container Container ViewGroup for [Fragment]
     * @param savedInstanceState contains any data passed to the fragment via Bundle
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRecipeBinding.inflate(inflater, container, false)
        binding.tvRecipeTitle.text = recipeTitle.toString()
        binding.tvRecipeCuisineType.text = cuisineType.toString()
        binding.tvRecipeStory.text = story.toString()
        if (imageURI != null) {
            Picasso.get()
                .load(imageURI)
                .into(binding.ivRecipePhoto)
        } else {
            Picasso.get()
                .load(R.drawable.cooking)
                .into(binding.ivRecipePhoto)
        }
        binding.rvIngredients.hasFixedSize()
        binding.rvIngredients.layoutManager = LinearLayoutManager(context)
        binding.rvIngredients.itemAnimator = DefaultItemAnimator()
        binding.rvIngredients.adapter = RecyclerAdapter(ingredients, R.layout.layout_text_layout)

        binding.rvSteps.hasFixedSize()
        binding.rvSteps.layoutManager = LinearLayoutManager(context)
        binding.rvSteps.itemAnimator = DefaultItemAnimator()
        binding.rvSteps.adapter = RecyclerAdapter(steps, R.layout.layout_text_layout)

        binding.rvComments.hasFixedSize()
        binding.rvComments.layoutManager = LinearLayoutManager(context)
        binding.rvComments.itemAnimator = DefaultItemAnimator()
        binding.rvComments.adapter = RecyclerAdapter(comments, R.layout.layout_text_layout)

        binding.btnBookmarks.setOnClickListener {
            addBookmark(postID.toString())
        }

        binding.btnComment.setOnClickListener {
            showDialog()
        }

        communicator = activity as Communicator
        return binding.root
    }

    /**
     * Constructs and shows a custom dialog box for adding a comment
     */
    @SuppressLint("NotifyDataSetChanged")
    fun showDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle("Write your comment below")

        val input = EditText(context)
        input.hint = "Enter Text"
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton("Send comment") { _, _ ->
            comments.add(auth.currentUser?.displayName + ": " + input.text.toString())
            binding.rvComments.adapter?.notifyDataSetChanged()
            addComment(auth.currentUser?.displayName + ": " + input.text.toString())
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
        builder.show()
    }

    /**
     * Adds the user comment to the database list of comments associated with the Post
     * in view
     *
     * @param comment The string containing the user comment
     * @throws Exception if comment unsuccessfully added
     */
    private fun addComment(comment: String) = CoroutineScope(Dispatchers.IO).launch {
        try {
            var commentsList: ArrayList<String>?
            var docRef: DocumentReference?
            val querySnapshot = postCollectionRef
                .whereEqualTo("postID", postID)
                .get()
                .await()
            for (document in querySnapshot!!.documents) {
                val post = document.toObject<Post>()
                commentsList = post?.comments
                commentsList?.add(comment)
                docRef = Firebase.firestore.collection("posts").document(document.id)
                docRef.update("comments", commentsList)
            }
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Successfully added comment", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                println(e.message)
            }
        }
    }

    /**
     * Adds the postID to the database list of bookmarks associated with the current user
     *
     * @param postID The string containing the current postID
     * @throws Exception if bookmark unsuccessfully added
     */
    private fun addBookmark(postID: String) = CoroutineScope(Dispatchers.IO).launch {
        try {
            var bookmarksList: ArrayList<String>?
            var docRef: DocumentReference?
            val querySnapshot = personCollectionRef
                .whereEqualTo("userID", auth.currentUser?.uid.toString())
                .get()
                .await()
            for (document in querySnapshot!!.documents) {
                val person = document.toObject<Person>()
                bookmarksList = person?.bookmarks
                bookmarksList?.add(postID)
                docRef = Firebase.firestore.collection("people").document(document.id)
                docRef.update("bookmarks", bookmarksList)
                communicator.updateBookmarkList(bookmarksList)
            }
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Successfully added bookmark.", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                println(e.message)
            }
        }

    }

    /**
     * Destroys the Fragment View so it is no longer visible
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * [RecyclerAdapter] customised class, adapts arrayList of strings to fit in a RecyclerView
     *
     * @param stringArray The arrayList of strings being adapted
     * @param layout The layout in which each link should be adapted to fit
     * @constructor Creates a new Adapter for the RecyclerView
     */
    inner class RecyclerAdapter(
        private val stringArray: ArrayList<String>,
        val layout: Int
    ) : RecyclerView.Adapter<RecipeFragment.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
            return ViewHolder(view)
        }

        /**
         * Updates the ViewHolder according to a given position
         *
         * @param holder The ViewHolder being updated
         * @param position THe position in the array in which the current item is being adapted to fit
         */
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.updateItems(stringArray[position])
        }

        /**
         * Returns the size of the arrayList being adapted
         *
         * @return stringArray.size
         */
        override fun getItemCount(): Int {
            return stringArray.size
        }

        /**
         * Returns the id of the item in the arrayList being adapted
         *
         * @param position
         * @return position.toLong()
         */
        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        /**
         * Returns item view type
         *
         * @param position The current array position
         * @return position
         */
        override fun getItemViewType(position: Int): Int {
            return position
        }

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var inputString: TextView = itemView.findViewById(R.id.tvString)

        /**
         * Updates each RecyclerView item to display the current string
         *
         * @param string The current string in the list being passed in
         */
        fun updateItems(string: String) {
            inputString.text = string
        }
    }
}
