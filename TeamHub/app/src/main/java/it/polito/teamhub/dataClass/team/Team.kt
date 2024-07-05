package it.polito.teamhub.dataClass.team

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.google.firebase.Timestamp
import it.polito.teamhub.R
import it.polito.teamhub.ui.theme.PurpleBlue
import java.security.SecureRandom
import java.util.Date
import kotlin.math.abs

data class Team(
    var name: String,
    var members: MutableList<TeamMember>,
    var description: String,
    val defaultImage: Int = R.drawable.group_2,
    var imageTeam: String = "",
    val longColor: Long? = PurpleBlue.toArgb().toLong(),
    var creationDate: Date,
) {

    companion object {
        fun fromMap(map: Map<String, Any>): Team {
            return Team(
                name = map["name"] as String,
                members = (map["members"] as List<Map<String, Any?>>).map { TeamMember.fromMap(it) }
                    .toMutableList(),
                description = map["description"] as String,
                defaultImage = R.drawable.group_2,
                imageTeam = map["imageTeam"] as String,
                longColor = map["longColor"] as Long? ?: PurpleBlue.toArgb().toLong(),
                creationDate = (map["creationDate"] as Timestamp).toDate(),
            )
        }
    }

    val color: Color
        get() = longColor?.let { Color(it) } ?: PurpleBlue

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "name" to name,
            "members" to members.map { it.toMap() },
            "description" to description,
            "defaultImage" to defaultImage,
            "imageTeam" to imageTeam,
            "longColor" to longColor,
            "creationDate" to creationDate,
        )
    }

    constructor() : this(
        name = "",
        members = mutableListOf(),
        description = "",
        defaultImage = R.drawable.group_2,
        imageTeam = "",
        longColor = PurpleBlue.toArgb().toLong(),
        creationDate = Date(),
    )

    private val secureRandom = SecureRandom()
    var id: Long

    init {
        do {
            id = abs(secureRandom.nextLong())
        } while (id < 0)
    }
}