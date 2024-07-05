package it.polito.teamhub.dataClass.task


enum class Action {
    CREATION,
    COMMENT,
    UPDATE_STATUS,
    UPDATE_TASK,
    UPDATE_MEMBERS,
    LEAVE_TEAM,
    REMOVED_FROM_TEAM,
    ADD_ATTACHMENT,
    ADD_URL,
    ADD_REVIEW;

    fun getUpdatedMembers(removedMembers: List<String>?, addedMembers: List<String>?): String {
        val removedMembersString = removedMembers?.joinToString { it }
        val addedMembersString = addedMembers?.joinToString { it }
        var description = ""
        if (removedMembersString!!.isNotEmpty()) {
            description += "Removed members: $removedMembersString"
        }
        if (addedMembersString!!.isNotEmpty()) {
            if (description.isNotEmpty()) {
                description += ", "
            }
            description += "Added members: $addedMembersString"
        }
        return description
    }

    fun getAction(description: String): String {
        return when (this) {
            CREATION -> "Task created"
            COMMENT -> "Added comment: \"$description\""
            UPDATE_STATUS -> "Updated Status to: \"$description\""
            UPDATE_TASK -> description
            UPDATE_MEMBERS -> ""
            LEAVE_TEAM -> "$description has left the team"
            REMOVED_FROM_TEAM -> "$description has been removed from the team"
            ADD_ATTACHMENT -> "Added attachment: \"$description\""
            ADD_URL -> "Added url: \"$description\""
            ADD_REVIEW -> "Added review with score: $description"
        }
    }


}