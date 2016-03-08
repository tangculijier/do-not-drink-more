package com.huang.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.huang.model.Habit;
import com.huang.model.ModelReaderContract.AnalysisEntry;
import com.huang.model.ModelReaderContract.HabitEntry;
import com.huang.model.ReportOfMonth;
/**
 * ����sqlite�Ĺ�����
 */
public class DatabaseHelper extends SQLiteOpenHelper
{
	private static DatabaseHelper myInstance = null;
	
	/**
	 * ���õ���ģʽ
	 * @param context
	 * @return 
	 */
	public synchronized static DatabaseHelper getInstance(Context context)
	{
		if(myInstance == null)
		{
			myInstance = new DatabaseHelper(context, DB_NAME, null, DB_VERSION);
		}
		return myInstance;
		
	}
	
	/**
	 * ���ݿ������
	 */
	private static final String DB_NAME = "mydatabase.db";
	/**
	 * ���ݿ�İ汾
	 */
	private static final int DB_VERSION = 2;//���ݿ�汾
	
	
	private static final String TEXT_TYPE = " TEXT";
	private static final String DATE_TYPE_1 = " timestamp";
	private static final String DATE_TYPE_2 = " date";
	private static final String INT_TYPE_1 = " SMALLINT";
	private static final String INT_TYPE_2 = " INT";
	private static final String COMMA_SEP = ",";
	
	private static final String SQL_CREATE_HABIT =
		    "CREATE TABLE " + HabitEntry.TABLE_NAME + " (" +
		    HabitEntry.COLUMN_NAME_ENTRY_ID + " INTEGER PRIMARY KEY," +
    		HabitEntry.COLUMN_NAME_DATE + DATE_TYPE_1 + COMMA_SEP +
		    HabitEntry.COLUMN_NAME_TYPE + INT_TYPE_1  +" )";
	

	private static final String SQL_DELETE_HABIT="DROP TABLE IF EXISTS " + HabitEntry.TABLE_NAME;
	
	
	private static final String SQL_CREATE_ANALYSIS =
		    "CREATE TABLE " + AnalysisEntry.TABLE_NAME + " (" +
    		AnalysisEntry.COLUMN_NAME_ENTRY_ID + " INTEGER PRIMARY KEY," +
    		AnalysisEntry.COLUMN_NAME_MONTH + DATE_TYPE_2 + COMMA_SEP +
		    AnalysisEntry.COLUMN_NAME_NODRINKDAYS + INT_TYPE_2  + COMMA_SEP +
    	    AnalysisEntry.COLUMN_NAME_LONGESTKEEPDAY + INT_TYPE_2  + COMMA_SEP +
    	    AnalysisEntry.COLUMN_NAME_MORNINGTIMES + INT_TYPE_2  + COMMA_SEP +
    	    AnalysisEntry.COLUMN_NAME_AFTERNOONTIMES + INT_TYPE_2  + COMMA_SEP +
    	    AnalysisEntry.COLUMN_NAME_EVENINGTIMES + INT_TYPE_2  +
		    " )";
	
	private static final String SQL_DELETE_ANALYSIS="DROP TABLE IF EXISTS " + AnalysisEntry.TABLE_NAME;


	private Context ctx;
	
	public DatabaseHelper(Context ctx)
	{
		super(ctx, DB_NAME, null, DB_VERSION);
	}
	public DatabaseHelper(Context context, String name, CursorFactory factory,
			int version)
	{
		super(context, name, factory, version);
		this.ctx = context;
		myInstance = this;
	}
	

	/**
	 * ��һ���õ�ʱ��ִ��
	 */
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		LogUtil.d("huang", "create database");
		db.execSQL(SQL_DELETE_HABIT);  
		db.execSQL(SQL_CREATE_HABIT);
		
		db.execSQL(SQL_DELETE_ANALYSIS);  
		db.execSQL(SQL_CREATE_ANALYSIS);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		
		db.execSQL(SQL_DELETE_ANALYSIS);  
		db.execSQL(SQL_CREATE_ANALYSIS);
		
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
		//LogUtil.d("huang", "this month first day="+FirstDayAndLast[0]+" lastday="+FirstDayAndLast[1]);

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
		    	//LogUtil.d("huang", "cursor i="+i+" date="+date +" dateDrinkTimes="+dateDrinkTimes);
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
		ContentValues values = new ContentValues();
		values.put(HabitEntry.COLUMN_NAME_DATE, DateUtil.DateToString(utilDate));
		values.put(HabitEntry.COLUMN_NAME_TYPE, "1");
		db.insert(HabitEntry.TABLE_NAME, null, values);
		//LogUtil.d("huang", "insert success");
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
				SharedPreferences  setting =ctx.getSharedPreferences(AppConst.SHARE_PS_Name, ctx.MODE_PRIVATE);
				String firstDay = setting.getString("firstDay", "");
				res[0] = DateUtil.daysBetween(DateUtil.StringToDate(firstDay), nowDay);
				recordNumber = "0";
			}
			else
			{
				Date lastDayDate = DateUtil.StringToDate(habit.getDate());
				res[0] = DateUtil.daysBetween(lastDayDate, nowDay);
				recordNumber = "more";
			}
	
		res[1] = recordNumber;
		
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
		
		int firstDayIndexInThisMonth = getFirstDayIndexInThisMonth(currentDate);//�õ�����µĵ�һ��index �п������³� �п����ǰ�װ����
		int currentDayIndexInThisMonth = DateUtil.getDateIndexInMonth(currentDate);//

		if (!cursor.moveToFirst())//��������޼�¼ ˵����һ��ʼ�ͱ���û�к�
		{
			//����ֲ�����������= ����-��һ��
	    	 longestKeepingDayOfMonth = DateUtil.getDateIndexInMonth(currentDate) - firstDayIndexInThisMonth ;
		}
		else
		{
			//��ÿ����¼�ó����Ƚ��Ҽ������
			int current = 0;
			int last = firstDayIndexInThisMonth;
			for (int i = 0; i < cursor.getCount(); i++)
			{
				current = Integer.parseInt(cursor.getString(0));
				longestKeepingDayOfMonth = (current - last - 1) > longestKeepingDayOfMonth ?
						(current - last - 1) : longestKeepingDayOfMonth;
				last = current;
				cursor.moveToNext();
				if(i == cursor.getCount() - 1)//�����һ����ʱ���¼�͵���ı��ּ��
				{
					longestKeepingDayOfMonth = (currentDayIndexInThisMonth- current  - 1) > longestKeepingDayOfMonth ?
							(currentDayIndexInThisMonth- current  - 1) : longestKeepingDayOfMonth;
				}
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
			conscienceDays =  DateUtil.getDateIndexInMonth(currentDate) - firstDayIndexInThisMonth ;
		} 
		else//��������м�¼
		{
			//��  ����û�����ϵ��Ծ����� = ��ǰ���� - ��һ�� - ���˶�����
			 int drinkDays = Integer.parseInt(cursor.getString(0));//���������
			 conscienceDays =  DateUtil.getDateIndexInMonth(currentDate) - firstDayIndexInThisMonth 
					 - drinkDays ;
		}
		conscienceDays = conscienceDays < 0 ? 0 : conscienceDays;
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
		SharedPreferences  setting =ctx.getSharedPreferences(AppConst.SHARE_PS_Name, ctx.MODE_PRIVATE);
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
	
	

	/**
	 * ����±��Ƿ񱣴棬���û����������±�
	 */
	public void checkAndinsertAnalysis()
	{
        SQLiteDatabase db = this.getReadableDatabase();
		SharedPreferences setting =  ctx. getSharedPreferences(AppConst.SHARE_PS_Name, ctx.MODE_PRIVATE);
		String lastLoginDateStr =setting.getString(AppConst.Last_LOGIN_DATE, "");//�õ��ϴε�¼ʱ��
		Date lastLoginDate= DateUtil.StringToDate(lastLoginDateStr);
		Date now = new Date();
		if(!TextUtils.isEmpty(lastLoginDateStr))
		{
			//����ǰʱ����ϴε�¼ʱ�䲻����ͬһ���·�ʱ
			//nowÿ����ǰ��һ���� ��������û����������� ֱ���·����ϴε�¼ʱ�䴦��ͬһ����
			while (!DateUtil.isSameMonth(lastLoginDate, now))
			{
				now = DateUtil.getPreMonthLastDay(now);
				//�ж����ݿ����Ƿ��е�ǰ�·ݵļ�¼
				Cursor cursor=db.rawQuery("select month from analysis where month = ?",new String[]{DateUtil.DateToStringNoHour(now)});  
				if(cursor.moveToFirst() == false)
				{
					LogUtil.d("huang", "����·�"+DateUtil.DateToString(now)+"û������,���Բ���");
					insertAnalysis(now);
				}
			
			} 
			
		}
		setting.edit().putString(AppConst.Last_LOGIN_DATE, DateUtil.DateToString(new Date())).commit();
	}
	
	
	/**
	 * 
	 * @param currentMonth  eg:2015-11-31
	 * @return ��������µ����з�������
	 */
	public void insertAnalysis(Date currentDate)
	{
        currentDate = DateUtil.getLastDateInMonth(currentDate);
        int nodrinkDays = getNoDrinkDaysNumber(currentDate);
        int longestKeepDays = getLongestKeepingDayOfMonth(currentDate);
        int[] timeSection = getTimeSectionOfDrinktimesOfMonth(currentDate);
        
        SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("insert into analysis values(null,?,?,?,?,?,?)",
				new Object[]{DateUtil.DateToStringNoHour(currentDate),nodrinkDays,longestKeepDays
				,timeSection[0],timeSection[1],timeSection[2]});
		
		LogUtil.d("huang", "insert Analysis"+DateUtil.DateToString(currentDate)+"success");
	}
	

	
	/**
	 * 
	 * @param currentMonth  eg:2015-11-31
	 *  currentMonthӦ��Ϊ����µ����һ��
	 * @return ���currentMonth�����з�������
	 */
	public ReportOfMonth getAnalysis(String currentMonth)
	{
        SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor=db.rawQuery("select nodrinkdays,longestkeepday,morningtimes,afternoontimes,eveningtimes"
				+ " from analysis where month = ?",new String[]{(currentMonth)});  

		ReportOfMonth report = new ReportOfMonth();
		
		if(cursor.moveToFirst())
		{
			report.setDate(currentMonth);
			report.setNoDrinkDays(cursor.getInt(cursor.getColumnIndexOrThrow(AnalysisEntry.COLUMN_NAME_NODRINKDAYS)));
			report.setLongestKeepDays(cursor.getInt(cursor.getColumnIndexOrThrow(AnalysisEntry.COLUMN_NAME_LONGESTKEEPDAY)));
			report.setMorningtimes(cursor.getInt(cursor.getColumnIndexOrThrow(AnalysisEntry.COLUMN_NAME_MORNINGTIMES)));
			report.setAfternoontimes(cursor.getInt(cursor.getColumnIndexOrThrow(AnalysisEntry.COLUMN_NAME_AFTERNOONTIMES)));
			report.setEveningtimes(cursor.getInt(cursor.getColumnIndexOrThrow(AnalysisEntry.COLUMN_NAME_EVENINGTIMES)));
		}
		//������Ҫ���ؿ�ֵ
		return report;
		
	}
	public List<ReportOfMonth> getYearStatist(Date currentDate)
	{
		List<ReportOfMonth> res = new  ArrayList<ReportOfMonth>();
		LogUtil.d("huang","currentDate="+currentDate);
		SQLiteDatabase db = this.getReadableDatabase();

		String[] FirstDayAndLastInThisYear = DateUtil.getThisYearFirstDayAndLastDay(currentDate);
		Cursor cursor = db.rawQuery("select month,morningtimes+afternoontimes+eveningtimes  from analysis where month between ? and ? order by `month` ",
				FirstDayAndLastInThisYear); 
		LogUtil.d("huang","FirstDayAndLastInThisYear="+FirstDayAndLastInThisYear[0]);
		LogUtil.d("huang","FirstDayAndLastInThisYear="+FirstDayAndLastInThisYear[1]);

		if (cursor.moveToFirst())
			
		{
			for (int i = 0; i < cursor.getCount(); i++)
			{
				//LogUtil.d("huang",cursor.getString(0)+":"+);
				ReportOfMonth report = new ReportOfMonth();
				report.setDate(cursor.getString(0));
				report.setTotaltime(cursor.getInt(1));
				res.add(report);
				cursor.moveToNext();
			
			}
			
		} 
		else//��������м�¼
		{
			LogUtil.d("huang","nothing");
		}
		if (!cursor.isClosed())
			cursor.close();
		return res;
		
	}
	
}
