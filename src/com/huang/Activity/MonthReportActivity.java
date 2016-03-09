package com.huang.Activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.widgets.Dialog;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.huang.model.ReportOfMonth;
import com.huang.nodrinkmore.R;
import com.huang.util.AppConst;
import com.huang.util.DatabaseHelper;
import com.huang.util.DateUtil;
import com.huang.util.LogUtil;
import com.huang.util.MyTextUtil;
import com.huang.views.MyDatePicker;

public class MonthReportActivity extends Activity implements
		OnChartValueSelectedListener
{
	
	DatabaseHelper databaseHelper;
	
	Calendar calendar ;
	
	Date currentTime ;
	/**
	 * �����·�-title
	 */
	private String currentMonth;
	/**
	 * �����·�-title textview
	 */
	private TextView currentMonthTextView;  
	/**
	 * �����·�-title��������ɫ
	 */
	private int currentMonthColor;
	/**
	 * �Ծ�����(û�к����ϵ�����)
	 */
	private int noDrinkDays = 0;
	/**
	 * ��ʾ�Ծ��������������ϵ�������textview
	 */
	private TextView noDrinkDaysTextView;  
	/**
	 * ����ּ�¼(��)
	 */
	private int longestKeepingDayOfMonth = 0;
	/**
	 * ����ּ�¼��textiview
	 */
	private TextView longestKeepingDayOfMonthTextView; // ����ּ�¼


	/**
	 * ����������
	 */
	private int monthSumOfDrinkTimes = 0;
	
	/**
	 * ���ص�cup Ĭ��gone
	 * һ�������û���κμ�¼����ʾcup
	 */
	ImageView cup;
	/**
	 * �ײ���С��ʾ
	 */
	private TextView tipsTextView;
	
	/**
	 *  ������ʱ������ֱ�������ϴ�����������������ϴ���
	 */
	private int partTimeOfDrinktimesOfMonth[] ;

	/**
	 * ������ʱ��ֲ�ͼ��
	 */
	private PieChart mChart;
	private Typeface tf;

	protected String[] sectionString = new String[] { "����", "����", "����" };
	/**
	 * �ֲ�ͼ����ɫ
	 */
	protected int[] sectionColor;
	
	/**
	 * mDatePicker��ʼ��ݺ��·�
	 */
	int curyear = -1;
	int curmonth = -1;
	
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// to title
		setContentView(R.layout.month_report);
		Intent intent = getIntent();
		String currentTimeStr  = intent.getStringExtra(AppConst.INTENT_EXTRA_TIME);
		if(!TextUtils.isEmpty(currentTimeStr))
		{
			LogUtil.d("huang", "intnt="+currentTimeStr);
			currentTime = DateUtil.StringToDate(currentTimeStr);
		}
		ifFirstAnalysis();
		findById();
		initData();

	}

	public void ifFirstAnalysis()
	{
		SharedPreferences setting = getSharedPreferences(AppConst.SHARE_PS_Name, MODE_PRIVATE);
		boolean isFirstAnalysis= setting.getBoolean(AppConst.IS_FIRST_ANANLYSIS, true);
		if(isFirstAnalysis == true)
		{
			
			final Dialog dialog = new Dialog(MonthReportActivity.this,
					getResources().getString(R.string.tip),
					getResources().getString(R.string.report_dialog_text));
			dialog.show();
			ButtonFlat acceptButton = dialog.getButtonAccept();
			acceptButton.setText("��֪����");
			setting.edit().putBoolean(AppConst.IS_FIRST_ANANLYSIS, false).commit();
			
		}
	}

	public void findById()
	{
		sectionColor= new int[] { 
				getResources().getColor(R.color.green_light_more)
				, getResources().getColor(R.color.blue)
				, getResources().getColor(R.color.grey)};
		currentMonthTextView = (TextView) findViewById(R.id.currentMonth);
		noDrinkDaysTextView = (TextView) findViewById(R.id.noDrinkDays);
		longestKeepingDayOfMonthTextView = (TextView) findViewById(R.id.longestKeepingDayOfMonth);
		cup = (ImageView)findViewById(R.id.cup);
		tipsTextView = (TextView)findViewById(R.id.tips);
		mChart = (PieChart) findViewById(R.id.pie_chart);
		currentMonthTextView.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				Calendar cal = Calendar.getInstance();
				MyDatePicker mDatePicker = new MyDatePicker(MonthReportActivity.this,Datelistener, Calendar.getInstance());
				mDatePicker.setIcon(R.drawable.datepicker_icon);
				mDatePicker.setTitle(R.string.report_datepicker_dialog_title);
				if(curyear != -1 && curmonth != -1)//��������ǳ�ʼֵ�Ļ���mDatePicker��ʼʱ����Ϊ��ǰ��ѡ
				{
					mDatePicker.updateDate(curyear, curmonth, 1);
				}
		
				mDatePicker.show();
			}
		});
	}
	private DatePickerDialog.OnDateSetListener Datelistener=new DatePickerDialog.OnDateSetListener()
    {
        @Override
        public void onDateSet(DatePicker view, int myyear, int monthOfYear,int dayOfMonth) {
        	
        	//�õ���ѡ�·ݵ����һ��
        	monthOfYear += 1 ;
        	String monthOfYearStr = monthOfYear < 10 ? ( "0" + monthOfYear ): monthOfYear + "";
            String pickDateStr = myyear + "-" + monthOfYearStr + "-" + "01";
            Date pickDate = DateUtil.getLastDateInMonth(DateUtil.StringToDate(pickDateStr)); 
            LogUtil.d("huang", "pickDate="+pickDate);
            final ReportOfMonth report = databaseHelper.getAnalysis(DateUtil.DateToStringNoHour(pickDate));
            if(!TextUtils.isEmpty(report.getDate()))
            {
            	//ˢ������
            	currentMonthTextView.setText(myyear + "/" + monthOfYearStr);
            	

				longestKeepingDayOfMonthTextView.setText(MyTextUtil.highLightNumber(longestKeepingDayOfMonthTextView, 
						report.getLongestKeepDays()+"", currentMonthColor));

				noDrinkDaysTextView.setText(MyTextUtil.highLightNumber(noDrinkDaysTextView, 
						report.getNoDrinkDays()+"", currentMonthColor));

            	int[] sectionDate = new int[]{report.getMorningtimes(),
            			report.getAfternoontimes(),report.getEveningtimes()}; 
    			initChartData(sectionDate);
    			curyear = myyear;
    			curmonth = monthOfYear - 1;
    		
            }
            else if(DateUtil.isSameMonth(currentTime, pickDate) == true)//�������ѡ��ص�ǰ�·�
            {
            	curyear = myyear;
    			curmonth = monthOfYear - 1;
            	initData();
            }
            else
            {
            	Toast.makeText(MonthReportActivity.this, "��ѡ�·�û�м�¼", Toast.LENGTH_LONG).show();

            }
            
        }
    };
	public void initData()
	{
		calendar = Calendar.getInstance();
		if(currentTime == null)
		{
			
			currentTime = calendar.getTime();
			LogUtil.d("huang", "currentTime == null:"+currentTime);

		}
		else//��Ϊnull˵���Ǵ�����activity��ת����
		{
			calendar.setTime(currentTime);
			LogUtil.d("huang", "currentTime != null:"+currentTime);

		}

		
		databaseHelper = DatabaseHelper.getInstance(this);
		
		noDrinkDays = databaseHelper.getNoDrinkDaysNumber(currentTime);
		longestKeepingDayOfMonth = databaseHelper.getLongestKeepingDayOfMonth(currentTime);
		partTimeOfDrinktimesOfMonth = databaseHelper.getTimeSectionOfDrinktimesOfMonth(currentTime);

		int month = calendar.get(Calendar.MONTH) + 1;
		String monthStr = month < 10 ? ( "0" + month ): month + "";
		tf = Typeface.createFromAsset(getAssets(), "AgencyFB.ttf");
		currentMonthTextView.setText(calendar.get(Calendar.YEAR)+"/"+monthStr);
		currentMonthTextView.setTypeface(tf,Typeface.BOLD);

		currentMonthColor = getResources().getColor(R.color.green_dark);
		noDrinkDaysTextView.setText(MyTextUtil.highLightNumber(noDrinkDaysTextView, noDrinkDays+"", currentMonthColor));
		longestKeepingDayOfMonthTextView.setText(MyTextUtil.highLightNumber(longestKeepingDayOfMonthTextView, longestKeepingDayOfMonth+"", currentMonthColor));

		initChart();



	
	}

	private void initChart()
	{
		
		mChart.setDescription("");// ���ñ������� �����½�
		mChart.setExtraOffsets(5, 10, 5, 5);// ����ͼ���⣬��������ʾ��ƫ����

		mChart.setDragDecelerationFrictionCoef(0.95f);// ��ק����ʱ���ٶȿ�����[0,1) 0��������ֹͣ
		tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
		mChart.setCenterTextTypeface(Typeface.createFromAsset(getAssets(),"OpenSans-Light.ttf"));
		mChart.setDrawHoleEnabled(true);// �Ƿ����м��Ϳյ�Բ
		mChart.setCenterTextSize(12f);
		mChart.setRotationAngle(270);// �����м俪ʼ����
		mChart.setRotationEnabled(true);// �����ֶ���ת
		mChart.setOnChartValueSelectedListener(this);
		mChart.setUsePercentValues(true);// �Ƿ���ٷֺ�
		mChart.animateX(1400, Easing.EasingOption.EaseInOutQuad);
		
		initChartData(partTimeOfDrinktimesOfMonth);
	}

	private SpannableString generateCenterSpannableText(int sum)
	{
		String title = "������ʱ��ֲ�";
		String subTitle = "\n�������� " ;
		String numberStr =  sum + "ƿ";
		int titleLength = title.length();
		int subTitleLength = subTitle.length();
		int numberStrLength = numberStr.length();
		
		SpannableString spannableString = new SpannableString(title + subTitle + numberStr);
		spannableString.setSpan(new RelativeSizeSpan(1.3f), 0, titleLength, 0);
		
		spannableString.setSpan(new StyleSpan(Typeface.NORMAL), titleLength,titleLength + subTitleLength, 0);
		spannableString.setSpan(new ForegroundColorSpan(Color.GRAY), titleLength,titleLength + subTitleLength, 0);
		spannableString.setSpan(new RelativeSizeSpan(1.1f), titleLength,titleLength + subTitleLength, 0);
		
		spannableString.setSpan(new StyleSpan(Typeface.ITALIC),spannableString.length() - numberStrLength, spannableString.length(), 0);
		spannableString.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()),
				spannableString.length() - numberStrLength, spannableString.length(), 0);
		return spannableString;
	}
	
	
	private void initChartData(int[] sectionData)
	{

		boolean isAllZero = true;
		//�����ڱ�ͼ�ϵ������б�
		ArrayList<String> yValStringArray = new ArrayList<String>();
		//�ײ�˵���б�
		ArrayList<String> legendStringArray = new ArrayList<String>();
		//���ݱ�ͼ
		ArrayList<Entry> yVals = new ArrayList<Entry>();
		//������ɫ
		ArrayList<Integer> colors = new ArrayList<Integer>();
		
		int maxDrinkTimes = 0;
		int maxIndex = -1;
		int sum = 0;
		//�������
		for(int i = 0 , j = 0;i < sectionData.length ; i++)
		{
			if(sectionData[i] != 0 )//˵����ʱ����м�¼
			{
				sum += sectionData[i];
				if(sectionData[i] > maxDrinkTimes)
				{
					maxDrinkTimes = sectionData[i] ;
					maxIndex = i;
			
				}
				yVals.add(new Entry(sectionData[i], j++));
				yValStringArray.add(sectionString[i]+" "+sectionData[i] + "ƿ");//ƴ�ӳ� ����:5 ƿ
				legendStringArray.add(sectionString[i]);
				colors.add(sectionColor[i]);
				isAllZero = false;
			}
		
		}
		mChart.setCenterText(generateCenterSpannableText(sum));
		makeTips(maxIndex);//give the suggestion of drink
		if(isAllZero == false)
		{
			

			PieDataSet dataSet = new PieDataSet(yVals, "");
			dataSet.setSliceSpace(5f);// ����ÿ����Ƭ֮��ļ��
			dataSet.setSelectionShift(12f);// �����Ŵ���
			// add a lot of colors

			dataSet.setColors(colors);
			
			PieData data = new PieData(yValStringArray, dataSet);
			data.setValueFormatter(new PercentFormatter());
			data.setValueTextSize(12f);
			data.setValueTextColor(Color.BLACK);
			data.setValueTypeface(tf);

			mChart.setData(data);

			// undo all highlights
			mChart.highlightValues(null);
			
			
			// ���˵��
			Legend legend = mChart.getLegend();
			legend.setPosition(LegendPosition.BELOW_CHART_CENTER);
			/*
			 * 	legend.setCustom(colors, legendStringArray);//�Զ���ĵײ�����˵�� 
			 *	С��4����bug :java.lang.ArrayIndexOutOfBoundsException: length=1; index=1
			 *	��ʱ��֪��ԭ��
			 */
			legend.setXEntrySpace(7f);
			legend.setYEntrySpace(0f);
			legend.setTextSize(12f);
			legend.setYOffset(5f);

			
			mChart.invalidate();
			showMyChart();
		
		}
		else
		{
			hideMyChart();
		}
		
	}

	
	
	private void showMyChart()
	{
		if(mChart.getVisibility() == View.GONE)
		{
			cup.setVisibility(View.GONE);
			mChart.setVisibility(View.VISIBLE);
			
		}
	}
	private void hideMyChart()
	{
		if(mChart.getVisibility() == View.VISIBLE)
		{
			cup.setVisibility(View.VISIBLE);
			mChart.setVisibility(View.GONE);
		
		}
	
	}
	private void makeTips(int maxIndex)//�����ĸ�ʱ�κȵ�������������ʾ
	{
		String tips = "";
		switch (maxIndex)
		{
			case -1://û�кȵļ�¼
					tips +="û�к����ϵļ�¼���ٽ�������";
					break;
			case 0://���Ϻȵ����
					tips +="���Ϻ�ˮ�Ļ�һ�����������";
					break;
			case 1://����ȵ����
					tips +="����������ˮ��";
					break;
			case 2://���Ϻȵ����
					tips +="���Ϻ�̫�����ϲ�����˯�ߡ�";
					break;
			default:
					break;
		}
		tipsTextView.setText(tips);
		
	}

	//ѡ�б�ͼ�ļ����¼�
	@Override
	public void onValueSelected(Entry e, int dataSetIndex, Highlight h)
	{

		if (e == null)
			return;
		LogUtil.i("VAL SELECTED",
				"Value: " + e.getVal() + ", xIndex: " + e.getXIndex()
						+ ", DataSet index: " + dataSetIndex);
	}

	@Override
	public void onNothingSelected()
	{
		LogUtil.i("PieChart", "nothing selected");
	}

}
