/**
 * Project Name:  iCam
 * File Name:     YuntaiButton.java
 * Package Name:  com.wulian.icam.widget
 * @Date:         2015年9月23日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.sdk.android.oem.honeywell.ipc.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.wulian.sdk.android.oem.honeywell.ipc.R;
import com.wulian.sdk.android.oem.honeywell.ipc.utils.Utils;

/**
 * @ClassName: YuntaiButton
 * @Function: 自定义云台控件
 * @Date: 2015年9月23日
 * @author: yuanjs
 * @email: jiansheng.yuan@wuliangroup.com.cn
 */
public class YuntaiLeftRightButton extends View {
	private Paint paint;// 画笔
	private Bitmap backBitmap; // 正常状态底部图片
	private Bitmap ballBitmap; // 白球图片
	private int backRadius;// 背景图片半径
	private int radius;// 浮动白圈半径
	private int circleX;// 白圈圆心x坐标
	private Direction direction = Direction.none;
	private OnDirectionLisenter directionLisenter;
	private static final String TAG = "YuntaiLeftRightButton";

	public enum Direction {
		left, right, none;
	}

	private Direction lastDirection = Direction.none;

	public YuntaiLeftRightButton(Context context, AttributeSet attrs,
								 int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public YuntaiLeftRightButton(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public YuntaiLeftRightButton(Context context) {
		this(context, null);
	}

	private void init() {
		paint = new Paint();
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(1.5f);
		paint.setTextSize(20);
		paint.setAntiAlias(true);
		paint.setColor(Color.WHITE);
		backBitmap = Utils.readBitMap(getContext(),
				R.drawable.video_control_panel_portrait_left_right);
		ballBitmap = Utils.readBitMap(getContext(),
				R.drawable.video_control_button_left_right);
		backRadius = backBitmap.getWidth() / 2;
		radius = ballBitmap.getWidth() / 2;
		circleX = backRadius;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		int width = 0, height = 0;
		// 大小为图片背景大小，不可设置
		switch (widthMode) {
		case MeasureSpec.AT_MOST:// wrap_content
			width = Math.min(widthSize, backRadius * 2);
			break;
		case MeasureSpec.EXACTLY:// 确定值
			width = backRadius * 2;
			break;
		case MeasureSpec.UNSPECIFIED:// 任意大
			width = backRadius * 2;
			break;
		}
		height = ballBitmap.getHeight();
		setMeasuredDimension(width, height);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawBitmap(backBitmap, 0, 0, paint);
		canvas.drawBitmap(ballBitmap, circleX - radius, 0, paint);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			int xDown = (int) event.getX();
			if (isValidArea(xDown)) {
				Utils.sysoInfo(TAG + ":DOWN");
				circleX = xDown;
				direction = getDirection(circleX);
//				Utils.sysoInfo(TAG + ":DOWN==direction:" + direction.name()
//						+ ";lastDirection==" + lastDirection.name());
				invalidate();
				if (lastDirection != direction) {
					lastDirection = direction;
					directionLisenter.directionLisenter(direction);
				}
			}
			break;
		case MotionEvent.ACTION_MOVE:
			int xMove = (int) event.getX();
			if (isValidArea(xMove)) {
				circleX = xMove;
			} else {
				touchOutViewHandle(xMove);
			}
			direction = getDirection(circleX);
//			Utils.sysoInfo(TAG + ":Move==direction:" + direction.name()
//					+ ";lastDirection==" + lastDirection.name());
			invalidate();
			if (lastDirection != direction) {
				lastDirection = direction;
				directionLisenter.directionLisenter(direction);
			}
			break;
		case MotionEvent.ACTION_UP:
			circleX = backRadius;
			direction = Direction.none;
//			Utils.sysoInfo(TAG + ":Up==direction:" + direction.name()
//					+ ";lastDirection==" + lastDirection.name());
			invalidate();
			if (lastDirection != direction) {
				lastDirection = direction;
				directionLisenter.directionLisenter(direction);
			}
			break;
		}
		return true;
	}

	// XXX 滑动到view外面的处理
	private void touchOutViewHandle(int x) {
		if (x != backRadius) {
			if (x < backRadius) {
				circleX = radius;
			} else {
				circleX = 2 * backRadius - radius;
			}
		} else {
			circleX = backRadius;
		}
	}

	// 有效功能范围[radius,++]
	private Direction getDirection(int ballCircleX) {
		// 距离圆心（backRadius）之间的距离
		/**
		double length = Math.abs(ballCircleX - backRadius);
		Utils.sysoInfo(TAG + ":getDirection==length:" + length
				+ ";ballCircleX:" + ballCircleX + ";backRadius:" + backRadius
				+ ";radius:" + radius);
		if (length > (double) (radius)) {
			if (ballCircleX == backRadius) {
				return Direction.none;
			} else {
				if (ballCircleX < backRadius) {
					return Direction.left;
				} else {
					return Direction.right;
				}
			}
		} else {
			return Direction.none;
		}
		**/

		if (ballCircleX > 2 * backRadius - radius || ballCircleX < radius) {
			return Direction.none;
		} else {
			if (ballCircleX > backRadius) {
				return Direction.right;
			} else if (ballCircleX < backRadius) {
				return Direction.left;
			} else {
				return Direction.none;
			}
		}

	}

	// 有效活动范围[0,backRadius-radius]
	private boolean isValidArea(int ballCircleX) {
		// 距离圆心（backRadius）之间的距离
		double length = Math.abs(ballCircleX - backRadius);
		// 不能超过最大的距离
		if (length >= (float) (backRadius - radius)) {
			return false;
		} else {
			return true;
		}
	}

	public interface OnDirectionLisenter {
		void directionLisenter(Direction direction);
	}

	public void setOnDirectionLisenter(OnDirectionLisenter l) {
		if (l != null) {
			this.directionLisenter = l;
		}
	}

	public void setBackground(Context context, int R) {
		backBitmap = Utils.readBitMap(context, R);
		invalidate();
	}
}
