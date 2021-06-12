package com.yumin.todolist.ui.navigation_list

import androidx.lifecycle.*
import com.yumin.todolist.data.TodoList
import com.yumin.todolist.data.RoomRepository
import kotlinx.coroutines.launch

class NavListViewModel(private val roomRepository: RoomRepository) : ViewModel() {

    // insert list
    fun insertList(todoList: TodoList): LiveData<Long> {
        val result = MutableLiveData<Long>()
        viewModelScope.launch {
            result.postValue(roomRepository.insertList(todoList))
        }
        return result
    }

    fun getEditListInfo(id: Int) : LiveData<TodoList>{
        return roomRepository.getListById(id).asLiveData()
    }

    fun updateListInfo(todoList: TodoList){
        roomRepository.updateListItem(todoList)
    }
}