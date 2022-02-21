import com.ryanamaral.arykey.module.apps.repo.AppsRepository
import com.ryanamaral.arykey.module.apps.model.AppItem
import javax.inject.Inject


class FakeAppsRepositoryImpl @Inject constructor() : AppsRepository {

    override fun getAppItem() =
        AppItem(name = "FakeApp", packageName = "com.fake.app")

    override fun getInstalledAppList(): List<AppItem> {
        return listOf(
            AppItem("FakeApp","com.fake.app"),
            AppItem("Contacts","com.android.contacts"),
            AppItem("Calendar","com.android.calendar"),
            AppItem("Documents","com.android.documentsui"),
            AppItem("FM Radio","com.android.fmradio"),
            AppItem("Messaging","com.android.messaging"),
            AppItem("Terminal","com.android.terminal"),
            AppItem("Downloads","com.android.providers"),
            AppItem("Google Play Store","com.android.vending"),
            AppItem("Android System WebView","com.android.webview"),
            AppItem("SIM Toolkit","com.android.stk"),
            AppItem("Phone and Messaging Storage","com.android.providers.telephony")
        )
    }
}
