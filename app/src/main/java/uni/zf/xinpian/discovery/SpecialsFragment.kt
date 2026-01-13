package uni.zf.xinpian.discovery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import uni.zf.xinpian.databinding.FragmentSpecialsBinding

class SpecialsFragment : Fragment() {
    private var _binding: FragmentSpecialsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSpecialsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val mockData = listOf(
            SpecialItem("杨门女将系列电影专辑", "https://picsum.photos/400/225?random=10"),
            SpecialItem("豆瓣2025评分最高纪录剧集", "https://picsum.photos/400/225?random=11"),
            SpecialItem("豆瓣2025评分最高动画剧集", "https://picsum.photos/400/225?random=12"),
            SpecialItem("豆瓣2025最受关注综艺", "https://picsum.photos/400/225?random=13"),
            SpecialItem("豆瓣2025评分最高英美新剧", "https://picsum.photos/400/225?random=14"),
            SpecialItem("豆瓣2025评分最高华语剧集", "https://picsum.photos/400/225?random=15")
        )
        
        binding.rvSpecials.layoutManager = GridLayoutManager(context, 2)
        binding.rvSpecials.adapter = SpecialsAdapter(mockData)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}