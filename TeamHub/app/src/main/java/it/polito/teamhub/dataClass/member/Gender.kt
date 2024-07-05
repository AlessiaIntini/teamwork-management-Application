package it.polito.teamhub.dataClass.member

enum class Gender {
    MALE,
    FEMALE,
    OTHER,
    PREFER_NOT_TO_SAY;

    fun getGenderString(): String {
        return when (this) {
            MALE -> "Male"
            FEMALE -> "Female"
            OTHER -> "Other"
            PREFER_NOT_TO_SAY -> "Prefer not to say"
        }
    }
}

fun getGenderList(): List<Gender> {
    val genderList = mutableListOf<Gender>()
    genderList.add(Gender.MALE)
    genderList.add(Gender.FEMALE)
    genderList.add(Gender.OTHER)
    genderList.add(Gender.PREFER_NOT_TO_SAY)
    return genderList
}