package it.polito.teamhub.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.polito.teamhub.dataClass.task.Category
import it.polito.teamhub.model.CategoryModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class CategoryViewModel(
    private val categoryModel: CategoryModel,
) : ViewModel() {
    //all'accesso ad un team, vengono scaricate le categorie di quel team

    var idTeam by mutableLongStateOf(-1L)
    fun getCategoryTeamId(teamId: Long) = categoryModel.getCategoryTeamId(teamId)

    private val _categories = MutableStateFlow(listOf<Category>())
    val categories: StateFlow<List<Category>> = _categories

    init {
        viewModelScope.launch {
            categoryModel.getCategoryTeamId(idTeam).collect { newCategories ->
                _categories.value = newCategories
            }
        }
    }
    fun updateIdTeam(newId: Long) {
        idTeam = newId
        viewModelScope.launch {
            categoryModel.getCategoryTeamId(idTeam).collect { newCategories ->
                _categories.value = newCategories
            }
        }
    }

    /*var idTask by mutableLongStateOf(-1L)
    var task by mutableStateOf<Task?>(null)
    private val _categoryTask = MutableStateFlow<Category?>(null)
    val categoryTask: StateFlow<Category?> = _categoryTask
    init {
        viewModelScope.launch {
            taskModel.getTaskById(idTask).collect { newTask ->
                task = newTask
            }
            if(task != null) {
                categoryModel.getCategoryById(task!!.category!!).collect { newCategory ->
                    _categoryTask.value = newCategory

                }
            }
        }
    }
    fun updateIdTask(newId: Long) {
        idTask = newId
        viewModelScope.launch {
            taskModel.getTaskById(idTask).collect { newTask ->
                task = newTask
            }
            if(task != null) {
                categoryModel.getCategoryById(task!!.category!!).collect { newCategory ->
                    _categoryTask.value = newCategory

                }
            }
        }
    }*/



    fun getCategories() = categoryModel.getCategories()


    fun getCategoryById(id: Long) = categoryModel.getCategoryById(id)

    fun addCategoryList(newCategory: Category) {
        categoryModel.addCategory(newCategory)
    }

    fun updateCategoryList(id: Long, newCategory: Category) {
        categoryModel.updateCategoryList(id, newCategory)
    }
}