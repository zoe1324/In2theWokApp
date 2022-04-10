package zoe.project.in2thewok

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import zoe.project.in2thewok.databinding.FragmentAddBinding

/**
 * [AddFragment], A [Fragment] class displayed by [HomeActivity],
 * linked via [FragmentAddBinding].
 *
 * @constructor Creates a new Fragment
 * @suppress TooGenericExceptionCaught
 */
@Suppress("TooGenericExceptionCaught")

class AddFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var communicator: Communicator
    private var imageUri: Uri? = null
    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!
    private val postCollectionRef = Firebase.firestore.collection("posts")
    private var storageRef = FirebaseStorage.getInstance().reference
    private var remoteUri: String? = null
    private val ingredients = ArrayList<String>()
    private val steps = ArrayList<String>()
    private var step = ""
    private var ingredient = ""

    /**
     * Adds user inputted ingredient to onscreen list, so user can see it has been added
     */
    private fun addIngredient() {
        ingredient = binding.etAddIngredient.text.toString()
        val tv = TextView(context?.applicationContext)
        tv.id = View.generateViewId()
        tv.text = ingredient
        ingredients.add(ingredient)
        binding.llIngredients.addView(tv)
        val params = tv.layoutParams as LinearLayout.LayoutParams
        params.width = MATCH_PARENT
        params.height = WRAP_CONTENT
        tv.requestLayout()
        binding.etAddIngredient.text.clear()
    }

    /**
     * Adds the created post to the database, retrieves its document reference ID and uses that to
     * also store the ID as an attribute within the Post, this is for direct retrieval with a given ID
     *
     * @param post The Post instance of the created post from user inputs
     * @throws Exception if db upload was unsuccessful
     */
    private fun addPost(post: Post) = CoroutineScope(Dispatchers.IO).launch {
        val context = context?.applicationContext
        try {
            val ref = postCollectionRef.add(post)
                .await()
            ref.update("postID", ref.id)
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Successfully made post.", Toast.LENGTH_LONG).show()
                clearAll()
            }
            communicator.updatePostList()
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Adds user inputted step to onscreen list, so user can see it has been added
     */
    private fun addStep() {
        step = binding.etAddStep.text.toString()
        val tv = TextView(context?.applicationContext)
        tv.id = View.generateViewId()
        tv.text = step
        steps.add(step)
        binding.llSteps.addView(tv)
        val params = tv.layoutParams as LinearLayout.LayoutParams
        params.width = MATCH_PARENT
        params.height = WRAP_CONTENT
        tv.requestLayout()
        binding.etAddStep.text.clear()
    }

    /**
     * Clears entire page of user inputs
     */
    private fun clearAll() {
        binding.recipeTitle.text.clear()
        binding.cuisineType.text.clear()
        binding.recipeStory.text.clear()
        binding.tvUploadPhoto.text = "Add A Photo"
        binding.imgUpload.visibility = INVISIBLE
        binding.llSteps.removeAllViews()
        binding.llIngredients.removeAllViews()
        ingredients.removeAll(ingredients)
        steps.removeAll(steps)
    }

    /**
     * Creates View for [AddFragment], has click listeners for both Clear All, Add New Step and Add New Ingredient
     * functionality, populates the screen if user is returning to a post draft. Also contains functionality for
     * retrieving an image from the user's own device storage
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
        _binding = FragmentAddBinding.inflate(inflater, container, false)
        communicator = activity as Communicator

        if (remoteUri != null) {
            Picasso.get()
                .load(remoteUri)
                .into(binding.imgUpload)
            binding.tvUploadPhoto.text = "Change Photo"
        }

        if (ingredients.isNotEmpty()) {
            populateIngredients()
        }
        if (steps.isNotEmpty()) {
            populateSteps()
        }

        binding.btnUploadData.setOnClickListener {
            if (!textFieldsEmpty() && ingredients.isNotEmpty() && steps.isNotEmpty()) {
                val post = Post(
                    null,
                    auth.currentUser?.uid.toString(),
                    auth.currentUser?.displayName.toString(),
                    remoteUri,
                    null,
                    binding.recipeTitle.text.toString(),
                    ingredients,
                    binding.cuisineType.text.toString().lowercase(),
                    steps,
                    binding.recipeStory.text.toString(),
                    arrayListOf()
                )
                addPost(post)
            } else {
                Toast.makeText(
                    context,
                    "Please finish your post, photos are optional!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        binding.btnAddIngred.setOnClickListener {
            addIngredient()
        }

        binding.btnAddSteps.setOnClickListener {
            addStep()
        }

        binding.btnClearAll.setOnClickListener {
            clearAll()
        }

        val getContent =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                imageUri = uri
                val user = auth.currentUser
                val re = Regex("[^A-Za-z0-9 ]")
                var uriLastSeg = imageUri!!.lastPathSegment
                uriLastSeg = uriLastSeg?.let { it1 -> re.replace(it1, "") }
                val imageRef = storageRef.child("images/" + user?.uid + "/" + uriLastSeg)
                val upload = imageRef.putFile(imageUri!!)
                upload.addOnSuccessListener {
                    val downloadUrl = imageRef.downloadUrl
                    downloadUrl.addOnSuccessListener {
                        remoteUri = it.toString()
                    }
                }
                binding.imgUpload.setImageURI(imageUri)
                binding.imgUpload.visibility = VISIBLE
                binding.tvUploadPhoto.text = "Change Photo"
            }

        binding.cvAddImg.setOnClickListener {
            getContent.launch("image/*")
        }

        return binding.root

    }

    /**
     * Sets the Firebase Authentication instance whenever the Fragment is started
     */
    override fun onStart() {
        super.onStart()
        auth = Firebase.auth
    }

    /**
     * Destroys the Fragment View so it is no longer visible
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * If user returns to page before posting, the previously entered ingredients reappear on the screen
     */
    private fun populateIngredients() {
        for (ingredient in ingredients) {
            val tv = TextView(context?.applicationContext)
            tv.id = View.generateViewId()
            tv.text = ingredient
            binding.llIngredients.addView(tv)
            val params = tv.layoutParams as LinearLayout.LayoutParams
            params.width = MATCH_PARENT
            params.height = WRAP_CONTENT
            tv.requestLayout()
        }
    }

    /**
     * If user returns to page before posting, the previously entered steps reappear on the screen
     */
    private fun populateSteps() {
        for (step in steps) {
            val tv = TextView(context?.applicationContext)
            tv.id = View.generateViewId()
            tv.text = step
            binding.llSteps.addView(tv)
            val params = tv.layoutParams as LinearLayout.LayoutParams
            params.width = MATCH_PARENT
            params.height = WRAP_CONTENT
            tv.requestLayout()
        }
    }

    /**
     * Checks if the user edit text fields are empty
     *
     * @return false if text fields are not empty
     * @return true if text fields are empty
     */
    private fun textFieldsEmpty(): Boolean {
        if (binding.recipeTitle.text.toString() != ""
            && binding.cuisineType.text.toString() != ""
            && binding.recipeStory.text.toString() != ""
        ) {
            return false
        }
        return true
    }
}
