package it.polito.teamhub.dataClass.team

data class TeamMember(
    val idMember: Long,
    var fullname: String,
    var role: Role? = null,
    var timeParticipation: TimeParticipation,
    var isMember: Boolean = true
) {
    companion object {
        fun fromMap(map: Map<String, Any?>): TeamMember {
            return TeamMember(
                idMember = map["idMember"] as Long,
                fullname = map["fullname"] as String,
                role = Role.valueOf(map["role"] as String),
                timeParticipation = TimeParticipation.valueOf(
                    map["timeParticipation"] as String
                ),
                isMember = map["isMember"] as Boolean
            )
        }
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "idMember" to idMember,
            "fullname" to fullname,
            "role" to role!!.name,
            "timeParticipation" to timeParticipation.name,
            "isMember" to isMember
        )
    }
}

