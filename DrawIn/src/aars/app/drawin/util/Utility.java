package aars.app.drawin.util;

import android.app.AlertDialog;
import android.widget.Toast;

public class Utility {

	public enum Tools {
		PENCIL, ERASER
	}
	
	public static void showToast(String message){
		Toast.makeText(MyApplication.getContext(), message, Toast.LENGTH_LONG).show();
	}
	
	public static AlertDialog.Builder getPopup(String title){
		AlertDialog.Builder popup = new AlertDialog.Builder(MyApplication.getContext());
		
		popup.setTitle(title);
		
		return popup;
	}
	
}
