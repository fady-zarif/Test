package com.tromke.mydrive.Home.Adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tromke.mydrive.Models.ItemObjects;
import com.tromke.mydrive.R;
import com.tromke.mydrive.ActHome;

import java.util.List;

/**
 * Created by Devrath on 10-09-2016.
 */
public class Adpt_home extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ItemObjects> itemList;
    private Context context;
    private int lastPosition = -1;

    public Adpt_home(Context context, List<ItemObjects> itemList) {
        this.itemList = itemList;
        this.context = context;//
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adpt_home, null);
        VhGridDocs rcv = new VhGridDocs(layoutView);


        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View mView = inflater.inflate(R.layout.adpt_home, parent, false);
        viewHolder = new ViewHolderDocs(mView);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolderDocs vObjPatientAllergies = (ViewHolderDocs) holder;
        if(itemList.get(position) instanceof ItemObjects){
            ViewHolderAllergiesConfigure(vObjPatientAllergies, itemList.get(position),position);
            // Here you apply the animation when the view is bound
            setAnimation(vObjPatientAllergies.card_view, position);

        }
    }


    private void ViewHolderAllergiesConfigure(ViewHolderDocs viewHolder, final ItemObjects mObj, final int position) {

        viewHolder.txt_doc_name.setText(mObj.getName());
        viewHolder.img_doc_pic.setImageURI(mObj.getPhoto());

        viewHolder.img_delete_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ActHome)context).deleteDocument(position);
            }
        });

        viewHolder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ActHome)context).addDocument(position);

            }
        });

    }




    @Override
    public int getItemCount() {
        return this.itemList.size();
    }

    public void removeAt(int position) {
       /* items.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, items.size());*/
    }

    public class ViewHolderDocs extends RecyclerView.ViewHolder {

        public TextView txt_doc_name;
        public ImageView img_doc_pic;
        public CardView card_view;
        public ImageView img_delete_id;

        public ViewHolderDocs(View itemLayoutView) {
            super(itemLayoutView);
            txt_doc_name = (TextView) itemView.findViewById(R.id.txt_doc_name);
            img_doc_pic = (ImageView) itemView.findViewById(R.id.img_doc_pic);
            card_view = (CardView) itemView.findViewById(R.id.card_view);
            img_delete_id = (ImageView) itemView.findViewById(R.id.img_delete_id);
        }

    }

    /**
     * Here is the key method to apply the animation
     */
    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            animation.setStartTime(50000);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    public class VhGridDocs extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView txt_doc_name;
        public ImageView img_doc_pic;

        public VhGridDocs(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            txt_doc_name = (TextView) itemView.findViewById(R.id.txt_doc_name);
            img_doc_pic = (ImageView) itemView.findViewById(R.id.img_doc_pic);
        }

        @Override
        public void onClick(View view) {
            Toast.makeText(view.getContext(), "Clicked Position = " + getPosition(), Toast.LENGTH_SHORT).show();
        }
    }


}