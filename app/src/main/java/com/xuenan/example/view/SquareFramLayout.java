package com.xuenan.example.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**使高度和宽度一致
 * Created by xuenan on 20168.
 */
public class SquareFramLayout extends FrameLayout {
    public SquareFramLayout(Context context, AttributeSet attrs,
                            int defStyle) {
        super(context, attrs, defStyle);
    }

    public SquareFramLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareFramLayout(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec),
                getDefaultSize(0, heightMeasureSpec));

        int childWidthSize = getMeasuredWidth();
        // 高度和宽度一样
        heightMeasureSpec = widthMeasureSpec = MeasureSpec.makeMeasureSpec(
                childWidthSize, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
