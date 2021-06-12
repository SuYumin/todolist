package com.yumin.todolist.ui.home

import androidx.lifecycle.*
import com.yumin.todolist.data.TodoList
import com.yumin.todolist.data.TodoItem
import com.yumin.todolist.data.RoomRepository

class HomeViewModel(private val roomRepository: RoomRepository) : ViewModel() {
    val allTodoItems: LiveData<List<TodoItem>> = roomRepository.allItem.asLiveData()
    val allTodoList: LiveData<List<TodoList>> = roomRepository.allList.asLiveData()

    companion object{
        val TAG: String = HomeViewModel.javaClass.toString()
    }

    fun getLoadingStatus(): LiveData<Boolean>{
        var status = MediatorLiveData<Boolean>()
        status.addSource(allTodoItems) {
            status.value = checkLoadingStatus()
        }
        status.addSource(allTodoList) {
            status.value = checkLoadingStatus()
        }
        return status
    }

    private fun checkLoadingStatus(): Boolean {
        return allTodoItems.value.isNullOrEmpty() && allTodoList.value.isNullOrEmpty()
    }

    fun deleteAllItems(){
        roomRepository.deleteAll()
    }
}