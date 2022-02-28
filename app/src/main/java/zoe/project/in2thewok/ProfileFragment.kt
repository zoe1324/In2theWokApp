package zoe.project.in2thewok

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
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
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val postCollectionRef = Firebase.firestore.collection("posts")
    private val personCollectionRef = Firebase.firestore.collection("people")
    private var posts = arrayListOf<Post>()

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
        return binding.root
    }

    private fun addPosts(posts: ArrayList<Post>) = CoroutineScope(Dispatchers.IO).launch{
        try{
            var current = binding.llQ4.id

            for(post in posts){
                withContext(Dispatchers.Main){
                    val cardView = context?.applicationContext?.let { CardView(it) }
                    val tvTitle = TextView(context?.applicationContext)
                    val imageView = ImageView(context?.applicationContext)
                    binding.clProfile.addView(cardView)
                    cardView?.addView(tvTitle)
                    cardView?.addView(imageView)

                    tvTitle.text = post.title
                    val textParams= tvTitle.layoutParams as ConstraintLayout.LayoutParams
                    val imgParams= imageView.layoutParams as ConstraintLayout.LayoutParams
                    val cardParams= cardView?.layoutParams as ConstraintLayout.LayoutParams

                    cardParams.startToStart = MATCH_PARENT
                    cardParams.endToEnd = MATCH_PARENT
                    cardParams.topToBottom = current
                    cardView.requestLayout()
                    cardParams.width = WRAP_CONTENT
                    cardParams.height = WRAP_CONTENT

                    cardView.id = View.generateViewId()
                    tvTitle.id = View.generateViewId()
                    imageView.id = View.generateViewId()

                    current = cardView.id

                    imageView.adjustViewBounds = true
                    imageView.maxHeight = 700
                    imageView.maxWidth = 700

                    textParams.startToStart = current
                    textParams.topToTop = current
                    textParams.bottomToTop = imageView.id
                    tvTitle.requestLayout()
                    textParams.width = WRAP_CONTENT
                    textParams.height = WRAP_CONTENT

                    imgParams.startToStart = current
                    imgParams.endToEnd = current
                    imgParams.topToBottom = tvTitle.id
                    imgParams.bottomToBottom = current

                    Picasso.get()
                        .load(post.imageURI)
                        .into(imageView)
                    cardView.isClickable = true
                    cardView.isFocusable = true

                }

            }
        } catch(e: Exception){

        }
    }

    override fun onStart() {
        super.onStart()
        auth = Firebase.auth
        setQuestionAnswers()
//        addPosts(posts)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    inner class RecyclerAdapter(private val postArray: ArrayList<Post>,
                                val layout: Int): RecyclerView.Adapter<ProfileFragment.ViewHolder>() {
//
//        private val cl: RecyclerViewClickListener =
//

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.updateItems(postArray[position])
        }

        override fun getItemCount(): Int {
            return postArray.size
        }

//    TODO: Look at explanation of this fix

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getItemViewType(position: Int): Int {
            return position
        }

//        inner class RecyclerViewClickListener(){
//            fun onClick(v: View, position: Int){
//
//            }
//        }

    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var recipeTitle: TextView = itemView.findViewById(R.id.tvRecipeTitle)
        var recipePhoto: ImageView = itemView.findViewById(R.id.ivRecipePhoto)
//        var cardView: CardView = itemView.findViewById(R.id.cvRecipe)

        fun updateItems(post: Post){
            recipeTitle.text = post.title.toString()
            Picasso.get()
                        .load(post.imageURI)
                        .into(recipePhoto)
//            cardView.setOnClickListener{
//                Toast.makeText(requireContext(), "clicked", Toast.LENGTH_LONG).show()
//            }
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