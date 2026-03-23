package com.example.collegeschedule.data.repository

import com.example.collegeschedule.data.api.ScheduleApi
import com.example.collegeschedule.data.dto.ScheduleByDateDto
class ScheduleRepository(private val api: ScheduleApi) {
    suspend fun loadSchedule(group: String): List<ScheduleByDateDto> {
        return api.getSchedule(
            groupName = group,
            start = "2026-03-23",
            end = "2026-03-28"
        )
    }
    suspend fun getAllGroups(): List<String> {
        return try {
            api.getAllGroups()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun searchGroups(query: String): List<String> {
        return try {
            if (query.isBlank()) {
                getAllGroups()
            } else {
                api.searchGroups(query)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}