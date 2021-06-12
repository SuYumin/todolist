package com.yumin.todolist.ui.item_list

import androidx.lifecycle.*
import com.yumin.todolist.data.*
import kotlinx.coroutines.launch

class ItemListViewModel(private val roomRepository: RoomRepository) : ViewModel() {
    val itemInfoList: LiveData<List<TodoItem>>? = roomRepository?.allItem.asLiveData()
    val listItemTodoList: LiveData<List<TodoList>>? = roomRepository?.allList.asLiveData()

    private val listInfoById: MutableLiveData<Int> = MutableLiveData()
    var listInfo = Transformations.switchMap(listInfoById){
        roomRepository.getListById(it).asLiveData()
    }

    private val todoListById: MutableLiveData<Int> = MutableLiveData()
    var unCompleteTodoItemList = Transformations.switchMap(todoListById){
        roomRepository.getUnCompleteItemsByListId(it).asLiveData()
    }

    var completeTodoItemList = Transformations.switchMap(todoListById){
        roomRepository.getCompleteItemsByListId(it).asLiveData()
    }

    var todoItemList = Transformations.switchMap(todoListById) {
        roomRepository.getItemsByListId(it).asLiveData()
    }

    fun updateTodoItem(todoItem: TodoItem) = viewModelScope.launch {
        roomRepository.updateItem(todoItem)
    }

    fun insertTodoItem(todoItem: TodoItem) = viewModelScope.launch {
        roomRepository.insertItem(todoItem)
    }

    fun updateListInfoQueryId(id: Int){
        listInfoById.postValue(id)
    }

    fun updateTodoListQueryId(id: Int){
        todoListById.postValue(id)
    }

    fun deleteList(id: Int){
        roomRepository.deleteListById(id)
    }

    fun deleteItem(todoItem: TodoItem) {
        roomRepository.deleteItem(todoItem)
    }
}