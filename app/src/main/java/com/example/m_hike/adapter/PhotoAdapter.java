package com.example.m_hike.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.m_hike.R;
import com.example.m_hike.model.Photo;


import java.util.List;



public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>{


    private List<Photo> photoList;



    public PhotoAdapter(List<Photo> photoList){

        this.photoList = photoList;

    }



    public void setPhotoList(List<Photo> photoList){

        this.photoList = photoList;

        notifyDataSetChanged();

    }



    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ){


        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(
                        R.layout.item_photo,
                        parent,
                        false
                );


        return new PhotoViewHolder(view);

    }




    @Override
    public void onBindViewHolder(
            @NonNull PhotoViewHolder holder,
            int position
    ){

        Photo photo = photoList.get(position);


        Bitmap bitmap = BitmapFactory.decodeFile(
                photo.getPhotoPath()
        );


        if(bitmap != null){

            holder.imgPhoto.setImageBitmap(bitmap);

        }
        else {

            holder.imgPhoto.setImageResource(
                    R.drawable.ic_launcher_background
            );

        }

    }



    @Override
    public int getItemCount(){

        return photoList.size();

    }




    public static class PhotoViewHolder extends RecyclerView.ViewHolder{


        ImageView imgPhoto;


        public PhotoViewHolder(@NonNull View itemView){

            super(itemView);


            imgPhoto =
                    itemView.findViewById(
                            R.id.imgPhoto
                    );

        }
    }

}