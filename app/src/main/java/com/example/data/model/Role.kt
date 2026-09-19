package com.example.data.model

enum class Role(val displayName: String, val badgeColorHex: Long) {
    SUPER_ADMIN("Super Admin", 0xFF6366F1),
    SCHOOL_ADMIN("School Admin", 0xFF1E3A8A),
    TEACHER("Class Teacher", 0xFF0F766E),
    GATE_GUARD("Gate Security", 0xFFD97706),
    PARENT("Parent / Guardian", 0xFF10B981);

    companion object {
        fun fromString(value: String): Role {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: PARENT
        }
    }
}
