package com.example.petvillage.Others;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

public class NonScrollableRecyclerView extends RecyclerView {

    public NonScrollableRecyclerView(Context context) {
        super(context);
    }

    public NonScrollableRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NonScrollableRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthSpec, expandSpec);
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = getMeasuredHeight();
    }
}
