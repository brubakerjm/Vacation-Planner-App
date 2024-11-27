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

    public void insertVacation(Vacation vacation) {
        databaseExecutor.execute(() -> vacationDao.insertVacation(vacation));
    }

    public void updateVacation(Vacation vacation) {
        databaseExecutor.execute(() -> vacationDao.updateVacation(vacation));
    }

    public void deleteVacation(Vacation vacation) {
        databaseExecutor.execute(() -> vacationDao.deleteVacation(vacation));
    }

    // Excursion methods
    public LiveData<List<Excursion>> getAllExcursions() {
        return excursionDao.getAllExcursions();
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

}
