package uni.zf.xinpian.discovery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import uni.zf.xinpian.databinding.FragmentScheduleBinding

private val filters = listOf(
    "即将上映",
    "2026",
    "2025",
    "2024",
    "2023",
    "2022",
    "2021",
    "2020",
    "2019",
    "2018",
    "2017",
    "2016",
    "2015以前"
)

class ScheduleFragment : Fragment() {
    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupFilters()
        setupScheduleList()
    }

    private fun setupFilters() {
        binding.rvFilters.layoutManager = LinearLayoutManager(context, HORIZONTAL, false)
        binding.rvFilters.adapter = ScheduleFilterAdapter(filters)
    }

    private fun setupScheduleList() {
        val items = listOf(
            ScheduleItem.Year("2026"),
            ScheduleItem.Date("· 04月03日"),
            ScheduleItem.Movie("凌晨两点半3", "https://picsum.photos/300/450?random=20"),
            ScheduleItem.Date("· 02月20日"),
            ScheduleItem.Movie("熊出没·年年有熊", "https://picsum.photos/300/450?random=21"),
            ScheduleItem.Movie("镖人：风起大漠", "https://picsum.photos/300/450?random=22"),
            ScheduleItem.Movie("熊猫计划2", "https://picsum.photos/300/450?random=23"),
            ScheduleItem.Date("· 02月17日"),
            ScheduleItem.Movie("飞驰人生3", "https://picsum.photos/300/450?random=24")
        )

        val layoutManager = GridLayoutManager(context, 3)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (items[position]) {
                    is ScheduleItem.Movie -> 1
                    else -> 3 // Headers (Year/Date) take full width
                }
            }
        }

        binding.rvSchedule.layoutManager = layoutManager
        binding.rvSchedule.adapter = ScheduleAdapter(items)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

sealed class ScheduleItem {
    data class Year(val year: String) : ScheduleItem()
    data class Date(val date: String) : ScheduleItem()
    data class Movie(val title: String, val posterUrl: String) : ScheduleItem()
}