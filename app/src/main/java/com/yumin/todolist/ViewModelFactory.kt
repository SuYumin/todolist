package com.yumin.todolist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yumin.todolist.data.TodoListRepository
import com.yumin.todolist.ui.home.HomeViewModel
import com.yumin.todolist.ui.navigation_list.NavListViewModel
import com.yumin.todolist.ui.item_list.ItemListViewModel

class ViewModelFactory(private val repository: TodoListRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        when {
            modelClass.isAssignableFrom(ItemListViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                return ItemListViewModel(repository) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                return HomeViewModel(repository) as T
            }
            modelClass.isAssignableFrom(NavListViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                return NavListViewModel(repository) as T
            }
        }
        throw IllegalArgumentException("Unable to construct view model")
    }
}