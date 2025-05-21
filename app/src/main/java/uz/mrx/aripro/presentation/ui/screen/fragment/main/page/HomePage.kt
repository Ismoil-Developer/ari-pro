package uz.mrx.aripro.presentation.ui.screen.fragment.main.page

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.mrx.aripro.R
import uz.mrx.aripro.data.local.shp.MySharedPreference
import uz.mrx.aripro.data.model.LoadData
import uz.mrx.aripro.data.remote.websocket.WebSocketOrderEvent
import uz.mrx.aripro.databinding.PageHomeBinding
import uz.mrx.aripro.presentation.adapter.LoadAdapter
import uz.mrx.aripro.presentation.ui.dialog.OrderTimeDialog
import uz.mrx.aripro.presentation.ui.viewmodel.homepage.HomePageViewModel
import uz.mrx.aripro.presentation.ui.viewmodel.homepage.impl.HomePageViewModelImpl
import javax.inject.Inject

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

        // Websocket ulanish
        viewModel.connectWebSocket("ws://ari.uzfati.uz/ws/pro/connect/", sharedPref.token)

        // Orderni kuzatish
        observeIncomingOrders()

        // Adapter setup
        val loadAdapter = LoadAdapter {
            viewModel.openOrderDetailScreen()
        }
        loadAdapter.submitList(loadList)
        binding.rv.adapter = loadAdapter

        // Profilni olish va UI ni yangilash
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.profileResponse.collectLatest { profile ->
                binding.prfName.text = profile.full_name
                binding.prfDate.text = profile.deliver_id
                active = profile.work_active

                updateActiveUI(active)
                setupActiveInactiveToggle()
            }
        }

        // Buyurtma olinganda navigatsiya qilish
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.orderTaken.collectLatest {
                if (it.orderId != -1) {
                    viewModel.openOrderDeliveryScreen()
                }
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

    private fun observeIncomingOrders() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.incomingOrders.collectLatest { newOrder ->
                showNewOrderDialog(newOrder)
            }
        }
    }

    private fun showNewOrderDialog(newOrder: WebSocketOrderEvent.NewOrder) {
        val orderDesc = newOrder.orderItems
        val orderPrice = newOrder.price.toString()

        val orderTimeDialog = OrderTimeDialog(
            requireContext(),
            orderDesc,
            orderPrice,
            onAcceptClickListener = {
                viewModel.acceptOrder(newOrder.orderId)
                Toast.makeText(requireContext(), "Zakaz qabul qilindi", Toast.LENGTH_SHORT).show()
            },
            onSkipClickListener = {
                viewModel.rejectOrder(newOrder.orderId)
                Toast.makeText(requireContext(), "Zakaz rad etildi", Toast.LENGTH_SHORT).show()
            }
        )

        orderTimeDialog.show()
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
