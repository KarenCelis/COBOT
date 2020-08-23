package com.example.cobot.Utils;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cobot.R;
import com.squareup.picasso.Picasso;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private String[]options;
    private boolean hasImages;
    private String[]imageResourceIds;
    private OnOptionListener monOptionListener;
    private static final String TAG = "RecyclerViewAdapter";
    private int actionSelected;

    public RecyclerViewAdapter(String[] options, boolean hasImages, String[]imageResourceIds, OnOptionListener onOptionListener, int actionSelected){
        this.options = options;
        this.hasImages = hasImages;
        this.imageResourceIds = imageResourceIds;
        this.monOptionListener = onOptionListener;
        this.actionSelected = actionSelected;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.action_row, parent, false);
        return new ViewHolder(view, monOptionListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try{

            holder.TVOption.setText(options[position]);
            if(actionSelected!=-1 && actionSelected==position){
                Log.d(TAG, "ViewHolder: "+" hay una selección anterior en "+position);
                holder.cardView.setBackgroundColor(holder.cardView.getResources().getColor(R.color.pressed_color));
            }

            if(hasImages){
                Picasso.get().load(imageResourceIds[position]).into(holder.IVOption);
            }
            else{
                Picasso.get().load(imageResourceIds[0]).into(holder.IVOption);
            }

        }catch (NullPointerException e){
            Log.e(TAG, "onBindViewHolder: Null Pointer: " + e.getMessage() );
        }

    }

    @Override
    public int getItemCount() {
        return options.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView TVOption;
        ImageView IVOption;
        CardView cardView;

        public ViewHolder(@NonNull View itemView, OnOptionListener onOptionListener) {
            super(itemView);

            TVOption = itemView.findViewById(R.id.TVOption);
            IVOption = itemView.findViewById(R.id.IVOption);
            cardView = itemView.findViewById(R.id.CVOption);
            monOptionListener = onOptionListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            cardView.setBackgroundColor(v.getResources().getColor(R.color.pressed_color));
            Log.d(TAG, "onClick: " + getAdapterPosition());
            monOptionListener.onOptionClick(getAdapterPosition());
        }
    }

    public interface OnOptionListener{
        void onOptionClick(int position);
    }
}
