package uni.zf.xinpian.discovery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uni.zf.xinpian.databinding.FragmentSpecialsBinding
import kotlin.getValue

class SpecialsFragment : Fragment() {
    private var _binding: FragmentSpecialsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: SpecialsAdapter
    private val viewModel: DiscoverViewModel by viewModels()
    private var hasLoaded = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSpecialsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvSpecials.layoutManager = GridLayoutManager(context, 2)
        adapter = SpecialsAdapter()
        binding.rvSpecials.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData() {
        if (!hasLoaded) {
            lifecycleScope.launch {
                viewModel.specialDataFlow.collectLatest {
                    adapter.submitData(it)
                }
            }
            hasLoaded = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}