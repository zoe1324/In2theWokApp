package zoe.project.in2thewok

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils.replace
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import zoe.project.in2thewok.databinding.FragmentAddBinding
import java.net.URLEncoder.encode

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
lateinit var auth: FirebaseAuth
//Users could hold a collection of post ids only, that match up to an id that is in a separate posts collection

/**
 * A simple [Fragment] subclass.
 * Use the [AddFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddFragment : Fragment() {
    // TODO: Rename and change types of parameters
//    private var param1: String? = null
//    private var param2: String? = null
    private lateinit var auth: FirebaseAuth
    private var imageUri : Uri? = null
    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!
//    private val personCollectionRef = Firebase.firestore.collection("people")
    private val postCollectionRef = Firebase.firestore.collection("posts")
    private var storageRef = FirebaseStorage.getInstance().reference
    private var remoteUri: String? = null
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
//        }
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAddBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onStart() {
        super.onStart()
        auth = Firebase.auth
        val db = Firebase.firestore


        binding.btnUploadData.setOnClickListener{
            val caption = binding.caption.text.toString()
            val post = Post(auth.currentUser?.uid.toString(), auth.currentUser?.displayName.toString(),remoteUri?.toString(), caption)
            addPost(post)
//            val posterID = personCollectionRef.whereEqualTo("userID", auth.currentUser?.uid)
//            for(document in posterID.d){
//
//            }
//            val posterRef = Firebase.firestore.collection("people").document(posterID).collection("posts")
//            posterRef.add(post)
        }
        binding.btnUploadPhoto.setOnClickListener {
            selectImage()

        }
//        binding.btnUploadData.setOnClickListener{
//            val firstName = binding.etFirstName.text.toString()
//            val lastName = binding.etLastName.text.toString()
//            val age = binding.etAge.text.toString().toInt()
//            val person = Person(firstName, lastName, age)
//            savePerson(person)
//        }

//        binding.btnRetrieveData.setOnClickListener{
//            retrievePersons()
//        }
    }

    private fun selectImage() {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            getResult.launch(intent)
//        startActivityForResult(intent, 100)
        }

    private val getResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { it ->
            if(it.resultCode == RESULT_OK){
                imageUri = it.data?.data!!
                var user = auth.currentUser
                val re = Regex("[^A-Za-z0-9 ]")
                var uriLastSeg = imageUri!!.lastPathSegment
                uriLastSeg = uriLastSeg?.let { it1 -> re.replace(it1, "") }
                val imageRef = storageRef.child("images/" + user?.uid + "/" + uriLastSeg)
                val upload = imageRef.putFile(imageUri!!)
//                remoteUri = downloadUrl.toString()
                upload.addOnSuccessListener {
                    val downloadUrl = imageRef.downloadUrl
                    downloadUrl.addOnSuccessListener {
                        remoteUri = it.toString()
                    }

                }
                binding.imgUpload.setImageURI(imageUri)
            }
        }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun addPost(post: Post) = CoroutineScope(Dispatchers.IO).launch{
        val context = context?.applicationContext
        try {
            //            personCollectionRef.add(person).await()
            //            personCollectionRef.document(auth.currentUser?.uid.toString()).collection("posts").add(post).await()
            postCollectionRef.add(post).await()

            withContext(Dispatchers.Main) {//Dispatchers.Main sends to the UI
                Toast.makeText(context, "Successfully made post.", Toast.LENGTH_LONG).show()
                binding.caption.editableText.clear()
            }
        } catch(e: Exception){
            withContext(Dispatchers.Main) {//Dispatchers.Main sends to the UI
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }
//    private fun retrievePersons() = CoroutineScope(Dispatchers.IO).launch{
//        try{
//            val querySnapshot = personCollectionRef.get().await()
//            val sb = StringBuilder()
//            for(document in querySnapshot.documents){
//                val person = document.toObject<Person>()
//                sb.append("$person\n") //append person followed by new line
//            }
//            // set string $ text to textview,
//            // so switch the co-routine context as UI can only be modified inside Main dispatchers
//            withContext(Dispatchers.Main){
//                val tvPersons = binding.tvPersons
//                tvPersons.text = sb.toString()
//            }
//        } catch (e: Exception){
//            withContext(Dispatchers.Main){
//                Toast.makeText(home, e.message, Toast.LENGTH_LONG).show()
//            }
//        }
//    }


//    companion object {
//        /**
//         * Use this factory method to create a new instance of
//         * this fragment using the provided parameters.
//         *
//         * @param param1 Parameter 1.
//         * @param param2 Parameter 2.
//         * @return A new instance of fragment HomeFragment.
//         */
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