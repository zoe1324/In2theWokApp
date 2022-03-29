package zoe.project.in2thewok

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.ponnamkarthik.richlinkpreview.RichLinkView
import io.github.ponnamkarthik.richlinkpreview.ViewListener
import zoe.project.in2thewok.databinding.FragmentArticleBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//private const val ARG_PARAM1 = "param1"
//private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ArticleFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ArticleFragment : Fragment() {
    // TODO: Rename and change types of parameters
//    private var param1: String? = null
//    private var param2: String? = null
    private var _binding: FragmentArticleBinding? = null
    private val binding get() = _binding!!
    private var articles = arrayListOf<String>()


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
        _binding = FragmentArticleBinding.inflate(inflater, container, false)
        binding.rvArticles.hasFixedSize()
        binding.rvArticles.layoutManager = LinearLayoutManager(context)
        binding.rvArticles.itemAnimator = DefaultItemAnimator()
        articles = (activity as? HomeActivity)!!.articles
        binding.rvArticles.adapter = RecyclerAdapter(articles, R.layout.layout_preview)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    //    inner class RecyclerAdapter(private val titles: ArrayList<String>, val details: ArrayList<String>, val images: ArrayList<Int>, val layout: Int): RecyclerView.Adapter<ArticleFragment.ViewHolder>(){
    inner class RecyclerAdapter(val linkArray: ArrayList<String>, val layout: Int): RecyclerView.Adapter<ArticleFragment.ViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//            holder.itemTitle.text = titles[position]
//            holder.itemDescription.text = details[position]
//            holder.itemImage.setImageResource(images[position])
            holder.updateItems(linkArray[position])
        }

        override fun getItemCount(): Int {
            return linkArray.size
        }

//    TODO: Look at explanation of this fix

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getItemViewType(position: Int): Int {
            return position
        }

    }
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var urlPreview: RichLinkView = itemView.findViewById(R.id.richLinkView)

        fun updateItems(title: String){
            urlPreview.setLink(title, object : ViewListener{
                override fun onSuccess(status: Boolean) {}
                override fun onError(e: Exception?) {}
            })

        }

    }

//    companion object {
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