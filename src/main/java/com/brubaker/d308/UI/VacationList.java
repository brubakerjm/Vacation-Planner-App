package com.brubaker.d308.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brubaker.d308.R;
import com.brubaker.d308.database.Repository;
import com.brubaker.d308.entities.Vacation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class VacationList extends AppCompatActivity {

    private RecyclerView vacationRecyclerView; // View that will be displaying the list
    private VacationListAdapter vacationListAdapter; // Adapter that is connecting the data to the RecyclerView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vacation_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // Finding the RecyclerView in the layout
        vacationRecyclerView = findViewById(R.id.vacation_list_recycler_view);

        // Setting up the layout manager for the RecyclerView
        vacationRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Fetching data from the repository
        Repository repository = new Repository(getApplication());
        repository.getAllVacations().observe(this, vacationList -> {
            // Create and set the adapter with the data from the repository
            vacationListAdapter = new VacationListAdapter(vacationList);

            // Setting up click listener for RecyclerView items
            vacationListAdapter.setOnItemClickListener(vacation -> {
                Intent intent = new Intent(VacationList.this, VacationDetails.class);
                intent.putExtra("vacationId", vacation.getVacationId()); // Passing the ID of the selected vacation
                startActivity(intent);
            });

            vacationRecyclerView.setAdapter(vacationListAdapter);
        });

        // Handling floating action button to initiate the creation of a new vacation
        FloatingActionButton fab = findViewById(R.id.vacation_list_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VacationList.this, VacationDetails.class);
                intent.putExtra("vacationId", -1); // -1 to indicate a new vacation
                startActivity(intent);
            }
        });
    }
}