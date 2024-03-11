package org.example

object UserFactory {
    fun createUser(type: String, username: String, password: String): User {
        return when (type) {
            "admin" -> Admin(username, password)
            "visitor" -> Visitor(username, password)
            else -> throw IllegalArgumentException("Unknown user type.")
        }
    }
}