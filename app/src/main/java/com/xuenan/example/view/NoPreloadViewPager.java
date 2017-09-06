package com.xuenan.example.view;

import android.view.ViewGroup;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.support.v4.os.ParcelableCompat;
import android.support.v4.os.ParcelableCompatCreatorCallbacks;
import android.support.v4.view.KeyEventCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.support.v4.widget.EdgeEffectCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.Interpolator;
import android.widget.Scroller;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * 北京平安快轿科技有限公司版权所有
 * ————————————————————————————————
 * 创建时间: 2017/03/21  17:41
 * ————————————————————————————————
 * author: CWB
 * ————————————————————————————————
 * 备注:禁止预加载的viewpager
 */
public class NoPreloadViewPager extends ViewGroup {
    private static final String TAG = NoPreloadViewPager.class.getSimpleName();
    private static final boolean DEBUG = false;

    private static final boolean USE_CACHE = false;
    private static final int DEFAULT_OFFSCREEN_PAGES = 0;//默认是1
    private static final int MAX_SETTLE_DURATION = 600; // ms




    static class ItemInfo {
        Object object;
        int position;
        boolean scrolling;
    }

    private static final Comparator<ItemInfo> COMPARATOR = new Comparator<ItemInfo>() {
        @Override
        public int compare(ItemInfo lhs, ItemInfo rhs) {
            return lhs.position - rhs.position;
        }
    };

    private static final Interpolator sInterpolator = new Interpolator() {
        public float getInterpolation(float t) {
            // _o(t) = t * t * ((tension + 1) * t + tension)
            // o(t) = _o(t - 1) + 1
            t -= 1.0f;
            return t * t * t + 1.0f;
        }
    };

    private final ArrayList<ItemInfo> mItems = new ArrayList<ItemInfo>();

    private PagerAdapter mAdapter;
    private int mCurItem;   // Index of currently displayed page.
    private int mRestoredCurItem = -1;
    private Parcelable mRestoredAdapterState = null;
    private ClassLoader mRestoredClassLoader = null;
    private Scroller mScroller;
    private PagerObserver mObserver;

    private int mPageMargin;
    private Drawable mMarginDrawable;

    private int mChildWidthMeasureSpec;
    private int mChildHeightMeasureSpec;
    private boolean mInLayout;

    private boolean mScrollingCacheEnabled;

    private boolean mPopulatePending;
    private boolean mScrolling;
    private int mOffscreenPageLimit = DEFAULT_OFFSCREEN_PAGES;

    private boolean mIsBeingDragged;
    private boolean mIsUnableToDrag;
    private int mTouchSlop;
    private float mInitialMotionX;

    private float mLastMotionX;
    private float mLastMotionY;

    private int mActivePointerId = INVALID_POINTER;

    private static final int INVALID_POINTER = -1;


    private VelocityTracker mVelocityTracker;
    private int mMinimumVelocity;
    private int mMaximumVelocity;
    private float mBaseLineFlingVelocity;
    private float mFlingVelocityInfluence;

    private boolean mFakeDragging;
    private long mFakeDragBeginTime;

    private EdgeEffectCompat mLeftEdge;
    private EdgeEffectCompat mRightEdge;

    private boolean mFirstLayout = true;

    private OnPageChangeListener mOnPageChangeListener;

    public static final int SCROLL_STATE_IDLE = 0;

    public static final int SCROLL_STATE_DRAGGING = 1;

    public static final int SCROLL_STATE_SETTLING = 2;

    private int mScrollState = SCROLL_STATE_IDLE;


    public interface OnPageChangeListener {


        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

        public void onPageSelected(int position);


        public void onPageScrollStateChanged(int state);
    }


    public static class SimpleOnPageChangeListener implements OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }

    public NoPreloadViewPager(Context context) {
        super(context);
        initViewPager();
    }

    public NoPreloadViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViewPager();
    }

    void initViewPager() {
        setWillNotDraw(false);
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
        setFocusable(true);
        final Context context = getContext();
        mScroller = new Scroller(context, sInterpolator);
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
        mLeftEdge = new EdgeEffectCompat(context);
        mRightEdge = new EdgeEffectCompat(context);

        float density = context.getResources().getDisplayMetrics().density;
        mBaseLineFlingVelocity = 2500.0f * density;
        mFlingVelocityInfluence = 0.4f;
    }


    private void setScrollState(int newState) {
        if (mScrollState == newState) {

            return;
        }

        mScrollState = newState;
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageScrollStateChanged(newState);
        }
    }

    public void setAdapter(PagerAdapter adapter) {
        if (mAdapter != null) {
            mAdapter.startUpdate(this);
            for (int i = 0; i < mItems.size(); i++) {
                final ItemInfo ii = mItems.get(i);
                mAdapter.destroyItem(this, ii.position, ii.object);
            }
            mAdapter.finishUpdate(this);
            mItems.clear();
            removeAllViews();
            mCurItem = 0;
            scrollTo(0, 0);
        }

        mAdapter = adapter;

        if (mAdapter != null) {
            if (mObserver == null) {
                mObserver = new PagerObserver();
            }
            mPopulatePending = false;
            if (mRestoredCurItem >= 0) {
                mAdapter.restoreState(mRestoredAdapterState, mRestoredClassLoader);
                setCurrentItemInternal(mRestoredCurItem, false, true);
                mRestoredCurItem = -1;
                mRestoredAdapterState = null;
                mRestoredClassLoader = null;
            } else {
                populate();
            }
        }
    }

    public PagerAdapter getAdapter() {
        return mAdapter;
    }

    public void setCurrentItem(int item) {
        mPopulatePending = false;
        setCurrentItemInternal(item, !mFirstLayout, false);
    }
    public void setCurrentItem(int item, boolean smoothScroll) {
        mPopulatePending = false;
        setCurrentItemInternal(item, smoothScroll, false);
    }

    public int getCurrentItem() {
        return mCurItem;
    }

    void setCurrentItemInternal(int item, boolean smoothScroll, boolean always) {
        setCurrentItemInternal(item, smoothScroll, always, 0);
    }

    void setCurrentItemInternal(int item, boolean smoothScroll, boolean always, int velocity) {
        if (mAdapter == null || mAdapter.getCount() <= 0) {
            setScrollingCacheEnabled(false);
            return;
        }
        if (!always && mCurItem == item && mItems.size() != 0) {
            setScrollingCacheEnabled(false);
            return;
        }
        if (item < 0) {
            item = 0;
        } else if (item >= mAdapter.getCount()) {
            item = mAdapter.getCount() - 1;
        }
        final int pageLimit = mOffscreenPageLimit;
        if (item > (mCurItem + pageLimit) || item < (mCurItem - pageLimit)) {
            for (int i = 0; i < mItems.size(); i++) {
                mItems.get(i).scrolling = true;
            }
        }

        final boolean dispatchSelected = mCurItem != item;
        mCurItem = item;
        populate();
        final int destX = (getWidth() + mPageMargin) * item;
        if (smoothScroll) {
            smoothScrollTo(destX, 0, velocity);
            if (dispatchSelected && mOnPageChangeListener != null) {
                mOnPageChangeListener.onPageSelected(item);
            }
        } else {
            if (dispatchSelected && mOnPageChangeListener != null) {
                mOnPageChangeListener.onPageSelected(item);
            }
            completeScroll();
            scrollTo(destX, 0);
        }
    }

    public void setOnPageChangeListener(OnPageChangeListener listener) {
        mOnPageChangeListener = listener;
    }

    public int getOffscreenPageLimit() {
        return mOffscreenPageLimit;
    }

    public void setOffscreenPageLimit(int limit) {
        if (limit < DEFAULT_OFFSCREEN_PAGES) {
            Log.w(TAG, "Requested offscreen page limit " + limit + " too small; defaulting to " +
                    DEFAULT_OFFSCREEN_PAGES);
            limit = DEFAULT_OFFSCREEN_PAGES;
        }
        if (limit != mOffscreenPageLimit) {
            mOffscreenPageLimit = limit;
            populate();
        }
    }

    public void setPageMargin(int marginPixels) {
        final int oldMargin = mPageMargin;
        mPageMargin = marginPixels;

        final int width = getWidth();
        recomputeScrollPosition(width, width, marginPixels, oldMargin);

        requestLayout();
    }

    public int getPageMargin() {
        return mPageMargin;
    }

    public void setPageMarginDrawable(Drawable d) {
        mMarginDrawable = d;
        if (d != null) refreshDrawableState();
        setWillNotDraw(d == null);
        invalidate();
    }


    public void setPageMarginDrawable(int resId) {
        setPageMarginDrawable(getContext().getResources().getDrawable(resId));
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        return super.verifyDrawable(who) || who == mMarginDrawable;
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        final Drawable d = mMarginDrawable;
        if (d != null && d.isStateful()) {
            d.setState(getDrawableState());
        }
    }

    float distanceInfluenceForSnapDuration(float f) {
        f -= 0.5f;
        f *= 0.3f * Math.PI / 2.0f;
        return (float) Math.sin(f);
    }


    void smoothScrollTo(int x, int y) {
        smoothScrollTo(x, y, 0);
    }


    void smoothScrollTo(int x, int y, int velocity) {
        if (getChildCount() == 0) {
            setScrollingCacheEnabled(false);
            return;
        }
        int sx = getScrollX();
        int sy = getScrollY();
        int dx = x - sx;
        int dy = y - sy;
        if (dx == 0 && dy == 0) {
            completeScroll();
            setScrollState(SCROLL_STATE_IDLE);
            return;
        }

        setScrollingCacheEnabled(true);
        mScrolling = true;
        setScrollState(SCROLL_STATE_SETTLING);

        final float pageDelta = (float) Math.abs(dx) / (getWidth() + mPageMargin);
        int duration = (int) (pageDelta * 100);

        velocity = Math.abs(velocity);
        if (velocity > 0) {
            duration += (duration / (velocity / mBaseLineFlingVelocity)) * mFlingVelocityInfluence;
        } else {
            duration += 100;
        }
        duration = Math.min(duration, MAX_SETTLE_DURATION);

        mScroller.startScroll(sx, sy, dx, dy, duration);
        invalidate();
    }

    void addNewItem(int position, int index) {
        ItemInfo ii = new ItemInfo();
        ii.position = position;
        ii.object = mAdapter.instantiateItem(this, position);
        if (index < 0) {
            mItems.add(ii);
        } else {
            mItems.add(index, ii);
        }
    }

    void dataSetChanged() {

        boolean needPopulate = mItems.size() < 3 && mItems.size() < mAdapter.getCount();
        int newCurrItem = -1;

        for (int i = 0; i < mItems.size(); i++) {
            final ItemInfo ii = mItems.get(i);
            final int newPos = mAdapter.getItemPosition(ii.object);

            if (newPos == PagerAdapter.POSITION_UNCHANGED) {
                continue;
            }

            if (newPos == PagerAdapter.POSITION_NONE) {
                mItems.remove(i);
                i--;
                mAdapter.destroyItem(this, ii.position, ii.object);
                needPopulate = true;

                if (mCurItem == ii.position) {
                    newCurrItem = Math.max(0, Math.min(mCurItem, mAdapter.getCount() - 1));
                }
                continue;
            }

            if (ii.position != newPos) {
                if (ii.position == mCurItem) {
                    newCurrItem = newPos;
                }

                ii.position = newPos;
                needPopulate = true;
            }
        }

        Collections.sort(mItems, COMPARATOR);

        if (newCurrItem >= 0) {
            setCurrentItemInternal(newCurrItem, false, true);
            needPopulate = true;
        }
        if (needPopulate) {
            populate();
            requestLayout();
        }
    }

    void populate() {
        if (mAdapter == null) {
            return;
        }

        if (mPopulatePending) {
            if (DEBUG) Log.i(TAG, "populate is pending, skipping for now...");
            return;
        }

        if (getWindowToken() == null) {
            return;
        }

        mAdapter.startUpdate(this);

        final int pageLimit = mOffscreenPageLimit;
        final int startPos = Math.max(0, mCurItem - pageLimit);
        final int N = mAdapter.getCount();
        final int endPos = Math.min(N - 1, mCurItem + pageLimit);

        if (DEBUG) Log.v(TAG, "populating: startPos=" + startPos + " endPos=" + endPos);


        int lastPos = -1;
        for (int i = 0; i < mItems.size(); i++) {
            ItemInfo ii = mItems.get(i);
            if ((ii.position < startPos || ii.position > endPos) && !ii.scrolling) {
                if (DEBUG) Log.i(TAG, "removing: " + ii.position + " @ " + i);
                mItems.remove(i);
                i--;
                mAdapter.destroyItem(this, ii.position, ii.object);
            } else if (lastPos < endPos && ii.position > startPos) {
                lastPos++;
                if (lastPos < startPos) {
                    lastPos = startPos;
                }
                while (lastPos <= endPos && lastPos < ii.position) {
                    if (DEBUG) Log.i(TAG, "inserting: " + lastPos + " @ " + i);
                    addNewItem(lastPos, i);
                    lastPos++;
                    i++;
                }
            }
            lastPos = ii.position;
        }

        lastPos = mItems.size() > 0 ? mItems.get(mItems.size() - 1).position : -1;
        if (lastPos < endPos) {
            lastPos++;
            lastPos = lastPos > startPos ? lastPos : startPos;
            while (lastPos <= endPos) {
                if (DEBUG) Log.i(TAG, "appending: " + lastPos);
                addNewItem(lastPos, -1);
                lastPos++;
            }
        }

        if (DEBUG) {
            Log.i(TAG, "Current page list:");
            for (int i = 0; i < mItems.size(); i++) {
                Log.i(TAG, "#" + i + ": page " + mItems.get(i).position);
            }
        }

        ItemInfo curItem = null;
        for (int i = 0; i < mItems.size(); i++) {
            if (mItems.get(i).position == mCurItem) {
                curItem = mItems.get(i);
                break;
            }
        }
        mAdapter.setPrimaryItem(this, mCurItem, curItem != null ? curItem.object : null);

        mAdapter.finishUpdate(this);

        if (hasFocus()) {
            View currentFocused = findFocus();
            ItemInfo ii = currentFocused != null ? infoForAnyChild(currentFocused) : null;
            if (ii == null || ii.position != mCurItem) {
                for (int i = 0; i < getChildCount(); i++) {
                    View child = getChildAt(i);
                    ii = infoForChild(child);
                    if (ii != null && ii.position == mCurItem) {
                        if (child.requestFocus(FOCUS_FORWARD)) {
                            break;
                        }
                    }
                }
            }
        }
    }

    public static class SavedState extends BaseSavedState {
        int position;
        Parcelable adapterState;
        ClassLoader loader;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(position);
            out.writeParcelable(adapterState, flags);
        }

        @Override
        public String toString() {
            return "FragmentPager.SavedState{"
                    + Integer.toHexString(System.identityHashCode(this))
                    + " position=" + position + "}";
        }

        public static final Creator<SavedState> CREATOR
                = ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in, ClassLoader loader) {
                return new SavedState(in, loader);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        });

        SavedState(Parcel in, ClassLoader loader) {
            super(in);
            if (loader == null) {
                loader = getClass().getClassLoader();
            }
            position = in.readInt();
            adapterState = in.readParcelable(loader);
            this.loader = loader;
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.position = mCurItem;
        if (mAdapter != null) {
            ss.adapterState = mAdapter.saveState();
        }
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        if (mAdapter != null) {
            mAdapter.restoreState(ss.adapterState, ss.loader);
            setCurrentItemInternal(ss.position, false, true);
        } else {
            mRestoredCurItem = ss.position;
            mRestoredAdapterState = ss.adapterState;
            mRestoredClassLoader = ss.loader;
        }
    }
    @Override
    public void addView(View child, int index, LayoutParams params) {
        addViewInLayout(child, index, params);
        child.measure(mChildWidthMeasureSpec, mChildHeightMeasureSpec);
//        if (mInLayout) {
//        //可重复添加
//        } else {
//            super.addView(child, index, params);
//        }

        if (USE_CACHE) {
            if (child.getVisibility() != GONE) {
                child.setDrawingCacheEnabled(mScrollingCacheEnabled);
            } else {
                child.setDrawingCacheEnabled(false);
            }
        }
    }

    ItemInfo infoForChild(View child) {
        for (int i = 0; i < mItems.size(); i++) {
            ItemInfo ii = mItems.get(i);
            if (mAdapter.isViewFromObject(child, ii.object)) {
                return ii;
            }
        }
        return null;
    }

    ItemInfo infoForAnyChild(View child) {
        ViewParent parent;
        while ((parent = child.getParent()) != this) {
            if (parent == null || !(parent instanceof View)) {
                return null;
            }
            child = (View) parent;
        }
        return infoForChild(child);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mFirstLayout = true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec),
                getDefaultSize(0, heightMeasureSpec));

        mChildWidthMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth() -
                getPaddingLeft() - getPaddingRight(), MeasureSpec.EXACTLY);
        mChildHeightMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredHeight() -
                getPaddingTop() - getPaddingBottom(), MeasureSpec.EXACTLY);

        mInLayout = true;
        populate();
        mInLayout = false;

        final int size = getChildCount();
        for (int i = 0; i < size; ++i) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                if (DEBUG) Log.v(TAG, "Measuring #" + i + " " + child
                        + ": " + mChildWidthMeasureSpec);
                child.measure(mChildWidthMeasureSpec, mChildHeightMeasureSpec);
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // Make sure scroll position is set correctly.
        if (w != oldw) {
            recomputeScrollPosition(w, oldw, mPageMargin, mPageMargin);
        }
    }

    private void recomputeScrollPosition(int width, int oldWidth, int margin, int oldMargin) {
        final int widthWithMargin = width + margin;
        if (oldWidth > 0) {
            final int oldScrollPos = getScrollX();
            final int oldwwm = oldWidth + oldMargin;
            final int oldScrollItem = oldScrollPos / oldwwm;
            final float scrollOffset = (float) (oldScrollPos % oldwwm) / oldwwm;
            final int scrollPos = (int) ((oldScrollItem + scrollOffset) * widthWithMargin);
            scrollTo(scrollPos, getScrollY());
            if (!mScroller.isFinished()) {
                // We now return to your regularly scheduled scroll, already in progress.
                final int newDuration = mScroller.getDuration() - mScroller.timePassed();
                mScroller.startScroll(scrollPos, 0, mCurItem * widthWithMargin, 0, newDuration);
            }
        } else {
            int scrollPos = mCurItem * widthWithMargin;
            if (scrollPos != getScrollX()) {
                completeScroll();
                scrollTo(scrollPos, getScrollY());
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mInLayout = true;
        populate();
        mInLayout = false;

        final int count = getChildCount();
        final int width = r - l;

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            ItemInfo ii;
            if (child.getVisibility() != GONE && (ii = infoForChild(child)) != null) {
                int loff = (width + mPageMargin) * ii.position;
                int childLeft = getPaddingLeft() + loff;
                int childTop = getPaddingTop();
                if (DEBUG) Log.v(TAG, "Positioning #" + i + " " + child + " f=" + ii.object
                        + ":" + childLeft + "," + childTop + " " + child.getMeasuredWidth()
                        + "x" + child.getMeasuredHeight());
                child.layout(childLeft, childTop,
                        childLeft + child.getMeasuredWidth(),
                        childTop + child.getMeasuredHeight());
            }
        }
        mFirstLayout = false;
    }

    @Override
    public void computeScroll() {
        if (DEBUG) Log.i(TAG, "computeScroll: finished=" + mScroller.isFinished());
        if (!mScroller.isFinished()) {
            if (mScroller.computeScrollOffset()) {
                if (DEBUG) Log.i(TAG, "computeScroll: still scrolling");
                int oldX = getScrollX();
                int oldY = getScrollY();
                int x = mScroller.getCurrX();
                int y = mScroller.getCurrY();

                if (oldX != x || oldY != y) {
                    scrollTo(x, y);
                }

                if (mOnPageChangeListener != null) {
                    final int widthWithMargin = getWidth() + mPageMargin;
                    final int position = x / widthWithMargin;
                    final int offsetPixels = x % widthWithMargin;
                    final float offset = (float) offsetPixels / widthWithMargin;
                    mOnPageChangeListener.onPageScrolled(position, offset, offsetPixels);
                }

                invalidate();
                return;
            }
        }


        completeScroll();
    }

    private void completeScroll() {
        boolean needPopulate = mScrolling;
        if (needPopulate) {
            // Done with scroll, no longer want to cache view drawing.
            setScrollingCacheEnabled(false);
            mScroller.abortAnimation();
            int oldX = getScrollX();
            int oldY = getScrollY();
            int x = mScroller.getCurrX();
            int y = mScroller.getCurrY();
            if (oldX != x || oldY != y) {
                scrollTo(x, y);
            }
            setScrollState(SCROLL_STATE_IDLE);
        }
        mPopulatePending = false;
        mScrolling = false;
        for (int i = 0; i < mItems.size(); i++) {
            ItemInfo ii = mItems.get(i);
            if (ii.scrolling) {
                needPopulate = true;
                ii.scrolling = false;
            }
        }
        if (needPopulate) {
            populate();
        }
    }

    //closed  scroll


    private Boolean isScrollable = true;
        @Override
        public boolean onTouchEvent(MotionEvent ev) {
            if (!isScrollable == false) {
                return false;
            } else {
                return super.onTouchEvent(ev);
            }

        }  @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            if (!isScrollable == false) {
                return false;
            } else {
                return super.onInterceptTouchEvent(ev);
            }

        }



//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//
//        final int action = ev.getAction() & MotionEventCompat.ACTION_MASK;
//
//
//        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
//
//            if (DEBUG) Log.v(TAG, "Intercept done!");
//            mIsBeingDragged = false;
//            mIsUnableToDrag = false;
//            mActivePointerId = INVALID_POINTER;
//            return false;
//        }
//
//        if (action != MotionEvent.ACTION_DOWN) {
//            if (mIsBeingDragged) {
//                if (DEBUG) Log.v(TAG, "Intercept returning true!");
//                return true;
//            }
//            if (mIsUnableToDrag) {
//                if (DEBUG) Log.v(TAG, "Intercept returning false!");
//                return false;
//            }
//        }
//
//        switch (action) {
//            case MotionEvent.ACTION_MOVE: {
//
//                final int activePointerId = mActivePointerId;
//                if (activePointerId == INVALID_POINTER) {
//
//                    break;
//                }
//
//                final int pointerIndex = MotionEventCompat.findPointerIndex(ev, activePointerId);
//                final float x = MotionEventCompat.getX(ev, pointerIndex);
//                final float dx = x - mLastMotionX;
//                final float xDiff = Math.abs(dx);
//                final float y = MotionEventCompat.getY(ev, pointerIndex);
//                final float yDiff = Math.abs(y - mLastMotionY);
//                final int scrollX = getScrollX();
//                final boolean atEdge = (dx > 0 && scrollX == 0) || (dx < 0 && mAdapter != null &&
//                        scrollX >= (mAdapter.getCount() - 1) * getWidth() - 1);
//                if (DEBUG) Log.v(TAG, "Moved x to " + x + "," + y + " diff=" + xDiff + "," + yDiff);
//
//                if (canScroll(this, false, (int) dx, (int) x, (int) y)) {
//                    // Nested view has scrollable area under this point. Let it be handled there.
//                    mInitialMotionX = mLastMotionX = x;
//                    mLastMotionY = y;
//                    return false;
//                }
//                if (xDiff > mTouchSlop && xDiff > yDiff) {
//                    if (DEBUG) Log.v(TAG, "Starting drag!");
//                    mIsBeingDragged = true;
//                    setScrollState(SCROLL_STATE_DRAGGING);
//                    mLastMotionX = x;
//                    setScrollingCacheEnabled(true);
//                } else {
//                    if (yDiff > mTouchSlop) {
//
//                        if (DEBUG) Log.v(TAG, "Starting unable to drag!");
//                        mIsUnableToDrag = true;
//                    }
//                }
//                break;
//            }
//
//            case MotionEvent.ACTION_DOWN: {
//
//                mLastMotionX = mInitialMotionX = ev.getX();
//                mLastMotionY = ev.getY();
//                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
//
//                if (mScrollState == SCROLL_STATE_SETTLING) {
//
//                    mIsBeingDragged = true;
//                    mIsUnableToDrag = false;
//                    setScrollState(SCROLL_STATE_DRAGGING);
//                } else {
//                    completeScroll();
//                    mIsBeingDragged = false;
//                    mIsUnableToDrag = false;
//                }
//
//                if (DEBUG) Log.v(TAG, "Down at " + mLastMotionX + "," + mLastMotionY
//                        + " mIsBeingDragged=" + mIsBeingDragged
//                        + "mIsUnableToDrag=" + mIsUnableToDrag);
//                break;
//            }
//
//            case MotionEventCompat.ACTION_POINTER_UP:
//                onSecondaryPointerUp(ev);
//                break;
//        }
//
//
//        return mIsBeingDragged;
//    }
//
//
//    @Override
//    public boolean onTouchEvent(MotionEvent ev) {
//        if (mFakeDragging) {
//
//            return true;
//        }
//
//        if (ev.getAction() == MotionEvent.ACTION_DOWN && ev.getEdgeFlags() != 0) {
//
//            return false;
//        }
//
//        if (mAdapter == null || mAdapter.getCount() == 0) {
//
//            return false;
//        }
//
//        if (mVelocityTracker == null) {
//            mVelocityTracker = VelocityTracker.obtain();
//        }
//        mVelocityTracker.addMovement(ev);
//
//        final int action = ev.getAction();
//        boolean needsInvalidate = false;
//
//        switch (action & MotionEventCompat.ACTION_MASK) {
//            case MotionEvent.ACTION_DOWN: {
//
//                completeScroll();
//
//
//                mLastMotionX = mInitialMotionX = ev.getX();
//                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
//                break;
//            }
//            case MotionEvent.ACTION_MOVE:
//                if (!mIsBeingDragged) {
//                    final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
//                    final float x = MotionEventCompat.getX(ev, pointerIndex);
//                    final float xDiff = Math.abs(x - mLastMotionX);
//                    final float y = MotionEventCompat.getY(ev, pointerIndex);
//                    final float yDiff = Math.abs(y - mLastMotionY);
//                    if (DEBUG)
//                        Log.v(TAG, "Moved x to " + x + "," + y + " diff=" + xDiff + "," + yDiff);
//                    if (xDiff > mTouchSlop && xDiff > yDiff) {
//                        if (DEBUG) Log.v(TAG, "Starting drag!");
//                        mIsBeingDragged = true;
//                        mLastMotionX = x;
//                        setScrollState(SCROLL_STATE_DRAGGING);
//                        setScrollingCacheEnabled(true);
//                    }
//                }
//                if (mIsBeingDragged) {
//
//                    final int activePointerIndex = MotionEventCompat.findPointerIndex(
//                            ev, mActivePointerId);
//                    final float x = MotionEventCompat.getX(ev, activePointerIndex);
//                    final float deltaX = mLastMotionX - x;
//                    mLastMotionX = x;
//                    float oldScrollX = getScrollX();
//                    float scrollX = oldScrollX + deltaX;
//                    final int width = getWidth();
//                    final int widthWithMargin = width + mPageMargin;
//
//                    final int lastItemIndex = mAdapter.getCount() - 1;
//                    final float leftBound = Math.max(0, (mCurItem - 1) * widthWithMargin);
//                    final float rightBound =
//                            Math.min(mCurItem + 1, lastItemIndex) * widthWithMargin;
//                    if (scrollX < leftBound) {
//                        if (leftBound == 0) {
//                            float over = -scrollX;
//                            needsInvalidate = mLeftEdge.onPull(over / width);
//                        }
//                        scrollX = leftBound;
//                    } else if (scrollX > rightBound) {
//                        if (rightBound == lastItemIndex * widthWithMargin) {
//                            float over = scrollX - rightBound;
//                            needsInvalidate = mRightEdge.onPull(over / width);
//                        }
//                        scrollX = rightBound;
//                    }
//
//                    mLastMotionX += scrollX - (int) scrollX;
//                    scrollTo((int) scrollX, getScrollY());
//                    if (mOnPageChangeListener != null) {
//                        final int position = (int) scrollX / widthWithMargin;
//                        final int positionOffsetPixels = (int) scrollX % widthWithMargin;
//                        final float positionOffset = (float) positionOffsetPixels / widthWithMargin;
//                        mOnPageChangeListener.onPageScrolled(position, positionOffset,
//                                positionOffsetPixels);
//                    }
//                }
//                break;
//            case MotionEvent.ACTION_UP:
//                if (mIsBeingDragged) {
//                    final VelocityTracker velocityTracker = mVelocityTracker;
//                    velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
//                    int initialVelocity = (int) VelocityTrackerCompat.getXVelocity(
//                            velocityTracker, mActivePointerId);
//                    mPopulatePending = true;
//                    final int widthWithMargin = getWidth() + mPageMargin;
//                    final int scrollX = getScrollX();
//                    final int currentPage = scrollX / widthWithMargin;
//                    int nextPage = initialVelocity > 0 ? currentPage : currentPage + 1;
//                    setCurrentItemInternal(nextPage, true, true, initialVelocity);
//
//                    mActivePointerId = INVALID_POINTER;
//                    endDrag();
//                    needsInvalidate = mLeftEdge.onRelease() | mRightEdge.onRelease();
//                }
//                break;
//            case MotionEvent.ACTION_CANCEL:
//                if (mIsBeingDragged) {
//                    setCurrentItemInternal(mCurItem, true, true);
//                    mActivePointerId = INVALID_POINTER;
//                    endDrag();
//                    needsInvalidate = mLeftEdge.onRelease() | mRightEdge.onRelease();
//                }
//                break;
//            case MotionEventCompat.ACTION_POINTER_DOWN: {
//                final int index = MotionEventCompat.getActionIndex(ev);
//                final float x = MotionEventCompat.getX(ev, index);
//                mLastMotionX = x;
//                mActivePointerId = MotionEventCompat.getPointerId(ev, index);
//                break;
//            }
//            case MotionEventCompat.ACTION_POINTER_UP:
//                onSecondaryPointerUp(ev);
//                mLastMotionX = MotionEventCompat.getX(ev,
//                        MotionEventCompat.findPointerIndex(ev, mActivePointerId));
//                break;
//        }
//        if (needsInvalidate) {
//            invalidate();
//        }
//        return true;
//    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        boolean needsInvalidate = false;

        final int overScrollMode = ViewCompat.getOverScrollMode(this);
        if (overScrollMode == ViewCompat.OVER_SCROLL_ALWAYS ||
                (overScrollMode == ViewCompat.OVER_SCROLL_IF_CONTENT_SCROLLS &&
                        mAdapter != null && mAdapter.getCount() > 1)) {
            if (!mLeftEdge.isFinished()) {
                final int restoreCount = canvas.save();
                final int height = getHeight() - getPaddingTop() - getPaddingBottom();

                canvas.rotate(270);
                canvas.translate(-height + getPaddingTop(), 0);
                mLeftEdge.setSize(height, getWidth());
                needsInvalidate |= mLeftEdge.draw(canvas);
                canvas.restoreToCount(restoreCount);
            }
            if (!mRightEdge.isFinished()) {
                final int restoreCount = canvas.save();
                final int width = getWidth();
                final int height = getHeight() - getPaddingTop() - getPaddingBottom();
                final int itemCount = mAdapter != null ? mAdapter.getCount() : 1;

                canvas.rotate(90);
                canvas.translate(-getPaddingTop(),
                        -itemCount * (width + mPageMargin) + mPageMargin);
                mRightEdge.setSize(height, width);
                needsInvalidate |= mRightEdge.draw(canvas);
                canvas.restoreToCount(restoreCount);
            }
        } else {
            mLeftEdge.finish();
            mRightEdge.finish();
        }

        if (needsInvalidate) {
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw the margin drawable if needed.
        if (mPageMargin > 0 && mMarginDrawable != null) {
            final int scrollX = getScrollX();
            final int width = getWidth();
            final int offset = scrollX % (width + mPageMargin);
            if (offset != 0) {
                // Pages fit completely when settled; we only need to draw when in between
                final int left = scrollX - offset + width;
                mMarginDrawable.setBounds(left, 0, left + mPageMargin, getHeight());
                mMarginDrawable.draw(canvas);
            }
        }
    }


    public boolean beginFakeDrag() {
        if (mIsBeingDragged) {
            return false;
        }
        mFakeDragging = true;
        setScrollState(SCROLL_STATE_DRAGGING);
        mInitialMotionX = mLastMotionX = 0;
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        } else {
            mVelocityTracker.clear();
        }
        final long time = SystemClock.uptimeMillis();
        final MotionEvent ev = MotionEvent.obtain(time, time, MotionEvent.ACTION_DOWN, 0, 0, 0);
        mVelocityTracker.addMovement(ev);
        ev.recycle();
        mFakeDragBeginTime = time;
        return true;
    }

    public void endFakeDrag() {
        if (!mFakeDragging) {
            throw new IllegalStateException("No fake drag in progress. Call beginFakeDrag first.");
        }

        final VelocityTracker velocityTracker = mVelocityTracker;
        velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
        int initialVelocity = (int) VelocityTrackerCompat.getYVelocity(
                velocityTracker, mActivePointerId);
        mPopulatePending = true;
        if ((Math.abs(initialVelocity) > mMinimumVelocity)
                || Math.abs(mInitialMotionX - mLastMotionX) >= (getWidth() / 3)) {
            if (mLastMotionX > mInitialMotionX) {
                setCurrentItemInternal(mCurItem - 1, true, true);
            } else {
                setCurrentItemInternal(mCurItem + 1, true, true);
            }
        } else {
            setCurrentItemInternal(mCurItem, true, true);
        }
        endDrag();

        mFakeDragging = false;
    }


    public void fakeDragBy(float xOffset) {
        if (!mFakeDragging) {
            throw new IllegalStateException("No fake drag in progress. Call beginFakeDrag first.");
        }

        mLastMotionX += xOffset;
        float scrollX = getScrollX() - xOffset;
        final int width = getWidth();
        final int widthWithMargin = width + mPageMargin;

        final float leftBound = Math.max(0, (mCurItem - 1) * widthWithMargin);
        final float rightBound =
                Math.min(mCurItem + 1, mAdapter.getCount() - 1) * widthWithMargin;
        if (scrollX < leftBound) {
            scrollX = leftBound;
        } else if (scrollX > rightBound) {
            scrollX = rightBound;
        }
        mLastMotionX += scrollX - (int) scrollX;
        scrollTo((int) scrollX, getScrollY());
        if (mOnPageChangeListener != null) {
            final int position = (int) scrollX / widthWithMargin;
            final int positionOffsetPixels = (int) scrollX % widthWithMargin;
            final float positionOffset = (float) positionOffsetPixels / widthWithMargin;
            mOnPageChangeListener.onPageScrolled(position, positionOffset,
                    positionOffsetPixels);
        }

        final long time = SystemClock.uptimeMillis();
        final MotionEvent ev = MotionEvent.obtain(mFakeDragBeginTime, time, MotionEvent.ACTION_MOVE,
                mLastMotionX, 0, 0);
        mVelocityTracker.addMovement(ev);
        ev.recycle();
    }

    public boolean isFakeDragging() {
        return mFakeDragging;
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = MotionEventCompat.getActionIndex(ev);
        final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
        if (pointerId == mActivePointerId) {

            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mLastMotionX = MotionEventCompat.getX(ev, newPointerIndex);
            mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
            if (mVelocityTracker != null) {
                mVelocityTracker.clear();
            }
        }
    }

    private void endDrag() {
        mIsBeingDragged = false;
        mIsUnableToDrag = false;

        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    private void setScrollingCacheEnabled(boolean enabled) {
        if (mScrollingCacheEnabled != enabled) {
            mScrollingCacheEnabled = enabled;
            if (USE_CACHE) {
                final int size = getChildCount();
                for (int i = 0; i < size; ++i) {
                    final View child = getChildAt(i);
                    if (child.getVisibility() != GONE) {
                        child.setDrawingCacheEnabled(enabled);
                    }
                }
            }
        }
    }


    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        if (v instanceof ViewGroup) {
            final ViewGroup group = (ViewGroup) v;
            final int scrollX = v.getScrollX();
            final int scrollY = v.getScrollY();
            final int count = group.getChildCount();

            for (int i = count - 1; i >= 0; i--) {

                final View child = group.getChildAt(i);
                if (x + scrollX >= child.getLeft() && x + scrollX < child.getRight() &&
                        y + scrollY >= child.getTop() && y + scrollY < child.getBottom() &&
                        canScroll(child, true, dx, x + scrollX - child.getLeft(),
                                y + scrollY - child.getTop())) {
                    return true;
                }
            }
        }

        return checkV && ViewCompat.canScrollHorizontally(v, -dx);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        return super.dispatchKeyEvent(event) || executeKeyEvent(event);
    }

    public boolean executeKeyEvent(KeyEvent event) {
        boolean handled = false;
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    handled = arrowScroll(FOCUS_LEFT);
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    handled = arrowScroll(FOCUS_RIGHT);
                    break;
                case KeyEvent.KEYCODE_TAB:
                    if (KeyEventCompat.hasNoModifiers(event)) {
                        handled = arrowScroll(FOCUS_FORWARD);
                    } else if (KeyEventCompat.hasModifiers(event, KeyEvent.META_SHIFT_ON)) {
                        handled = arrowScroll(FOCUS_BACKWARD);
                    }
                    break;
            }
        }
        return handled;
    }

    public boolean arrowScroll(int direction) {
        View currentFocused = findFocus();
        if (currentFocused == this) currentFocused = null;

        boolean handled = false;

        View nextFocused = FocusFinder.getInstance().findNextFocus(this, currentFocused,
                direction);
        if (nextFocused != null && nextFocused != currentFocused) {
            if (direction == View.FOCUS_LEFT) {

                if (currentFocused != null && nextFocused.getLeft() >= currentFocused.getLeft()) {
                    handled = pageLeft();
                } else {
                    handled = nextFocused.requestFocus();
                }
            } else if (direction == View.FOCUS_RIGHT) {

                if (currentFocused != null && nextFocused.getLeft() <= currentFocused.getLeft()) {
                    handled = pageRight();
                } else {
                    handled = nextFocused.requestFocus();
                }
            }
        } else if (direction == FOCUS_LEFT || direction == FOCUS_BACKWARD) {

            handled = pageLeft();
        } else if (direction == FOCUS_RIGHT || direction == FOCUS_FORWARD) {

            handled = pageRight();
        }
        if (handled) {
            playSoundEffect(SoundEffectConstants.getContantForFocusDirection(direction));
        }
        return handled;
    }

    boolean pageLeft() {
        if (mCurItem > 0) {
            setCurrentItem(mCurItem - 1, true);
            return true;
        }
        return false;
    }

    boolean pageRight() {
        if (mAdapter != null && mCurItem < (mAdapter.getCount() - 1)) {
            setCurrentItem(mCurItem + 1, true);
            return true;
        }
        return false;
    }


    @Override
    public void addFocusables(ArrayList<View> views, int direction, int focusableMode) {
        final int focusableCount = views.size();

        final int descendantFocusability = getDescendantFocusability();

        if (descendantFocusability != FOCUS_BLOCK_DESCENDANTS) {
            for (int i = 0; i < getChildCount(); i++) {
                final View child = getChildAt(i);
                if (child.getVisibility() == VISIBLE) {
                    ItemInfo ii = infoForChild(child);
                    if (ii != null && ii.position == mCurItem) {
                        child.addFocusables(views, direction, focusableMode);
                    }
                }
            }
        }


        if (
                descendantFocusability != FOCUS_AFTER_DESCENDANTS ||

                        (focusableCount == views.size())) {

            if (!isFocusable()) {
                return;
            }
            if ((focusableMode & FOCUSABLES_TOUCH_MODE) == FOCUSABLES_TOUCH_MODE &&
                    isInTouchMode() && !isFocusableInTouchMode()) {
                return;
            }
            if (views != null) {
                views.add(this);
            }
        }
    }


    @Override
    public void addTouchables(ArrayList<View> views) {
        for (int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == VISIBLE) {
                ItemInfo ii = infoForChild(child);
                if (ii != null && ii.position == mCurItem) {
                    child.addTouchables(views);
                }
            }
        }
    }


    @Override
    protected boolean onRequestFocusInDescendants(int direction,
                                                  Rect previouslyFocusedRect) {
        int index;
        int increment;
        int end;
        int count = getChildCount();
        if ((direction & FOCUS_FORWARD) != 0) {
            index = 0;
            increment = 1;
            end = count;
        } else {
            index = count - 1;
            increment = -1;
            end = -1;
        }
        for (int i = index; i != end; i += increment) {
            View child = getChildAt(i);
            if (child.getVisibility() == VISIBLE) {
                ItemInfo ii = infoForChild(child);
                if (ii != null && ii.position == mCurItem) {
                    if (child.requestFocus(direction, previouslyFocusedRect)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {

        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == VISIBLE) {
                final ItemInfo ii = infoForChild(child);
                if (ii != null && ii.position == mCurItem &&
                        child.dispatchPopulateAccessibilityEvent(event)) {
                    return true;
                }
            }
        }

        return false;
    }

    private class PagerObserver extends DataSetObserver {

        @Override
        public void onChanged() {
            dataSetChanged();
        }

        @Override
        public void onInvalidated() {
            dataSetChanged();
        }
    }
}