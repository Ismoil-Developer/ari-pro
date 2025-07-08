package uz.mrx.aripro.presentation.ui.screen.fragment.main.page

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.mrx.aripro.R
import uz.mrx.aripro.data.local.shp.MySharedPreference
import uz.mrx.aripro.data.model.LoadData
import uz.mrx.aripro.data.remote.websocket.WebSocketOrderEvent
import uz.mrx.aripro.databinding.PageHomeBinding
import uz.mrx.aripro.presentation.adapter.AssignedAdapter
import uz.mrx.aripro.presentation.adapter.LoadAdapter
import uz.mrx.aripro.presentation.ui.dialog.OrderTimeDialog
import uz.mrx.aripro.presentation.ui.viewmodel.homepage.HomePageViewModel
import uz.mrx.aripro.presentation.ui.viewmodel.homepage.impl.HomePageViewModelImpl
import javax.inject.Inject
import kotlin.math.log

@AndroidEntryPoint
class HomePage : Fragment(R.layout.page_home) {

    private val binding: PageHomeBinding by viewBinding(PageHomeBinding::bind)
    private val viewModel: HomePageViewModel by viewModels<HomePageViewModelImpl>()

    @Inject
    lateinit var sharedPref: MySharedPreference

    private var loadList = arrayListOf<LoadData>()
    private var active = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadData()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.deliveryWeeklyPriceResponse.collectLatest {
                binding.balance.text = it.totalPrice.toString()
            }
        }


        val adapterAssigned = AssignedAdapter {
            viewModel.openOrderDeliveryScreen(it.id)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.assignedResponse.collectLatest {

                adapterAssigned.submitList(it)
//                viewModel.getActiveOrder()
                Log.d("RRRRRRRR", "onViewCreated: ${it.map { it.items }}")
            }
        }

        binding.orderContainer.adapter = adapterAssigned

        Log.d("TTTTTT", "onViewCreated: ${sharedPref.token}")

        // Adapter setup
        val loadAdapter = LoadAdapter {
            viewModel.openHistoryDetailScreen(it.id)
        }


        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.historyResponse.collectLatest {
                if (it != null){

                    loadAdapter.submitList(it)

                }
            }
        }

        binding.rv.adapter = loadAdapter

        // Profilni olish va UI ni yangilash
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.profileResponse.collectLatest { profile ->
                binding.prfName.text = profile.full_name
                binding.prfDate.text = profile.deliver_id
                active = profile.work_active

                profile.avatar?.let { uri ->
                    Glide.with(requireContext())
                        .load(uri)
                        .apply(
                            RequestOptions().skipMemoryCache(true)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                        )
                        .into(binding.prfImage)
                }

                updateActiveUI(active)
                setupActiveInactiveToggle()
            }
        }

    }

    private fun loadData() {
        repeat(5) {
            loadList.add(
                LoadData(
                    R.drawable.ic_load,
                    "J002576877423000",
                    "Yetkazib berishda"
                )
            )
        }
    }


    // Faqat UI holatini yangilaydi
    private fun updateActiveUI(isActive: Boolean) {
        val activeLayout = binding.active
        val inactiveLayout = binding.inActive
        val activeImg = binding.activeImg
        val inactiveImg = binding.inActiveImg
        val activeTxt = binding.activeTxt
        val inactiveTxt = binding.inActiveTxt

        if (isActive) {
            activeLayout.setBackgroundResource(R.drawable.bg_green_rounded)
            activeImg.setColorFilter(ContextCompat.getColor(requireContext(), R.color.white))
            activeTxt.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

            inactiveLayout.setBackgroundColor(Color.TRANSPARENT)
            inactiveImg.setColorFilter(ContextCompat.getColor(requireContext(), R.color.black))
            inactiveTxt.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        } else {
            inactiveLayout.setBackgroundResource(R.drawable.bg_red_rounded)
            inactiveImg.setColorFilter(ContextCompat.getColor(requireContext(), R.color.white))
            inactiveTxt.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

            activeLayout.setBackgroundColor(Color.TRANSPARENT)
            activeImg.setColorFilter(ContextCompat.getColor(requireContext(), R.color.black))
            activeTxt.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        }
    }

    // Foydalanuvchi bosgandagina statusni o‘zgartiradi va ViewModelga yuboradi
    private fun setupActiveInactiveToggle() {
        binding.active.setOnClickListener(null)
        binding.inActive.setOnClickListener(null)

        if (active) {
            binding.inActive.setOnClickListener {
                active = false
                updateActiveUI(false)
                viewModel.postDeliveryActive()
                setupActiveInactiveToggle()
            }
        } else {
            binding.active.setOnClickListener {
                active = true
                updateActiveUI(true)
                viewModel.postDeliveryActive()
                setupActiveInactiveToggle()
            }
        }
    }

}
