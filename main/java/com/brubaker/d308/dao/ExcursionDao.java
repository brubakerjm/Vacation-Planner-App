package com.brubaker.d308.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.brubaker.d308.entities.Excursion;
import com.brubaker.d308.entities.Vacation;

import java.util.List;

@Dao
public interface ExcursionDao {
    @Insert
    void insertExcursion(Excursion excursion);

    @Update
    void updateExcursion(Excursion excursion);

    @Delete
    void deleteExcursion(Excursion excursion);

    @Query("SELECT * FROM excursion")
    LiveData<List<Excursion>> getAllExcursions();

//    @Query("SELECT * FROM excursion WHERE excursionId = :id")
//    Excursion getExcursionById(int id);

    // Query to assist with validating no excursions are associated with a vacation
    // TODO Implement in the repository after essential database framework is in place
    @Query("SELECT * FROM excursion WHERE vacationId = :vacationId")
    LiveData<List<Excursion>> getExcursionsForVacation(int vacationId);

    @Query("SELECT * FROM excursion WHERE vacationId = :vacationId")
    LiveData<Excursion> getExcursionById(int vacationId);
}
