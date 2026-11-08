package com.example.packguide.data.auth

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserAccountDao {
    @Query("SELECT * FROM user_accounts WHERE username = :username COLLATE NOCASE LIMIT 1")
    suspend fun findByUsername(username: String): UserAccount?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(account: UserAccount): Long
}
