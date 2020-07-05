package com.vinh.myapplication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class TypeSelectAdapter extends RecyclerView.Adapter<TypeSelectAdapter.MyViewHolder> {

    private Context context;
    private TypeSelectActivity typeSelectActivity;
    private List<Note.TypeNote> listType;

    public TypeSelectAdapter(TypeSelectActivity typeSelectActivity, List<Note.TypeNote> listType) {
        this.typeSelectActivity = typeSelectActivity;
        this.context = typeSelectActivity.getApplicationContext();
        this.listType = listType;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemview = LayoutInflater.from(context).inflate(R.layout.row_type_select, viewGroup, false);
        return new MyViewHolder(itemview);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        final Note.TypeNote typeNote = listType.get(i);
        myViewHolder.txtTypeSelect.setText(typeNote.getType_name());
        myViewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if(typeSelectActivity.isChangeListType){
                    typeSelectActivity.returnData(typeNote.getType_id());
                }else {
                    typeSelectActivity.changeTypeNote(typeNote.getType_id());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listType.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView txtTypeSelect;
        private ItemClickListener itemClickListener;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTypeSelect = itemView.findViewById(R.id.txtTypeSelect);
            itemView.setOnClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onClick(view, getAdapterPosition(), false);
        }
    }
}
