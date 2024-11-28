package com.brubaker.d308.UI;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brubaker.d308.R;
import com.brubaker.d308.database.Repository;
import com.brubaker.d308.entities.Excursion;
import com.brubaker.d308.entities.Vacation;
import com.brubaker.d308.receivers.VacationAlertReceiver;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VacationDetails extends AppCompatActivity {

    // UI
    private EditText vacationTitle;
    private EditText vacationHotel;
    private Button startDateButton;
    private Button endDateButton;

    // Non-UI
    private Date startDate;
    private Date endDate;
    private int vacationId = -1;

    // RecyclerView
    private RecyclerView excursionRecyclerView;
    private ExcursionListAdapter excursionListAdapter;
    private List<Excursion> excursionList = new ArrayList<>();

    // Database
    private Repository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vacation_details);

        // Handles system window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Retrieving vacationId from VacationList if the user selected an existing vacation
        if (getIntent().hasExtra("vacationId")) {
            vacationId = getIntent().getIntExtra("vacationId", -1);
        }

        // Link UI components
        vacationTitle = findViewById(R.id.vacation_title_input);
        vacationHotel = findViewById(R.id.vacation_hotel_input);
        startDateButton = findViewById(R.id.start_date_btn);
        endDateButton = findViewById(R.id.end_date_btn);
        excursionRecyclerView = findViewById(R.id.excursion_recycler_view);

        // Initialize RecyclerView for listing Excursions
        excursionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        excursionListAdapter = new ExcursionListAdapter(excursionList);
        excursionRecyclerView.setAdapter(excursionListAdapter);

        // Initialize Repository
        repository = new Repository(getApplication());

        // Load excursions for existing vacationId, if provided
        if (vacationId != -1) {
            loadVacationDetails();
            loadExcursions();
        }

        // Start date selection
        startDateButton.setOnClickListener(v -> showDatePicker(startDate, startDateButton, true));

        // End date selection
        endDateButton.setOnClickListener(v -> showDatePicker(startDate, endDateButton, false));

        // Transition from VacationDetails to ExcursionDetails via vacation_details_fab
        FloatingActionButton fab = findViewById(R.id.vacation_details_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VacationDetails.this, ExcursionDetails.class);
                startActivity(intent);
            }
        });
    }

    // Load vacation details for existing vacation
    private void loadVacationDetails() {
        repository.getVacationById(vacationId).observe(this, vacation -> {
            if (vacation != null) {
                //Populating vacation fields
                vacationTitle.setText(vacation.getTitle());
                vacationHotel.setText(vacation.getHotel());
                startDate = vacation.getStart();
                endDate = vacation.getEnd();

                // Update buttons with formatted dates
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
                startDateButton.setText(dateFormat.format(startDate));
                endDateButton.setText(dateFormat.format(endDate));
            }
        });
    }

    // Load excursions for existing vacation
    private void loadExcursions() {
        repository.getAllExcursionsForVacation(vacationId).observe(this, excursions -> {
            excursionList.clear(); // Clearing current data to avoid duplicates
            excursionList.addAll(excursions);
            excursionListAdapter.notifyDataSetChanged();
        });
    }

    // Inflating vacation_details_menu for Save, Update, Share, Delete vacation options
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.vacation_details_menu, menu);
        return true;
    }

    // Menu options selection logic
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.save_vacation) {
            saveVacation();
            return true;
        } else if (itemId == R.id.delete_vacation) {
            deleteVacation();
            return true;
        } else if (itemId == R.id.set_alarm) {
            setAlarms();
            return true;
        } else if (itemId == R.id.share_vacation) {
            shareVacation();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    // Menu options methods
    private void saveVacation() {
        // Collecting user input
        String title = vacationTitle.getText().toString();
        String hotel = vacationHotel.getText().toString();

        // Validate input
        if (title.isEmpty() || startDate == null || endDate == null) {
            Toast.makeText(this, "Please fill in all fields and dates", Toast.LENGTH_SHORT).show();
            return;
        }

        if (endDate.before(startDate)) {
            Toast.makeText(this, "The end date cannot before the start date", Toast.LENGTH_SHORT).show();
        }

        // Creating the vacation object
        Vacation vacation;

        // Save or update the vacation object
        if (vacationId == -1) {
            vacation = new Vacation(0, title, hotel, startDate, endDate);
            vacationId = repository.insertVacation(vacation); // New vacation
            Toast.makeText(this, "New vacation saved", Toast.LENGTH_SHORT).show();
        } else {
            vacation = new Vacation(vacationId, title, hotel, startDate, endDate);
            repository.updateVacation(vacation); // Existing vacation
            Toast.makeText(this, "Vacation updated", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteVacation() {
        // Checking if a vacation has any excursions prior to deletion
        boolean hasExcursions = repository.hasAssociatedExcursions(vacationId);
        if (hasExcursions) {
            // Cannot delete due to excursions
            Toast.makeText(this, "Cannot delete a vacation that has excursions. Please delete excursions before deleting vacation.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Deletion of vacation post-validation
        repository.deleteVacation(new Vacation(vacationId, null, null, null, null));
        Toast.makeText(this, "Vacation successfully deleted", Toast.LENGTH_SHORT).show();

        // Closing activity
        finish();
    }

    private void setAlarms() {
        // Validate that necessary fields are filled
        if (startDate == null || endDate == null || vacationTitle.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please ensure all fields are filled before setting an alarm.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Inflate the custom dialog layout
        View dialogView = getLayoutInflater().inflate(R.layout.alarm_dialog, null);

        // Initialize the AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView) // Attach the custom layout
                .setTitle("Set Alarm") // Set the title for the dialog
                .setCancelable(false); // Prevent the dialog from being dismissed without a user action

        // Add "OK" and "Cancel" buttons
        builder.setPositiveButton("OK", (dialogInterface, which) -> {
            // Access the RadioGroup in the custom layout
            RadioGroup radioGroup = dialogView.findViewById(R.id.radio_group_alarm_options);
            int selectedId = radioGroup.getCheckedRadioButtonId();

            // Get the vacation title
            String title = vacationTitle.getText().toString();

            // Perform the action based on the selected RadioButton
            if (selectedId == R.id.radio_start_date) {
                setAlarm(startDate, title, "starting");
                Toast.makeText(this, "Alarm set for the start date.", Toast.LENGTH_SHORT).show();
            } else if (selectedId == R.id.radio_end_date) {
                setAlarm(endDate, title, "ending");
                Toast.makeText(this, "Alarm set for the end date.", Toast.LENGTH_SHORT).show();
            } else if (selectedId == R.id.radio_both_dates) {
                setAlarm(startDate, title, "starting");
                setAlarm(endDate, title, "ending");
                Toast.makeText(this, "Alarms set for both start and end dates.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialogInterface, which) -> {
            // Dismiss the dialog without taking any action
            dialogInterface.dismiss();
        });

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void shareVacation() {
        Toast.makeText(VacationDetails.this, "Share Vacation button pressed", Toast.LENGTH_SHORT).show();
    }

    // Date picker methods for start and end date buttons
    private void showDatePicker(Date currentDate, Button buttonToUpdate, boolean isStartDate) {
        // Use currentDate for default date, or fallback to today's date
        Calendar calendar = Calendar.getInstance();
        if (currentDate != null) {
            calendar.setTime(currentDate); // Use existing date
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create the DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, month1, dayOfMonth) -> {
                    // Update the selected date
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(year1, month1, dayOfMonth);
                    Date newDate = selectedDate.getTime();

                    // Update the appropriate field
                    if (isStartDate) {
                        startDate = newDate;
                    } else {
                        endDate = newDate;
                    }

                    // Update the button text
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
                    buttonToUpdate.setText(dateFormat.format(newDate));
                }, year, month, day);

        // Show the dialog
        datePickerDialog.show();
    }

    private void setAlarm(Date date, String title, String type) {
        // Convert the Date object to milliseconds
        long triggerTime = date.getTime();

        // Create an Intent for the alarm
        Intent intent = new Intent(this, VacationAlertReceiver.class);
        intent.putExtra("title", title); // Pass the vacation title
        intent.putExtra("type", type);   // Pass whether it's "starting" or "ending"

        // Create a PendingIntent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                (int) System.currentTimeMillis(), // Unique request code
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Schedule the alarm using AlarmManager
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        }
    }

}