package com.michael.instrumentpanelview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wu.xingbiao on 2016/1/11.
 */
public class FlowLayout extends ViewGroup {

    private final  String TAG = "FlowLayout";
    private List<List<View>> mAllViews = new ArrayList<List<View>>();
    private List<Integer> mLineHeight = new ArrayList<Integer>();

    public FlowLayout(Context context) {
        this(context, null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mAllViews.clear();
        mLineHeight.clear();
        int width = getWidth();//行宽

        int lineWidth = 0;
        int lineHeight = 0;
        List<View> lineViews = new ArrayList<View>();

        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) child.getLayoutParams();
            int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;//子控件实际宽度
            int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;//子控件实际高度
            if (childWidth + lineWidth > width) {//如果需要换行
                mLineHeight.add(lineHeight);
                //保存当前行的所有子view，然后重新创建集合记录下一行的子view
                mAllViews.add(lineViews);
                lineWidth = 0;
                lineViews = new ArrayList<View>();
            }
            //如果不需要换行
            lineWidth += childWidth;
            lineHeight = Math.max(lineHeight, childHeight);
            lineViews.add(child);
        }
        // 记录最后一行
        mLineHeight.add(lineHeight);
        mAllViews.add(lineViews);

        int left = 0;//子view的左边
        int top = 0;//子view的上边
        // 得到总行数
        int lineNums = mAllViews.size();
        for (int i = 0; i < lineNums; i++) {
            // 每一行的所有的views
            lineViews = mAllViews.get(i);
            // 当前行的最大高度
            lineHeight = mLineHeight.get(i);
            Log.e(TAG, "第" + i + "行 ：" + lineViews.size() + " , " + lineViews);
            Log.e(TAG, "第" + i + "行， ：" + lineHeight);

            // 遍历当前行所有的View
            for (int j = 0; j < lineViews.size(); j++) {
                View child = lineViews.get(j);
                if (child.getVisibility() == View.GONE) {
                    continue;
                }
                ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) child
                        .getLayoutParams();
                //计算childView的left,top,right,bottom
                int lc = left + lp.leftMargin;
                int tc = top + lp.topMargin;
                int rc = lc + child.getMeasuredWidth();
                int bc = tc + child.getMeasuredHeight();
                Log.e(TAG, child + " , l = " + lc + " , t = " + t + " , r ="
                        + rc + " , b = " + bc);
                child.layout(lc, tc, rc, bc);

                left += child.getMeasuredWidth() + lp.rightMargin
                        + lp.leftMargin;
            }
            left = 0;//下一行第一个view的左边
            top += lineHeight;//下一行第一个view的上边
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获取父布局给予的宽高
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        //如果是wrap_content的情况下,记录宽高
        int width = 0;
        int height = 0;

        //记录行宽和高
        int lineWidth = 0;
        int lineHeight = 0;
        int count  = getChildCount();
        for(int i = 0;i < count;i++){
            View child = getChildAt(i);
            // 测量每一个child的宽和高
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams)child.getLayoutParams();
            int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;//子控件实际宽度
            int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;//子控件实际高度
            if(lineWidth + childWidth > sizeWidth){//当行宽加上子控件的宽度大于父布局给的宽度时
                width = Math.max(lineWidth,childWidth);
                lineWidth = childWidth;//重新开启新行记录
                height += lineHeight;//叠加高度
                lineHeight = childHeight;//将当前子控件高度赋值给行高
            }else{
                lineWidth += childWidth;
                lineHeight = Math.max(lineHeight,childHeight);
            }
            if (i == count - 1){
                width = Math.max(width,lineWidth);
                height += lineHeight;
            }
        }
        setMeasuredDimension(modeWidth == MeasureSpec.EXACTLY ? sizeWidth : width,modeHeight == MeasureSpec.EXACTLY ? sizeHeight : height);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }
}
