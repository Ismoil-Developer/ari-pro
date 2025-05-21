package uz.mrx.aripro.utils

import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow

fun <T> flow() = MutableSharedFlow<T>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

fun Fragment.toast(it: String) =
    Toast.makeText(this.requireContext(), it, Toast.LENGTH_SHORT).show()
