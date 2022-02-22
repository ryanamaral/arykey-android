package com.ryanamaral.arykey.module.id

import android.Manifest
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Animatable
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import coil.ImageLoader
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.ryanamaral.arykey.R
import com.ryanamaral.arykey.databinding.FragmentIdBinding
import com.ryanamaral.arykey.di.IoDispatcher
import com.ryanamaral.arykey.di.MainImmediateDispatcher
import com.ryanamaral.arykey.module.account.adapter.AccountAdapter
import com.ryanamaral.arykey.module.account.adapter.loadImageResource
import com.ryanamaral.arykey.module.account.model.AccountItem
import com.ryanamaral.arykey.module.account.viewmodel.AccountViewModel
import com.ryanamaral.arykey.common.permission.hasPermissions
import com.ryanamaral.arykey.common.permission.shouldShowPermissionRationale
import com.ryanamaral.arykey.module.apps.adapter.AppsAdapter
import com.ryanamaral.arykey.module.apps.model.AppItem
import com.ryanamaral.arykey.module.apps.viewmodel.AppsViewModel
import com.ryanamaral.arykey.module.root.withRootBottomSheet
import com.ryanamaral.arykey.common.domain.Result
import com.ryanamaral.arykey.common.view.addDismissListener
import com.ryanamaral.arykey.common.view.removeBottomPadding
import com.ryanamaral.arykey.common.view.setKeyboardCloseAndClearFocus
import com.ryanamaral.arykey.common.flow.onClick
import com.ryanamaral.arykey.common.flow.throttleFirst
import com.ryanamaral.arykey.module.packagename.data.PackageRepository
import com.ryanamaral.arykey.module.packagename.util.isAccessibilityServiceEnabled
import com.ryanamaral.arykey.module.usb.UsbRepository
import com.ryanamaral.arykey.module.usb.UsbState
import com.ryanamaral.arykey.withMainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class IdFragment : Fragment() {

    private var _binding: FragmentIdBinding? = null
    private val binding get() = _binding!!

    @Inject @IoDispatcher lateinit var ioDispatcher: CoroutineDispatcher
    @Inject @MainImmediateDispatcher lateinit var immediateDispatcher: CoroutineDispatcher

    // Data store from which to receive package updates via Flow, injected via Hilt
    @Inject lateinit var repository: PackageRepository
    // get a reference to the Job from the Flow so we can stop it from UI events
    private var packageFlow: Job? = null
    @Inject lateinit var usbRepository: UsbRepository
    @Inject lateinit var imageLoader: ImageLoader

    // Create a ViewModel the first time the system calls an activity's onCreate() method.
    // Re-created activities receive the same MyViewModel instance created by the first activity.
    // https://developer.android.com/topic/libraries/architecture/viewmodel
    private val identifyViewModel: IdViewModel by viewModels()
    private val accountsViewModel: AccountViewModel by viewModels()
    private val appsViewModel: AppsViewModel by viewModels()
    private lateinit var progressDrawableApps: CircularProgressDrawable
    private lateinit var progressDrawableAccounts: CircularProgressDrawable


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIdBinding.inflate(LayoutInflater.from(context))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()
        initEditText()
        setBoxStrokeColor()
        progressDrawableApps = getCircularProgressDrawable()
        progressDrawableAccounts = getCircularProgressDrawable()
        binding.textInputLayoutApp.endIconMode = TextInputLayout.END_ICON_DROPDOWN_MENU
        binding.textInputLayoutAccount.endIconMode = TextInputLayout.END_ICON_DROPDOWN_MENU

        listenToLiveData()
        setupAppListTask()
        setupAccountListTask()

        if (requireContext().isAccessibilityServiceEnabled().not()) {
            showSnackbarAccessibility()
        }
        subscribeToPackageUpdates()
    }

    @ExperimentalCoroutinesApi
    private fun subscribeToPackageUpdates() {
        packageFlow = repository.getPackages() // observe packages via Flow
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .buffer(Channel.CONFLATED) // handle back pressure by dropping oldest
            .filterNotNull()
            .distinctUntilChanged() // filter out subsequent repetition of values
            .mapLatest { packageName ->
                val applicationLabel = getApplicationInfo(packageName)?.let { applicationInfo ->
                    context?.packageManager?.getApplicationLabel(applicationInfo).toString()
                } ?: packageName
                AppItem(
                    name = applicationLabel,
                    packageName = packageName
                )
            }
            .onEach { appItem ->
                Timber.d("Label: ${appItem.name}")
                Timber.d("Package: ${appItem.packageName}")
                selectAppItem(appItem)
            }
            .launchIn(lifecycleScope)
    }

    private fun setBoxStrokeColor() {
        val colorList = ColorStateList(
            arrayOf(
                intArrayOf(-android.R.attr.state_focused),  // Unfocused
                intArrayOf(android.R.attr.state_focused)    // Focused
            ),
            intArrayOf(
                Color.parseColor("#DCDCDC"), // The color for the Unfocused state
                Color.RED                               // The color for the Focused state
            )
        )
        binding.textInputLayoutApp.setBoxStrokeColorStateList(colorList)
        binding.textInputLayoutAccount.setBoxStrokeColorStateList(colorList)
    }

    private fun getApplicationInfo(packageName: String): ApplicationInfo? {
        return try {
            context?.packageManager?.getApplicationInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            Timber.e(e)
            return null
        }
    }

    private fun unsubscribeToPackageUpdates() {
        packageFlow?.cancel()
    }

    private fun getCircularProgressDrawable() =
        CircularProgressDrawable(requireContext()).apply {
            setStyle(CircularProgressDrawable.DEFAULT)
            setColorSchemeColors(Color.LTGRAY)
        }

    private fun listenToLiveData() {
        usbRepository.state.observe(this, {
            binding.circularPulseView.setAnimationState(isEnabled = (it == UsbState.Ready))
        })
    }

    private fun setupAccountListTask() {
        if (requireContext().hasPermissions(
                Manifest.permission.GET_ACCOUNTS,
                Manifest.permission.READ_CONTACTS
            )
        ) {
            accountsViewModel.startAccountListTask()
        }
        binding.autoCompleteTextViewAccount.setOnFocusChangeListener { _, _ ->
            if (requireContext().hasPermissions(
                    Manifest.permission.GET_ACCOUNTS,
                    Manifest.permission.READ_CONTACTS
                ).not()
            ) {
                if (requireActivity().shouldShowPermissionRationale(
                        Manifest.permission.GET_ACCOUNTS,
                        Manifest.permission.READ_CONTACTS
                    )
                ) {
                    showSnackbarPermissionRationale()
                } else {
                    requestPermissions()
                }
            }
        }
        accountsViewModel.status.observe(this, {
            when (it) {
                Result.Loading -> {
                    Timber.d("Loading Accounts")
                    binding.textInputLayoutApp.endIconMode = TextInputLayout.END_ICON_CUSTOM
                    binding.textInputLayoutApp.endIconDrawable = progressDrawableAccounts
                    (progressDrawableAccounts as? Animatable)?.start()
                }
                is Result.Success<List<AccountItem>> -> {
                    Timber.d("Success Accounts [data=${it.data}]")
                    loadAccounts(it.data)
                    progressDrawableAccounts.stop()
                    binding.textInputLayoutApp.endIconMode = TextInputLayout.END_ICON_DROPDOWN_MENU
                }
                is Result.Error -> {
                    Timber.e("Error Accounts [exception=${it.error}]")
                    progressDrawableAccounts.stop()
                    binding.textInputLayoutApp.endIconMode = TextInputLayout.END_ICON_NONE
                }
            }
        })
    }

    private fun setupAppListTask() {
        appsViewModel.status.observe(this, {
            when (it) {
                Result.Loading -> {
                    Timber.d("Loading")
                    binding.textInputLayoutApp.endIconMode = TextInputLayout.END_ICON_CUSTOM
                    binding.textInputLayoutApp.endIconDrawable = progressDrawableApps
                    (progressDrawableApps as? Animatable)?.start()
                }
                is Result.Success<List<AppItem>> -> {
                    Timber.d("Success[data=${it.data}]")
                    loadAutoCompleteTextViewApps(it.data)
                    progressDrawableApps.stop()
                    binding.textInputLayoutApp.endIconMode = TextInputLayout.END_ICON_DROPDOWN_MENU
                }
                is Result.Error -> {
                    Timber.e("Error[exception=${it.error}]")
                    progressDrawableApps.stop()
                    binding.textInputLayoutApp.endIconMode = TextInputLayout.END_ICON_NONE
                }
            }
        })
        appsViewModel.startAppListTask()
    }

    private fun loadAutoCompleteTextViewApps(appList: List<AppItem>) {
        val adapter = AppsAdapter(requireContext(), R.layout.row_app, appList)
        adapter.setDropDownViewResource(R.layout.row_app)

        binding.autoCompleteTextViewApp.apply {
            setAdapter(adapter)
            ResourcesCompat.getDrawable(resources, R.drawable.shape_dropdown_background, null)
                ?.let {
                    setDropDownBackgroundDrawable(it)
                }
            threshold = 1 // no. of chars needed to filter (default: 2)
            setOnItemClickListener { parent, _, position, _ ->
                (parent?.adapter?.getItem(position) as? AppItem)?.let { selectedItem ->
                    selectAppItem(selectedItem)
                    identifyViewModel.selectAppItem(selectedItem)
                }
            }
            doOnTextChanged { text, _, _, _ ->
                binding.textInputLayoutApp.error = null
                text?.let {
                    identifyViewModel.onAppNameChange(it.toString())
                    if (it.isEmpty()) binding.imageViewApp.setImageResource(R.drawable.ic_apps)
                }
            }
        }
    }

    private fun selectAppItem(selectedItem: AppItem) {
        identifyViewModel.selectApplicationItem(selectedItem)

        binding.autoCompleteTextViewApp.setText(selectedItem.name)
        binding.imageViewApp.apply {
            setImageDrawable(context.packageManager.getApplicationIcon(selectedItem.packageName))
        }
        binding.autoCompleteTextViewApp.dismissDropDown()
        binding.autoCompleteTextViewApp.setKeyboardCloseAndClearFocus(requireContext())
    }

    private fun loadAccounts(list: List<AccountItem>) {
        val adapter = AccountAdapter(requireContext(), R.layout.row_app, list, imageLoader)
        binding.autoCompleteTextViewAccount.apply {
            setAdapter(adapter)
            ResourcesCompat.getDrawable(resources, R.drawable.shape_dropdown_background, null)
                ?.let {
                    setDropDownBackgroundDrawable(it)
                }
            threshold = 1 // no. of chars needed to filter (default: 2)
            setOnItemClickListener { parent, _, position, _ ->
                (parent.adapter.getItem(position) as? AccountItem)?.let { selectedItem ->
                    identifyViewModel.selectAccount(selectedItem)

                    binding.imageViewUser.loadImageResource(imageLoader, selectedItem)
                    binding.autoCompleteTextViewAccount.setText(selectedItem.email)

                    binding.autoCompleteTextViewAccount.dismissDropDown()
                    binding.autoCompleteTextViewAccount.setKeyboardCloseAndClearFocus(requireContext())
                }
            }
            doOnTextChanged { text, _, _, _ ->
                binding.textInputLayoutAccount.error = null
                text?.let {
                    identifyViewModel.onAccountNameChange(it.toString())
                    if (it.isEmpty()) binding.imageViewUser.setImageResource(R.drawable.ic_account)
                }
            }
        }
    }

    private fun initEditText() {
        binding.autoCompleteTextViewApp.apply {
            setSelectAllOnFocus(true)
            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    binding.autoCompleteTextViewApp.dismissDropDown()
                    binding.autoCompleteTextViewApp.setKeyboardCloseAndClearFocus(requireContext())
                    true
                } else {
                    false
                }
            }
        }
        binding.autoCompleteTextViewAccount.apply {
            setSelectAllOnFocus(true)
            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    binding.autoCompleteTextViewAccount.dismissDropDown()
                    binding.autoCompleteTextViewAccount.setKeyboardCloseAndClearFocus(requireContext())
                    true
                } else {
                    false
                }
            }
        }
    }

    @FlowPreview
    private fun setClickListeners() {
        binding.buttonCancel.onClick()
            .throttleFirst()
            .debounce(300)
            .flowOn(ioDispatcher) //^
            .onEach { withMainActivity { finishApp() } }
            .launchIn(lifecycleScope)

        binding.buttonUnlock.onClick()
            .throttleFirst()
            .combineTransform(identifyViewModel.isInputValid) { _, isInputValid ->
                emit(isInputValid)
            }
            //todo: combine USB state
            .flowOn(ioDispatcher) //^
            .onEach { isInputValid ->
                if (isInputValid) {
                    showSnackbarAboutUsbDisconnected()
                } else {
                    handleUnlockClick()
                }
            }
            .launchIn(lifecycleScope)
    }

    private fun handleUnlockClick() {
        binding.autoCompleteTextViewAccount.setKeyboardCloseAndClearFocus(requireContext())
        IdItem(
            appPackageName = binding.autoCompleteTextViewApp.text.toString().trim(),
            accountUserId = binding.autoCompleteTextViewAccount.text.toString().trim(),
        ).let { idItem ->
            if (isInputValid(idItem)) withRootBottomSheet { showAuthFragment(idItem) }
        }
    }

    private fun isInputValid(identifyItem: IdItem): Boolean {
        val viewWithError = validateFields(identifyItem.appPackageName, identifyItem.accountUserId)
        if (viewWithError != null) {
            viewWithError.requestFocus() // focus on the 1st field with error
            return false
        }
        return true
    }

    /**
     * @return Null if no errors, otherwise the first view (on the layout) with an error
     */
    private fun validateFields(appPackageName: String, accountUserId: String): View? {
        var viewWithError: View? = null

        val accountView = accountUserIdHasErrors(accountUserId)
        if (accountView != null) {
            viewWithError = accountView
        }
        val appView = appPackageNameHasErrors(appPackageName)
        if (appView != null) {
            viewWithError = appView
        }
        return viewWithError
    }

    private fun accountUserIdHasErrors(accountUserId: String): View? {
        if (identifyViewModel.isAccountUserIdValid(accountUserId).not()) {
            binding.textInputLayoutAccount.error = getString(R.string.fill_in_username)
            return binding.autoCompleteTextViewAccount
        }
        binding.textInputLayoutAccount.error = null
        return null
    }

    private fun appPackageNameHasErrors(appPackageName: String): View? {
        if (identifyViewModel.isAppPackageNameValid(appPackageName).not()) {
            binding.textInputLayoutApp.error = getString(R.string.choose_app)
            return binding.autoCompleteTextViewApp
        }
        binding.textInputLayoutApp.error = null
        return null
    }

    private fun showSnackbarAboutUsbDisconnected() {
        Snackbar.make(
            binding.bottomSheetRoot,
            getString(R.string.snack_disconnected),
            Snackbar.LENGTH_INDEFINITE
        ).apply {
            removeBottomPadding()
            setAction(R.string.snack_disconnected_action) { dismiss() }
            addDismissListener(onDismissed = { event ->
                when (event) {
                    BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_ACTION -> {
                        handleUnlockClick()
                    }
                    BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_SWIPE -> {
                        this@IdFragment.withRootBottomSheet { goBack() }
                    }
                    else -> {} // do nothing
                }
            })
        }.show()
    }

    private fun showSnackbarAccessibility() {
        Snackbar.make(
            binding.bottomSheetRoot,
            getString(R.string.snack_accessibility),
            Snackbar.LENGTH_INDEFINITE
        ).apply {
            removeBottomPadding()
            setAction(R.string.snack_accessibility_action) { dismiss() }
            addDismissListener(onDismissed = { event ->
                if (event == BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_ACTION) {
                    // open Accessibility Settings
                    startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                }
            })
        }.show()
    }

    private fun showSnackbarPermissionRationale() {
        Snackbar.make(
            binding.bottomSheetRoot,
            getString(R.string.snack_permissions),
            Snackbar.LENGTH_INDEFINITE
        ).apply {
            removeBottomPadding()
            setAction(R.string.snack_permissions_action) { dismiss() }
            addDismissListener(onDismissed = { event ->
                if (event == BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_ACTION) {
                    requestPermissions()
                }
            })
        }.show()
    }

    private val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions[Manifest.permission.GET_ACCOUNTS] == true
                && permissions[Manifest.permission.READ_CONTACTS] == true
            ) { // permissions granted
                accountsViewModel.startAccountListTask()
            } else {
                binding.textInputLayoutAccount.endIconMode = TextInputLayout.END_ICON_NONE
            }
        }

    private fun requestPermissions() {
        requestMultiplePermissions.launch(
            arrayOf(Manifest.permission.GET_ACCOUNTS, Manifest.permission.READ_CONTACTS)
        )
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
