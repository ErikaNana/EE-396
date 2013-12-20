package com.example.drawing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

public class DrawingView extends ImageView {
	private Path drawPath; //drawing path
	private Paint drawPaint, canvasPaint; //drawing and canvas paint
	private int paintColor = 0xFF660000; //initial color
	private Canvas drawCanvas; //canvas
	private Bitmap canvasBitmap; //canvasBitmap
	private int height;
	private int width;
	public static final int MOVE = 1;
	public static final int UP = 2;
	public static final int DOWN = 3;
	
	public DrawingView(Context context, AttributeSet attrs) {
		super(context,attrs);
		setupDrawing();
	}
	public DrawingView(Context context) {
		super(context);
		setupDrawing();
	}
	private void setupDrawing() {
	//get drawing setup for interaction
	//these make drawings appear smoother
		drawPath = new Path();
		drawPaint = new Paint();
		drawPaint.setColor(paintColor);
		drawPaint.setAntiAlias(true);
		drawPaint.setStrokeWidth(20);
		drawPaint.setStyle(Paint.Style.STROKE);
		drawPaint.setStrokeJoin(Paint.Join.ROUND);
		drawPaint.setStrokeCap(Paint.Cap.ROUND);
		canvasPaint = new Paint(Paint.DITHER_FLAG);
	}
	//this is called when the custom View is assigned a size
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		width = w;
		height = h;
		//view given size
		super.onSizeChanged(w, h, oldw, oldh);
		//this config is the best config for bitmap
		canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		drawCanvas = new Canvas(canvasBitmap);
		Log.w("app", "height:  " + h);
	}
	
	/*drawing!
	every time the user draws using touch interaction, invalidate the View so onDraw executes */
	@Override
	protected void onDraw(Canvas canvas) {
		//draw view
/*		Log.w("Drawing view", "DRAWING!");*/
		canvas.drawBitmap(canvasBitmap, 0, 0,canvasPaint);
		canvas.drawPath(drawPath, drawPaint);
	}
	/*Listening for touch events*/
/*	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float touchX = event.getX();
		float touchY = event.getY();
		int xCoord = (int) Math.floor(touchX);
		int yCoord = (int) Math.floor(touchY);
		
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				drawPath.moveTo(touchX, touchY);
				int color = Utils.findColor(this, xCoord, yCoord);
				Log.w("DW", "color:  " + color);
				Log.w("Drawing view", "ACTION_DOWN");
				Log.w("Drawing view", "x coord:  " + touchX);
				Log.w("Drawing view", "y coord:  " + touchY);
				break;
			
			case MotionEvent.ACTION_MOVE:
				drawPath.lineTo(touchX, touchY);
				int color2 = Utils.findColor(this, xCoord, yCoord);
				Log.w("DW", "color:  " + color2);		
				Log.w("Drawing view", "ACTION_MOVE");
				Log.w("Drawing view", "x coord:  " + touchX);
				Log.w("Drawing view", "y coord:  " + touchY);
				break;
			case MotionEvent.ACTION_UP:
				drawCanvas.drawPath(drawPath, drawPaint);
				Log.w("Drawing view", "ACTION_UP");
				Log.w("Drawing view", "x coord:  " + touchX);
				Log.w("Drawing view", "y coord:  " + touchY);
				drawPath.reset();
			default:
				return false;
		}
		//invalidate the view and return true
		invalidate();
		return true;
	}*/
	public void draw (int status, float touchX, float touchY) {
		switch (status) {
			case DOWN:{
				drawPath.moveTo(touchX, touchY);
				break;
			}
			case MOVE:{
				drawPath.lineTo(touchX, touchY);
				break;
			}
			case UP:{
				drawCanvas.drawPath(drawPath, drawPaint);
/*				Log.w("Drawing view", "ACTION_UP");
				Log.w("Drawing view", "x coord:  " + touchX);
				Log.w("Drawing view", "y coord:  " + touchY);*/
				drawPath.reset();
				break;
			}
		}
		invalidate();
	}
	//set the color
	public void setColor(String newColor) {
		invalidate(); //invalidate the view
		//parse and set the color for drawing
		paintColor = Color.parseColor(newColor);
		drawPaint.setColor(paintColor);
	}
	
	public void clear() {
		drawPath = new Path();
		drawPaint = new Paint();
		drawPaint.setColor(paintColor);
		drawPaint.setAntiAlias(true);
		drawPaint.setStrokeWidth(20);
		drawPaint.setStyle(Paint.Style.STROKE);
		drawPaint.setStrokeJoin(Paint.Join.ROUND);
		drawPaint.setStrokeCap(Paint.Cap.ROUND);
		canvasPaint = new Paint(Paint.DITHER_FLAG);
		canvasBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		drawCanvas = new Canvas(canvasBitmap);
		invalidate();
	}
}
