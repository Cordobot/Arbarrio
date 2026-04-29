package com.arbarrio.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PropertyDao {
    @Query("SELECT * FROM properties")
    fun getAllProperties(): Flow<List<PropertyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProperties(properties: List<PropertyEntity>)

    @Query("SELECT * FROM properties WHERE id = :id")
    fun getPropertyById(id: String): PropertyEntity?

    @Query("DELETE FROM properties")
    fun clearAll()
}
