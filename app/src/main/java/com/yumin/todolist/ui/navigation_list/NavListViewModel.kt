package com.yumin.todolist.ui.navigation_list

import androidx.lifecycle.*
import com.yumin.todolist.data.ListInfo
import com.yumin.todolist.data.TodoListRepository
import kotlinx.coroutines.launch

class NavListViewModel(private val repository: TodoListRepository) : ViewModel() {

    // insert list
    fun insertList(listInfo: ListInfo) = viewModelScope.launch {
        repository.insertListItem(listInfo)
    }

    fun getEditListInfo(id: Int) : LiveData<ListInfo>{
        return repository.getListById(id).asLiveData()
    }

    fun updateListInfo(listInfo: ListInfo){
        repository.updateListItem(listInfo)
    }
}