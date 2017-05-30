package com.demo.fractionview.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.demo.fractionview.R;

/**
 * by y on 2016/10/18
 */

public class FractionView extends View {

    private Paint mPaint;
    private int mRingRadius; // 圆环半径
    private FractionVisibilityChanged fractionVisibilityChanged = null;
    private static final int RING_ANGLE_MAX = 100;
    /**
     * 外圆
     */
    private boolean isOuter = true; //是否显示外圆
    private RectF mOuterRectF;
    private ValueAnimator outerValueAnimator;
    private float mOuterRingAngleWidth;
    private float mOuterRingWidth; //宽度
    private int mOuterRingSelect;    //分成多少段
    private int mOuterRingSelectAngle;//每个圆环之间的间隔
    private int mOuterRingSelectRing;//显示几段彩色
    private int mOuterRingSelectColor;//分段的颜色
    private int mOuterRingSelectAngleColor;//分段间隔的颜色
    private int mOuterRingSpeed;// 外圆旋转速度
    private int mOuterRingAngle = FractionDefaults.OUTER_RING_ANGLE;
    /**
     * 内圆
     */
    private boolean isInner = true;//是否显示内圆
    private RectF mInnerRectF;
    private ValueAnimator innerValueAnimator;
    private float mInnerRingAngleWidth;
    private float mInnerRingWidth; //宽度
    private int mInnerRingSelect;    //分成多少段
    private int mInnerRingSelectAngle;//每个圆环之间的间隔
    private int mInnerRingSelectRing;//显示几段彩色
    private int mInnerRingSelectColor;//分段的颜色
    private int mInnerRingSelectAngleColor;//分段间隔的颜色
    private int mInnerRingSpeed;// 内圆旋转速度
    private int mInnerRingAngle = FractionDefaults.INNER_RING_ANGLE;

    /**
     * 文字
     */
    private boolean isText = true;
    private String mText = getClass().getSimpleName();
    private float mTextCrude;//进度字体的粗细程度
    private float mTextSize;    //字体大小
    private int mTextColor;  //字体颜色

    public FractionView(Context context) {
        super(context, null);
        init(null);
    }

    public FractionView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        init(attrs);
    }

    public FractionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }


    private void init(AttributeSet attrs) {
        TypedArray mTypedArray = getContext().obtainStyledAttributes(attrs, R.styleable.FractionView);
        mRingRadius = mTypedArray.getInt(R.styleable.FractionView_ringRadius, FractionDefaults.RING_RADIUS);

        mOuterRingWidth = mTypedArray.getDimension(R.styleable.FractionView_outerRingWidth, FractionDefaults.OUTER_RING_WIDTH);
        mOuterRingSelect = mTypedArray.getInt(R.styleable.FractionView_outerRingSelect, FractionDefaults.OUTER_RING_SELECT);
        mOuterRingSelectAngle = mTypedArray.getInt(R.styleable.FractionView_outerRingSelectAngle, FractionDefaults.OUTER_RING_SELECT_ANGLE);
        mOuterRingSelectRing = mTypedArray.getInt(R.styleable.FractionView_outerRingSelectRing, FractionDefaults.OUTER_RING_SELECT_RING);
        mOuterRingSelectColor = mTypedArray.getColor(R.styleable.FractionView_outerRingSelectColor, FractionDefaults.OUTER_RING_SELECT_COLOR);
        mOuterRingSelectAngleColor = mTypedArray.getColor(R.styleable.FractionView_outerRingSelectAngleColor, FractionDefaults.OUTER_RING_SELECT_ANGLE_COLOR);
        mOuterRingSpeed = mTypedArray.getInt(R.styleable.FractionView_outerRingSpeed, FractionDefaults.OUTER_RING_SPEED);

        mInnerRingWidth = mTypedArray.getDimension(R.styleable.FractionView_innerRingWidth, FractionDefaults.INNER_RING_WIDTH);
        mInnerRingSelect = mTypedArray.getInt(R.styleable.FractionView_innerRingSelect, FractionDefaults.INNER_RING_SELECT);
        mInnerRingSelectAngle = mTypedArray.getInt(R.styleable.FractionView_innerRingSelectAngle, FractionDefaults.INNER_RING_SELECT_ANGLE);
        mInnerRingSelectRing = mTypedArray.getInt(R.styleable.FractionView_innerRingSelectRing, FractionDefaults.INNER_RING_SELECT_RING);
        mInnerRingSelectColor = mTypedArray.getColor(R.styleable.FractionView_innerRingSelectColor, FractionDefaults.INNER_RING_SELECT_COLOR);
        mInnerRingSelectAngleColor = mTypedArray.getColor(R.styleable.FractionView_innerRingSelectAngleColor, FractionDefaults.INNER_RING_SELECT_ANGLE_COLOR);
        mInnerRingSpeed = mTypedArray.getInt(R.styleable.FractionView_innerRingSpeed, FractionDefaults.INNER_RING_SPEED);

        mTextColor = mTypedArray.getColor(R.styleable.FractionView_textColor, FractionDefaults.TEXT_COLOR);
        mTextCrude = mTypedArray.getDimension(R.styleable.FractionView_textCrude, FractionDefaults.TEXT_CRUDE);
        mTextSize = mTypedArray.getDimension(R.styleable.FractionView_textSize, FractionDefaults.TEXT_SIZE);

        mTypedArray.recycle();
        mPaint = new Paint();
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        setWillNotDraw(false);
        mInnerRectF = new RectF();
        mOuterRectF = new RectF();
    }

    public void setFractionVisibilityChanged(FractionVisibilityChanged fractionVisibilityChanged) {
        this.fractionVisibilityChanged = fractionVisibilityChanged;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int mViewX = getMeasuredWidth() / 2;
        int mViewY = getMeasuredHeight() / 2;
        mInnerRectF.set(
                mViewX - mRingRadius - mInnerRingWidth / 3,
                mViewY - mRingRadius - mInnerRingWidth / 3,
                mViewX + mRingRadius + mInnerRingWidth / 3,
                mViewY + mRingRadius + mInnerRingWidth / 3);
        mOuterRectF.set(
                mViewX - mRingRadius - mOuterRingWidth * 3,
                mViewY - mRingRadius - mOuterRingWidth * 3,
                mViewX + mRingRadius + mOuterRingWidth * 3,
                mViewY + mRingRadius + mOuterRingWidth * 3
        );
        mOuterRingAngleWidth = (360 - mOuterRingSelect * mOuterRingSelectAngle) / mOuterRingSelect;
        mInnerRingAngleWidth = (360 - mInnerRingSelect * mInnerRingSelectAngle) / mInnerRingSelect;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isOuter) {
            drawOuterRing(canvas);
        }
        if (isInner) {
            drawInnerRing(canvas);
        }
        if (isText) {
            drawTextRing(canvas);
        }
        Log("onDraw - ------------------>>>>>>>>>");
    }

    private void drawTextRing(Canvas canvas) {
        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(mTextCrude);
        mPaint.setTextSize(mTextSize);
        mPaint.setColor(mTextColor);
        float textWidth = mPaint.measureText(mText);
        canvas.drawText(mText, getMeasuredWidth() / 2 - textWidth / 2, getMeasuredHeight() / 2 + mTextSize / 2, mPaint);
    }

    /**
     * 内圆
     */
    private void drawInnerRing(Canvas canvas) {
        mPaint.setStrokeWidth(mInnerRingWidth);
        mPaint.setColor(mInnerRingSelectColor);
        canvas.drawArc(mInnerRectF, mInnerRingAngle, mInnerRingAngleWidth * mInnerRingSelectRing + mInnerRingSelectAngle * mInnerRingSelectRing, false, mPaint);
        mPaint.setColor(mInnerRingSelectAngleColor);
        for (int i = 0; i < mInnerRingSelectRing; i++) {
            canvas.drawArc(mInnerRectF, mInnerRingAngle + (i * mInnerRingAngleWidth + (i) * mInnerRingSelectAngle), mInnerRingSelectAngle, false, mPaint);
        }
    }

    private void innerRoundAnimator() {
        innerValueAnimator = ValueAnimator.ofFloat(-0, -0);
        innerValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        innerValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (mInnerRingAngle < -RING_ANGLE_MAX) {
                    mInnerRingAngle = FractionDefaults.INNER_RING_ANGLE;
                }
                setInnerRingAngle(mInnerRingAngle -= mInnerRingSpeed);
            }
        });
    }

    public void startInnerRoundAnimator() {
        if (getVisibility() == VISIBLE && null != innerValueAnimator && !isInnerAnimatorRunning() && isInner) {
            mInnerRingAngle = FractionDefaults.INNER_RING_ANGLE;
            innerValueAnimator.start();
        }
    }


    public void stopInnerRoundAnimator() {
        if (innerValueAnimator != null && isInnerAnimatorRunning()) {
            innerValueAnimator.end();
        }
    }

    private void setInnerRingAngle(int mInnerRingAngle) {
        this.mInnerRingAngle = mInnerRingAngle;
        postInvalidate();
    }

    /**
     * 外圆
     */
    private void drawOuterRing(Canvas canvas) {
        mPaint.setStrokeWidth(mOuterRingWidth);
        mPaint.setColor(mOuterRingSelectColor);
        canvas.drawArc(mOuterRectF, mOuterRingAngle, mOuterRingAngleWidth * mOuterRingSelectRing + mOuterRingSelectAngle * mOuterRingSelectRing, false, mPaint);
        mPaint.setColor(mOuterRingSelectAngleColor);
        for (int i = 0; i < mOuterRingSelectRing; i++) {
            canvas.drawArc(mOuterRectF, mOuterRingAngle + (i * mOuterRingAngleWidth + (i) * mOuterRingSelectAngle), mOuterRingSelectAngle, false, mPaint);
        }
    }

    private void outerRoundAnimator() {
        outerValueAnimator = ValueAnimator.ofFloat(0, 0);
        outerValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        outerValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (mOuterRingAngle > RING_ANGLE_MAX) {
                    mOuterRingAngle = FractionDefaults.OUTER_RING_ANGLE;
                }
                setOuterRingAngle(mOuterRingAngle += mOuterRingSpeed);
            }
        });
    }


    public void startOuterRoundAnimator() {
        if (getVisibility() == VISIBLE && null != outerValueAnimator && !isOuterAnimatorRunning() && isOuter) {
            mOuterRingAngle = FractionDefaults.OUTER_RING_ANGLE;
            outerValueAnimator.start();
        }
    }

    public void stopOuterRoundAnimator() {
        if (outerValueAnimator != null && isOuterAnimatorRunning()) {
            outerValueAnimator.end();
        }
    }

    private void setOuterRingAngle(int mOuterRingAngle) {
        this.mOuterRingAngle = mOuterRingAngle;
        postInvalidate();
    }


    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (null != fractionVisibilityChanged) {
            fractionVisibilityChanged.onVisibilityChanged(changedView, visibility);
        }
    }

    public interface FractionVisibilityChanged {
        void onVisibilityChanged(View changedView, int visibility);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log("onAttachedToWindow");
        outerRoundAnimator();
        innerRoundAnimator();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log("onDetachedFromWindow");
        stopOuterRoundAnimator();
        stopInnerRoundAnimator();
    }

    public void startAnimator() {
        startOuterRoundAnimator();
        startInnerRoundAnimator();
    }

    public void stopAnimator() {
        stopOuterRoundAnimator();
        stopInnerRoundAnimator();
    }

    public boolean isInnerAnimatorRunning() {
        return innerValueAnimator.isRunning();
    }

    public boolean isOuterAnimatorRunning() {
        return outerValueAnimator.isRunning();
    }


    /**
     * 圆环半径
     */
    public void setRingRadius(int radius) {
        this.mRingRadius = radius;
    }

    /**
     * 获取外圆宽度
     */
    public float getOuterRingWidth() {
        return mOuterRingWidth;
    }

    /**
     * 设置外圆宽度
     */
    public void setOuterRingWidth(float mOuterRingWidth) {
        this.mOuterRingWidth = mOuterRingWidth;
    }

    /**
     * 获取外圆有多少段
     */
    public int getOuterRingSelect() {
        return mOuterRingSelect;
    }

    /**
     * 设置外圆有多少段
     */
    public void setOuterRingSelect(int mOuterRingSelect) {
        this.mOuterRingSelect = mOuterRingSelect;
    }

    /**
     * 获取外圆每个圆环之间的间隔
     */
    public int getOuterRingSelectAngle() {
        return mOuterRingSelectAngle;
    }

    /**
     * 设置外圆每个圆环之间的间隔
     */
    public void setOuterRingSelectAngle(int mOuterRingSelectAngle) {
        this.mOuterRingSelectAngle = mOuterRingSelectAngle;
    }

    /**
     * 获取外圆显示几段彩色
     */
    public int getOuterRingSelectRing() {
        return mOuterRingSelectRing;
    }

    /**
     * 设置外圆显示几段彩色
     */
    public void setOuterRingSelectRing(int mOuterRingSelectRing) {
        this.mOuterRingSelectRing = mOuterRingSelectRing;
    }

    /**
     * 获取外圆分段颜色
     */
    public int getOuterRingSelectColor() {
        return mOuterRingSelectColor;
    }

    /**
     * 设置外圆分段颜色
     */
    public void setOuterRingSelectColor(int mOuterRingSelectColor) {
        this.mOuterRingSelectColor = mOuterRingSelectColor;
    }

    /**
     * 获取外圆分段间隔颜色
     */
    public int getOuterRingSelectAngleColor() {
        return mOuterRingSelectAngleColor;
    }

    /**
     * 设置外圆分段间隔颜色
     */
    public void setOuterRingSelectAngleColor(int mOuterRingSelectAngleColor) {
        this.mOuterRingSelectAngleColor = mOuterRingSelectAngleColor;
    }

    /**
     * 获取外圆旋转速度
     */
    public int getOuterRingSpeed() {
        return mOuterRingSpeed;
    }

    /**
     * 设置外圆旋转速度
     */
    public void setOuterRingSpeed(int mOuterRingSpeed) {
        this.mOuterRingSpeed = mOuterRingSpeed;
    }

    /**
     * 获取内圆宽度
     */
    public float getInnerRingWidth() {
        return mInnerRingWidth;
    }

    /**
     * 设置内圆宽度
     */
    public void setInnerRingWidth(float mInnerRingWidth) {
        this.mInnerRingWidth = mInnerRingWidth;
    }

    /**
     * 获取内圆有多少段
     */
    public int getInnerRingSelect() {
        return mInnerRingSelect;
    }

    /**
     * 设置内圆有多少段
     */
    public void setInnerRingSelect(int mInnerRingSelect) {
        this.mInnerRingSelect = mInnerRingSelect;
    }

    /**
     * 获取内圆每个圆环之间的间隔
     */
    public int getInnerRingSelectAngle() {
        return mInnerRingSelectAngle;
    }

    /**
     * 设置内圆每个圆环之间的间隔
     */
    public void setInnerRingSelectAngle(int mInnerRingSelectAngle) {
        this.mInnerRingSelectAngle = mInnerRingSelectAngle;
    }

    /**
     * 获取内圆显示几段彩色
     */
    public int getInnerRingSelectRing() {
        return mInnerRingSelectRing;
    }

    /**
     * 设置内圆显示几段彩色
     */
    public void setInnerRingSelectRing(int mInnerRingSelectRing) {
        this.mInnerRingSelectRing = mInnerRingSelectRing;
    }

    /**
     * 获取内圆分段颜色
     */
    public int getInnerRingSelectColor() {
        return mInnerRingSelectColor;
    }

    /**
     * 设置内圆分段颜色
     */
    public void setInnerRingSelectColor(int mInnerRingSelectColor) {
        this.mInnerRingSelectColor = mInnerRingSelectColor;
    }

    /**
     * 获取内圆分段间隔颜色
     */
    public int getInnerRingSelectAngleColor() {
        return mInnerRingSelectAngleColor;
    }

    /**
     * 设置内圆分段间隔颜色
     */
    public void setInnerRingSelectAngleColor(int mInnerRingSelectAngleColor) {
        this.mInnerRingSelectAngleColor = mInnerRingSelectAngleColor;
    }

    /**
     * 获取内圆旋转速度
     */
    public int getInnerRingSpeed() {
        return mInnerRingSpeed;
    }

    /**
     * 设置内圆旋转速度
     */
    public void setInnerRingSpeed(int mInnerRingSpeed) {
        this.mInnerRingSpeed = mInnerRingSpeed;
    }

    /**
     * 获取文字
     */
    public String getText() {
        return mText;
    }

    /**
     * 设置文字
     */
    public void setText(String mText) {
        this.mText = mText;
    }

    /**
     * 获取文字宽度
     */
    public float getTextCrude() {
        return mTextCrude;
    }

    /**
     * 设置文字宽度
     */
    public void setTextCrude(float mTextCrude) {
        this.mTextCrude = mTextCrude;
    }

    /**
     * 获取文字大小
     */
    public float getTextSize() {
        return mTextSize;
    }

    /**
     * 设置文字大小
     */
    public void setTextSize(float mTextSize) {
        this.mTextSize = mTextSize;
    }

    /**
     * 获取文字颜色
     */
    public int getTextColor() {
        return mTextColor;
    }

    /**
     * 设置文字颜色
     */
    public void setTextColor(int mTextColor) {
        this.mTextColor = mTextColor;
    }

    /**
     * 获取内圆状态
     */
    public boolean isInner() {
        return isInner;
    }

    /**
     * 设置内圆状态
     */
    public void setInner(boolean inner) {
        isInner = inner;
    }

    /**
     * 获取外圆状态
     */
    public boolean isOuter() {
        return isOuter;
    }

    /**
     * 设置外圆状态
     */
    public void setOuter(boolean outer) {
        isOuter = outer;
    }

    /**
     * 获取是否开启文字状态
     */
    public boolean isText() {
        return isText;
    }

    /**
     * 设置是否开启文字
     */
    public void setText(boolean isText) {
        this.isText = isText;
    }

    private static final String TAG = "FractionView";

    private void Log(Object o) {
        Log.i(TAG, o.toString());
    }
}
