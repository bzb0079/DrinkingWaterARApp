package app.th.project.drinkingWaterAR.view;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import app.th.project.drinkingWaterAR.R;
import app.th.project.drinkingWaterAR.model.PlaceItem;
import app.th.project.drinkingWaterAR.model.PlaceList;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddComplaintsActivity extends AppCompatActivity {
    private Intent intent;
    // Firebase auth instance
    final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    // Firebase cloud firestore instance
    final FirebaseFirestore mFirestoreDB = FirebaseFirestore.getInstance();
    // Firebase current signed in user instance
    final FirebaseUser currentUser = mAuth.getCurrentUser();
    List<Map<String, Object>> currentUserComplaints;
//    UserComplaints userComplaints;
    private TextView complaintsError;
    ScrollView scrollView;
    TextView fileComplaintText;
    Spinner spinner;
    Button fileComplaint;
    Button logout;

    String emptyComplaintError = "Please fill in the complaint box";


    private boolean validateComplaints(String text) {
        if (text.isEmpty()) {
            complaintsError.setText(emptyComplaintError);
            return false;
        } else {
            complaintsError.setText(null);
            return true;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_complaints);
        logout = findViewById(R.id.logoutButton);
        fileComplaint = findViewById(R.id.complaintButton);
        fileComplaintText = findViewById(R.id.plain_text_input);
        spinner = (Spinner)findViewById(R.id.location_spinner);
        complaintsError = findViewById(R.id.complaintInputError);
        scrollView = (ScrollView) findViewById(R.id.complaintScrollView);
        List<PlaceItem> placeList = PlaceList.getBuildings();
        List<String> placeNameList = new ArrayList<>();
        placeList.forEach((placeItem)->{
                    placeNameList.add(placeItem.getName());
                });
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this,android.R.layout.simple_spinner_item, placeNameList);

        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);

        fileComplaint.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                if(validateComplaints(fileComplaintText.getText().toString())){
                    saveResponsesToFirestore();
                }
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                intent = new Intent(AddComplaintsActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void saveResponsesToFirestore() {
        LocalDateTime timeCST = LocalDateTime.now(ZoneId.of("America/Chicago"));
        String formattedDate = timeCST.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        if (currentUser != null) {
//            userComplaints = new UserComplaints(spinner.getSelectedItem().toString(), fileComplaintText.getText().toString(),formattedDate);
            HashMap<String, Object> hashMap = new HashMap();
            hashMap.put("Complaint",fileComplaintText.getText().toString());
            hashMap.put("date", formattedDate);
            hashMap.put("place", spinner.getSelectedItem().toString());
            hashMap.put("isResolved", false);
//            currentUserComplaints.add(hashMap);
            DocumentReference userDocumentReference = mFirestoreDB.collection("Complaints").document();
            userDocumentReference.set(hashMap, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Snackbar snackbar = Snackbar
                            .make(scrollView, "Complaint Successfully Filed. Thank You", Snackbar.LENGTH_LONG);
                    snackbar.show();
                        fileComplaintText.setText("");
                }
            });
        } else {
            Snackbar snackbar = Snackbar
                    .make(scrollView, "Error while connecting with the server. Please try again later.", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
    }
}