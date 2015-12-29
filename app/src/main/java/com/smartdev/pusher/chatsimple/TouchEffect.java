package com.smartdev.pusher.chatsimple;

import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by nguyenbon on 11/27/15.
 */
public class TouchEffect implements View.OnTouchListener
{

    /* (non-Javadoc)
     * @see android.view.View.OnTouchListener#onTouch(android.view.View, android.view.MotionEvent)
     */
    @Override
    public boolean onTouch(View v, MotionEvent event)
    {

        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {
            Drawable d = v.getBackground();
            d.mutate();
            d.setAlpha(150);
            v.setBackgroundDrawable(d);
        }
        else if (event.getAction() == MotionEvent.ACTION_UP
                || event.getAction() == MotionEvent.ACTION_CANCEL)
        {
            Drawable d = v.getBackground();
            d.setAlpha(255);
            v.setBackgroundDrawable(d);
        }
        return false;
    }

}

