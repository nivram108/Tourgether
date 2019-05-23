package nctu.cs.cgv.itour.custom;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;

public class TogoListView extends RecyclerView {
    public TogoListView(Context context) {
        super(context);
    }

    public TogoListView(Context context,
                        AttributeSet attrs) {
        super(context, attrs);

    }
    public TogoListView(Context context,
                        AttributeSet attrs,
                        int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);

        if(clampedY){
            if(scrollY==0){
                //over Scroll at top
            }else {
                //over Scroll at Bottom
            }
        }
    }
}
