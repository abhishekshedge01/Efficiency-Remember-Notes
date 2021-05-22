package com.abhishek.notesapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton fltact;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    RecyclerView recyclerView;
    FirestoreRecyclerAdapter<model, viewholder> noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fltact = findViewById(R.id.flt);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        fltact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateNotes.class);
                startActivity(intent);
            }
        });

        Query query = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").orderBy("title", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<model> allusers = new FirestoreRecyclerOptions.Builder<model>().setQuery(query, model.class).build();
        noteAdapter = new FirestoreRecyclerAdapter<model, viewholder>(allusers) {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            protected void onBindViewHolder(@NonNull viewholder holder, int position, @NonNull model model) {

                int randomColor = getRandomColor();
                ImageView popmenubtn=holder.itemView.findViewById(R.id.popbtn);
                holder.mlayout.setBackgroundColor(holder.itemView.getResources().getColor(randomColor, null));
                holder.givenTitle.setText(model.getTitle());
                holder.givenContent.setText(model.getContent());

                String docId=noteAdapter.getSnapshots().getSnapshot(position).getId();

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(v.getContext(),DisplayActivity.class);

                        intent.putExtra("title",model.getTitle());
                        intent.putExtra("content",model.getContent());
                        intent.putExtra("noteId",docId);
                        startActivity(intent);
                    }
                });

                popmenubtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        PopupMenu popupMenu=new PopupMenu(v.getContext(),v);
                        popupMenu.setGravity(Gravity.END);
                        popupMenu.getMenu().add("edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                Intent intent=new Intent(v.getContext(),EditActivity.class);
                                intent.putExtra("title",model.getTitle());
                                intent.putExtra("content",model.getContent());
                                intent.putExtra("noteId",docId);
                                v.getContext().startActivity(intent);

                                return false;
                            }
                        });


                        popupMenu.getMenu().add("delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
//                                Toast.makeText(v.getContext(),"Deleted",Toast.LENGTH_SHORT).show();
                                DocumentReference documentreference=firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").document(docId);
                                documentreference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(v.getContext(),"Note is Deleted",Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(v.getContext(),"Note is not Deleted",Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return  false;
                            }
                        });
                        popupMenu.show();
                    }
                });

            }

            @NonNull
            @Override
            public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.noteslayout, parent, false);
                return new viewholder(view);
            }
        };

        recyclerView = findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setAdapter(noteAdapter);

    }

    private class viewholder extends RecyclerView.ViewHolder {
        private TextView givenTitle;
        private TextView givenContent;
        LinearLayout mlayout;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            givenTitle = itemView.findViewById(R.id.tvtitle);
            givenContent = itemView.findViewById(R.id.tvcontent);
            mlayout = itemView.findViewById(R.id.mnote);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menubar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.settings:
                Toast.makeText(getApplicationContext(), "Settings up", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.logout:
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(MainActivity.this, SignIn.class));
                return true;

            case R.id.exit:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (noteAdapter != null) {
            noteAdapter.stopListening();
        }
    }

    private int getRandomColor()
    {
        List<Integer> color=new ArrayList<>();
//        color.add(R.color.color1);
//        color.add(R.color.color2);
//        color.add(R.color.color3);
//        color.add(R.color.color4);
//        color.add(R.color.color5);
//        color.add(R.color.color6);
//        color.add(R.color.color7);
//        color.add(R.color.color8);
//        color.add(R.color.color9);
//        color.add(R.color.color10);


        color.add(R.color.color1);
        color.add(R.color.color2);
        color.add(R.color.color3);
        color.add(R.color.color4);
        color.add(R.color.color5);


        Random random=new Random();
        int number=random.nextInt(color.size());
        return color.get(number);

    }

}