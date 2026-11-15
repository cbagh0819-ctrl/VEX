package com.vex.security.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ThreatEventDao {

    @Query("SELECT * FROM threat_events ORDER BY timestamp DESC")
    suspend fun getAllEvents(): List<ThreatEventEntity>

    @Query("SELECT * FROM threat_events WHERE isSynced = 0")
    suspend fun getUnsyncedEvents(): List<ThreatEventEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: ThreatEventEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<ThreatEventEntity>)

    @Query("UPDATE threat_events SET isSynced = 1 WHERE eventId = :eventId")
    suspend fun markAsSynced(eventId: String)

    @Query("SELECT COUNT(*) FROM threat_events")
    suspend fun getThreatCount(): Int

    @Query("SELECT COUNT(*) FROM threat_events WHERE riskLevel = :riskLevel")
    suspend fun getCountByRiskLevel(riskLevel: String): Int

    @Query("DELETE FROM threat_events")
    suspend fun clearAll()
}
