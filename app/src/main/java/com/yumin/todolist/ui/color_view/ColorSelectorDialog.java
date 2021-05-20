package com.yumin.todolist.ui.color_view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.yumin.todolist.R;

import java.util.ArrayList;
import java.util.List;

public class ColorSelectorDialog extends Dialog {
    private List<ColorView> mColorViewList;
    private GridView mColorGrid;
    private int mSelectPosition,mCurrentPosition;
    private ColorView mColorViewSelected;
    private TextView mDialogTitle;
    private Context mContext;
    private List<ColorViewInfo> mColors;
    private Button mButtonOK, mButtonCancel;
    private View.OnClickListener mClickListeanr;


    public ColorSelectorDialog(@NonNull Context context, List<ColorViewInfo> colors, int currentPosition) {
        super(context);
        mCurrentPosition = currentPosition;
        mContext = context;
        mColors = colors;
    }

    public void setClickListener(View.OnClickListener listener){
        this.mClickListeanr = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View mainView = LayoutInflater.from(mContext).inflate(R.layout.dialog_color_selector_layout, null, false);
        setContentView(mainView);
        initView(mContext,mColors,mCurrentPosition);
        setCanceledOnTouchOutside(false);
    }

    public void initView(Context context, List<ColorViewInfo> colors, int currentPosition){
        mColorGrid = findViewById(R.id.colorGridView);
        mDialogTitle = findViewById(R.id.textView);
        mButtonCancel = findViewById(R.id.buttonCancel);
        mButtonOK = findViewById(R.id.buttonOk);
        mButtonOK.setOnClickListener(mClickListeanr);
        mButtonCancel = findViewById(R.id.buttonCancel);
        mButtonCancel.setOnClickListener(mClickListeanr);
        mColorViewList = new ArrayList<ColorView>();

        mColorGrid.setNumColumns(4);
        mColorGrid.setGravity(Gravity.CENTER);
        mColorGrid.setVerticalSpacing(5);
        mColorGrid.setHorizontalSpacing(5);

        for(int position = 0; position<colors.size(); position++){
            ColorView colorView = new ColorView(context);
            colorView.setColorValue(colors.get(position).colorValue);

            if(currentPosition == position){
                colorView.setSelect(true);
                mColorViewSelected = colorView;
            }
            android.widget.AbsListView.LayoutParams colorParams = new android.widget.AbsListView.LayoutParams(
                    90, 90);
            colorView.setLayoutParams(colorParams);
            mColorViewList.add(colorView);
        }

        mColorGrid.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return mColorViewList.size();
            }

            @Override
            public Object getItem(int i) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View view, ViewGroup viewGroup) {
                if(view != null)
                    return view;
                else
                    return  mColorViewList.get(position);
            }
        });

        mColorGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if(view instanceof ColorView){
                    ColorView colorView = (ColorView) view;
                    if(mCurrentPosition != position){
                        mColorViewSelected.setSelect(false);
                        colorView.setSelect(true);
                        mCurrentPosition = position;
                        mColorViewSelected = colorView;
                    }
                }
            }
        });
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width =  ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.height =  ViewGroup.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(lp);

    }

    public int getPosition(){
        return mCurrentPosition;
    }

    public int getSelectColor(){
        if(mColorViewSelected != null)
            return mColorViewSelected.getColorValue();
        else
            return Color.TRANSPARENT;
    }
}

