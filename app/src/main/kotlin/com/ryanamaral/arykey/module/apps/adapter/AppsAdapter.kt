package com.ryanamaral.arykey.module.apps.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import androidx.annotation.LayoutRes
import com.ryanamaral.arykey.databinding.RowAppBinding
import com.ryanamaral.arykey.module.apps.model.AppItem


class AppsAdapter(
    context: Context,
    @LayoutRes private val layoutResource: Int,
    private val appList: List<AppItem>
) : ArrayAdapter<AppItem>(context, layoutResource, appList), Filterable {

    private var layoutInflater: LayoutInflater = LayoutInflater.from(context)
    private var list: List<AppItem> = appList
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getCount(): Int = list.size

    override fun getItem(position: Int): AppItem {
        return list[position]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val viewHolder: AppsViewHolder
        if (convertView == null) {
            val itemBinding = RowAppBinding.inflate(layoutInflater, parent, false)
            viewHolder = AppsViewHolder(itemBinding)
            viewHolder.view.tag = viewHolder
        } else {
            viewHolder = convertView.tag as AppsViewHolder
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
                (filterResults.values as? List<AppItem>)?.let { filteredResults ->
                    list = filteredResults
                }
                notifyDataSetChanged()
            }

            override fun performFiltering(charSequence: CharSequence?): Filter.FilterResults {
                val queryString = charSequence?.toString()?.lowercase()
                val filterResults = Filter.FilterResults()

                filterResults.values = when {
                    queryString == null || queryString.isEmpty() -> {
                        appList
                    }
                    queryString.length == 1 -> {
                        appList.filter {
                            it.name.lowercase().startsWith(queryString)
                        }
                    }
                    else -> { // length: 2+
                        appList.filter {
                            it.name.lowercase().contains(queryString)
                        }
                    }
                }
                return filterResults
            }
        }
    }
}
