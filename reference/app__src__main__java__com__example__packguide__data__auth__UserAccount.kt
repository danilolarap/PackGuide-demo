package com.example.packguide.data.auth

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "user_accounts", indices = [Index(value = ["username"], unique = true)])
data class UserAccount(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val username: String,
    val passwordHash: String,
    val passwordSalt: String,
    val createdAtMillis: Long = System.currentTimeMillis()
)
