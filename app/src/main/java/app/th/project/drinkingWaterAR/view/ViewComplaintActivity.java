package app.th.project.drinkingWaterAR.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import app.th.project.drinkingWaterAR.R;
import app.th.project.drinkingWaterAR.utils.CustomAdapterUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ViewComplaintActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
        private Intent intent;
        ListView apps;
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    // Firebase cloud firestore instance
    final FirebaseFirestore mFirestoreDB = FirebaseFirestore.getInstance();
    View view;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();

    final FirebaseUser currentUser = mAuth.getCurrentUser();
        List <String> checkedValue = new ArrayList<>();
        Button bt1;
@Override
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_complaint);
        Button logout = findViewById(R.id.logoutButton);
        Button resolve = findViewById(R.id.resolveButton);
        apps = (ListView) findViewById(R.id.listView1);
        view = (View) findViewById(R.id.complaintView);
    final List<String> list = new ArrayList<>();
    final int[] resultHolder = {0};


        loadComplaints(list);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                intent = new Intent(ViewComplaintActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });
        
        resolve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkedValue.forEach((item) ->
                {
                    Log.d("checkedItems", item);
                    saveResponsesToFirestore(item);
                });
                loadComplaints(new ArrayList<>());
            }
        });
        }

public void loadComplaints(List<String> list){
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Task<QuerySnapshot> task = db.collection("Complaints")
            .whereEqualTo("isResolved",false)
            .get()
            .addOnSuccessListener(executor, new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Log.d("all documents", document.getId() + " => " + document.getData().toString());
                        String complaintByUser = (String) document.get("Complaint");
                        String complaintPlace = (String) document.get("place");
                        String compalintId = document.getId();
//                        Log.d("mylcomplaint", complaintByUser);
                        list.add("ID: " + compalintId + " Msg: " + complaintByUser + " Place: " + complaintPlace);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("error getting documents", "Error getting documents: ", e);
                }
            });

    try {
        Thread.sleep(500);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }

    CustomAdapterUtil Adapter = new CustomAdapterUtil(this, list);
    apps.setAdapter(Adapter);
    apps.setOnItemClickListener(this);
}

String id = "";
@Override
public void onItemClick(AdapterView arg0, View v, int position, long arg3) {
        // TODO Auto-generated method stub
        CheckBox cb = (CheckBox) v.findViewById(R.id.checkBox1);
        TextView tv = (TextView) v.findViewById(R.id.textView1);
        String[] splitString = new String[10];

//        cb.setEnabled(false);
        cb.performClick();
        if (cb.isChecked()) {
            String tvText = tv.getText().toString();
            splitString = tvText.split(" ");
            id = splitString[1];
            Log.d("checked", id);
            checkedValue.add(id);
        } else if (!cb.isChecked()) {
//            Log.d("checkedHello", "hello");
            String tvText = tv.getText().toString();
            splitString = tvText.split(" ");
            id = splitString[1];
//            Log.d("checkedHello", id);
            checkedValue.remove(id);
        }
//        Log.d("checkedvalue", String.valueOf(checkedValue));
//    Log.d("checkedvaluesize", String.valueOf(checkedValue.size()));

        }

    private void saveResponsesToFirestore(String documentPath) {
        DocumentReference documentReference = db.collection("Complaints").document(documentPath);
        if (currentUser != null) {
            documentReference
                    .update("isResolved", true)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Snackbar snackbar = Snackbar
                                    .make(view, "Complaints Successfully Resolved. Thank You", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Snackbar snackbar = Snackbar
                                    .make(view, "Error while connecting with the server. Please try again later.", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        }
                    });
    }
}
}