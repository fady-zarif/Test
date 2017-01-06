package com.tromke.mydrive.Home.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;
import com.tromke.mydrive.R;

/**
 * Created by Devrath on 11-09-2016.
 */
public class DlgTransparentProgress  extends Dialog {

    //private GifImageView iv;
    private ImageView imageView;
    private Uri uri;

    public DlgTransparentProgress(Context context, String text) {
        super(context, R.style.TransparentProgressDialog);
        WindowManager.LayoutParams wlmp = getWindow().getAttributes();
        wlmp.gravity = Gravity.CENTER_HORIZONTAL;
        getWindow().setAttributes(wlmp);
        setTitle(null);
        setCancelable(false);
        setOnCancelListener(null);
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        //iv = new GifImageView(context);
        //iv.setImageResource(R.drawable.doc_loader);
        //layout.addView(iv, params);

        uri = Uri.parse("android.resource://" + getContext().getPackageName() + "/" + R.drawable.doc_loader);
        imageView = new ImageView(getContext());
        //Picasso.with(getContext()).load(uri).into(imageView);
        Ion.with(imageView).load(uri.toString());
        layout.addView(imageView, params);

        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setTextColor(Color.WHITE);

        layout.addView(textView);

        addContentView(layout, params);
    }

    @Override
    public void show() {
        super.show();
        setCancelable(false);
//        RotateAnimation anim = new RotateAnimation(0.0f, 360.0f , Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f);
//        anim.setInterpolator(new LinearInterpolator());
//        anim.setRepeatCount(Animation.INFINITE);
//        anim.setDuration(3000);
//        iv.setAnimation(anim);
//        iv.startAnimation(anim);
    }
}