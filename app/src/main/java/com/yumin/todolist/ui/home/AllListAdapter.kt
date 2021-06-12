package com.yumin.todolist.ui.home

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.yumin.todolist.LogUtils
import com.yumin.todolist.R
import com.yumin.todolist.data.TodoList
import com.yumin.todolist.data.TodoItem
import com.yumin.todolist.ui.color_view.ColorView
import com.yumin.todolist.ui.item_list.ItemListAdapter

class AllListAdapter(val itemListener: ItemListener): ListAdapter<TodoList,AllListAdapter.ViewHolder>(AllListDiffCallBack()) {
    private var mItemsList: MutableMap<Int,List<TodoItem>>? = null
    private var mContext: Context? = null

    fun setTodoItemsDataSet(data: List<TodoItem>){
        data?.apply {
            // remap todolist map
            mItemsList = mutableMapOf<Int,List<TodoItem>>()
            for (todoItem in this) {
                val key = todoItem.listId
                if (mItemsList!!.containsKey(key)){
                    val tmpList = mutableListOf<TodoItem>()
                    mItemsList!![key]?.let { it1 -> tmpList.addAll(it1) }
                    tmpList.add(todoItem)
                    mItemsList!![key] = tmpList
                } else {
                    val itemList = mutableListOf<TodoItem>()
                    itemList.add(todoItem)
                    mItemsList!![key] = itemList
                }
            }
        }
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View, context: Context): RecyclerView.ViewHolder(itemView) {
        var adapter = ItemListAdapter(null)
        var listName: TextView = itemView.findViewById(R.id.list_name)
        var itemListRecyclerView: RecyclerView = itemView.findViewById(R.id.item_list_recyclerview)
        var listColorView: ColorView = itemView.findViewById(R.id.list_color)
        private val list_info_view: ConstraintLayout = itemView.findViewById(R.id.list_info_view)

        init {
            adapter.mEnableLimitSize = true
            adapter.mEnableTextGrayOut = true
            adapter.mDisableCheckBox = true
            itemListRecyclerView.adapter = adapter
            val staggeredGridLayoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
            itemListRecyclerView.layoutManager = staggeredGridLayoutManager
            itemListRecyclerView.setItemViewCacheSize(20)
        }

        fun setOnLayoutClick(todoList: TodoList, itemListener: ItemListener) {
            list_info_view.setOnClickListener {
                itemListener.onItemLayoutClick(todoList)
            }

            itemListRecyclerView.setOnTouchListener { view, motionEvent ->
                when(motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> {
                        itemListener.onItemLayoutClick(todoList)
                    }
                }
                view?.onTouchEvent(motionEvent) ?: true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_all_list_layout, parent,false)
        mContext = parent.context
        return ViewHolder(view,parent.context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (getItem(position) != null) {
            holder.listName.text = getItem(position).name
            holder.listColorView.colorValue = getItem(position).color

            var dataSet = mutableListOf<TodoItem>()
            // re-list todo_items by list id
            mItemsList?.get(getItem(position).id)?.let { it ->
                dataSet.addAll(it)
            }
            dataSet.reverse()
            LogUtils.logD("[AllListAdapter]","dataset key = ${getItem(position).id} , values = ${dataSet.toString()}")
            holder.adapter.submitList(dataSet)

            itemListener?.apply {
                holder.setOnLayoutClick(getItem(position),itemListener)
            }
        }
    }
}

interface ItemListener{
    fun onItemLayoutClick(todoList: TodoList)
}

class AllListDiffCallBack : DiffUtil.ItemCallback<TodoList>() {
    override fun areItemsTheSame(oldItem: TodoList, newItem: TodoList): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: TodoList, newItem: TodoList): Boolean {
        return oldItem == newItem
    }
}