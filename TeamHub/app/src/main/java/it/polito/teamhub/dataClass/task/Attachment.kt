package it.polito.teamhub.dataClass.task

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Attachment(
    var name: String,
    var url: String,
    var extension: String,
) : Parcelable {
    companion object {
        var lastId: Long = 0

        fun fromMap(map: Map<String, Any>): Attachment {
            return Attachment(
                name = map["name"] as String,
                url = map["url"] as String,
                extension = map["extension"] as String
            )
        }
    }

    var id: Long = lastId++

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "name" to name,
            "url" to url,
            "extension" to extension
        )
    }
}

