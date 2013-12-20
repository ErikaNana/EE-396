package com.example.drawing;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout.LayoutParams;

public class MainActivity extends Activity {
	private DrawingView drawView;
	private ImageButton currPaint;
	private ImageButton resetButton;
	private ImageButton newPatternButton;
	private boolean drawDraw = true; //flag so only one dialog is shown at a time
	private boolean firstTouch = true; //check if it user's first time drawing
	private static final int START_POSITION_ERROR = 1;
	private static final int OUT_OF_BOUNDS_ERROR = 2;
	private static final int FINISHED = 3;
	private static final int ONE_STROKE_ERROR = 4;
	private static final int NUMBEROFPATTERNS = 7;
	private static final int COUNTER_COUNT = 5;		// how much error allowed
	
	// colors
	private static final int RED = -65536;
	// private static final int BLUE1 = -16776961;
	private static final int BLUE = -16776961;
	private static final int BLACK = -16777216;
	
	int counter = 0;
	int letGo = 0;
	private int patternCount = 0;
	int difficulty;	// 0: easy, 1: med, 2: hard
	
	// accelerometer stuff
	private SensorManager mSensorManager;
	private ShakeEventListener mSensorListener;
	
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Bundle bundle = getIntent().getExtras();
		difficulty = bundle.getInt("diff");
		
		//get drawing view
		LinearLayout layout = (LinearLayout) findViewById(R.id.drawing);
		//set the listener on view
		drawView = new DrawingView(this);
		
		drawView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				float touchX = event.getX();
				float touchY = event.getY();
				int xCoord = (int) Math.floor(touchX);
				int yCoord = (int) Math.floor(touchY);
				DrawingView drawing = (DrawingView)v;
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
/*						int color = Utils.findColor(drawing, xCoord, yCoord);
						Log.w("DW", "color:  " + color);*/
						drawing.draw(DrawingView.DOWN, touchX, touchY);
						int color2 = Utils.findColor(drawing, xCoord, yCoord);
						//check if just starting
						Log.w("DW", "color:  " + color2);
						break;
					
					case MotionEvent.ACTION_MOVE:
						drawing.draw(DrawingView.MOVE, touchX, touchY);
						int color = Utils.findColor(drawing, xCoord, yCoord);
						if (firstTouch) {
							if (color != RED && drawDraw) {
								drawDraw = false;
								showErrorDialog(START_POSITION_ERROR);
							}
							else {
								firstTouch = false;
							}
						}
						else if ((color != BLACK &&	
								color != BLUE && 	
								color != RED)	 
								&& drawDraw){
							counter++;
						}
						
						if (letGo == 1 && drawDraw) {
							drawDraw = false;
							showErrorDialog(ONE_STROKE_ERROR);
						}
						
						if (color == BLUE && drawDraw) {
							drawDraw = false;
							showErrorDialog(FINISHED);
						}
						// color: 855310 --> R: 242, G: 242, B: 242
						/*
						if ((color == -1 || color == -855310) && drawDraw) {
							counter++;
						}
						*/
						if (!drawDraw) {
							break;
						}
						if (counter >= COUNTER_COUNT && drawDraw) {
							drawDraw = false;
							showErrorDialog(OUT_OF_BOUNDS_ERROR);
						}
						Log.w("DW", "color:  " + color);
						break;
					case MotionEvent.ACTION_UP:;
						letGo = 1;
						drawing.draw(DrawingView.UP, touchX, touchY);
					default:
						return false;
				}
				return true;
			}
		});
		
		drawView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
		//drawView.setImageDrawable(getResources().getDrawable(R.drawable.bg));
		switch(difficulty){
			case(1): drawView.setBackground(getResources().getDrawable(R.drawable.pattern0m));
					 break;
			case(2): drawView.setBackground(getResources().getDrawable(R.drawable.pattern0h));
					 break;
			default: drawView.setBackground(getResources().getDrawable(R.drawable.pattern0e));
					 break;
		}
		
		layout.addView(drawView);
		//get the palette
		LinearLayout paintLayout = (LinearLayout) findViewById(R.id.paint_colors);
		currPaint = (ImageButton)paintLayout.getChildAt(0);
		currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
		addResetButton();
		addNewPatternButton();
		
		
		
		
		
		// accelerometer stuff
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
	    mSensorListener = new ShakeEventListener();   

	    mSensorListener.setOnShakeListener(new ShakeEventListener.OnShakeListener() {

	      public void onShake() {
	    	  Log.w("shake", "very shake such wow");
	      }
	    });
	}
	
	public void paintClicked(View view) {
		//check if current is not already the selected one
		if (view!=currPaint) {
			//update the color
			ImageButton imgView = (ImageButton)view;
			String color = view.getTag().toString();
			drawView.setColor(color);
			//update the UI
			imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
			currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
			currPaint = (ImageButton)view;
		}
	}
	public void addResetButton() {
		resetButton = (ImageButton) findViewById(R.id.reset_btn);
		resetButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				drawView.clear();
				drawDraw = true;
				firstTouch = true;
				counter = 0;
				letGo = 0;
			}
		});
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	
	// -------------------- back key stuff ---------------- 
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	        exitByBackKey();

	        //moveTaskToBack(false);

	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}

	protected void exitByBackKey() {

	    AlertDialog alertbox = new AlertDialog.Builder(this)
	    .setMessage("Do you want to return to title screen?")
	    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

	        // do something when the button is clicked
	        public void onClick(DialogInterface arg0, int arg1) {

	            finish();
	            //close();

	        }
	    })
	    .setNegativeButton("No", new DialogInterface.OnClickListener() {

	        // do something when the button is clicked
	        public void onClick(DialogInterface arg0, int arg1) {
	                       }
	    })
	      .show();

	}
	
	// -------------------- END OF back key stuff ---------------- 
	
	
	@SuppressLint("InlinedApi")
	public void showErrorDialog(int type) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK)
		.setTitle(R.string.app_name);
		switch (type) {
			case OUT_OF_BOUNDS_ERROR:{
				builder.setMessage("Out of bounds!  Please try again");
				builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						drawDraw = true;
						firstTouch = true;
						counter = 0;
						letGo = 0;
						drawView.clear();	
					}
				});
				break;
			}
			case START_POSITION_ERROR:{
				builder.setMessage("Please start on the red dot");
				builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						drawDraw = true;
						counter = 0;
						drawView.clear();
						firstTouch = true;
						letGo = 0;
					}
				});
				break;	
			}
			case FINISHED:{
				builder.setMessage("Good job you made it through! Start again?");
				builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						drawDraw = true;
						counter = 0;
						drawView.clear();
						firstTouch = true;
						letGo = 0;
					}
				});
				builder.setNegativeButton("New Pattern", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						drawDraw = true;
						counter = 0;
						drawView.clear();
						firstTouch = true;
						letGo = 0;
						patternCount = (patternCount + 1) % NUMBEROFPATTERNS;        // imagecount++; if hit last image go back to zero
                        togglePattern();
					}
				});
				break;
			}
			case ONE_STROKE_ERROR:{
				builder.setMessage("Sorry but you gotta do it one stroke! Try again?");
				builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						drawDraw = true;
						counter = 0;
						drawView.clear();
						firstTouch = true;
						letGo = 0;
					}
				});
				builder.setNegativeButton("Quit", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
				break;
			}
		}
		AlertDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.setCancelable(false);
		dialog.show();
	}
    public void addNewPatternButton(){     
        newPatternButton = (ImageButton) findViewById(R.id.new_pattern_button);
        newPatternButton.setOnClickListener(new OnClickListener() {
                @Override
        		public void onClick(View v){
                		Log.w("hello", "SWITCH" + patternCount);
                		drawDraw = true;
						firstTouch = true;
						counter = 0;
						letGo = 0;
                        patternCount = (patternCount + 1) % NUMBEROFPATTERNS;        // imagecount++; if hit last image go back to zero
                        togglePattern();                
                }
        });
	}	

	@SuppressLint("NewApi")
	private void togglePattern(){

        // DrawingView drawingView = (DrawingView) findViewById(R.id.drawing);
        Drawable newPattern;
        switch(difficulty){
        	case(1):	// medium
        		if(patternCount == 0){
	                newPattern = getResources().getDrawable(R.drawable.pattern0m);
		        }
		        else if(patternCount == 1){
		                newPattern = getResources().getDrawable(R.drawable.pattern1m);
		        }
		
		        //else if (patternCount == 2)
		        else if(patternCount == 2){         
		                newPattern = getResources().getDrawable(R.drawable.pattern2m);
		        }
		        else if(patternCount == 3){         
	                newPattern = getResources().getDrawable(R.drawable.pattern3m);
		        }
		        else if(patternCount == 4){         
	                newPattern = getResources().getDrawable(R.drawable.pattern4m);
		        }
		        else if(patternCount == 5){         
	                newPattern = getResources().getDrawable(R.drawable.pattern5m);
		        }
		        else newPattern = getResources().getDrawable(R.drawable.pattern6m);
        		break;
        		
        	case(2):	// hard
        		if(patternCount == 0){
	                newPattern = getResources().getDrawable(R.drawable.pattern0h);
		        }
		        else if(patternCount == 1){
		                newPattern = getResources().getDrawable(R.drawable.pattern1h);
		        }
		
		        else if(patternCount == 2){         
		                newPattern = getResources().getDrawable(R.drawable.pattern2h);
		        }
		        else if(patternCount == 3){         
	                newPattern = getResources().getDrawable(R.drawable.pattern3h);
		        }
		        else if(patternCount == 4){         
	                newPattern = getResources().getDrawable(R.drawable.pattern4h);
		        }
		        else if(patternCount == 5){         
	                newPattern = getResources().getDrawable(R.drawable.pattern5h);
		        }
		        else newPattern = getResources().getDrawable(R.drawable.pattern6h);
        		break;
        		
        	default:	// easy
        		if(patternCount == 0){
	                newPattern = getResources().getDrawable(R.drawable.pattern0e);
		        }
		        else if(patternCount == 1){
		                newPattern = getResources().getDrawable(R.drawable.pattern1e);
		        }
		
		        else if(patternCount == 2){         
		                newPattern = getResources().getDrawable(R.drawable.pattern2e);
		        }
		        else if(patternCount == 3){         
	                newPattern = getResources().getDrawable(R.drawable.pattern3e);
		        }
		        else if(patternCount == 4){         
	                newPattern = getResources().getDrawable(R.drawable.pattern4e);
		        }
		        else if(patternCount == 5){         
	                newPattern = getResources().getDrawable(R.drawable.pattern5e);
		        }
		        else newPattern = getResources().getDrawable(R.drawable.pattern6e);
        		break;
        }       	        		
        		    
        drawView.setBackground(newPattern);
        drawView.clear();
    }
}
