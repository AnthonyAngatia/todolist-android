package com.codinginflow.mvvmtodo.ui.tasks

import androidx.lifecycle.*
import com.codinginflow.mvvmtodo.data.PreferenceManager
import com.codinginflow.mvvmtodo.data.SortOrder
import com.codinginflow.mvvmtodo.data.Task
import com.codinginflow.mvvmtodo.data.TaskDao
import com.codinginflow.mvvmtodo.ui.ADD_TASK_RESULT_OK
import com.codinginflow.mvvmtodo.ui.EDIT_TASK_RESULT_OK
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksViewModel  @Inject constructor(
    private val taskDao: TaskDao,
    private val preferenceManager: PreferenceManager,
    private val state:SavedStateHandle //Can work withou the assisted annotation in the other viewmodel
) : ViewModel() {

    private val taskEventChannel = Channel<TaskEvent>()
    val taskEvent:Flow<TaskEvent> = taskEventChannel.receiveAsFlow()

    val searchQuery = state.getLiveData("searchQuery", "")
//    val sortOrder = MutableStateFlow(SortOrder.BY_DATE)
//    val hideCompleted = MutableStateFlow(false)
    val preferenceFlow = preferenceManager.preferenceFlow

    private val tasksFlow: Flow<List<Task>> =
        combine(searchQuery.asFlow(), preferenceFlow)
        { query, preferenceFlow ->
        Pair(query, preferenceFlow)
    }.flatMapLatest {(query, preferenceFlow)->
        taskDao.getTasks(query, preferenceFlow.sortOrder, preferenceFlow.hideCompleted)
    }
    val tasks = tasksFlow.asLiveData()

    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferenceManager.updateSortOrder(sortOrder)
    }
    fun onHideCompletedClicked(hideCompleted: Boolean) = viewModelScope.launch {
        preferenceManager.updateHideCompleted(hideCompleted)
    }

    fun onTaskCheckedChange(task: Task, checked: Boolean) = viewModelScope.launch {
        taskDao.update(task.copy(completed = checked))
    }

    fun onTaskSelected(task: Task) =viewModelScope.launch{
        taskEventChannel.send(TaskEvent.NavigateToEditTaskScreen(task))
    }

    fun onTaskSwiped(task: Task) = viewModelScope.launch {
        taskDao.delete(task)
        taskEventChannel.send(TaskEvent.ShowUndoDeleteTaskMessage(task))
    }

    fun onUndoDeleteClick(task: Task) = viewModelScope.launch {
        taskDao.insert(task)
    }

    fun onAddNewTestClick() = viewModelScope.launch {
        taskEventChannel.send(TaskEvent.NavigateToAddScreen)
    }

    fun onAddEditResult(result: Int) {
        when(result){
            ADD_TASK_RESULT_OK -> showTaskSavedConfirmationMessage("Task Added")
            EDIT_TASK_RESULT_OK -> showTaskSavedConfirmationMessage("Task Updated")
        }

    }

    private fun showTaskSavedConfirmationMessage(text: String) = viewModelScope.launch{
        taskEventChannel.send(TaskEvent.ShowTaskSavedConfirmationMessage(text))
    }

    sealed class TaskEvent{
        object NavigateToAddScreen: TaskEvent()
        data class NavigateToEditTaskScreen(val task: Task): TaskEvent()
        data class ShowUndoDeleteTaskMessage(val task: Task): TaskEvent()
        data class ShowTaskSavedConfirmationMessage(val msg:String): TaskEvent()
    }

}

