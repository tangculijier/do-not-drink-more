package com.huang.views;

/**
 * �����ؼ�
 */


import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.huang.model.Habit;
import com.huang.util.DateUtil;
import com.huang.util.LogUtil;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;

public class CalendarView extends View  implements View.OnTouchListener
{
	/**
	 * ��Ļ�ܶ�
	 */
	private float density;
	
	/**
	 * �����ؼ�����߶�
	 */
	private int calHeight ;
	
	private int calWidth ;
	
	/**
	 * ��һ ������ ��ĸ߶�
	 */
	private float weekHeight;
	
	/**
	 * ��ʾ���� ��ĸ߶�
	 */
	private float yearHeight;
	
	/**
	 * ��ʾ����ĳһ�� ��ĳ���
	 */
	private float cellWidth;
	
	private float cellHeight;
	
	/**
	 * �����ؼ����븸�ؼ���ߵı߾�
	 */
	private float calLeftMargin;
	/**
	 * �����ؼ����븸�ؼ��ϱߵı߾�
	 */
	private float calTopMargin;
	
	/**
	 * �������߿��������ɫ
	 */
	private int borderColor = Color.parseColor("#CCCCCC");
	/**
	 * ������һ�����յ�������ɫ
	 */
	private int WeekTextColor = Color.BLACK;
	/**
	 * �������ڵ���ɫ ���û�ɫ
	 */
	private int dateTextColor = Color.parseColor("#666666");//Color.BLACK;//Color.argb(255, 245, 247, 249);////RGB(245,247,249)
	/**
	 * �������ڿ򱳾�����ɫ
	 */
	public int todayBgColor = Color.parseColor("#379BFF");//��ɫ	
	/**
	 * �����������ֵ���ɫ
	 */
	public int todayNumberColor = Color.WHITE;
	/**
	 * ������������������ڱ�����ɫ
	 */
	public int todayDrinkedBgColor = Color.BLACK;
	
	/**
	 * ����չʾ�������� 7 * 6
	 */
	private int[] date = new int[42];
	
	public String[] weekText = { "Mon", "Tue", "Wed", "Thu", "Fri", "Sat","Sun"};
	
	private Calendar calendar;
	
	/**
	 * ��ǰҳ��չʾ��date
	 */
	private Date currentDate;
	public Date getCurrentDate()
	{
		return currentDate;
	}

	public void setCurrentDate(Date currentDate)
	{
		this.currentDate = currentDate;
	}

	/**
	 * ʵ�ʵ����date
	 */
	private Date todayDate;
	/**
	 * ��ǰչʾ�·ݿ�ʼdate�ͽ���date
	 */
	private Date firstShowDate,lastShowDate;
	/**
	 * ��չʾ��ʼ������������ʵ�ʵĵ�һ����date[]�����е�index
	 */
	private int currentStartIndex,currentEndIndex;
	
	//���ؼ����ü����¼�
	private OnItemClickListener onItemClickListener;
	
	/**
	 * �����Ƿ�ȹ�����
	 */
	public boolean isTodayDrinked = false;
	
	List<Habit> drinkRecords ;
	
	public List<Habit> getDrinkRecords()
	{
		return drinkRecords;
	}

	public void setDrinkRecords(List<Habit> drinkDateRecords)
	{
		this.drinkRecords = drinkDateRecords;
	}

	public CalendarView(Context context)
	{
		this(context,null);
	}

	public CalendarView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom)
	{
		super.onLayout(changed, left, top, right, bottom);
	}
	
	void init()
	{
		 density = getResources().getDisplayMetrics().density;
		 calHeight = getResources().getDisplayMetrics().heightPixels * 2 / 5;//phone��2/5
		 calWidth = getResources().getDisplayMetrics().widthPixels;

		 cellHeight =  yearHeight = weekHeight = calHeight / 7f;//һ��7��
		 cellWidth = calWidth / (weekText.length + 1);//��һ-����7��
		 calLeftMargin = cellWidth / 2;
		 calTopMargin = cellWidth / 3;
		 
		 //��ʼ��ʱ��
		 currentDate = todayDate = new Date();
		 calendar = Calendar.getInstance();
		 calendar.setTime(todayDate);
		 
		 setOnTouchListener(this);
		 
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		//LogUtil.d("huang", "onDraw");
		Paint borderPaint = setBorderPaint();
		Path borderPath = drawBorderPath();//���߿�
		
		String yearText = getYearAndMonth(currentDate);//������
		Paint yearPaint = setYearPaint();
		//Paint yearBgPaint = setYearBgPaint();
		float textWidth = yearPaint.measureText(yearText);
		
		canvas.drawText(yearText, (calWidth - textWidth) / 2f,yearHeight+10 , yearPaint);
		
		
		Paint weekPaint = setWeekPaint();//����һ-���յ���
		for(int i = 0 ; i < weekText.length; i++ )
		{
			float x =  calLeftMargin + i * cellWidth + (cellWidth - weekPaint.measureText(weekText[i]))/2f;//������ʾ
			float y =  yearHeight + weekHeight * 3 / 4f;// 3/4�����С
			canvas.drawText(weekText[i],x,y, weekPaint);
		}
		
		calculateDate(date);
		
		//������
		int todayIndexArray = -1;
		calendar.setTime(currentDate);
		String currentYearAndMonth = getYearAndMonth(currentDate);//���������������
		calendar.setTime(todayDate);
		String todayYearAndMonth = getYearAndMonth(todayDate);//ԭʼ���������
		if(TextUtils.equals(currentYearAndMonth, todayYearAndMonth))
		{
			todayIndexArray = currentStartIndex + calendar.get(Calendar.DAY_OF_MONTH)  - 1 ;
		}
		//LogUtil.d("huang", "currentdate="+currentDate+" currentStartIndex="+currentStartIndex);
		
		drawDrinkRecords(canvas);//���ȹ����ϵ����ڱ���
		
		drawDateNumberAndBg(canvas, todayIndexArray);//����������
		
		

		canvas.drawPath(borderPath, borderPaint);
	
		super.onDraw(canvas);
	}


	public void drawDrinkRecords(Canvas canvas)
	{
		//LogUtil.d("huang", "drawDrinkRecords");
		if(drinkRecords != null && drinkRecords.size() != 0)
		{
			Paint drinkBgPaint = new Paint();
			drinkBgPaint.setColor(todayDrinkedBgColor);
			
			Calendar calTemp = Calendar.getInstance();
			for(int j = 0;j < drinkRecords.size();j++)
			{
				Habit habit = drinkRecords.get(j);
				int dateDrinkTimes = habit.getDateDrinkTimes();
				Date drinkDate = DateUtil.StringToDate(habit.getDate());
				calTemp.setTime(drinkDate);
				int dayInMonth  =  calTemp.get(Calendar.DAY_OF_MONTH);
				int index = currentStartIndex + dayInMonth - 1;
				drawBgByIndex(canvas, index, drinkBgPaint,dateDrinkTimes);
			//	LogUtil.d("huang", "drinkDate="+drinkDate.toString()+" dayInMonth="+dayInMonth +" index="+index);
				if(TextUtils.equals(DateUtil.DateToStringNoMinute(drinkDate), DateUtil.DateToStringNoMinute(todayDate)))
				{
					isTodayDrinked = true;
				}
				//and habit drintime
			
				
				
			}
			
			
	
		}
	}


	public void drawDateNumberAndBg(Canvas canvas, int todayIndexArray)
	{
		for(int i = 0 ; i < date.length ; i++ )
		{
			int color = dateTextColor;
			if( i < currentStartIndex)//��ǰ��֮ǰ�ϸ��¼����������ڣ�չʾ�ɻ�ɫ
			{
				color = borderColor;
			}
			else if( i >= currentEndIndex)//�¸���Ҳչʾ��ɫ
			{
				color = borderColor;
			}
			if( todayIndexArray != -1 && i == todayIndexArray) //����ǽ��� �ú�ɫ�����ע
			{
				//LogUtil.d("huang", "isTodayDrinked="+isTodayDrinked);
				if(isTodayDrinked == false)//���컹û�к�
				{
					color = todayNumberColor;
					Paint bgPaint = new Paint();
					bgPaint.setColor(todayBgColor);
					drawBgByIndex(canvas, todayIndexArray, bgPaint,0);
				}
			
			}
			
			drawCellText(canvas,i,date[i]+"",color);//�������ú�ɫ

		}
	}
	
	public void drawBgByIndex(Canvas canvas, int index, Paint Paint, int dateDrinkTimes)
	{
			//LogUtil.d("huang", "drawBgByIndex ���յ���index="+index+" dateDrinkTimes"+dateDrinkTimes);
			//LogUtil.d("huang", "today��"+index % weekText.length );
			//LogUtil.d("huang", "today��"+(index / weekText.length ));
			float left = calLeftMargin + (index % weekText.length ) * cellWidth;
			float top = yearHeight + weekHeight + (index / weekText.length ) * cellHeight;
			float right = left + cellWidth;
			float bottom = top + cellHeight;
			canvas.drawRect(left,top,right,bottom,Paint);//����ı���
			if(dateDrinkTimes > 1)
			{
				LogUtil.d("huang", "dateDrinkTimes>1 left="+left+" top="+top);
				Paint drinkTimePaint = new Paint();
				float drinkTimesTextSize = cellHeight * 1 / 3;
				drinkTimePaint.setColor(Color.WHITE);
				drinkTimePaint.setTextSize(drinkTimesTextSize);
				String dateDrinkTimeStr = "��"+dateDrinkTimes;
				canvas.drawText(dateDrinkTimeStr, 
						right - drinkTimePaint.measureText(dateDrinkTimeStr) - 5
						, top + drinkTimesTextSize , drinkTimePaint);
			}
	}

	

	private void calculateDate(int[] date)//��2015��10��11��Ϊ��
	{
		calendar.setTime(currentDate);
		calendar.set(Calendar.DAY_OF_MONTH, 1);// �õ�2015��10��1��
		int dayInWeek = calendar.get(Calendar.DAY_OF_WEEK);// ����1���ǵڼ���
		int monthStart = dayInWeek;
		if (monthStart == 1)
		{
			monthStart = 8;
		}
		monthStart -= 2; // ����Ϊ��ͷ-1��������һΪ��ͷ-2 ��Ҫ��Ҫ��Ӧ��������-1
		currentStartIndex = monthStart;
		date[monthStart] = 1;
		// last month
		if (monthStart > 0)
		{
			calendar.set(Calendar.DAY_OF_MONTH, 0);// �ϸ���9�����һ��
			int dayInmonth = calendar.get(Calendar.DAY_OF_MONTH);
			for (int i = monthStart - 1; i >= 0; i--)
			{
				date[i] = dayInmonth;
				dayInmonth--;
			}
			calendar.set(Calendar.DAY_OF_MONTH, date[0]);// �õ�9��27��
		}
		firstShowDate = calendar.getTime();// ����set��õ���һ����ʾ��ʱ�� 9��27
		// this month
		calendar.setTime(currentDate);// ��now
		calendar.add(Calendar.MONTH, 1);// ��calendar�ƺ�һ����
		calendar.set(Calendar.DAY_OF_MONTH, 0);// �ƺ�һ���µĵ�һ�� ��ǰһ�� Ҳ��������µ����һ��
												// 10��31��
		int monthDay = calendar.get(Calendar.DAY_OF_MONTH);// 31��
		for (int i = 1; i < monthDay; i++)// �� monthStart����¿�ʼ��ֵ
		{
			date[monthStart + i] = i + 1;
		}
		currentEndIndex = monthStart + monthDay;// �ϸ��µ������ һ���ж��ٸ����� 4+31
		// next month
		for (int i = monthStart + monthDay; i < 42; i++)
		{
			date[i] = i - (monthStart + monthDay) + 1;
		}
		if (currentEndIndex < 42)
		{
			// ��ʾ����һ�µ�
			calendar.add(Calendar.DAY_OF_MONTH, 1);
		}
		calendar.set(Calendar.DAY_OF_MONTH, date[41]);// �������õ����һ��
		lastShowDate = calendar.getTime();
		
	}

	private void drawCellText(Canvas canvas, int index, String text,int dateTextColor)
	{
		int x = getXByIndex(index);
		int y = getYByIndex(index);
	
		Paint datePaint = new Paint();
		datePaint.setColor(dateTextColor);
		datePaint.setAntiAlias(true);
		float dateTextSize = cellHeight * 0.5f;
		datePaint.setTextSize(dateTextSize);
		datePaint.setTypeface(Typeface.DEFAULT_BOLD);
		
		float dateX = calLeftMargin + cellWidth * x +(cellWidth - datePaint.measureText(text))/2f;
		float dateY = yearHeight + weekHeight + y * cellHeight + cellHeight * 3 / 4f;
		canvas.drawText(text, dateX, dateY, datePaint);
	}

	/**
	 * @param index ��ǰ������date[]�����е��±�
	 * @return ��ǰ������������ĵڼ���
	 */
	private int getXByIndex(int index)
	{
		return  index % weekText.length ;
	}

	/**
	 * @param index ��ǰ������date[]�����е��±�
	 * @return ��ǰ������������ĵڼ���
	 */
	private int getYByIndex(int index)
	{
		return  index / weekText.length ;
	}

	/**
	 * ���ڻ���
	 */
	private Paint setWeekPaint()
	{
		Paint weekPaint = new Paint();
		weekPaint.setColor(dateTextColor);
		weekPaint.setAntiAlias(true);
		float weekTextSize = weekHeight * 0.4f;
		weekPaint.setTextSize(weekTextSize);
		//weekPaint.setTypeface(Typeface.DEFAULT_BOLD);
		return weekPaint;
	}
	
	/**
	 * �������廭��
	 */
	private Paint setYearPaint()
	{
		Paint yearPaint = new Paint();
		yearPaint.setColor(dateTextColor);
		yearPaint.setAntiAlias(true);
		float weekTextSize = weekHeight * 0.5f;
		yearPaint.setTextSize(weekTextSize);
		yearPaint.setTypeface(Typeface.DEFAULT_BOLD);
		return yearPaint;
	}

	/**
	 * �߿򻭱�
	 */
	private Paint setBorderPaint()
	{
		Paint borderPaint = new Paint();
		borderPaint.setColor(borderColor);
		borderPaint.setAntiAlias(true);
		borderPaint.setStyle(Paint.Style.STROKE);
		float borderWidth = (float) (0.5 * density);
		borderWidth = borderWidth < 1 ? 1 : borderWidth;
		borderPaint.setStrokeWidth(borderWidth);
		return borderPaint;
	}

	/**
	 * �߿�·��
	 */
	private Path drawBorderPath()
	{
		Path borderPath = new Path();
		float beginX = calLeftMargin;
		float beginY = yearHeight + weekHeight;
		float drawLength = calWidth - 2 * calLeftMargin;
		float drawHeight = 6 * cellHeight;
		borderPath.moveTo(beginX,calTopMargin);
		borderPath.rLineTo(drawLength,0 );//���������
		borderPath.moveTo(beginX, yearHeight + weekHeight + (weekText.length )  * cellHeight );//���������
		borderPath.rLineTo(drawLength, 0);
		
		for(int i = 0 ;i <= 31/7 + 2 ; i++)
		{
			borderPath.moveTo(beginX, beginY + i * cellHeight);//��ÿһ�еĺ���
			borderPath.rLineTo(drawLength, 0);
		}
		for(int i = 0 ;i < weekText.length ; i++)
		{//������
			borderPath.moveTo(beginX + i * cellWidth,beginY );
			borderPath.rLineTo(0,drawHeight);
		}
		borderPath.moveTo(calWidth - calLeftMargin, beginY );//�����ұߵ�����
		borderPath.rLineTo(0,drawHeight);
		return borderPath;
	}

	/**
	 * �õ���ǰ����
	 * @return eg:2015 �� 10 ��
	 */
	public  String getYearAndMonth(Date date)
	{
		calendar.setTime(date);
		return calendar.get(Calendar.YEAR) + " �� " +  ((calendar.get(Calendar.MONTH) + 1)+" ��" );
		
	}
	
	/**
	 * �õ��ϸ��µ� ��ݺ��·�
	 * @return eg:2015��9��
	 */
	public String getLastMonth()
	{
		calendar.setTime(currentDate);
		calendar.add(Calendar.MONTH, -1);
		currentDate = calendar.getTime();
		invalidate();//ˢ�½���һ��
		return getYearAndMonth(currentDate);
	}
	
	/**
	 * �õ��¸��µ� ��ݺ��·�
	 * @return eg:2015��11��
	 */
	public String getNextMonth()
	{
		calendar.setTime(currentDate);
		calendar.add(Calendar.MONTH, 1);
		currentDate = calendar.getTime();
		invalidate();//ˢ�½���һ��
		return getYearAndMonth(currentDate);
	}
	
	public void makeBlackToday()
	{
		todayBgColor = todayDrinkedBgColor;
		invalidate();
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		
		// TODO Auto-generated method stub
		return false;
	}

}
