package fr.wolfdev.wolfanime.domain.service

import fr.wolfdev.wolfanime.domain.User
import fr.wolfdev.wolfanime.domain.repository.UserRepository

class UserService(private val userRepository: UserRepository) {
    suspend fun getOrCreate(id: Int, name: String): User {
        return userRepository.getUser(name) ?: userRepository.addUser(id, name)
    }
}
