package org.aars.android.util;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class ExportUtil {

	public static void exportToExcel(Context context, Cursor cursor, List<String> cols, String excelfileName) {
		//final String fileName = "wallet_export.xls";
		final String fileName = excelfileName + ".xls";

		// Saving file in external storage
		File sdCard = null;
		
		Log.d("ExportUtil.exportToExcel() -->> externalstorage state", Environment.getExternalStorageState());
		
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
			sdCard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
		else {
			Toast.makeText(context, "Media Card Not Available", Toast.LENGTH_SHORT).show();
			return;
		}
		
		File directory = new File(sdCard.getAbsolutePath() + "/wallet_exports");

		Log.d("Export Directory", directory.getAbsolutePath());
		
		// create directory if not exist
		if (!directory.isDirectory()) {
			directory.mkdirs();
		}

		// file path
		File file = new File(directory, fileName);
		
		WorkbookSettings wbSettings = new WorkbookSettings();
		wbSettings.setLocale(new Locale("en", "EN"));
		WritableWorkbook workbook;

		try {
			workbook = Workbook.createWorkbook(file, wbSettings);
			// Excel sheet name. 0 represents first sheet
			WritableSheet sheet = null;

			try {
				//String date = null;
				//long total = 0;
				int row = 1;
				sheet = workbook.createSheet("Transactions", 0);
				addHeaders(cols, sheet);
				
				if (cursor.moveToFirst()) {
					
					do {
						/*if(date == null){
							date = cursor.getString(1);

							sheet = workbook.createSheet(date, 0);

							row = 1;
							
							addHeaders(cols, sheet);
						} else if(!date.equals(cursor.getString(1))){
							date = cursor.getString(1);
							
							row = 1;
							sheet = workbook.createSheet(date, 0);
							
							addHeaders(cols, sheet);
							total = 0;
						}
						
						//String category = cursor.getString(3);
						
						if(category.equals("income")){
							total += cursor.getInt(2);
						} else if(category.equals("expense")){
							total -= cursor.getInt(2);
						}*/
						
						for(int col = 0; col < cursor.getColumnCount(); col++){
							sheet.addCell(new Label(col, row, cursor.getString(col)));
						}
						row++;
						//sheet.addCell(new Label(0, row, "TOTAL"));
						//sheet.addCell(new Label(2, row, String.valueOf(total)));
					} while (cursor.moveToNext());
				}
				// closing cursor
				cursor.close();
			} catch (RowsExceededException e) {
				Toast.makeText(context, "Not Exported", Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			} catch (WriteException e) {
				Toast.makeText(context, "Not Exported", Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
			workbook.write();
			try {
				workbook.close();
			} catch (WriteException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(context, "Not Exported", Toast.LENGTH_SHORT).show();
		}
		Toast.makeText(context, "Exported to " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
	}
	
	private static void addHeaders(List<String> cols, WritableSheet sheet) throws RowsExceededException, WriteException{
		int index = 0;
		for(String colName : cols){
			sheet.addCell(new Label(index, 0, colName));
			index++;
		}
	}
	
}
