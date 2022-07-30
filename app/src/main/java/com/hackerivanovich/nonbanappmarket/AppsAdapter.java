package com.hackerivanovich.nonbanappmarket;

import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AppsAdapter extends RecyclerView.Adapter<AppsAdapter.MyViewHolder> {

    Context context;
    ArrayList<Apps> list;

    public AppsAdapter(Context context, ArrayList<Apps> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Apps apps = list.get(position);
        String url = apps.getLogo();

        holder.name.setText(apps.getName());
        holder.company.setText(apps.getCompany());

        Glide.with(holder.itemView.getContext())
                .load(url)
                .into(holder.icon);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AppActivity.class).addFlags(FLAG_ACTIVITY_NO_ANIMATION);
                intent.putExtra("name", apps.getName());
                intent.putExtra("id", apps.getId());
                intent.putExtra("company", apps.getCompany());
                intent.putExtra("likes", String.valueOf(apps.getLikes()));
                intent.putExtra("dislikes", String.valueOf(apps.getDislikes()));
                intent.putExtra("link", apps.getLink());
                intent.putExtra("logo", apps.getLogo());
                intent.putExtra("preview", apps.getPreview());
                intent.putExtra("downloads", String.valueOf(apps.getDown()));
                intent.putExtra("package", apps.getPack());
                intent.putExtra("uid", apps.getUid());
                v.getContext().startActivity(intent);
            }
        });
    }

    public void filterList(ArrayList<Apps> filterList) {
        list = filterList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name, company;
        RoundedImageView icon;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.apps_name);
            company = itemView.findViewById(R.id.apps_company);
            icon = itemView.findViewById(R.id.apps_icon);
        }
    }
}
