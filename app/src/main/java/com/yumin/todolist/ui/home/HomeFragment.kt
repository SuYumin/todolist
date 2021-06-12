package com.yumin.todolist.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.yumin.todolist.*
import com.yumin.todolist.data.TodoList

class HomeFragment : Fragment(), ItemListener, MainActivity.SignOutListener {
    private lateinit var mListAdapter: AllListAdapter
    private lateinit var mNoItemLayout: ConstraintLayout
    private lateinit var mRecyclerView: RecyclerView

    private val mHomeViewModel: HomeViewModel by viewModels {
        ViewModelFactory((activity?.application as TodoListApplication).roomRepository
            ,(activity?.application as TodoListApplication).firebaseRepository)
    }

    companion object {
        val TAG: String = HomeFragment.javaClass.toString()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        (activity as MainActivity).setSignOutListener(this)
        mRecyclerView = root.findViewById(R.id.home_recyclerView)
        // set up adapter
        mListAdapter = AllListAdapter(this)
        mRecyclerView.adapter = mListAdapter
        val staggeredGridLayoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
        staggeredGridLayoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
        mRecyclerView.layoutManager = staggeredGridLayoutManager
        mRecyclerView.setItemViewCacheSize(20)
        mNoItemLayout = root.findViewById(R.id.no_item_view)
        observeViewModel()
        return root
    }


    private fun observeViewModel(){
        mHomeViewModel.allTodoList.observe(viewLifecycleOwner, Observer {
            LogUtils.logD(TAG, "allList it = "+it.size)
            mListAdapter.submitList(it)
        })

        mHomeViewModel.allTodoItems.observe(viewLifecycleOwner, Observer {
            LogUtils.logD(TAG, "allTodoItems it = "+it.size)
            mListAdapter.setTodoItemsDataSet(it)
        })

        mHomeViewModel.getLoadingStatus().observe(viewLifecycleOwner, Observer {
            if (it) {
                mNoItemLayout.visibility = View.VISIBLE
                mRecyclerView.visibility = View.INVISIBLE
            } else {
                mNoItemLayout.visibility = View.INVISIBLE
                mRecyclerView.visibility = View.VISIBLE
            }
        })
    }

    override fun onItemLayoutClick(todoList: TodoList) {
        // switch to fragment
        var bundle = Bundle()
        bundle.putInt(MainActivity.KEY_CHOSEN_LIST_ID,todoList.id)
        findNavController().navigate(R.id.nav_list_view,bundle)
        LogUtils.logD(TAG,"[onItemLayoutClick] MainActivity.CHOSEN_LIST_ID = ${todoList.id}")
    }

    override fun onDeleteAllItems() {
        mHomeViewModel.deleteAllItems()
    }
}
