# FractionView
android 自定义view 两个相反方向的嵌套转盘

最近没事干朋友刚好有个需求感兴趣，就花了些时间写了个

先看下截图，显示的是外圆顺时针转向，内圆逆时针转向，gif估计看不开效果，太刺眼了，录的时候也花了好一会时间，录好了一看怎么感觉是内圆变成顺时针了。。。，其实是逆时针转向，在gif截图上看起来会有点卡，其实在手机上很流畅，没有卡顿

有兴趣的也可以看看我之前写的自定义组件

###自定义view实现的下载进度展示：[https://github.com/7449/ProgressView](https://github.com/7449/ProgressView)

###自定义view实现的通讯录快速索引：[https://github.com/7449/SlideView](https://github.com/7449/SlideView)


![](http://i.imgur.com/AnwcO44.gif)

这个就类似于车载导航那种不停的旋转然后让UI看起来高大上的那种，不知道的人看上去是挺装逼的。


FractionDefaults 这个类是初始化时的一些默认值。
其实也不是很难，就是确定两个圆的位置，只要一个圆的位置确定了，那另一个就很好确定，
例如外圆如果确定了位置，那内圆就是使用同一个圆心，然后半径比外圆小一定程度就可以画出来，一般感觉比外圆的宽度大一点点就很好了

至于分段就是使用drawArc方法然后指定画笔为不同的颜色就行

例如外圆

	    private void drawOuterRing(Canvas canvas) {
	        mPaint.setStrokeWidth(mOuterRingWidth);
	        mPaint.setColor(mOuterRingSelectColor);
	        canvas.drawArc(mOuterRectF, mOuterRingAngle, mOuterRingAngleWidth * mOuterRingSelectRing + mOuterRingSelectAngle * mOuterRingSelectRing, false, mPaint);
	        mPaint.setColor(mOuterRingSelectAngleColor);
	        for (int i = 0; i < mOuterRingSelectRing; i++) {
	            canvas.drawArc(mOuterRectF, mOuterRingAngle + (i * mOuterRingAngleWidth + (i) * mOuterRingSelectAngle), mOuterRingSelectAngle, false, mPaint);
	        }
	    }


不同的状态下指定不同的颜色即可。

动画效果就是不停的进行view绘制，

例如外圆

让动画无限进行，然后检测动画，不停的更新参数，使用postInvalidate();更新UI界面，这样不停的刷新视觉上就是旋转起来了


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

	   private void setOuterRingAngle(int mOuterRingAngle) {
	        this.mOuterRingAngle = mOuterRingAngle;
	        postInvalidate();
	    }

大概就这么多吧，时间紧稍微看了下log，应该没什么Bug了。