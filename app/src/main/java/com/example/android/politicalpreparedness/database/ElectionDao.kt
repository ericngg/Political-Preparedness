package com.example.android.politicalpreparedness.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.android.politicalpreparedness.network.models.Election

@Dao
interface ElectionDao {

    //TODO: Add insert query
    @Insert
    fun insert(election: Election)

    //TODO: Add select all election query
    @Query("SELECT * FROM election_table ORDER BY date(electionDay) ASC")
    fun getAllElection(): List<Election>

    //TODO: Add select single election query
    @Query("SELECT * FROM election_table WHERE id = :key")
    fun get(key: Long): Election?

    //TODO: Add delete query
    @Delete
    fun delete(election: Election)

    //TODO: Add clear query
    @Query("DELETE FROM election_table")
    fun clear()

}