package com.ryanamaral.arykey.module.account.adapter

import android.view.View
import android.widget.ImageView
import coil.ImageLoader
import coil.load
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.ryanamaral.arykey.R
import com.ryanamaral.arykey.databinding.RowAppBinding
import com.ryanamaral.arykey.module.account.model.AccountItem
import com.ryanamaral.arykey.module.account.viewmodel.getContactBitmapById
import com.ryanamaral.arykey.module.account.viewmodel.getGravatarUrl


class AccountViewHolder(
    private val binding: RowAppBinding,
    private val imageLoader: ImageLoader,
    val view: View = binding.root
) {
    fun bind(app: AccountItem) {
        binding.textView.text = app.email
        binding.imageView.loadImageResource(imageLoader, app)
    }
}

fun ImageView.loadImageResource(imageLoader: ImageLoader, app: AccountItem) {
    if (app.contactId != null) {
        load(context.getContactBitmapById(app.contactId), imageLoader) {
            imageViewProperties()
        }
    } else {
        load(getGravatarUrl(app.email), imageLoader) {
            imageViewProperties()
        }
    }
}

fun ImageRequest.Builder.imageViewProperties() {
    crossfade(true)
    placeholder(R.drawable.ic_account)
    error(R.drawable.ic_account)
    transformations(CircleCropTransformation())
}
