package com.example.android.politicalpreparedness.database


import androidx.room.*
import com.example.android.politicalpreparedness.network.models.Election

@Dao
interface ElectionDao {

    /**
     *  Insert an election into the database
     *
    **/
    @Insert
    fun insert(election: Election)

    /**
     *  Query the database for all elections
     *
     **/
    @Query("SELECT * FROM election_table ORDER BY date(electionDay) ASC")
    fun getAllElection(): List<Election>

    /**
     *  Query the database for one election with an id
     *
     **/
    @Query("SELECT * FROM election_table WHERE id = :key")
    fun get(key: Long): Election?

    /**
     *  Delete an election from the database
     *
     **/
    @Delete
    fun delete(election: Election)

    /**
     *  Clears the whole database
     *
     **/
    @Query("DELETE FROM election_table")
    fun clear()

}