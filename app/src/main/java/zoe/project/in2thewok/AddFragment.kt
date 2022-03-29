package zoe.project.in2thewok

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
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

// TODO: Rename parameter arguments, choose names that match
//private const val ARG_PARAM1 = "param1"
//private const val ARG_PARAM2 = "param2"
private lateinit var communicator: Communicator
lateinit var auth: FirebaseAuth

/**
 * A simple [Fragment] subclass.
 * Use the [AddFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

// TODO: Don't upload photo to Firebase before the post is done? or delete off the database if user clears it/changes photo

// TODO: Check if regex is necessary, probably not


class AddFragment : Fragment() {
    // TODO: Rename and change types of parameters
//    private var param1: String? = null
//    private var param2: String? = null
    private lateinit var auth: FirebaseAuth
    private var imageUri : Uri? = null
    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!
    private val postCollectionRef = Firebase.firestore.collection("posts")
    private var storageRef = FirebaseStorage.getInstance().reference
    private var remoteUri: String? = null
    private val ingredients = ArrayList<String>()
    private val steps = ArrayList<String>()
    private var step = ""
    private var ingredient = ""

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
//        }
//    }
    private fun addIngredient(){
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

    private fun addPost(post: Post) = CoroutineScope(Dispatchers.IO).launch{
        val context = context?.applicationContext
        try {
            //            personCollectionRef.add(person).await()
            //            personCollectionRef.document(auth.currentUser?.uid.toString()).collection("posts").add(post).await()
            val ref = postCollectionRef.add(post)
                .await()
            val doc = ref.update("postID", ref.id)
            withContext(Dispatchers.Main) {//Dispatchers.Main sends to the UI
                Toast.makeText(context, "Successfully made post.", Toast.LENGTH_LONG).show()
                clearAll()
    //                binding.recipeTitle.text.clear()
    //                binding.cuisineType.text.clear()
    //                binding.recipeStory.text.clear()
    //                binding.tvUploadPhoto.text = "Add A Photo"
    //                binding.imgUpload.visibility = INVISIBLE
    //                binding.llSteps.removeAllViews()
    //                binding.llIngredients.removeAllViews()
    //                ingredients.removeAll(ingredients)
    //                steps.removeAll(steps)
    //                imageUri = null
    //                remoteUri = null
            }
            communicator.updatePostList()
        } catch(e: Exception){
            withContext(Dispatchers.Main) {//Dispatchers.Main sends to the UI
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun addStep(){
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

    private fun clearAll(){
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAddBinding.inflate(inflater, container, false)
        communicator = activity as Communicator

        if (!remoteUri.equals(null)) {
            Picasso.get()
                .load(remoteUri)
                .into(binding.imgUpload)
            binding.tvUploadPhoto.text = "Change Photo"
        }

        if(ingredients.isNotEmpty()){
            populateIngredients()
//            for(ingredient in ingredients){
//                val tv = TextView(context?.applicationContext)
//                tv.id = View.generateViewId()
//                tv.text = ingredient
//                binding.llIngredients.addView(tv)
//                val params = tv.layoutParams as LinearLayout.LayoutParams
//                params.width = MATCH_PARENT
//                params.height = WRAP_CONTENT
//                tv.requestLayout()
//            }
        }
        if(steps.isNotEmpty()){
            populateSteps()
//            for(step in steps){
//                val tv = TextView(context?.applicationContext)
//                tv.id = View.generateViewId()
//                tv.text = step
//                binding.llSteps.addView(tv)
//                val params = tv.layoutParams as LinearLayout.LayoutParams
//                params.width = MATCH_PARENT
//                params.height = WRAP_CONTENT
//                tv.requestLayout()
//            }
        }

        binding.btnUploadData.setOnClickListener{
            if(!textFieldsEmpty() && ingredients.isNotEmpty() && steps.isNotEmpty()){
                val post = Post(null, auth.currentUser?.uid.toString(), auth.currentUser?.displayName.toString(),
                    remoteUri, null, binding.recipeTitle.text.toString(), ingredients, binding.cuisineType.text.toString().lowercase(), steps, binding.recipeStory.text.toString(), arrayListOf())
                addPost(post)
            } else {
                Toast.makeText(context, "Please finish your post, photos are optional!", Toast.LENGTH_LONG).show()
            }
        }

        binding.btnAddIngred.setOnClickListener{
            addIngredient()
//            ingredient = binding.etAddIngredient.text.toString()
//            val tv = TextView(context?.applicationContext)
//            tv.id = View.generateViewId()
//            tv.text = ingredient
//            ingredients.add(ingredient)
//            binding.llIngredients.addView(tv)
//            val params = tv.layoutParams as LinearLayout.LayoutParams
//            params.width = MATCH_PARENT
//            params.height = WRAP_CONTENT
//            tv.requestLayout()
//            binding.etAddIngredient.text.clear()
        }

        binding.btnAddSteps.setOnClickListener{
            addStep()
//            step = binding.etAddStep.text.toString()
//            val tv = TextView(context?.applicationContext)
//            tv.id = View.generateViewId()
//            tv.text = step
//            steps.add(step)
//            binding.llSteps.addView(tv)
//            val params = tv.layoutParams as LinearLayout.LayoutParams
//            params.width = MATCH_PARENT
//            params.height = WRAP_CONTENT
//            tv.requestLayout()
//            binding.etAddStep.text.clear()
        }
        binding.btnClearAll.setOnClickListener{
            clearAll()
//            binding.recipeTitle.text.clear()
//            binding.cuisineType.text.clear()
//            binding.recipeStory.text.clear()
//            binding.tvUploadPhoto.text = "Add A Photo"
//            binding.imgUpload.visibility = INVISIBLE
//            binding.llSteps.removeAllViews()
//            binding.llIngredients.removeAllViews()
        }

        val getContent = registerForActivityResult(ActivityResultContracts.GetContent()){
                uri: Uri? ->
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

    override fun onStart() {
        super.onStart()
        auth = Firebase.auth
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun populateIngredients(){
        for(ingredient in ingredients){
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

    private fun populateSteps(){
        for(step in steps){
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

    private fun textFieldsEmpty() : Boolean{
        if (binding.recipeTitle.text.toString() != "" && binding.cuisineType.text.toString() != "" && binding.recipeStory.text.toString() != "" ){
            return false
        }
        return true
    }

//    companion object {
//        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            HomeFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
//    }
}