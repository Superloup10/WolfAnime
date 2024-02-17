package fr.wolfdev.wolfanime.domain.service

import fr.wolfdev.wolfanime.domain.Crunchyroll
import fr.wolfdev.wolfanime.domain.repository.CrunchyrollRepository

class CrunchyrollService(private val crunchyrollRepository: CrunchyrollRepository) {
    suspend fun getAllCrunchyroll(urlList: List<String>): List<Crunchyroll?> {
        return crunchyrollRepository.getAllCrunchyroll(urlList)
    }

    suspend fun addBatchInsertCrunchyroll(urlMap: Map<String, String>): List<Crunchyroll> {
        return crunchyrollRepository.addBatchInsertCrunchyroll(urlMap)
    }
}
