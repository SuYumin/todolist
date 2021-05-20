package com.yumin.todolist.ui.home

import android.util.Log
import androidx.lifecycle.*
import com.yumin.todolist.data.ListInfo
import com.yumin.todolist.data.ItemInfo
import com.yumin.todolist.data.TodoListRepository

class HomeViewModel(private val repository: TodoListRepository) : ViewModel() {
    val allItemsInfo: LiveData<List<ItemInfo>> = repository.allItemInfo.asLiveData()
    val allList: LiveData<List<ListInfo>> = repository.allListInfo.asLiveData()

    companion object{
        val TAG: String = HomeViewModel.javaClass.toString()
    }

    fun getLoadingStatus(): LiveData<Boolean>{
        Log.d(TAG,"[getLoadingStatus]")
        var status = MediatorLiveData<Boolean>()
        status.addSource(allItemsInfo) {
            status.value = checkLoadingStatus()
        }
        status.addSource(allList) {
            status.value = checkLoadingStatus()
        }
        return status
    }

    private fun checkLoadingStatus(): Boolean {
        return allItemsInfo.value.isNullOrEmpty() && allList.value.isNullOrEmpty()
    }
}