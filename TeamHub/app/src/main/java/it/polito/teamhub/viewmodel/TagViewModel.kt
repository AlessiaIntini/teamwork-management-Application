package it.polito.teamhub.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.polito.teamhub.dataClass.task.Tag
import it.polito.teamhub.model.TagModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TagViewModel(private val tagModel: TagModel) : ViewModel() {
    var idTeam by mutableLongStateOf(-1L)

    fun getTags() = tagModel.getTags()

    fun getTagTeamId(teamId: Long) = tagModel.getTagTeamId(teamId)

    private val _tags = MutableStateFlow(listOf<Tag>())
    val tags: StateFlow<List<Tag>> = _tags

    init {
        viewModelScope.launch {
            tagModel.getTagTeamId(idTeam).collect { newTags ->
                _tags.value = newTags
            }
        }
    }
    fun updateIdTeam(newId: Long) {
        idTeam = newId
        viewModelScope.launch {
            tagModel.getTagTeamId(idTeam).collect { newTags ->
                _tags.value = newTags
            }
        }
    }

    fun getTagById(id: Long) = tagModel.getTagById(id)

    fun getListTagById(ids: List<Long>) = tagModel.getListTagById(ids)

    fun addTag(newTag: Tag) {
        tagModel.addTag(newTag)
    }

    fun updateTagList(id: Long, newTag: Tag) {
        tagModel.updateTagList(id, newTag)
    }



}