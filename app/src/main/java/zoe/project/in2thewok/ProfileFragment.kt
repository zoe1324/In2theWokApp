package zoe.project.in2thewok

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import io.github.ponnamkarthik.richlinkpreview.RichLinkView
import io.github.ponnamkarthik.richlinkpreview.ViewListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import zoe.project.in2thewok.databinding.FragmentProfileBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

// TODO: Look at explanation of recyclerview fix
// TODO: Add a 'My Bookmarks' view to switch between posts & bookmarked posts
// TODO: Show user they have no bookmarks yet if there are none, and not just a blank screen?
// TODO: Edit your favourites?? Or at least add some if the user skips that stage to start with

class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val personCollectionRef = Firebase.firestore.collection("people")
    private var posts = arrayListOf<Post>()
    private var bookmarked = arrayListOf<Post>()
    private lateinit var communicator: Communicator
    private var auth = Firebase.auth
//    private lateinit var currentPost: Post

//    interface FragmentProfileListener{
//        fun onRecipeSent(post: Post)
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

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

    override fun onStart() {
        super.onStart()
        auth = Firebase.auth
        setQuestionAnswers()
//        posts = (activity as? HomeActivity)!!.posts
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

//    override fun onAttach(context: Context){
//        super.onAttach(context)
//        if (context is FragmentProfileListener){
//            listener = (FragmentProfileListener) context
//        } else {
//            throw RuntimeException(context.toString() + "must implement FragmentProfileListener")
//        }
//    }

    inner class RecyclerAdapter(private val postArray: ArrayList<Post>,
                                val layout: Int): RecyclerView.Adapter<ProfileFragment.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//            currentPost = postArray[position]
            holder.init(postArray[position])
            holder.updateItems(postArray[position])
        }

        override fun getItemCount(): Int {
            return postArray.size
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getItemViewType(position: Int): Int {
            return position
        }

    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var recipeTitle: TextView = itemView.findViewById(R.id.tvRecipeTitle)
        var recipeType: TextView = itemView.findViewById(R.id.tvRecipeDetails)
        var recipePhoto: ImageView = itemView.findViewById(R.id.ivRecipePhoto)

       fun init(post: Post){
            itemView.setOnClickListener{
                Toast.makeText(itemView.context, "clicked on ${recipeTitle.text}", Toast.LENGTH_LONG).show()
                communicator.recipePassComm(post)
            }
        }

        fun updateItems(post: Post){
            recipeTitle.text = post.title.toString()
            recipeType.text = post.cuisineType.toString()
            if (post.imageURI != null) {
                Picasso.get()
                    .load(post.imageURI)
                    .into(recipePhoto)
            }
            else{
                Picasso.get()
                    .load(R.drawable.cooking)
                    .into(recipePhoto)
            }
        }

    }

    //Add functionality for the TextViews to change to users answers
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
//                sb.append("$person\n") //append person followed by new line
            }
            // set string $ text to textview,
            // so switch the co-routine context as UI can only be modified inside Main dispatchers
            withContext(Dispatchers.Main) {
                tvA1.text = a1.toString()
                tvA2.text = a2.toString()
                tvA3.text = a3.toString()
                tvA4.text = a4.toString()
//                tvPosts.text = sb.toString()
            }
        } catch (e: Exception) {
            val context = context?.applicationContext
            withContext(Dispatchers.Main) {
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}