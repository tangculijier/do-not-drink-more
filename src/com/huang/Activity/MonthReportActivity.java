package com.huang.Activity;

import java.util.Calendar;

import com.huang.nodrinkmore.R;
import com.huang.util.DatabaseHelper;
import com.huang.views.CalendarView;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

public class MonthReportActivity extends Activity
{
	DatabaseHelper databaseHelper;
	Calendar calendar = Calendar.getInstance();
	
	TextView monthSumOfDrinkTimesTextView; // ������
	int monthSumOfDrinkTimes = 0;
	
	TextView longestKeepingDayOfMonthTextView; // ����ּ�¼
	int longestKeepingDayOfMonth = 0;
	
	TextView mostDrinkTimesOfMonthTextView; // �����ƿ��
	int mostDrinkTimesOfMonth = 0;
	
	TextView amDrinkTimesOfMonthTextView;//00:00--12:00
	TextView pmDrinkTimesOfMonthTextView;//12:00--18:00
	TextView eveningDrinkTimesOfMonthTextView;//18:00--24:00
	int partTimeOfDrinktimesOfMonth[]=new int[3];
	
	
	
	
	

	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// to title
		setContentView(R.layout.mon_report);
		init();

	}

	public void init()
	{
		databaseHelper = new DatabaseHelper(this);
		
		monthSumOfDrinkTimes = databaseHelper.getMonthSumOfDrinkTimes(calendar.getTime());
		monthSumOfDrinkTimesTextView = (TextView) findViewById(R.id.monthSumOfDrinkTimes_textview);
		monthSumOfDrinkTimesTextView.setText("���¹���" + monthSumOfDrinkTimes + "ƿ");
		
		mostDrinkTimesOfMonth = databaseHelper.getMostDrinkTimesOfMonth(calendar.getTime());
		mostDrinkTimesOfMonthTextView = (TextView) findViewById(R.id.mostDrinkTimesOfMonth_textview);
		mostDrinkTimesOfMonthTextView.setText("��������" + mostDrinkTimesOfMonth + "ƿ");
		
		partTimeOfDrinktimesOfMonth = databaseHelper.getPartTimeOfDrinktimesOfMonth(calendar.getTime());
		amDrinkTimesOfMonthTextView = (TextView) findViewById(R.id.amDrinkTimesOfMonth_textview);
		amDrinkTimesOfMonthTextView.setText("�������繲��"+partTimeOfDrinktimesOfMonth[0]+"ƿ");
		pmDrinkTimesOfMonthTextView = (TextView) findViewById(R.id.pmDrinkTimesOfMonth_textview);
		pmDrinkTimesOfMonthTextView.setText("�������繲��"+partTimeOfDrinktimesOfMonth[1]+"ƿ");
		eveningDrinkTimesOfMonthTextView = (TextView) findViewById(R.id.eveningDrinkTimesOfMonth_textview);
		eveningDrinkTimesOfMonthTextView.setText("����ҹ�乲��"+partTimeOfDrinktimesOfMonth[2]+"ƿ");
		
		longestKeepingDayOfMonthTextView = (TextView) findViewById(R.id.longestKeepingDayOfMonth_textview);
	}

}
