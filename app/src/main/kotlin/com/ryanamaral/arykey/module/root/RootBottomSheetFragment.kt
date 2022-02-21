package com.ryanamaral.arykey.module.root

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ryanamaral.arykey.R
import com.ryanamaral.arykey.common.flow.stateChanges
import com.ryanamaral.arykey.common.transition.BottomSheetSharedTransition
import com.ryanamaral.arykey.databinding.FragmentBottomSheetBinding
import com.ryanamaral.arykey.module.auth.AuthFragment
import com.ryanamaral.arykey.module.connect.ConnectFragment
import com.ryanamaral.arykey.module.id.IdFragment
import com.ryanamaral.arykey.module.id.IdItem
import com.ryanamaral.arykey.withMainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


@AndroidEntryPoint
class RootBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentBottomSheetBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)
    }

    override fun getTheme(): Int = R.style.AppBottomSheetDialogTheme

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : BottomSheetDialog(requireContext(), theme) {
            override fun onBackPressed() {
                withMainActivity { finishApp() }
            }
        }.also { dialog ->
            dialog.setOnShowListener {
                dialog.setupBehavior()
                dialog.setupTouchOutside()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomSheetBinding.inflate(LayoutInflater.from(context))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState == null) {
            setInitialFragment(ConnectFragment())
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    withMainActivity { finishApp() }
                }
            })
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun BottomSheetDialog.setupTouchOutside() {
        (findViewById(com.google.android.material.R.id.coordinator) as? CoordinatorLayout)?.apply {
            findViewById<View>(com.google.android.material.R.id.touch_outside)
                .setOnClickListener { if (isShowing) minimiseApp() }
        }
    }

    /**
     * Minimise the app and go to the android home activity
     */
    private fun minimiseApp() {
        startActivity(Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        })
    }

    private fun BottomSheetDialog.setupBehavior() {
        (findViewById(com.google.android.material.R.id.design_bottom_sheet) as? FrameLayout)?.let { view ->
            BottomSheetBehavior.from<View>(view).apply {
                state = BottomSheetBehavior.STATE_EXPANDED
                isHideable = true
                skipCollapsed = true
            }
            observerBottomSheetBehavior(view)
        }
    }

    private fun observerBottomSheetBehavior(frameLayout: FrameLayout) {
        frameLayout.stateChanges()
            .dropInitialValue()
            .onEach { state ->
                if (state == BottomSheetBehavior.STATE_HIDDEN) {
                    withMainActivity { finishApp() }
                }
            }
            .flowWithLifecycle(lifecycle)
            .launchIn(lifecycleScope)
    }

    fun showIdFragment() = performFragmentTransition(IdFragment())

    fun showAuthFragment(idItem: IdItem?) {
        val authFragment = if (idItem != null) AuthFragment.newInstance(idItem) else AuthFragment()
        performFragmentTransition(authFragment)
    }

    private fun setInitialFragment(fragment: Fragment) {
        childFragmentManager
            .beginTransaction()
            .add(R.id.container, fragment)
            .addToBackStack(fragment::class.java.simpleName)
            .commit()
    }

    private fun performFragmentTransition(newFragment: Fragment) {
        val currentFragmentRoot = childFragmentManager.fragments[0].requireView()
        childFragmentManager
            .beginTransaction()
            .apply {
                addSharedElement(currentFragmentRoot, currentFragmentRoot.transitionName)
                setReorderingAllowed(true)
                newFragment.sharedElementEnterTransition = BottomSheetSharedTransition()
            }
            .replace(R.id.container, newFragment)
            .addToBackStack(newFragment.javaClass.name)
            .commit()
    }

    fun goBack() {
        childFragmentManager.popBackStack()
    }
}

fun Fragment.withRootBottomSheet(action: RootBottomSheetFragment.() -> Unit) {
    (parentFragment as? RootBottomSheetFragment)?.run(action)
}
