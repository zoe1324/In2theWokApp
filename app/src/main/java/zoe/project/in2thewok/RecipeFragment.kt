package zoe.project.in2thewok

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import zoe.project.in2thewok.databinding.FragmentRecipeBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RecipeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
// TODO: Bookmark button functionality
// TODO: Add a comment functionality, will involve maybe a dialogue box??/alertbox or add a text box below and button,
//  will need to update the recyclerview for comments somehow?

class RecipeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentRecipeBinding? = null
    private val binding get() = _binding!!
    private val postCollectionRef = Firebase.firestore.collection("posts")
    private var ingredients: ArrayList<String> = arrayListOf()
    private var steps: ArrayList<String> = arrayListOf()
    private var comments: ArrayList<String> = arrayListOf()
    var recipeTitle: String? = null
    var imageURI: String? = null
    var story: String? = null
    var postID: String? = null
    var userID: String? = null
    var username: String? = null
    var cuisineType: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
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
        if (userID != null){
           val userCollectionRef = Firebase.firestore.collection("people").document(userID!!).collection("bookmarks")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRecipeBinding.inflate(inflater, container, false)
//        val post = displayMessage.title
        binding.tvRecipeTitle.text = recipeTitle.toString()
        binding.tvRecipeCuisineType.text = cuisineType.toString()
        binding.tvRecipeStory.text = story.toString()
        if (!imageURI.equals(null)) {
            Picasso.get()
                .load(imageURI)
                .into(binding.ivRecipePhoto)
        }
        else{
            Picasso.get()
                .load(R.drawable.cooking)
                .into(binding.ivRecipePhoto)
        }
        if(comments.isEmpty()){
            comments.add("no comments yet!")
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
        return binding.root
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
//            currentPost = postArray[position]
//            holder.init(postArray[position])
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
//        var recipeType: TextView = itemView.findViewById(R.id.tvRecipeCuisineType)
//        var recipePhoto: ImageView = itemView.findViewById(R.id.ivRecipePhoto)

//        fun init(post: Post){
//            itemView.setOnClickListener{
//                Toast.makeText(itemView.context, "clicked on ${recipeTitle.text}", Toast.LENGTH_LONG).show()
//                communicator.recipePassComm(post)
//            }
//        }

        fun updateItems(string: String){
            inputString.text = string
//            recipeTitle.text = post.title.toString()
//            recipeType.text = post.cuisineType.toString()
//            if (post.imageURI != null) {
//                Picasso.get()
//                    .load(post.imageURI)
//                    .into(recipePhoto)
//            }
//            else{
//                Picasso.get()
//                    .load(R.drawable.cooking)
//                    .into(recipePhoto)
//            }
        }

    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RecipeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RecipeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}