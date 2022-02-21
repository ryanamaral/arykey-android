package com.ryanamaral.arykey.module.apps.adapter

import android.view.View
import com.ryanamaral.arykey.databinding.RowAppBinding
import com.ryanamaral.arykey.module.apps.model.AppItem


class AppsViewHolder(private val binding: RowAppBinding, val view: View = binding.root) {

    fun bind(app: AppItem) {
        binding.textView.text = app.name
        binding.imageView.apply {
            setImageDrawable(context.packageManager.getApplicationIcon(app.packageName))
        }
    }
}
