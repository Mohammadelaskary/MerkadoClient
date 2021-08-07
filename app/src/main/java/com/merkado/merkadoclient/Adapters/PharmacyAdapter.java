package com.merkado.merkadoclient.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.merkado.merkadoclient.Interfaces.setOnPharmacyItemClicked;
import com.merkado.merkadoclient.Model.PharmacyOrder;
import com.merkado.merkadoclient.databinding.PharmacyOrderItemBinding;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.List;

import static android.content.ContentValues.TAG;

public class PharmacyAdapter extends RecyclerView.Adapter<PharmacyAdapter.PharmacyViewHolder> {
    List<PharmacyOrder> orders;
    Context context;

    public PharmacyAdapter(List<PharmacyOrder> orders,Context context) {
        this.orders = orders;
        this.context = context;
    }

    @NonNull
    @Override
    public PharmacyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PharmacyViewHolder(PharmacyOrderItemBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull PharmacyViewHolder holder, int position) {
        String imageUrl = orders.get(position).getImageUrl();
        String therapyName = orders.get(position).getTherapyName();
        String numberOfItems = orders.get(position).getNumberOfItems();
        String typeOfItem    = orders.get(position).getTypeOfItem();
        String acceptAlternative = orders.get(position).getAcceptAlternative();
        String decription        = orders.get(position).getDescribtion();
        String pharmacyOrderId = orders.get(position).getPharmacyCartOrderId();
        String fileName        = orders.get(position).getImageFileName();
        if (imageUrl != null){
            Glide.with(context).load(imageUrl).into(holder.binding.image);
        } else {
            holder.binding.image.setVisibility(View.GONE);
        }

        if (therapyName.isEmpty()){
            holder.binding.productNameContainer.setVisibility(View.GONE);
        } else {
            holder.binding.therapyName.setText(therapyName);
        }

        if (numberOfItems.isEmpty()){
            holder.binding.therapyNumberContainer.setVisibility(View.GONE);
        } else {
            holder.binding.therapyNum.setText(numberOfItems);
            holder.binding.therapyItemType.setText(typeOfItem);
        }

        if (acceptAlternative.isEmpty()){
            holder.binding.acceptAlternativeContainer.setVisibility(View.GONE);
        } else {
            if (acceptAlternative.equals("true"))
                holder.binding.acceptAlternative.setText("نعم");
            else
                holder.binding.acceptAlternative.setText("لا");
        }

        if (decription.isEmpty()){
            holder.binding.descriptionContainer.setVisibility(View.GONE);
        } else {
            holder.binding.description.setText(decription);
        }

        holder.binding.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                alertDialog.setMessage("هل أنت متأكد من إزالة هذا العنصر!");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "نعم",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (!imageUrl.isEmpty()){
                                    deleteImage("Pharmacy/"+fileName,pharmacyOrderId);
                                } else {
                                    removeItem(pharmacyOrderId);
                                }
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "لا",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });
    }

    private void deleteImage(String fileName,String pharmacyOrderId) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(fileName);
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                removeItem(pharmacyOrderId);
                Log.d(TAG, "onSuccess: deleted file");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                FancyToast.makeText(context,"حدث خطأ أثناء إزالة العنصر ، حاول مرة أخري لاحقا!" , FancyToast.LENGTH_SHORT,FancyToast.ERROR,false);
                Log.d(TAG, "onFailure: did not delete file");
            }
        });
    }

    private void removeItem(String pharmacyOrderId) {
        String userId = FirebaseAuth.getInstance().getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Pharmacy Cart")
                .child(userId);
        Query query = reference.orderByChild("pharmacyCartOrderId").equalTo(pharmacyOrderId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    dataSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return orders==null?0:orders.size();
    }

    static class PharmacyViewHolder extends RecyclerView.ViewHolder{
        PharmacyOrderItemBinding binding;
        public PharmacyViewHolder(PharmacyOrderItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
