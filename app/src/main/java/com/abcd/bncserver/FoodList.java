package com.abcd.bncserver;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.abcd.bncserver.Common.Common;
import com.abcd.bncserver.Interface.ItemClickListener;
import com.abcd.bncserver.Model.Food;
import com.abcd.bncserver.ViewHolder.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import info.hoang8f.widget.FButton;

import static android.R.id.progress;


public class FoodList extends AppCompatActivity {

    FloatingActionButton fab;
    RecyclerView recycler_food;
    RecyclerView.LayoutManager layoutManager;



    RelativeLayout rootLayout;
    Uri saveUri;
    FirebaseDatabase db;
    DatabaseReference foodList;
    FirebaseStorage storage;
    StorageReference storageReference;
    String categoryId="";
    FirebaseRecyclerAdapter<Food,FoodViewHolder> adapter;

    MaterialEditText Name,Description,Price,Discount;
    Food newFood;
    FButton btnUpload,btnSelect;
    private final int PICK_IMAGE_REQUEST=71;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);


        db = FirebaseDatabase.getInstance();
        foodList=db.getReference("Food");
        storage= FirebaseStorage.getInstance();
        storageReference=storage.getReference();

        recycler_food=(RecyclerView)findViewById(R.id.recycler_food);
        recycler_food.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recycler_food.setLayoutManager(layoutManager);

        rootLayout=(RelativeLayout)findViewById(R.id.rootLayout);


        fab=(FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddFoodDialog();
            }
        });

        if(getIntent()!= null)
            categoryId=getIntent().getStringExtra("CategoryId");
        if(!categoryId.isEmpty())
        {
            loadListFood(categoryId);

        }

    }

    private void showAddFoodDialog() {
        {
            AlertDialog.Builder alert=new AlertDialog.Builder(FoodList.this) ;
            alert.setTitle("Add new Food");
            alert.setMessage("Please fill Information");

            LayoutInflater inflator=this.getLayoutInflater();
            View add_menu_layout=inflator.inflate(R.layout.add_new_food_layout,null);

            Name= (MaterialEditText) add_menu_layout.findViewById(R.id.edtName);
            Description= (MaterialEditText) add_menu_layout.findViewById(R.id.edtDescription);
            Price= (MaterialEditText) add_menu_layout.findViewById(R.id.edtPrice);
            Discount= (MaterialEditText) add_menu_layout.findViewById(R.id.edtDiscount);

            btnSelect=(FButton)add_menu_layout.findViewById(R.id.btnSelect);
            btnUpload=(FButton)add_menu_layout.findViewById(R.id.btnUpload);

            btnSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chooseImage();
                }
            });

            btnUpload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UploadImage();
                }
            });


            alert.setView(add_menu_layout);
            alert.setIcon(R.drawable.ic_shopping_cart_black_24dp);

            alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    if(newFood !=null)
                    {
                        foodList.push().setValue(newFood);
                        Snackbar.make(rootLayout, "New Category" + newFood.getName()+" " + " Added",Snackbar.LENGTH_SHORT).show();
                    }
                }
            });

            alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            alert.show();

        }
    }
    private void chooseImage() {
        Intent intent =new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), Common.PICK_IMAGE_REQUEST);
    }



    private void UploadImage() {

        if(saveUri!=null)
        {
            final ProgressDialog mDailog=new ProgressDialog(this);
            mDailog.setMessage("Uploading...");
            mDailog.show();

            String imageName= UUID.randomUUID().toString();
            final StorageReference imageFolder=storageReference.child("image/"+imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDailog.dismiss();
                            Toast.makeText(FoodList.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    newFood=new Food();
                                    newFood.setName(Name.getText().toString());
                                    newFood.setDescripton(Description.getText().toString());
                                    newFood.setPrice(Price.getText().toString());
                                    newFood.setDiscount(Discount.getText().toString());
                                    newFood.setMenuId(categoryId);
                                    newFood.setImage(uri.toString());


                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDailog.dismiss();
                            Toast.makeText(FoodList.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress=(100.0 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            mDailog.setMessage("Uploading "+progress+"%");
                        }
                    });
        }
    }


    private void loadListFood(String categoryId) {
        adapter=new FirebaseRecyclerAdapter<Food,FoodViewHolder> (Food.class,R.layout.food_item,FoodViewHolder.class,
                foodList.orderByChild("MenuId").equalTo(categoryId)
        ){
            protected void populateViewHolder(FoodViewHolder viewHolder, Food model,int position){
                viewHolder.food_name.setText(model.getName());
                Picasso.with(FoodList.this).load(model.getImage()).into(viewHolder.food_image);

                final Food local =model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Toast.makeText(FoodList.this, ""+local.getName(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        };

        adapter.notifyDataSetChanged();
        recycler_food.setAdapter(adapter);
    }

    protected void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==Common.PICK_IMAGE_REQUEST && resultCode==RESULT_OK && data!=null && data.getData()!=null)
        {
            saveUri=data.getData();
            btnSelect.setText("IMAGE SELECTED");
        }

    }

    public boolean onContextItemSelected(MenuItem item){

        if(item.getTitle().equals(Common.UPDATE))
        {
            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        }
        else if(item.getTitle().equals(Common.DELETE))
        {
            deleteFood(adapter.getRef(item.getOrder()).getKey());
        }

        return super.onContextItemSelected(item);
    }

    private void deleteFood(String key) {

        foodList.child(key).removeValue();
        Toast.makeText(this, "Item Deleted", Toast.LENGTH_SHORT).show();
    }
    private void showUpdateDialog(final String key, final Food item) {
        {
            {
                AlertDialog.Builder alert=new AlertDialog.Builder(FoodList.this) ;
                alert.setTitle("Edit Food");
                alert.setMessage("Please fill Information");

                LayoutInflater inflator=this.getLayoutInflater();
                View add_menu_layout=inflator.inflate(R.layout.add_new_food_layout,null);

                Name= (MaterialEditText) add_menu_layout.findViewById(R.id.edtName);
                Description= (MaterialEditText) add_menu_layout.findViewById(R.id.edtDescription);
                Price= (MaterialEditText) add_menu_layout.findViewById(R.id.edtPrice);
                Discount= (MaterialEditText) add_menu_layout.findViewById(R.id.edtDiscount);

                Name.setText(item.getName());
                Description.setText(item.getDescripton());
                Price.setText(item.getPrice());
                Discount.setText(item.getDiscount());

                btnSelect=(FButton)add_menu_layout.findViewById(R.id.btnSelect);
                btnUpload=(FButton)add_menu_layout.findViewById(R.id.btnUpload);

                btnSelect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        chooseImage();
                    }
                });

                btnUpload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        changeImage(item);
                    }
                });


                alert.setView(add_menu_layout);
                alert.setIcon(R.drawable.ic_shopping_cart_black_24dp);

                alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                           item.setName(Name.getText().toString());
                            item.setDescripton(Description.getText().toString());
                            item.setPrice(Price.getText().toString());
                            item.setDiscount(Discount.getText().toString());

                            foodList.child(key).setValue(item);

                            Snackbar.make(rootLayout,"Food"+item.getName()+"was edited",Snackbar.LENGTH_SHORT).show();

                    }
                });

                alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alert.show();

            }
        }
    }

    private void changeImage(final Food item) {
        if(saveUri!=null)
        {
            final ProgressDialog mDailog=new ProgressDialog(this);
            mDailog.setMessage("Uploading...");
            mDailog.show();

            String imageName= UUID.randomUUID().toString();
            final StorageReference imageFolder=storageReference.child("image/"+imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDailog.dismiss();
                            Toast.makeText(FoodList.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    item.setImage(uri.toString());

                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDailog.dismiss();
                            Toast.makeText(FoodList.this,""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            mDailog.setMessage("Uploading "+progress+"%");
                        }
                    });
        }
    }

}