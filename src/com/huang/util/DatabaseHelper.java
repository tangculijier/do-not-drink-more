package com.huang.util;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.github.mikephil.charting.utils.Utils;
import com.huang.model.Habit;
/**
 * ����sqlite�Ĺ�����
 */
public class DatabaseHelper extends SQLiteOpenHelper
{
	private final String TAG = this.getClass().getSimpleName();
	/**
	 * ���ݿ������
	 */
	private static final String DB_NAME = "mydatabase.db";
	/**
	 * ���ݿ�İ汾
	 */
	private static final int version = 1;//���ݿ�汾
	
	public static final String CREATE_DRINK = "create table habit (id integer PRIMARY KEY AUTOINCREMENT, date timestamp ,type SMALLINT);";

	private Context ctx;
	
	public DatabaseHelper(Context ctx)
	{
		super(ctx, DB_NAME, null, version);
		this.ctx = ctx;
	}
	public DatabaseHelper(Context context, String name, CursorFactory factory,
			int version)
	{
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}
	


	@Override
	public void onCreate(SQLiteDatabase db)
	{
		LogUtil.d("huang", "create database");
		db.execSQL("DROP TABLE IF EXISTS habit");  
		db.execSQL(CREATE_DRINK);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * @param currentDate  ��ǰ������
	 * @return List<Habit> ��ǰ���������·ݺȹ����ϵ�ʱ�伯��
	 */
	
	public List<Habit> getCurrentMonthDrinkRecord(Date currentDate)
	{
		List<Habit> currentMonthDrinkRecord = new ArrayList<Habit>();
		
		SQLiteDatabase db = this.getReadableDatabase();
		String[] FirstDayAndLast = DateUtil.getMonthFirstAndLastDate(currentDate);
		LogUtil.d("huang", "this month first day="+FirstDayAndLast[0]+" lastday="+FirstDayAndLast[1]);

		 Cursor cursor = db.rawQuery("select  DATE(date),COUNT(id) from habit  where date between ? and ? group by DATE(date)",FirstDayAndLast);
		//select result
		if (cursor.moveToFirst()) 
		{  
		    for (int i = 0; i < cursor.getCount(); i++) 
		    {  
		    	String date = cursor.getString(0) ;
		    	int dateDrinkTimes = cursor.getInt(1);
		    	Habit habit = new Habit();
		    	habit.setDate(date);
		    	habit.setDateDrinkTimes(dateDrinkTimes);
		    	LogUtil.d("huang", "cursor i="+i+" date="+date +" dateDrinkTimes="+dateDrinkTimes);
		    	currentMonthDrinkRecord.add(habit);
		    	cursor.moveToNext();  
		    	
		    } 
		}
		if(!cursor.isClosed())
		cursor.close();
		return currentMonthDrinkRecord;
		
		
	}
	
	/**
	 * ��¼��������ϵ�ʱ��
	 */
	public void insertDrinkTime()
	{
		SQLiteDatabase db = this.getWritableDatabase();
		java.util.Date utilDate = new java.util.Date();  
		db.execSQL("insert into habit values(null,?,?)",new Object[]{DateUtil.DateToString(utilDate),"1"});
		LogUtil.d("huang", "insert success");
	}
	
	
	/**
	 * ��ѯ�����ϴκ����ϱ��ֵ�ʱ��
	 */
	public String[] getKeepTime()
	{
		String[] res = new String[2];
		String keepTimeFromLastDrink = "0";
		String recordNumber = "0";
		java.util.Date utilDate = new java.util.Date();  
		java.sql.Date nowDay = new java.sql.Date(utilDate.getTime());  
		
			Habit habit = getRecentDrinkRecord();
			if(habit == null)//no record
			{
				SharedPreferences  setting =ctx.getSharedPreferences(Constant.SHARE_PS_Name, ctx.MODE_PRIVATE);
				String firstDay = setting.getString("firstDay", "");
				res[0] = DateUtil.daysBetween(DateUtil.StringToDate(firstDay), nowDay);
				recordNumber = "0";
				LogUtil.d("huang", "no records firstday="+firstDay+" nowday="+nowDay);
			}
			else
			{
				Date lastDayDate = DateUtil.StringToDate(habit.getDate());
				res[0] = DateUtil.daysBetween(lastDayDate, nowDay);
				recordNumber = "more";
				LogUtil.d("huang", "lastDay="+habit.getDate()+"nowDay="+nowDay+"  daysbetween="+res[0]);
			}
	
		res[1] = recordNumber;
		LogUtil.d("huang", "recordNumber="+res[1]);
		
		return res;
		
	
	}
	
	/**
	 * �õ����һ������ϵļ�¼
	 */
	public Habit getRecentDrinkRecord()
	{
		Habit habit = null;
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor=db.rawQuery("select * from habit order by id DESC limit ?,?",new String[]{"0","1"});  
		if(cursor.moveToFirst())  
		{
			int id = cursor.getInt(0);
			String date = cursor.getString(1);
			int type = cursor.getInt(2);
			habit = new Habit(id, date, type); 
			
		}
		cursor.close();
		return habit;
	}

	/**
	 * @param currentDate ��ǰ����
	 * @return monthSumOfDrinkTimes ���º������ܴ���
	 */
	public int getMonthSumOfDrinkTimes(Date currentDate)
	{
		int monthSumOfDrinkTimes = 0;
		SQLiteDatabase db = this.getReadableDatabase();
		String[] FirstDayAndLast = DateUtil
				.getMonthFirstAndLastDate(currentDate);
		Cursor cursor = db.rawQuery(
				"select  COUNT(id) from habit  where date between ? and ? ",
				FirstDayAndLast);
		// select result
		if (cursor.moveToFirst())
		{
			monthSumOfDrinkTimes = cursor.getInt(0);
		}
		if (!cursor.isClosed())
			cursor.close();
		return monthSumOfDrinkTimes;
	}

	/**
	 * @param currentDate ��ǰ����
	 * @return ���ص��º��������һ��ĺ����ϴ���
	 */
	public int getMostDrinkTimesOfMonth(Date currentDate)
	{
		// TODO Auto-generated method stub
		int mostDrinkTimesOfMonth = 0;
		SQLiteDatabase db = this.getReadableDatabase();
		String[] FirstDayAndLast = DateUtil
				.getMonthFirstAndLastDate(currentDate);
		Cursor cursor = db.rawQuery("select COUNT(id) from habit  where date between ? and ? group by DATE(date) order by COUNT(id) DESC",
						FirstDayAndLast); 
		// select result
		if (cursor.moveToFirst())
		{
			mostDrinkTimesOfMonth = cursor.getInt(0);
		}
		if (!cursor.isClosed())
			cursor.close();
		return mostDrinkTimesOfMonth;
	}
	
	
	/**
	 * @param currentDate ��ǰʱ��
	 * @return ��������PartTimeOfDrinktimesOfMonth�������������ֱ�Ϊһ�������磬�������Ϻ����ϵ�ͳ�ƴ���
	 */
	public int[] getTimeSectionOfDrinktimesOfMonth(Date currentDate)
	{
		int[] timeSectionOfDrinktimesOfMonth = new int[3];
		SQLiteDatabase db = this.getReadableDatabase();
		
		int[] timeSection = {0 ,12 ,18 ,24 };
		int hour;
		String[] FirstDayAndLast = DateUtil.getMonthFirstAndLastDate(currentDate);
		Cursor cursor = null;
		
			cursor = db.rawQuery("select strftime('%H',date)  from habit  where   date between ? and ? ",
							FirstDayAndLast); 
		if (cursor.moveToFirst())
		{
			for (int i = 0; i < cursor.getCount(); i++)
			{
				hour = Integer.parseInt(cursor.getString(0));
				if (timeSection[0] <= hour && hour < timeSection[1])
					timeSectionOfDrinktimesOfMonth[0]++;
				else if (timeSection[1] <= hour && hour < timeSection[2])
					timeSectionOfDrinktimesOfMonth[1]++;
				else
					timeSectionOfDrinktimesOfMonth[2]++;
				cursor.moveToNext();
			}
		}
		
		if (!cursor.isClosed())
			cursor.close();
		return timeSectionOfDrinktimesOfMonth;
	}
	
	
	/**
	 * @param currentDat ��ǰ����ʱ��
	 * @return ����������ֲ�����������
	 */
	public int getLongestKeepingDayOfMonth(Date currentDate)
	{
		
		int longestKeepingDayOfMonth = 0;
		SQLiteDatabase db = this.getReadableDatabase();
		String[] FirstDayAndLast = DateUtil.getMonthFirstAndLastDate(currentDate);
		Cursor cursor = null;
		
		cursor = db.rawQuery("select strftime('%d',date) from habit  where   date between ? and ? ",
						FirstDayAndLast); 
		
		int firstDayIndexInThisMonth = getFirstDayIndexInThisMonth(currentDate);//�õ�����µĵ�һ��index
		if (!cursor.moveToFirst())//��������޼�¼ ˵����һ��ʼ�ͱ���û�к�
		{
			//����ֲ�����������= ����-��һ��
	    	 longestKeepingDayOfMonth = DateUtil.getDateIndexInMonth(currentDate) - firstDayIndexInThisMonth ;
		}
		else
		{
			int thisRecordDate = 0;
			LogUtil.d("huang",firstDayIndexInThisMonth+"=lastRecordDate");
			for (int i = 0; i < cursor.getCount(); i++)
			{
				thisRecordDate = Integer.parseInt(cursor.getString(0));
				LogUtil.d("huang",thisRecordDate+"=thisRecordDate");
				longestKeepingDayOfMonth = (thisRecordDate - firstDayIndexInThisMonth - 1) > longestKeepingDayOfMonth ? (thisRecordDate - firstDayIndexInThisMonth - 1) : longestKeepingDayOfMonth;
				firstDayIndexInThisMonth = thisRecordDate;
				cursor.moveToNext();
			}
		}
	
	if (!cursor.isClosed())
		cursor.close();
		
		return longestKeepingDayOfMonth; 
	}
	
	
	/**
	 * @param time ��ǰ����
	 * @return ����û�����ϵ��Ծ�����
	 */
	public int getNoDrinkDaysNumber(Date currentDate)
	{
		int conscienceDays = 0;
		SQLiteDatabase db = this.getReadableDatabase();

		String[] FirstDayAndLast = DateUtil.getMonthFirstAndLastDate(currentDate);
		Cursor cursor = db.rawQuery("select  COUNT(*) from (select  COUNT(DATE(date)) from habit  where date between ? and ? group by DATE(date)) ",FirstDayAndLast); 
		// select result
		int firstDayIndexInThisMonth = getFirstDayIndexInThisMonth(currentDate);//�õ�����µĵ�һ��index

		if (!cursor.moveToFirst()) //�������û�м�¼
		{
			//��  ����û�����ϵ��Ծ����� = ��ǰ���� - ��һ��
			conscienceDays =  DateUtil.getDateIndexInMonth(currentDate) - firstDayIndexInThisMonth + 1;
		} 
		else//��������м�¼
		{
			//��  ����û�����ϵ��Ծ����� = ��ǰ���� - ��һ�� - ���˶�����
			 int drinkDays = Integer.parseInt(cursor.getString(0));//���������
			 conscienceDays =  DateUtil.getDateIndexInMonth(currentDate) - firstDayIndexInThisMonth 
					 -drinkDays + 1;
		}
		if (!cursor.isClosed())
			cursor.close();
		return conscienceDays;
	}
	
	/**
	 * 
	 * @param currentDate ��ǰʱ��
	 * @return �õ���ǰʱ�������·ݵĵ�һ��
	 */
	public int getFirstDayIndexInThisMonth(Date currentDate)
	{
		int firstDayIndexInThisMonth;
		SharedPreferences  setting =ctx.getSharedPreferences(Constant.SHARE_PS_Name, ctx.MODE_PRIVATE);
		String firstDay = setting.getString("firstDay", "");//example 2015-11-04
		
		Calendar cal = Calendar.getInstance();
		if (DateUtil.isSameMonth(DateUtil.StringToDate(firstDay), currentDate))//����ǰ�װ��ʱ��͵�ǰʱ������ͬһ���·�
		{
			
			cal.setTime(DateUtil.StringToDate(firstDay));
			firstDayIndexInThisMonth = cal.get(Calendar.DAY_OF_MONTH);//��һ��Ӱ�װ����һ�쿪ʼ����
		}
		else
		{
			firstDayIndexInThisMonth = 0;//�����������°�װ�� �����µĵ�һ�쿪ʼ�� 
		}
		return firstDayIndexInThisMonth;
	}
	
	
}
