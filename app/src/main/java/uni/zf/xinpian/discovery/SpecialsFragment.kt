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
    private val viewModel:DiscoverViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSpecialsBinding.inflate(inflater, container, false)
        setupRecyclerView()
        return binding.root
    }

    private fun setupRecyclerView() {
        binding.rvSpecials.layoutManager = GridLayoutManager(context, 2)
        val adapter = SpecialsAdapter()
        binding.rvSpecials.adapter = adapter
        lifecycleScope.launch {
            viewModel.specialDataFlow.collectLatest {
                adapter.submitData(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}