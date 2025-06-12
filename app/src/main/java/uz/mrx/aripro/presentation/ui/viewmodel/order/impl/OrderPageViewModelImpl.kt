package uz.mrx.aripro.presentation.ui.viewmodel.order.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import uz.mrx.aripro.presentation.direction.main.MainScreenDirection
import uz.mrx.aripro.presentation.ui.viewmodel.order.OrderPageViewModel
import javax.inject.Inject

@HiltViewModel
class OrderPageViewModelImpl @Inject constructor(private val direction:MainScreenDirection):OrderPageViewModel, ViewModel() {

    override fun openOrderDetailScreen() {

        viewModelScope.launch {
            direction.openOrderDetailScreen()
        }

    }

}