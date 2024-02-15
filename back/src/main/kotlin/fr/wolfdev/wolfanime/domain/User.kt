package fr.wolfdev.wolfanime.domain

import kotlinx.serialization.Serializable

@Serializable
data class User(val id: Int, val name: String)

data class UserDTO(val user: User?)
