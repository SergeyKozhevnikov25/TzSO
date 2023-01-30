package space.game.android.tzsantehnikaonline.presentation.info

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.yandex.mapkit.geometry.Point
import space.game.android.tzsantehnikaonline.R
import space.game.android.tzsantehnikaonline.databinding.FragmentInfoBinding
import space.game.android.tzsantehnikaonline.presentation.map.MapFragment

class InfoFragment : Fragment() {


    private var _binding: FragmentInfoBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException("FragmentInfoBinding = null")

    private lateinit var viewModel: InfoViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentInfoBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[InfoViewModel::class.java]
        viewModel.point.observe(viewLifecycleOwner) {
            if (it != null) {
                setPointInfo()
            }
        }

        binding.btnShowOnMap.setOnClickListener {
            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, MapFragment.newInstance(viewModel.point.value))
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onResume() {
        val lat = arguments?.getDouble(LATITUDE_ARG)
        val long = arguments?.getDouble(LONGITUDE_ARG)
        if (long != null && lat != null) {
            viewModel.setPoint(Point(lat, long))
        }
        super.onResume()
    }

    @SuppressLint("StringFormatMatches")
    private fun setPointInfo() {
        binding.tvInfo.text =
            getString(R.string.point_info_text, viewModel.getLatitude(), viewModel.getLongitude())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    companion object {
        const val LATITUDE_ARG = "LATITUDE_ARG"
        const val LONGITUDE_ARG = "LONGITUDE_ARG"

        fun newInstance(location: Point?): InfoFragment {
            return if (location == null) {
                InfoFragment()
            } else {
                InfoFragment().apply {
                    arguments = bundleOf(
                        LATITUDE_ARG to location.latitude,
                        LONGITUDE_ARG to location.longitude
                    )
                }
            }
        }
    }
}