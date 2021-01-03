package com.merkado.merkadoclient.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.merkado.merkadoclient.Model.AdImages;
import com.merkado.merkadoclient.R;

import java.util.List;

public class AdImagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<AdImages> adImagesList;
    Context context;
    private static final int WITH_TEXT = 0;
    private static final int WITHOUT_TEXT = 1;

    public AdImagesAdapter(Context context, List<AdImages> adImagesList) {
        this.adImagesList = adImagesList;
        this.context = context;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewWithText = LayoutInflater.from(parent.getContext()).inflate(R.layout.ad_layout_with_text, parent, false);
        View viewWithoutText = LayoutInflater.from(parent.getContext()).inflate(R.layout.ad_layout_without_text, parent, false);
        if (viewType == WITH_TEXT)
            return new AdImagesViewHolderWithText(viewWithText);
        else
            return new AdImagesViewHolderWithoutText(viewWithoutText);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case WITH_TEXT: {
                String adImage = adImagesList.get(position).getAdImageUrl();
                String adText = adImagesList.get(position).getAdText();
                AdImagesViewHolderWithText viewHolderWithText = (AdImagesViewHolderWithText) holder;
                Glide.with(context).load(adImage).diskCacheStrategy(DiskCacheStrategy.ALL).into(viewHolderWithText.adImage);
                viewHolderWithText.adText.setText(adText);
                break;
            }
            case WITHOUT_TEXT: {
                String adImage = adImagesList.get(position).getAdImageUrl();
                AdImagesViewHolderWithoutText viewHolderWithoutText = (AdImagesViewHolderWithoutText) holder;
                if (adImage.isEmpty())
                    viewHolderWithoutText.adImage.setImageResource(R.drawable.default_ad_image);
                else
                    Glide.with(context).load(adImage).diskCacheStrategy(DiskCacheStrategy.ALL).into(viewHolderWithoutText.adImage);
                break;
            }
        }
    }


    @Override
    public int getItemViewType(int position) {
        if (adImagesList.get(position).getAdText().equals(""))
            return WITHOUT_TEXT;
        else
            return WITH_TEXT;
    }

    @Override
    public int getItemCount() {
        return adImagesList.size();
    }

    static class AdImagesViewHolderWithText extends ViewHolder {
        ImageView adImage;
        TextView adText;

        public AdImagesViewHolderWithText(@NonNull View itemView) {
            super(itemView);
            adImage = itemView.findViewById(R.id.ad_image);
            adText = itemView.findViewById(R.id.ad_text);
        }
    }

    static class AdImagesViewHolderWithoutText extends ViewHolder {
        ImageView adImage;

        public AdImagesViewHolderWithoutText(@NonNull View itemView) {
            super(itemView);
            adImage = itemView.findViewById(R.id.ad_image);
        }
    }
}
