package aars.app.drawin.activities;

import java.io.File;
import java.util.List;

import aars.app.drawin.custom.views.Sheet;
import aars.app.drawin.util.MyApplication;
import aars.app.drawin.util.Utility;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

public class NoteActivity extends Activity implements OnClickListener {

	private RelativeLayout layoutSheet = null;
	private LayoutParams params;
	private EditText etFileName;
	private LinearLayout layoutColors;
	private View layoutBrush;
	private Button btnHide;
	private SeekBar seekBar;
	
	private Sheet sheet;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_draw_sheet);
        
        params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        layoutSheet = (RelativeLayout)findViewById(R.id.sheet);
        
        layoutBrush = (View)findViewById(R.id.toolHolder);
        layoutColors = (LinearLayout)findViewById(R.id.layoutColors);
        
        btnHide = (Button)findViewById(R.id.btnHide);
        btnHide.setOnClickListener(this);
        
        sheet = new Sheet(this);
        sheet.setLayoutParams(params);
        sheet.setTool(Utility.Tools.PENCIL);
        layoutSheet.addView(sheet);
        
        seekBar = (SeekBar)findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			
			public void onStopTrackingTouch(SeekBar seekBar) {
				
			}
			
			public void onStartTrackingTouch(SeekBar seekBar) {
				
			}
			
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				Sheet.setPencilSize(Float.valueOf(progress));
			}
		});
        
        instantiateColorViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }
    
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		
		switch (item.getItemId()) {
		case R.id.pencil:
			sheet.setTool(Utility.Tools.PENCIL);
			
			if (layoutBrush.getVisibility() == View.VISIBLE) {
				layoutBrush.setVisibility(View.GONE);
			}
			else {
				layoutBrush.setVisibility(View.VISIBLE);
			}
			
			break;
		case R.id.eraser:
			sheet.setTool(Utility.Tools.ERASER);
			break;
		case R.id.clear:
			sheet.clear();
			break;
		case R.id.save:
			openFileNameEditorPopup();
			break;
		case R.id.newNote:
			sheet.clear();
			sheet = new Sheet(this);
			Utility.showToast("New Sheet Opened");
			break;
		case R.id.open:
			displayAvailableFiles();
			break;
		case R.id.exit:
			finish();
			break;
		default:
			break;
		}
		
		return super.onMenuItemSelected(featureId, item);
	}
	
	private void instantiateColorViews(){
		int childCount = layoutColors.getChildCount();
		if(childCount > 0){
			for(int i = 0; i < childCount; i++){
				View view = layoutColors.getChildAt(i);
				view.setOnClickListener(this);
			}
		}
	}
	
	public void openFileNameEditorPopup(){
		AlertDialog.Builder popup = new AlertDialog.Builder(this);
		
		popup.setTitle("Enter File Name");
		
		etFileName = new EditText(this);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		etFileName.setLayoutParams(params);
		
		popup.setView(etFileName);
		
		popup.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				String fileName = etFileName.getText().toString();
				if(!fileName.equals("")){
					sheet.saveDrawing(fileName);
				}
				else{
					Utility.showToast("File Name should not be empty");
					openFileNameEditorPopup();
				}
			}
		});
		popup.show();
	}
	
	public void displayAvailableFiles(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Saved Drawings");
		
		final List<String> list = Sheet.getAvailableFiles();
		if(list != null){
			if(list.size() <= 0){
				Toast.makeText(this, "No Files Found", Toast.LENGTH_SHORT).show();
				return;
			}
		}
		else{
			Toast.makeText(this, "No Files Found", Toast.LENGTH_SHORT).show();
			return;
		}
		
		final ListView listView = new ListView(MyApplication.getContext());
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, list);
		listView.setAdapter(adapter);
		
		builder.setView(listView);
		
		builder.setPositiveButton("Open", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				String fileName = list.get(listView.getCheckedItemPosition()).trim();
				openFileInPopup(fileName);
			}
		});
		builder.setNegativeButton("Delete",  new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				String fileName = list.get(listView.getCheckedItemPosition()).trim();
				File mediaFile = new File(
						Environment
								.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
						"MyDrawing/" + fileName);
				mediaFile.delete();
			}
		});
		
		builder.show();
	}
	
	public void openFileInPopup(String fileName){
		File mediaFile = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"MyDrawing/" + fileName);
		Bitmap myBitmap = BitmapFactory.decodeFile(mediaFile.getAbsolutePath());
		
		ImageView view = new ImageView(this);
		view.setImageBitmap(myBitmap);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(fileName);
		builder.setView(view);
		builder.show();
		builder.setPositiveButton("OK", null);
		
	}
	
	public void openFileUsingGallery(String fileName){
		File mediaFile = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"MyDrawing/" + fileName);
		
		try {
			if (!mediaFile.exists()) {
				Log.d("openDrawing()", "file not exists at " + mediaFile.getAbsolutePath());
				return;
			}

			File file = new File(fileName);
			
			MimeTypeMap map = MimeTypeMap.getSingleton();
		    String ext = MimeTypeMap.getFileExtensionFromUrl(file.getName());
		    String type = map.getMimeTypeFromExtension(ext);
		    
		    if (type == null)
		        type = "*/*";
		    
		    Uri data = Uri.fromFile(file);
			
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_VIEW);
			intent.setDataAndType(data, type);
			startActivity(intent);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivBlack:
			Sheet.setPencilColor(getResources().getColor(R.color.black));
			break;
		case R.id.ivBlue:
			Sheet.setPencilColor(getResources().getColor(R.color.dark_blue));
			break;
		case R.id.ivCyan:
			Sheet.setPencilColor(getResources().getColor(R.color.cyan));
			break;
		case R.id.ivGreen:
			Sheet.setPencilColor(getResources().getColor(R.color.green));
			break;
		case R.id.ivOrange:
			Sheet.setPencilColor(getResources().getColor(R.color.orange));
			break;
		case R.id.ivPink:
			Sheet.setPencilColor(getResources().getColor(R.color.dark_pink));
			break;
		case R.id.ivPurple:
			Sheet.setPencilColor(getResources().getColor(R.color.purple));
			break;
		case R.id.ivRed:
			Sheet.setPencilColor(getResources().getColor(R.color.red));
			break;
		case R.id.ivSilver:
			Sheet.setPencilColor(getResources().getColor(R.color.silver));
			break;
		case R.id.ivYellow:
			Sheet.setPencilColor(getResources().getColor(R.color.yellow));
			break;
		case R.id.btnHide:
			layoutBrush.setVisibility(View.GONE);
			break;
			
		default:
			break;
		}
	}

}
