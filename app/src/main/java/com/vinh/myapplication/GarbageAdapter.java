package com.vinh.myapplication;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class GarbageAdapter extends RecyclerView.Adapter<GarbageAdapter.MyViewHolder> {

    private GarbageActivity main;
    private List<Note> list, listSelectionNotes;
    private Context context;

    public GarbageAdapter(GarbageActivity main, List<Note> list) {
        this.main = main;
        this.list = list;
        listSelectionNotes = new ArrayList<>();
        context = main.getApplicationContext();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, int position) {
        final Note note = list.get(position);
        myViewHolder.title.setText(note.getNote_title());
        myViewHolder.type.setText(main.database.getTypeNote(note.getNote_type()).getType_name());
        if (note.isLock()) {
            myViewHolder.content.setText("Nội dung đã được ẩn!");
            myViewHolder.content.setTypeface(null, Typeface.ITALIC);
        } else {
            myViewHolder.content.setText(note.getNote_content());
        }
        myViewHolder.time.setText(Helper.toTimeString(note.getNote_time()));

        if (note.isStar()) {
            if (note.isLock()) {
                myViewHolder.img1.setVisibility(View.VISIBLE);
                myViewHolder.img2.setVisibility(View.VISIBLE);
                myViewHolder.img1.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_star_row));
                myViewHolder.img2.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_lock_row));
            } else {
                myViewHolder.img1.setVisibility(View.VISIBLE);
                myViewHolder.img1.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_star_row));
                myViewHolder.img2.setVisibility(View.INVISIBLE);
            }
        } else {
            if (note.isLock()) {
                myViewHolder.img1.setVisibility(View.VISIBLE);
                myViewHolder.img1.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_lock_row));
                myViewHolder.img2.setVisibility(View.INVISIBLE);
            } else {
                myViewHolder.img1.setVisibility(View.INVISIBLE);
                myViewHolder.img2.setVisibility(View.INVISIBLE);
            }
        }

        myViewHolder.setBackgroundColor(note.isSelected());
        // sự kiện click vào Note
        myViewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if (isLongClick) {
//                    Toast.makeText(context, "Long: " + note.getNote_title(), Toast.LENGTH_SHORT).show();

                    if (main.actionMode == null) {
                        main.actionMode = main.startSupportActionMode(main.actionModeCallback);
                    }

                    if (note.isSelected()) {
                        listSelectionNotes.remove(note);
                        note.setSelected(false);
                    } else {
                        note.setSelected(true);
                        listSelectionNotes.add(note);
                    }
                    myViewHolder.setBackgroundColor(note.isSelected());

                    int count = listSelectionNotes.size();
                    main.actionMode.setTitle(count + "");
                    main.actionMode.invalidate();
                } else {
                    //Toast.makeText(context, "Click: " + note.getNote_title(), Toast.LENGTH_SHORT).show();
                    if (main.actionMode != null) {
                        if (note.isSelected()) {
                            listSelectionNotes.remove(note);
                            note.setSelected(false);
                        } else {
                            note.setSelected(true);
                            listSelectionNotes.add(note);
                        }
                        myViewHolder.setBackgroundColor(note.isSelected());
                        int count = listSelectionNotes.size();
                        if (count == 0) {
                            main.actionMode.finish();
                            return;
                        } else {
                            main.actionMode.setTitle(count + "");
                            main.actionMode.invalidate();
                        }
                    } else {
                        // xem note

                        note.setSelected(false);
                        int pos = list.indexOf(note);
                        if (note.isLock()) confirmPassword(note, pos);
                        else main.moveToViewGarbage(note.getNote_id(), pos);
                    }
                }
            }
        });
    }

    public void confirmPassword(final Note note, final int position) {
        class Process implements Runnable {
            int id;
            int pos;

            private Process(int id, int pos) {
                this.id = id;
                this.pos = pos;
            }

            @Override
            public void run() {
                main.moveToViewGarbage(id, pos);
            }
        }
        Process process = new Process(note.getNote_id(), position);
        Helper.dialogConfirmPassword(main, process);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public boolean hasLockNote() {
        if (listSelectionNotes == null) return false;
        for (int i = 0; i < listSelectionNotes.size(); i++) {
            if (listSelectionNotes.get(i).isLock()) return true;
        }
        return false;
    }

    public void restoreSelectionNotes() {
        if (listSelectionNotes != null) {
            for (int i = 0; i < listSelectionNotes.size(); i++) {
                Note note = listSelectionNotes.get(i);
                int position = list.indexOf(note);
                list.remove(note);
                notifyItemRemoved(position);
                main.database.moveToDefault(note.getNote_id());
            }
            listSelectionNotes = new ArrayList<>();
        }
    }

    public void deleteSelectionNotes() {
        if (listSelectionNotes != null) {
            for (int i = 0; i < listSelectionNotes.size(); i++) {
                Note note = listSelectionNotes.get(i);
                int position = list.indexOf(note);
                list.remove(note);
                notifyItemRemoved(position);
                main.database.deleteNote(note.getNote_id());
            }
            listSelectionNotes = new ArrayList<>();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public TextView title, type, content, time;
        public ImageView img1, img2;

        public RelativeLayout layout;
        private ItemClickListener itemClickListener;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.row);
            title = itemView.findViewById(R.id.title);
            type = itemView.findViewById(R.id.type);
            content = itemView.findViewById(R.id.content);
            time = itemView.findViewById(R.id.time);
            img1 = itemView.findViewById(R.id.img1);
            img2 = itemView.findViewById(R.id.img2);

            Helper.setTextSizeForTitleAndContent(title, content);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void setBackgroundColor(boolean isSelected) {
            if (isSelected)
                layout.setBackgroundDrawable(main.getResources().getDrawable(R.drawable.shape_row_selected));
            else
                layout.setBackgroundDrawable(main.getResources().getDrawable(R.drawable.shape_row_default));
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
