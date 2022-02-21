package com.ryanamaral.arykey.module.account.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import androidx.annotation.LayoutRes
import coil.ImageLoader
import com.ryanamaral.arykey.databinding.RowAppBinding
import com.ryanamaral.arykey.module.account.model.AccountItem


class AccountAdapter(
    context: Context,
    @LayoutRes private val layoutResource: Int,
    private val accountList: List<AccountItem>,
    private val imageLoader: ImageLoader
) : ArrayAdapter<AccountItem>(context, layoutResource, accountList), Filterable {

    private var layoutInflater: LayoutInflater = LayoutInflater.from(context)
    private var list: List<AccountItem> = accountList
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getCount(): Int = list.size

    override fun getItem(position: Int): AccountItem {
        return list[position]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val viewHolder: AccountViewHolder
        if (convertView == null) {
            val itemBinding = RowAppBinding.inflate(layoutInflater, parent, false)
            viewHolder = AccountViewHolder(itemBinding, imageLoader)
            viewHolder.view.tag = viewHolder
        } else {
            viewHolder = convertView.tag as AccountViewHolder
        }
        viewHolder.bind(getItem(position))
        return viewHolder.view
    }

    override fun getFilter(): Filter {
        return object : Filter() {

            override fun publishResults(
                charSequence: CharSequence?,
                filterResults: Filter.FilterResults
            ) {
                (filterResults.values as? List<AccountItem>)?.let { filteredResults ->
                    list = filteredResults
                }
            }

            override fun performFiltering(charSequence: CharSequence?): Filter.FilterResults {
                val queryString = charSequence?.toString()?.lowercase()
                val filterResults = Filter.FilterResults()

                filterResults.values = when {
                    queryString == null || queryString.isEmpty() -> {
                        accountList
                    }
                    else -> {
                        accountList.filter {
                            it.email.lowercase().contains(queryString)
                        }
                    }
                }
                return filterResults
            }
        }
    }
}
