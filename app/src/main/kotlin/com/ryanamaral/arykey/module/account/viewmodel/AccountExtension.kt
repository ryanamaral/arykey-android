package com.ryanamaral.arykey.module.account.viewmodel

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.Email
import com.ryanamaral.arykey.BuildConfig
import java.security.MessageDigest


//////////////
// Gravatar //
//////////////
private const val DEFAULT_AVATAR_SIZE = 200

/**
 * Output ex: https://s.gravatar.com/avatar/84c1c149c46b921d221f3febf47ad606?s=200
 */
fun getGravatarUrl(email: String, size: Int = DEFAULT_AVATAR_SIZE): String =
    "${BuildConfig.GRAVATAR_BASE_URL}/avatar/${email.md5()}?s=$size"

fun String.md5(): String {
    return MessageDigest.getInstance("MD5")
        .digest(this.toByteArray())
        .joinToString(separator = "") {
            ((it.toInt() and 0xff) + 0x100)
                .toString(16)
                .substring(1)
        }
}

//////////////
// Contacts //
//////////////

/**
 * Get Android Contact ID given associated Email
 */
fun Context.getContactIdByEmail(email: String): Long? {
    var contactId: Long? = null
    contentResolver.query(
        Email.CONTENT_URI,
        arrayOf(Email.CONTACT_ID),
        "${Email.ADDRESS}='$email'",
        null,
        null
    )?.use { cursor ->
        if (cursor.moveToFirst()) {
            contactId = cursor.getLong(0)
        }
    }
    return contactId
}

/**
 * Get Android Contact Bitmap (Image) given contact ID
 */
fun Context.getContactBitmapById(contactId: Long): Bitmap? {
    var bitmap: Bitmap? = null
    ContactsContract.Contacts.openContactPhotoInputStream(
        contentResolver,
        ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId)
    )?.use {
        bitmap = BitmapFactory.decodeStream(it)
    }
    return bitmap
}
