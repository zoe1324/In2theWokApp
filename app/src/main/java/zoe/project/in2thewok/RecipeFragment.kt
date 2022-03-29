package zoe.project.in2thewok

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import zoe.project.in2thewok.databinding.FragmentRecipeBinding

@Suppress("TooGenericExceptionCaught")

/**
 * A simple [Fragment] subclass.
 * Use the [RecipeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class RecipeFragment : Fragment() {

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
        }
        else{
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

        binding.btnBookmarks.setOnClickListener{
            addBookmark(postID.toString())
        }

        binding.btnComment.setOnClickListener{
            showDialog()
        }

        communicator = activity as Communicator
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    fun showDialog(){
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle("Write your comment below")

        // Set up the input
        val input = EditText(context)
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.hint = "Enter Text"
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        // Set up the buttons
        builder.setPositiveButton("Send comment") { _, _ ->
            // Here you get input text from the Edittext
            comments.add(auth.currentUser?.displayName + ": " + input.text.toString())
            binding.rvComments.adapter?.notifyDataSetChanged()
            addComment(auth.currentUser?.displayName + ": " + input.text.toString())
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
        builder.show()
    }
    private fun addComment(comment: String) = CoroutineScope(Dispatchers.IO).launch{
        try{
            var commentsList: ArrayList<String>?
            var docRef: DocumentReference?
            val querySnapshot = postCollectionRef
                .whereEqualTo("postID", postID)
                .get()
                .await()
            for(document in querySnapshot!!.documents){
                val post = document.toObject<Post>()
                commentsList = post?.comments
                commentsList?.add(comment)
                docRef = Firebase.firestore.collection("posts").document(document.id)
                docRef.update("comments", commentsList)
//                communicator.updateBookmarkList(commentsList)
            }
            withContext(Dispatchers.Main){
                Toast.makeText(context, "Successfully added comment", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception){
            withContext(Dispatchers.Main) {
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                println(e.message)
            }
        }

    }
    private fun addBookmark(postID : String) = CoroutineScope(Dispatchers.IO).launch{
        try{
            var bookmarksList: ArrayList<String>?
            var docRef: DocumentReference?
            val querySnapshot = personCollectionRef
                .whereEqualTo("userID", auth.currentUser?.uid.toString())
                .get()
                .await()
            for(document in querySnapshot!!.documents){
                val person = document.toObject<Person>()
                bookmarksList = person?.bookmarks
                bookmarksList?.add(postID)
                docRef = Firebase.firestore.collection("people").document(document.id)
                docRef.update("bookmarks", bookmarksList)
                communicator.updateBookmarkList(bookmarksList)
            }
            withContext(Dispatchers.Main){
                Toast.makeText(context, "Successfully added bookmark.", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception){
            withContext(Dispatchers.Main) {
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                println(e.message)
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    inner class RecyclerAdapter(private val stringArray: ArrayList<String>,
                                val layout: Int): RecyclerView.Adapter<RecipeFragment.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.updateItems(stringArray[position])
        }

        override fun getItemCount(): Int {
            return stringArray.size
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getItemViewType(position: Int): Int {
            return position
        }

    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var inputString: TextView = itemView.findViewById(R.id.tvString)

        fun updateItems(string: String){
            inputString.text = string
        }
    }
}
