package uz.mrx.aripro.presentation.ui.screen.fragment.main.page

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import uz.mrx.aripro.R
import uz.mrx.aripro.databinding.PageConfirmBinding

@AndroidEntryPoint
class ConfirmPage:Fragment(R.layout.page_confirm) {

    private val binding:PageConfirmBinding by viewBinding(PageConfirmBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}