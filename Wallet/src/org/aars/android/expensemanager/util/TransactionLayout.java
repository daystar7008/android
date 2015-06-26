package org.aars.android.expensemanager.util;

import org.aars.android.wallet.R;

import android.content.Context;
import android.text.InputFilter;
import android.text.InputType;
import android.view.ViewGroup.LayoutParams;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TransactionLayout {

	private LinearLayout layout;
	private DatePicker datePicker;
	private TextView tvName, tvAmount;
	private EditText etName, etAmount;
	
	public TransactionLayout(Context context){
		
		LayoutParams parentParam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		LayoutParams childParam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		
		layout = new LinearLayout(context);
		layout.setLayoutParams(parentParam);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setPadding(10, 10, 10, 10);
		layout.setBackgroundColor(context.getResources().getColor(R.color.bg));
		
		datePicker = new DatePicker(context);
		datePicker.setLayoutParams(childParam);
		datePicker.setPadding(10, 10, 10, 10);
		layout.addView(datePicker);
		
		tvName = new TextView(context);
		tvName.setText("Name");
		tvName.setTextColor(context.getResources().getColor(R.color.fg));
		tvName.setLayoutParams(childParam);
		tvName.setPadding(10, 10, 10, 10);
		layout.addView(tvName);
		
		etName = new EditText(context);
		etName.setLayoutParams(childParam);
		etName.setPadding(10, 10, 10, 10);
		etName.setSingleLine(true);
		etName.setTextColor(context.getResources().getColor(R.color.fg));
		etName.setBackgroundResource(R.drawable.custom_textview);
		
		InputFilter filter = new InputFilter.LengthFilter(20);
		etName.setFilters(new InputFilter[]{filter});
		
		layout.addView(etName);
		
		tvAmount = new TextView(context);
		tvAmount.setText("Amount");
		tvAmount.setTextColor(context.getResources().getColor(R.color.fg));
		tvAmount.setLayoutParams(childParam);
		tvAmount.setPadding(10, 10, 10, 10);
		layout.addView(tvAmount);
		
		etAmount = new EditText(context);
		etAmount.setLayoutParams(childParam);
		etAmount.setHint("0");
		etAmount.setSingleLine(true);
		etAmount.setTextColor(context.getResources().getColor(R.color.fg));
		etAmount.setPadding(10, 10, 10, 10);
		etAmount.setBackgroundResource(R.drawable.custom_textview);
		etAmount.setInputType(InputType.TYPE_CLASS_NUMBER);
		
		InputFilter filter1 = new InputFilter.LengthFilter(9);
		etAmount.setFilters(new InputFilter[]{filter1});
		
		layout.addView(etAmount);
	}
	
	public LinearLayout getLayout(){
		return layout;
	}
	
	public String getDate(){
		int day = datePicker.getDayOfMonth();
		int month = datePicker.getMonth();
		int year = datePicker.getYear();
		
		String date = Integer.toString(day) + "/" + Integer.toString(month + 1) + "/" + Integer.toString(year);
		return date;
	}
	
	public String getName(){
		return etName.getText().toString();
	}
	
	public int getAmount(){
		return Integer.parseInt(!etAmount.getText().toString().equals("") ? etAmount.getText().toString() : "0");
	}
	
}
