package com.tromke.mydrive;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by drrao on 12/27/2016.
 */
public class TripsViewHolder extends RecyclerView.ViewHolder {
    TextView customerName;
    TextView contactNumber;
    TextView timings;
    Button acceptTrip;
    Button rejectTrip;
    CardView cardBaseLayout;


    public TripsViewHolder(View itemView) {
        super(itemView);
        customerName = (TextView) itemView.findViewById(R.id.customerName);
        contactNumber = (TextView) itemView.findViewById(R.id.contactNumber);
        timings = (TextView) itemView.findViewById(R.id.timings);
        acceptTrip = (Button) itemView.findViewById(R.id.accept_trip);
        rejectTrip = (Button) itemView.findViewById(R.id.reject_trip);
        cardBaseLayout = (CardView) itemView.findViewById(R.id.cardBaseLayout);
    }
}
