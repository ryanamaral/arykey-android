package com.ryanamaral.arykey.module.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.airbnb.lottie.LottieDrawable
import com.davidmiguel.numberkeyboard.NumberKeyboardListener
import com.mukesh.OnOtpCompletionListener
import com.ryanamaral.arykey.databinding.FragmentAuthBinding
import com.ryanamaral.arykey.di.IoDispatcher
import com.ryanamaral.arykey.module.root.withRootBottomSheet
import com.ryanamaral.arykey.common.domain.Result
import com.ryanamaral.arykey.common.extension.addAnimationListener
import com.ryanamaral.arykey.module.id.IdItem
import com.ryanamaral.arykey.module.usb.UsbRepository
import com.ryanamaral.arykey.module.usb.UsbWriteResult
import com.ryanamaral.arykey.withMainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class AuthFragment : Fragment(), NumberKeyboardListener, OnOtpCompletionListener {

    private var _binding: FragmentAuthBinding? = null
    private val binding get() = _binding!!
    private val authViewModel by viewModels<AuthViewModel>()
    @Inject @IoDispatcher lateinit var ioDispatcher: CoroutineDispatcher
    @Inject lateinit var usbRepository: UsbRepository


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        setClickListeners()
    }

    private fun initToolbar() {
        binding.toolbar.apply {
            title = ""
            setNavigationOnClickListener {
                withRootBottomSheet { goBack() }
            }
        }
    }

    private fun setClickListeners() {
        binding.numberKeyboard.setListener(this)
        binding.otpView.apply {
            isCursorVisible = false
            setAnimationEnable(true)
            setHideLineWhenFilled(true)
            setOtpCompletionListener(this@AuthFragment)
            showSoftInputOnFocus = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * TODO: send elements (pin + app + username) to view model to be hashed and sent out
     */
    override fun onOtpCompleted(pin: String) {
        //if (usbRepository.isUSBReady && usbRepository.isConnect.value == true) { }

        arguments?.takeIf { it.containsKey(EXTRA_ID_ITEM_PARCELABLE) }?.apply {
            getParcelable<IdItem?>(EXTRA_ID_ITEM_PARCELABLE)?.let { idItem ->
                Timber.d(idItem.toString())
                setupSendMessageTask()
                authViewModel.startSendMessageTask(idItem, pin)
            }
        }
    }

    override fun onLeftAuxButtonClicked() {
        // do nothing
    }

    override fun onNumberClicked(number: Int) {
        binding.otpView.setText("${binding.otpView.text}${number}")
    }

    override fun onRightAuxButtonClicked() {
        binding.otpView.setText("")
    }

    private fun setupSendMessageTask() {
        authViewModel.status.observe(this, Observer<Result<UsbWriteResult>> {
            when (it) {
                Result.Loading -> {
                    Timber.d("Loading")
                    binding.contentLayout.isVisible = false
                    binding.lottieAnimationView.apply {
                        // loop between start and end frame
                        setMinAndMaxFrame(0, 237) // loading frames
                        repeatCount = LottieDrawable.INFINITE
                        repeatMode = LottieDrawable.RESTART
                        isVisible = true
                        playAnimation()
                    }
                }
                is Result.Success<UsbWriteResult> -> {
                    Timber.d("Success[data=${it.data}]")
                    binding.contentLayout.isVisible = false
                    binding.lottieAnimationView.apply {
                        pauseAnimation()
                        // go from start to end frame
                        setMinAndMaxFrame(232, 416) // success frames
                        repeatCount = 0
                        isVisible = true
                        addAnimationListener(onAnimationEnd = {
                            // close app, job done
                            withMainActivity { finishApp() }
                        })
                        playAnimation()
                    }
                }
                is Result.Error -> {
                    Timber.e("Error[exception=${it.error}]")
                    binding.contentLayout.isVisible = false
                    binding.lottieAnimationView.apply {
                        pauseAnimation()
                        // go from start to end frame
                        setMinAndMaxFrame(539, 841) // error frames
                        repeatCount = 0
                        isVisible = true
                        addAnimationListener(onAnimationEnd = {
                            //todo: show snack bar
                            //  USB Communication Error. Please try again.
                            binding.otpView.setText("")
                            isVisible = false
                            binding.contentLayout.isVisible = true
                        })
                        playAnimation()
                    }
                }
            }
        })
    }

    companion object {
        private const val EXTRA_ID_ITEM_PARCELABLE = "extra_id_item_parcelable"

        fun newInstance(idItem: IdItem): AuthFragment {
            return AuthFragment().apply {
                arguments = Bundle().apply { putParcelable(EXTRA_ID_ITEM_PARCELABLE, idItem) }
            }
        }
    }
}
