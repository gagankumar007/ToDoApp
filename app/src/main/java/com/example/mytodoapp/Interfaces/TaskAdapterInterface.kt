package com.example.mytodoapp.Interfaces
import com.example.mytodoapp.models.ToDoData

interface TaskAdapterInterface{
    fun onDeleteItemClicked(toDoData: ToDoData , position : Int)
    fun onEditItemClicked(toDoData: ToDoData, position: Int)
}