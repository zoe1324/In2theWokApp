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
import zoe.project.in2thewok.databinding.FragmentInfoBinding

@Suppress("EmptyFunctionBlock")

/**
 * A simple [Fragment] subclass.
 * Use the [InfoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InfoFragment : Fragment() {
    private var _binding: FragmentInfoBinding? = null
    private val binding get() = _binding!!
    private var healthArticles = arrayListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentInfoBinding.inflate(inflater, container, false)
        binding.rvInfo.hasFixedSize()
        binding.rvInfo.layoutManager = LinearLayoutManager(context)
        binding.rvInfo.itemAnimator = DefaultItemAnimator()
        healthArticles = (activity as? HomeActivity)!!.healthArticles
        binding.rvInfo.adapter = RecyclerAdapter(healthArticles, R.layout.layout_preview)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    inner class RecyclerAdapter(val linkArray: ArrayList<String>,
                                val layout: Int): RecyclerView.Adapter<InfoFragment.ViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.updateItems(linkArray[position])
        }

        override fun getItemCount(): Int {
            return linkArray.size
        }

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
}
