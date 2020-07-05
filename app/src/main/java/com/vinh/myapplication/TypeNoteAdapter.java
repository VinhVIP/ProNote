package com.vinh.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class TypeNoteAdapter extends RecyclerView.Adapter<TypeNoteAdapter.MyViewHolder> {

    private List<Note.TypeNote> listTypes;
    private TypeNoteActivity main;
    private Context context;

    public TypeNoteAdapter(TypeNoteActivity main, List<Note.TypeNote> listTypes) {
        this.main = main;
        this.listTypes = listTypes;
        context = main.getApplicationContext();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_type, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
        final Note.TypeNote typeNote = listTypes.get(i);
        myViewHolder.txtType.setText(typeNote.getType_name());
        myViewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                main.dialog_delete(typeNote.getType_id(), listTypes.indexOf(typeNote));
            }
        });

        myViewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if (isLongClick) {

                } else {
                    main.showDialog(typeNote.getType_id(), typeNote.getType_name(), listTypes.indexOf(typeNote), false);
                }
            }
        });
    }

    public void notifyTypeNote(int position){
        listTypes.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return listTypes.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private TextView txtType;
        private ImageView btnDelete;
        private ItemClickListener itemClickListener;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtType = itemView.findViewById(R.id.txtType);
            btnDelete = itemView.findViewById(R.id.btnDelete);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onClick(view, getAdapterPosition(), false);
        }

        @Override
        public boolean onLongClick(View view) {
            itemClickListener.onClick(view, getAdapterPosition(), true);
            return true;
        }
    }
}
