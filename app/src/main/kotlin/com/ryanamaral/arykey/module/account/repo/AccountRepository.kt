package com.ryanamaral.arykey.module.account.repo

import com.ryanamaral.arykey.module.account.model.AccountItem


interface AccountRepository {

    fun getAccountItem(): AccountItem?

    fun getAccountItemLocal(packageName: String): AccountItem?

    fun setAccountItemLocal(packageName: String, account: String)

    fun getUserAccountList(): List<AccountItem>
}
