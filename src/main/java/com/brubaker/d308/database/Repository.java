package com.brubaker.d308.database;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.brubaker.d308.dao.ExcursionDao;
import com.brubaker.d308.dao.VacationDao;
import com.brubaker.d308.entities.Excursion;
import com.brubaker.d308.entities.Vacation;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Repository {
    private final VacationDao vacationDao;
    private final ExcursionDao excursionDao;
    private final Database db;
    private List<Vacation> allVacations;
    private List<Excursion> allExcursions;

    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public Repository(Context context) {
        db = Database.getDatabase(context);
        this.vacationDao = db.vacationDao();
        this.excursionDao = db.excursionDao();
    }

    // Vacation methods
    public LiveData<List<Vacation>> getAllVacations() {
        return vacationDao.getAllVacations();
    }

    public LiveData<Vacation> getVacationById(int vacationId) {
        return vacationDao.getVacationById(vacationId);
    }

    // Modifying insertVacation to return back Room's auto-generated vacationId
    public int insertVacation(Vacation vacation) {
        final int[] generatedId = new int[1]; // using an array for lambda assignment
        databaseExecutor.execute(() -> {
            generatedId[0] = (int) vacationDao.insertVacation(vacation); // Grabbing the auto-generated vacationId
        });

        // Passing back the vacationId
        return generatedId[0];
    }

//    public void insertVacation(Vacation vacation) {
//        databaseExecutor.execute(() -> vacationDao.insertVacation(vacation));
//    }

    public void updateVacation(Vacation vacation) {
        databaseExecutor.execute(() -> vacationDao.updateVacation(vacation));
    }

    public void deleteVacation(Vacation vacation) {
        databaseExecutor.execute(() -> vacationDao.deleteVacation(vacation));
    }

    // Excursion methods

    // TODO: A generic getAllExcursions method may not be needed. Review again when production build is ready
    public LiveData<List<Excursion>> getAllExcursions() {
        return excursionDao.getAllExcursions();
    }

    // More specific getAllExcursions method that retrieves based on vacationId
    public LiveData<List<Excursion>> getAllExcursionsForVacation(int vacationId) {
        return excursionDao.getExcursionsForVacation(vacationId);
    }

    public void insertExcursion(Excursion excursion) {
        databaseExecutor.execute(() -> excursionDao.insertExcursion(excursion));
    }
    public void updateExcursion(Excursion excursion) {
        databaseExecutor.execute(() -> excursionDao.updateExcursion(excursion));
    }

    public void deleteExcursion(Excursion excursion) {
        databaseExecutor.execute(() -> excursionDao.deleteExcursion(excursion));
    }

    // Method for checking if a vacation has any excursions
    public boolean hasAssociatedExcursions(int vacationId) {
        final boolean[] hasExcursions = {false}; // Boolean array to capture value in a lambda

        // Execute query on a background thread
        databaseExecutor.execute(() -> {
            List<Excursion> excursions = excursionDao.getExcursionsForVacation(vacationId).getValue();
            if (excursions != null && !excursions.isEmpty()) {
                hasExcursions[0] = true; // Set to true if excursions exist
            }
        });

        // Temporary wait to ensure query execution completes
        try {
            Thread.sleep(500); // Delay to allow background query to finish
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return hasExcursions[0]; // Return the result
    }

}
