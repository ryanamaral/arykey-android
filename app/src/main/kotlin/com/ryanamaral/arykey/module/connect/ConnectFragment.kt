package com.ryanamaral.arykey.module.connect

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.ryanamaral.arykey.R
import com.ryanamaral.arykey.databinding.FragmentConnectBinding
import com.ryanamaral.arykey.di.IoDispatcher
import com.ryanamaral.arykey.di.MainImmediateDispatcher
import com.ryanamaral.arykey.module.root.withRootBottomSheet
import com.ryanamaral.arykey.common.flow.onClick
import com.ryanamaral.arykey.common.flow.throttleFirst
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject


@AndroidEntryPoint
class ConnectFragment : Fragment(R.layout.fragment_connect) {

    private var _binding: FragmentConnectBinding? = null
    private val binding: FragmentConnectBinding get() = _binding!!

    @Inject @IoDispatcher lateinit var ioDispatcher: CoroutineDispatcher
    @Inject @MainImmediateDispatcher lateinit var immediateDispatcher: CoroutineDispatcher


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConnectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListener()
    }

    private fun setClickListener() {
        binding.buttonSkip.onClick()
            .onEach {
                binding.lottieAnimationView.pauseAnimation()
                binding.lottieAnimationView.progress = 1f
            }
            .flowOn(immediateDispatcher) //^
            .throttleFirst()
            .debounce(100)
            .flowOn(ioDispatcher) //^
            .onEach { withRootBottomSheet { showIdFragment() } }
            .flowOn(immediateDispatcher) //^
            .launchIn(lifecycleScope)

        // dark theme was released in Android 10 (API level 29)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            // so we enable a shortcut to toggle the dark theme for previous versions
            binding.imageViewLogo.setOnClickListener {
                when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                    Configuration.UI_MODE_NIGHT_YES ->
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    Configuration.UI_MODE_NIGHT_NO ->
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
            }
        }
    }

    override fun onDestroyView() {
        binding.lottieAnimationView.cancelAnimation()
        _binding = null
        super.onDestroyView()
    }
}
