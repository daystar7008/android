package aars.app.drawin.custom.views;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import aars.app.drawin.util.Utility;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class Sheet extends View {

	private static Paint paint;
	private Paint circlePaint;
	private Path path, circlePath;
	private ArrayList<Path> paths;
	private ArrayList<Paint> paints;
	private int pauseCount = 0;
	private View view;
	private Canvas canvas;
	private Bitmap bitMap;
	
	private float x, y;
	
	public Sheet(Context context) {
		super(context);
		view = this;
		view.setBackgroundColor(Color.WHITE);
		view.setDrawingCacheEnabled(true);
		view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
		
		circlePaint = new Paint();
		circlePath = new Path();
		path = new Path();
		paths = new ArrayList<Path>();
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paints = new ArrayList<Paint>();
		
		paint.setAntiAlias(true);
		paint.setColor(Color.BLACK);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(5f);
		
		circlePaint.setAntiAlias(true);
		circlePaint.setColor(Color.BLUE);
		circlePaint.setStyle(Paint.Style.STROKE);
		circlePaint.setStrokeWidth(1f);
		
		bitMap = Bitmap.createBitmap(320, 480, Bitmap.Config.ARGB_8888);
	}

	public static void setPencilColor(int color){
		paint.setColor(color);
	}
	
	public static void setPencilSize(float size){
		if(size >= 30){
			paint.setStrokeWidth(30f);
		}
		else{
			paint.setStrokeWidth(size);
		}
	}
	
	public void setTool(Utility.Tools tool){
		if(tool.equals(Utility.Tools.PENCIL)){
			paint = new Paint();
			
			paint.setAntiAlias(true);
			//paint.setColor(Color.BLACK);
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(5f);
		}
		else if(tool.equals(Utility.Tools.ERASER)){
			paint = new Paint();
			
			paint.setAntiAlias(true);
			paint.setColor(Color.WHITE);
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeJoin(Paint.Join.ROUND);
			paint.setStrokeWidth(25f);
		}
	}
	
	@Override
	protected void onDraw(Canvas mCanvas) {
		this.canvas = mCanvas;
		//canvas.drawColor(Color.WHITE, PorterDuff.Mode.LIGHTEN);
		
		canvas.drawBitmap(bitMap, 0, 0, paint);
		
		int index = 0;
		while(index < paths.size() && index < paints.size()){
			canvas.drawPath(paths.get(index), paints.get(index));
			index++;
		}
		canvas.drawPath(path, paint);
		canvas.drawPath(circlePath, circlePaint);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		x = event.getX();
		y = event.getY();
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			path.moveTo(x, y);
			return true;
		case MotionEvent.ACTION_MOVE:
			path.lineTo(x, y);
			circlePath.reset();
			circlePath.addCircle(x, y, 20, Path.Direction.CW);
			postInvalidate();
			break;
		case MotionEvent.ACTION_UP:
			paths.add(path);
			paints.add(paint);
			path = new Path();
			pauseCount++;
			circlePath.reset();
			break;
		default:
			break;
		}
		// Schedules a repaint.
		// Force a view to draw.
		postInvalidate();
		
		return true;
	}

	public void undo(){
		if(pauseCount > 1){
			pauseCount--;
			paths.remove(paths.size() - 1);
			postInvalidate();
		}
		else{
			Utility.showToast("Can't Undo Further");
		}
	}
	
	public void clear(){
		paths = new ArrayList<Path>();
		paints = new ArrayList<Paint>();
		path = new Path();
		pauseCount = 0;
		postInvalidate();
	}
	
	public void saveDrawing(String fileName){
		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"MyDrawing/");
		Bitmap drawnImage = view.getDrawingCache();
		
		try {
			if (!mediaStorageDir.exists()) {
				mediaStorageDir.getParentFile().mkdirs();
			}
			File mediaFile = new File(mediaStorageDir.getPath()
					+ File.separator + fileName.trim() + ".png");

			if (!mediaFile.exists()) {
				mediaFile.getParentFile().mkdirs();
			}
			
			FileOutputStream out = new FileOutputStream(mediaFile);

			drawnImage.compress(CompressFormat.PNG, 100, out);
			out.flush();
			out.close();
			
			Utility.showToast("saved  to : " + mediaFile);
			Log.d("Saved to", mediaFile.getAbsolutePath());
			
		} catch (Exception e) {
			e.printStackTrace();
			Utility.showToast("Not Saved");
		}
	}
	
	public static List<String> getAvailableFiles(){
		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"MyDrawing/");
		return Arrays.asList(mediaStorageDir.list());
	}
	
	public void openDrawing(String fileName){
		File mediaFile = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"MyDrawing/" + fileName);
		
		try {
			if (!mediaFile.exists()) {
				Log.d("openDrawing()", "file not exists at " + mediaFile.getAbsolutePath());
				return;
			}
			
			Bitmap bitmap = BitmapFactory.decodeFile(mediaFile.getAbsolutePath()).copy(Bitmap.Config.ARGB_8888, true);
			
			canvas = new Canvas(bitmap);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
