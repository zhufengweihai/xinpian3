package uni.zf.xinpian.discovery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import uni.zf.xinpian.databinding.FragmentRankingBinding
import uni.zf.xinpian.json.model.VideoData

class RankingFragment : Fragment() {
    private var _binding: FragmentRankingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRankingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        // Mock data for demonstration, in a real app this would come from a ViewModel
        val mockData = List(10) { i ->
            VideoData(
                title = when(i) {
                    0 -> "疯狂动物城2"
                    1 -> "浪浪人生"
                    2 -> "志愿军：浴血和平"
                    3 -> "震耳欲聋"
                    4 -> "惊天魔盗团3"
                    else -> "电影名称 $i"
                },
                description = "这是一段关于电影内容的简短描述，介绍剧情概要...",
                thumbnail = "https://picsum.photos/200/300?random=$i"
            )
        }
        
        binding.rvRanking.layoutManager = LinearLayoutManager(context)
        binding.rvRanking.adapter = RankingAdapter(mockData)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}