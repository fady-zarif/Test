package com.tromke.mydrive.Home.Camera;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.tromke.mydrive.Constants.Constants;


/**
 * Created by Miroslaw Stanek on 26.12.14.
 */
public class CameraFrameLayout extends FrameLayout {


    public CameraFrameLayout(Context context) {
        super(context);
    }

    public CameraFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CameraFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CameraFrameLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int widthWithoutPadding = width - getPaddingLeft() - getPaddingRight();
        int heigthWithoutPadding = height - getPaddingTop() - getPaddingBottom();

        int maxWidth = heigthWithoutPadding * Constants.RATIO;
        int maxHeight = widthWithoutPadding / Constants.RATIO;

        if (widthWithoutPadding > maxWidth) {
            width = maxWidth + getPaddingLeft() + getPaddingRight();
        } else {
            height = maxHeight + getPaddingTop() + getPaddingBottom();
        }

        setMeasuredDimension(width, height);
    }
}
