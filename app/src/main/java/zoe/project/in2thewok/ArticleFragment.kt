 package zoe.project.in2thewok

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import zoe.project.in2thewok.databinding.FragmentArticleBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ArticleFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ArticleFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentArticleBinding? = null
    private val binding get() = _binding!!
    private var titles = arrayListOf("1","2")
    private var details = arrayListOf("details of person icon", "details of home icon")
    private var images = arrayListOf(R.drawable.ic_person, R.drawable.ic_home)


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
        _binding = FragmentArticleBinding.inflate(inflater, container, false)
        binding.rvArticles.hasFixedSize()
        binding.rvArticles.layoutManager = LinearLayoutManager(context)
        binding.rvArticles.itemAnimator = DefaultItemAnimator()
        binding.rvArticles.adapter = RecyclerAdapter(titles, details, images, R.layout.layout_preview)





//        layoutManager = LinearLayoutManager(context)
//        var recyclerView = binding.rvArticles
//        recyclerView.layoutManager = layoutManager
//        adapter = RecyclerAdapter()
//        recyclerView.adapter = adapter
//        val wv = binding.webViewTest
//        wv.loadUrl("https://www.caribbeangreenliving.com/20-fun-facts-about-healthy-eating/")

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    inner class RecyclerAdapter(private val titles: ArrayList<String>, val details: ArrayList<String>, val images: ArrayList<Int>, val layout: Int): RecyclerView.Adapter<ArticleFragment.ViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//            holder.itemTitle.text = titles[position]
//            holder.itemDescription.text = details[position]
//            holder.itemImage.setImageResource(images[position])
            holder.updateItems(titles[position], details[position], images[position])
        }

        override fun getItemCount(): Int {
            return titles.size
        }

    }
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var itemImage: ImageView = itemView.findViewById(R.id.ivPreview)
        var itemTitle: TextView = itemView.findViewById(R.id.tvPreviewTitle)
        var itemDescription: TextView = itemView.findViewById(R.id.tvPreviewDescription)

        fun updateItems(title: String, detail: String, image: Int){
            itemTitle.text = title
            itemDescription.text = detail
            itemImage.setImageResource(image)
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