package com.ryanamaral.arykey.common.extension

import java.lang.ref.WeakReference
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


/**
 * [WeakReference](https://developer.android.com/reference/java/lang/ref/WeakReference)
 */
fun <T> weakReference(tIn: T? = null): ReadWriteProperty<Any?, T?> {

    return object : ReadWriteProperty<Any?, T?> {

        var t = WeakReference<T?>(tIn)

        override fun getValue(thisRef: Any?, property: KProperty<*>): T? = t.get()

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
            t = WeakReference(value)
        }
    }
}
