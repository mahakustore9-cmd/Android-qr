package com.example.data.model

enum class TrackingStage(
    val stepOrder: Int,
    val titleHindi: String,
    val titleEnglish: String,
    val voiceTemplateHindi: String,
    val allowedRole: Role,
    val iconName: String,
    val colorHex: Long
) {
    AT_HOME(
        stepOrder = 0,
        titleHindi = "Ghar par hai",
        titleEnglish = "At Home (Ready)",
        voiceTemplateHindi = "Aapka bachcha {name} abhi ghar par hai",
        allowedRole = Role.PARENT,
        iconName = "home",
        colorHex = 0xFF64748B
    ),
    DEPARTED_HOME(
        stepOrder = 1,
        titleHindi = "Ghar se nikal chuka hai",
        titleEnglish = "Departed from Home",
        voiceTemplateHindi = "Aapka bachcha {name} ghar se school ke liye nikal chuka hai",
        allowedRole = Role.PARENT,
        iconName = "directions_walk",
        colorHex = 0xFFF59E0B
    ),
    REACHED_SCHOOL_GATE(
        stepOrder = 2,
        titleHindi = "School pahuch gaya hai",
        titleEnglish = "Arrived at School Gate",
        voiceTemplateHindi = "Aapka bachcha {name} school pahuch gaya hai",
        allowedRole = Role.GATE_GUARD,
        iconName = "security",
        colorHex = 0xFF3B82F6
    ),
    ENTERED_CLASSROOM(
        stepOrder = 3,
        titleHindi = "Class me pahuch gaya hai",
        titleEnglish = "Entered Classroom",
        voiceTemplateHindi = "Aapka bachcha {name} class me pahuch gaya hai",
        allowedRole = Role.TEACHER,
        iconName = "school",
        colorHex = 0xFF8B5CF6
    ),
    LEFT_SCHOOL_GATE(
        stepOrder = 4,
        titleHindi = "School se nikal chuka hai",
        titleEnglish = "Exited School Gate",
        voiceTemplateHindi = "Aapka bachcha {name} school se nikal chuka hai",
        allowedRole = Role.GATE_GUARD,
        iconName = "directions_run",
        colorHex = 0xFFEA580C
    ),
    REACHED_HOME(
        stepOrder = 5,
        titleHindi = "Ghar pahuch gaya hai",
        titleEnglish = "Reached Home Safely",
        voiceTemplateHindi = "Aapka bachcha {name} surakshit ghar pahuch gaya hai",
        allowedRole = Role.PARENT,
        iconName = "check_circle",
        colorHex = 0xFF10B981
    );

    fun formatVoiceMessage(studentName: String): String {
        return voiceTemplateHindi.replace("{name}", studentName)
    }

    companion object {
        fun fromName(name: String): TrackingStage {
            return entries.find { it.name.equals(name, ignoreCase = true) } ?: AT_HOME
        }
    }
}
