package zoe.project.in2thewok

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import zoe.project.in2thewok.databinding.FragmentProfileBinding

/**
 * [ProfileFragment], A [Fragment] class displayed by [HomeActivity],
 * linked via [FragmentProfileBinding].
 *
 * @constructor Creates a new Fragment
 * @suppress TooGenericExceptionCaught
 */
@Suppress("TooGenericExceptionCaught")

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val personCollectionRef = Firebase.firestore.collection("people")
    private var posts = arrayListOf<Post>()
    private lateinit var communicator: Communicator
    private var auth = Firebase.auth

    /**
     * Creates View for [ProfileFragment]
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
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        binding.rvUserPosts.hasFixedSize()
        binding.rvUserPosts.layoutManager = LinearLayoutManager(context)
        binding.rvUserPosts.itemAnimator = DefaultItemAnimator()
        posts = (activity as? HomeActivity)!!.posts
        binding.rvUserPosts.adapter = RecyclerAdapter(posts, R.layout.layout_recipe)
        communicator = activity as Communicator
        return binding.root
    }

    /**
     * Sets the Firebase Authentication instance whenever the Fragment is started,
     * and sets the user's questionnaire answers
     */
    override fun onStart() {
        super.onStart()
        auth = Firebase.auth
        setQuestionAnswers()
    }

    /**
     * Destroys the Fragment View so it is no longer visible
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * [RecyclerAdapter] customised class, adapts arrayList of Post instances to fit CardView items
     *
     * @param postArray The arrayList of Posts being adapted
     * @param layout The layout in which each post should be adapted to fit
     * @constructor Creates a new Adapter for the RecyclerView
     */
    inner class RecyclerAdapter(
        private val postArray: ArrayList<Post>,
        val layout: Int
    ) : RecyclerView.Adapter<ProfileFragment.ViewHolder>() {

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
            holder.init(postArray[position])
            holder.updateItems(postArray[position])
        }

        /**
         * Returns the size of the arrayList being adapted
         *
         * @return postArray.size
         */
        override fun getItemCount(): Int {
            return postArray.size
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
        var recipeTitle: TextView = itemView.findViewById(R.id.tvRecipeTitle)
        var recipeType: TextView = itemView.findViewById(R.id.tvRecipeDetails)
        var recipePhoto: ImageView = itemView.findViewById(R.id.ivRecipePhoto)

        /**
         * Initialises each item click listener within the RecyclerView
         *
         * @param post The current post in the list being passed in
         */
        fun init(post: Post) {
            itemView.setOnClickListener {
                communicator.recipePassComm(post)
            }
        }

        /**
         * Updates each RecyclerView item to display the content contained within the post instance,
         * uses Picasso library to display images
         *
         * @param post The current post in the list being passed in
         */
        fun updateItems(post: Post) {
            recipeTitle.text = post.title.toString()
            recipeType.text = post.cuisineType.toString()
            if (post.imageURI != null) {
                Picasso.get()
                    .load(post.imageURI)
                    .into(recipePhoto)
            } else {
                Picasso.get()
                    .load(R.drawable.cooking)
                    .into(recipePhoto)
            }
        }
    }

    /**
     * Sets the user question answer TextViews to hold the values of each questionnaire answer
     * associated with the current user. Uses CoRoutines to asynchronously display text.
     *
     * @throws Exception if db retrieval fails
     */
    private fun setQuestionAnswers() = CoroutineScope(Dispatchers.IO).launch {
        try {
            val tvA1 = binding.tvA1
            val tvA2 = binding.tvA2
            val tvA3 = binding.tvA3
            val tvA4 = binding.tvA4
            val querySnapshot = personCollectionRef
                .whereEqualTo("userID", auth.currentUser?.uid)
                .get()
                .await()
            val a1 = StringBuilder()
            val a2 = StringBuilder()
            val a3 = StringBuilder()
            val a4 = StringBuilder()
            for (document in querySnapshot.documents) {
                val person = document.toObject<Person>()
                a1.append(person?.q1)
                a2.append(person?.q2)
                a3.append(person?.q3)
                a4.append(person?.q4)
            }

            withContext(Dispatchers.Main) {
                tvA1.text = a1.toString()
                tvA2.text = a2.toString()
                tvA3.text = a3.toString()
                tvA4.text = a4.toString()
            }
        } catch (e: Exception) {
            val context = context?.applicationContext
            withContext(Dispatchers.Main) {
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }
}
