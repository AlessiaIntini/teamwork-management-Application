package it.polito.teamhub.dataClass.team

enum class Role {
    ADMIN,
    MEMBER,
    GUEST;

    fun getRoleString(): String {
        return when (this) {
            ADMIN -> "Admin"
            MEMBER -> "Member"
            GUEST -> "Guest"
        }
    }
}

fun listOfRolePrintable(): List<String> {
    return Role.entries.map { it.getRoleString() }
}