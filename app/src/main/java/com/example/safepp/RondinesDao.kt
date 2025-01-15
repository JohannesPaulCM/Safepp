package com.example.safepp

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery


@Dao
interface RondinesDao {
    @Query("SELECT*FROM rondines")
    fun getAll(): LiveData<List<Rondin>>

    @Query("SELECT*FROM rondines WHERE idRondin = :id")
    fun get(id: Int): LiveData<Rondin>

    @Insert
    fun inserAll(vararg rondines: Rondin)

    @Update
    fun update(rondin: Rondin)

    @Delete
    fun delete (rondin: Rondin)

    @Query("DELETE FROM rondines")
    suspend fun deleteAll()







}