package com.example.drawing;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout.LayoutParams;

public class MainActivity extends Activity {
	private DrawingView drawView;
	private ImageButton currPaint;
	private Button resetButton;
	private boolean drawDraw = true;
	private boolean firstTouch = true;
	int counter = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
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
						if (firstTouch) {
						}
						Log.w("DW", "color:  " + color2);
						break;
					
					case MotionEvent.ACTION_MOVE:
						drawing.draw(DrawingView.MOVE, touchX, touchY);
						int color = Utils.findColor(drawing, xCoord, yCoord);
						if (color == -1 && drawDraw) {
							counter++;
						}
						if (!drawDraw) {
							break;
						}
						if (counter >= 10 && drawDraw) {
							drawDraw = false;
							showErrorDialog();
						}
						Log.w("DW", "color:  " + color);
						break;
					case MotionEvent.ACTION_UP:;
						drawing.draw(DrawingView.UP, touchX, touchY);
					default:
						return false;
				}
				return true;
			}
		});
		
		drawView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
		//drawView.setImageDrawable(getResources().getDrawable(R.drawable.bg));
		drawView.setBackground(getResources().getDrawable(R.drawable.background2));
		layout.addView(drawView);
		//get the palette
		LinearLayout paintLayout = (LinearLayout) findViewById(R.id.paint_colors);
		currPaint = (ImageButton)paintLayout.getChildAt(0);
		currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
		addResetButton();
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

/*	
    public void addNewPatternButton(){
        newPatternButton = (Button) findViewById(R.id.pattern_button);
        newPatternButton.setOnClickListener(setToggleButtonClickListener()));
}	
*/
	public void addResetButton() {
		resetButton = (Button) findViewById(R.id.reset_btn);
		resetButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				drawView.clear();
			}
		});
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void showErrorDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK)
																.setTitle(R.string.app_name);
		builder.setMessage("Out of bounds!  Please try again");
		builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				drawDraw = true;
				counter = 0;
				drawView.clear();	
			}
		});
		AlertDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.setCancelable(false);
		dialog.show();
	}
	

	 	
/*    // --------------------------- button switching stuff -----------------------------
    private void togglePattern(){

            DrawingView drawingView = (DrawingView) findViewById(R.id.drawing);
            Drawable newPattern;
            
            if(imageCount == 0){
                    newPattern = getResources().getDrawable(R.drawable.pattern0);
            }
            else if(imageCount == 1){
                    newPattern = getResources().getDrawable(R.drawable.pattern1);
            }

            //else if (imageCount == 2)
            else {         
                    newPattern = getResources().getDrawable(R.drawable.pattern2);
            }
            drawingView.setImageDrawable(newPattern);        
    }
    
    
    // listener for button switching patterns
    private void setToggleButtonClickListener(){
            Button toggleButton = (Button) findViewById(R.id.pattern_button);
            toggleButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v){
                            
                            imageCount = (imageCount++) % NUMBEROFPATTERNS;        // imagecount++; if hit last image go back to zero
                            togglePattern();
                    }
            });
    }*/
	
}
