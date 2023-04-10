package com.example.planatrip;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private Context mContext;
    private List<MyObject> mMyObjects;
    private OnItemClickListener mListener;

    public MyAdapter(Context context, List<MyObject> myObjects) {
        mContext = context;
        mMyObjects = myObjects;
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(MyObject myObject, View view);
    }

    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        mOnItemLongClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_my, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MyObject myObject = mMyObjects.get(position);
        holder.textView1.setText(myObject.getNameOfTrip());

            final MyObject currentObject = mMyObjects.get(position);

            // Set the click listeners
/*            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(currentObject);
                    }
                }
            });*/

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (mOnItemLongClickListener != null) {
                        mOnItemLongClickListener.onItemLongClick(currentObject, view);
                        return true;
                    }
                    return false;
                }
            });
        }

    @Override
    public int getItemCount() {
        return mMyObjects.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder { //implements View.OnLongClickListener
        private TextView textView1;

        public ViewHolder(View itemView) {
            super(itemView);
            textView1 = itemView.findViewById(R.id.textViewNameOfTrip);
            //itemView.setOnLongClickListener(this);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            MyObject myObject = mMyObjects.get(position);
                            mListener.onItemClick(myObject);
                        }
                    }
                }
            });

        }
    }

    public interface OnItemClickListener {
        void onItemClick(MyObject myObject);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

}