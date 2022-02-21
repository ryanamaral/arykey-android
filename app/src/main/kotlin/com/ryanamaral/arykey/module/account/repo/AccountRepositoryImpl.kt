package com.ryanamaral.arykey.module.account.repo

import android.accounts.AccountManager
import android.content.Context
import android.util.Patterns
import com.ryanamaral.arykey.data.AppPreferences
import com.ryanamaral.arykey.module.account.model.AccountItem
import com.ryanamaral.arykey.module.account.viewmodel.getContactBitmapById
import com.ryanamaral.arykey.module.account.viewmodel.getContactIdByEmail
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.regex.Pattern
import javax.inject.Inject


class AccountRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appPreferences: AppPreferences
) : AccountRepository {

    override fun getAccountItem(): AccountItem? = null

    override fun getAccountItemLocal(packageName: String): AccountItem? =
        appPreferences.getLastSelectedAccount(packageName)?.let { AccountItem(it, null) }

    override fun setAccountItemLocal(packageName: String, account: String) =
        appPreferences.setLastSelectedAccount(packageName, account)

    override fun getUserAccountList(): List<AccountItem> {
        val accountList: MutableList<AccountItem> = mutableListOf()
        val emailPattern: Pattern = Patterns.EMAIL_ADDRESS
        val accounts = AccountManager.get(context).accounts
        for (account in accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                val contactId = context.getContactIdByEmail(account.name)
                accountList.add(
                    AccountItem(
                        email = account.name,
                        contactId = if (contactId != null &&
                            context.getContactBitmapById(contactId) != null
                        ) contactId else null
                    )
                )
            }
        }
        return accountList
    }
}
