package uz.mrx.aripro.presentation.ui.viewmodel.order.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import uz.mrx.aripro.presentation.direction.order.QrScannerFragmentDirection
import uz.mrx.aripro.presentation.ui.viewmodel.order.QrScannerFragmentViewModel
import javax.inject.Inject

@HiltViewModel
class QrScannerFragmentViewModelImpl @Inject constructor(private val direction: QrScannerFragmentDirection):QrScannerFragmentViewModel, ViewModel() {

    override fun openDeliveryScreen() {
        viewModelScope.launch {
            direction.openDeliveryScreen()
        }
    }

}