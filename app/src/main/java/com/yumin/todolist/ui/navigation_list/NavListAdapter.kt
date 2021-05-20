package com.yumin.todolist.ui.navigation_list

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.yumin.todolist.R
import com.yumin.todolist.data.ListInfo
import com.yumin.todolist.ui.color_view.ColorView

class NavListAdapter(private var activity: Activity, private var dataSet: List<ListInfo>?): BaseAdapter() {

    fun updateDataSet(newDataSet: List<ListInfo>?){
        Log.d("[NavListAdapter]","updateDataSet = $newDataSet")
        dataSet = newDataSet
        notifyDataSetChanged()
    }

    private class ViewHolder(view: View?) {
        var name: TextView? = null
        var colorView: ColorView? = null

        init {
            this.name = view?.findViewById(R.id.list_name)
            this.colorView = view?.findViewById(R.id.list_color)
        }
    }


    override fun getCount(): Int {
        return dataSet?.size ?: 0
    }

    override fun getItem(position: Int): ListInfo? {
        return dataSet?.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View?
        val viewHolder: ViewHolder

        if (convertView == null) {
            val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            // create view and set tag
            view = inflater.inflate(R.layout.adapter_navigation_list_layout, null)
            viewHolder = ViewHolder(view)
            view?.tag = ViewHolder(view)
        } else {
            // get tag from view
            view = convertView
            viewHolder = view.tag as ViewHolder
        }
        // bind data to view
        viewHolder.name?.text = dataSet?.get(position)?.name
        viewHolder.colorView?.colorValue = dataSet?.get(position)?.color!!
        return view as View
    }
}