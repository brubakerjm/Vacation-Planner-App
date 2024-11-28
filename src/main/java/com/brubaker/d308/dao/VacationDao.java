package com.brubaker.d308.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.brubaker.d308.entities.Vacation;

import java.util.List;

@Dao
public interface VacationDao {

    // changing from void to long since vacationId is being sent back to Repository
    @Insert
    long insertVacation(Vacation vacation);

    @Update
    void updateVacation(Vacation vacation);

    @Delete
    void deleteVacation(Vacation vacation);

    @Query("SELECT * FROM vacation")
    LiveData<List<Vacation>> getAllVacations();

    // TODO: Review if this method is needed
//    @Query("SELECT * FROM vacation WHERE vacationId = :id")
//    Vacation getVacationById(int id);

    @Query("SELECT * FROM vacation WHERE vacationId = :vacationId")
    LiveData<Vacation> getVacationById(int vacationId);
}
