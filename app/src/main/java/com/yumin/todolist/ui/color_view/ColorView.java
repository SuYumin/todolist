package com.yumin.todolist.ui.color_view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.yumin.todolist.R;

public class ColorView extends View {
    private Context mContext;
    private int mColorValue = Color.parseColor("#e57373"); //Default value
    private Paint mPaint;
    private Path path;
    private int mStrokeWidth = 8;
    private boolean mSelect = false;

    public ColorView(Context context) {
        super(context);
        initColorView(context);
    }

    public ColorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initColorView(context);
    }


    public ColorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initColorView(context);
    }


    public void initColorView(Context context){
        mContext = context;
        mPaint = new Paint();
        path = new Path();
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setDither(true);
        mPaint.setAntiAlias(true);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d("[ColorView]","[onDraw]");
        float cx = getWidth() / 2.0f;
        float cy = getHeight() / 2.0f;
        float radius = Math.min(getWidth(), getHeight());
        radius = ( radius - 20 ) / 2.0f;
        mPaint.setColor(mColorValue);
        canvas.drawCircle(cx, cy, radius, mPaint);


        if(mSelect){
            Paint.Style paintStyle = mPaint.getStyle();
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(Color.GRAY);
            path.moveTo(cx - 15, cy);
            path.lineTo(cx, cy + 15 );
            path.lineTo(cx + 15, cy - 15);
            canvas.drawPath(path,mPaint);
            mPaint.setColor(mColorValue);
            mPaint.setStyle(paintStyle);
        }
    }

    public void setColorValue(int color){
        this.mColorValue = color;
        mPaint.setColor(mColorValue);
        invalidate();
    }

    public int getColorValue(){
        return this.mColorValue;
    }

    public void setSelect(boolean select){
        this.mSelect = select;
        invalidate();
    }

    public boolean getSelect(){
        return this.mSelect;
    }
}