package com.vinh.myapplication;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static com.vinh.myapplication.R.drawable.shape_row_default;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.MyViewHolder> {

    public List<Note> listNotes, listSelectionNotes;
    private Context context;
    private MainActivity mainActivity;


    public NoteAdapter(List<Note> listNotes, MainActivity mainActivity) {
        this.listNotes = listNotes;
        this.mainActivity = mainActivity;
        this.context = mainActivity.getApplicationContext();
        this.listSelectionNotes = new ArrayList<Note>();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, int position) {
        final Note note = listNotes.get(position);
        myViewHolder.title.setText(note.getNote_title());
        myViewHolder.type.setText(mainActivity.database.getTypeNote(note.getNote_type()).getType_name());
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

                    if (mainActivity.actionMode == null) {
                        mainActivity.actionMode = mainActivity.startSupportActionMode(mainActivity.actionModeCallback);
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
                    mainActivity.actionMode.setTitle(count + "");
                    mainActivity.actionMode.invalidate();
                } else {
                    //Toast.makeText(context, "Click: " + note.getNote_title(), Toast.LENGTH_SHORT).show();
                    if (mainActivity.actionMode != null) {
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
                            mainActivity.actionMode.finish();
                            return;
                        } else {
                            mainActivity.actionMode.setTitle(count + "");
                            mainActivity.actionMode.invalidate();
                        }
                    } else {
                        note.setSelected(false);
                        int pos = listNotes.indexOf(note);
                        if(note.isLock()) confirmPassword(note, pos);
                        else mainActivity.moveToViewActivity(note.getNote_id(), pos, Define.CMD_CHANGE_NOTE);
                    }
                }
            }
        });

    }

    public void confirmPassword(final Note note, final int position) {
        class Process implements Runnable{
            int id;
            int pos;

            private Process(int id, int pos){
                this.id = id;
                this.pos = pos;
            }
            @Override
            public void run() {
                mainActivity.moveToViewActivity(id, pos, Define.CMD_CHANGE_NOTE);
            }
        }
        Process process = new Process(note.getNote_id(), position);
        Helper.dialogConfirmPassword(mainActivity, process);
    }


    @Override
    public int getItemCount() {
        return listNotes.size();
    }


    public boolean hasLockNote(){
        if(listSelectionNotes == null) return false;
        for(int i=0; i<listSelectionNotes.size(); i++){
            if (listSelectionNotes.get(i).isLock()) return true;
        }
        return false;
    }

    public void deleteSelectionNotes() {
        if (listSelectionNotes == null || listSelectionNotes.size() == 0) return;

        for (int i = 0; i < listSelectionNotes.size(); i++) {
            Note note = listSelectionNotes.get(i);
            int index = listNotes.indexOf(note);
            listNotes.remove(index);
            mainActivity.database.moveToGarbage(note.getNote_id());
            notifyItemRemoved(index);
        }
        listSelectionNotes = new ArrayList<Note>();
    }

    public void moveSelectionNotes(int newTypeID) {
        if (listSelectionNotes == null || listSelectionNotes.size() == 0) return;
        for (int i = 0; i < listSelectionNotes.size(); i++) {
            Note note = listSelectionNotes.get(i);
            note.setNote_type(newTypeID);
            int index = listNotes.indexOf(note);
            listNotes.set(index, note);
            mainActivity.database.updateNote(note.getNote_id(), note);
            notifyItemChanged(index);
        }
        clearAllSelection();
    }

    public void clearAllSelection() {
        listSelectionNotes = new ArrayList<>();
        for (int i = 0; i < listNotes.size(); i++) {
            if (listNotes.get(i).isSelected()) {
                listNotes.get(i).setSelected(false);
                notifyItemChanged(i);
            }
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
                layout.setBackgroundDrawable(mainActivity.getResources().getDrawable(R.drawable.shape_row_selected));
            else
                layout.setBackgroundDrawable(mainActivity.getResources().getDrawable(R.drawable.shape_row_default));
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
