package model;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.EditText;

import com.example.jason.jasonweather2.R;

/**
 * Created by Jason on 2016/3/14.
 */
public class EditTextWithDel extends EditText {
    private final static String TAG="EditTextWithDel";
    private Drawable imgInable;

    private Context mContext;

    public EditTextWithDel(Context context)
    {
        super(context);
        mContext=context;
        init();
    }
    public EditTextWithDel(Context context,AttributeSet attributeSet,int defStyleAttr) {
        super(context, attributeSet, defStyleAttr);
        mContext=context;
        init();
    }


    public EditTextWithDel(Context context,AttributeSet attrs)
    {
        super(context,attrs);
        mContext=context;
        init();
    }
    private void init()
    {
        imgInable=mContext.getResources().getDrawable(R.drawable.search);
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setDrawable();
            }
        });

    }
    //
    private void setDrawable()
    {
        if(length()<1)
            setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        else
            setCompoundDrawablesWithIntrinsicBounds(null,null,imgInable,null);
    }

    //处理点击事件
/*
    public boolean onTouchEvent(MotionEvent event)
    {
        if(imgInable!=null && event.getAction()==MotionEvent.ACTION_UP)
        {
            int eventX= (int) event.getRawX();
            int eventY= (int) event.getRawY();

            Log.e(TAG, "eventX:" + eventX + "; eventY:" + eventY);
            Rect rect=new Rect();
            getGlobalVisibleRect(rect);
            Log.e(TAG, "rect.left:" + rect.left + " ;rect.top" + rect.top + "; rect.right:" + rect.right + " ;rect.bottom" + rect.bottom);
            rect.left=rect.right-80;
            Log.e(TAG, "rect.left:" + rect.left+" ;rect.top"+rect.top + "; rect.right:" + rect.right+" ;rect.bottom"+rect.bottom );
            System.out.printf("globalRect:" + rect);
            if(rect.contains(eventX,eventY))
            {
                setText("");
            }
        }
        return super.onTouchEvent(event);
    }*/

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}
