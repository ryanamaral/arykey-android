import com.ryanamaral.arykey.module.account.model.AccountItem
import com.ryanamaral.arykey.module.account.repo.AccountRepository
import javax.inject.Inject


class FakeAccountRepositoryImpl @Inject constructor() : AccountRepository {

    override fun getAccountItem(): AccountItem? =
        AccountItem("fake@email.com", null)

    override fun getAccountItemLocal(packageName: String): AccountItem? =
        AccountItem("fake@email.com", null)

    override fun setAccountItemLocal(packageName: String, account: String) {
        // do nothing
    }

    override fun getUserAccountList(): List<AccountItem> {
        return listOf(
            AccountItem("fake@email.com", null),
            AccountItem("fake1@email.com", 1L),
            AccountItem("fake12@email.com", 12L),
            AccountItem("fake123@email.com", 123L),
            AccountItem("fake1234@email.com", 1234L),
            AccountItem("fake12345@email.com", 12345L),
            AccountItem("fake123456@email.com", 123456L)
        )
    }
}
