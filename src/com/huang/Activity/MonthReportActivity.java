package com.huang.Activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.huang.nodrinkmore.R;
import com.huang.util.DatabaseHelper;
import com.huang.util.DateUtil;
import com.huang.util.LogUtil;
import com.huang.util.MyTextUtil;

public class MonthReportActivity extends Activity implements
		OnChartValueSelectedListener
{
	
	DatabaseHelper databaseHelper;
	
	
	Calendar calendar ;
	
	Date currentTime ;
	/**
	 * ʱ������
	 */
	private TextView dateDurationTextView;  
	private String dateDuration;

	private TextView noDrinkDaysTextView;  
	/**
	 * �Ծ�����(û�к����ϵ�����)
	 */
	private int noDrinkDays = 0;

	private TextView monthSumOfDrinkTimesTextView; // ����������
	/**
	 * ����������
	 */
	private int monthSumOfDrinkTimes = 0;

	private TextView longestKeepingDayOfMonthTextView; // ����ּ�¼
	/**
	 * ����ּ�¼(��)
	 */
	private int longestKeepingDayOfMonth = 0;

	/**
	 * �ײ���С��ʾ
	 */
	private TextView tipsTextView;
	
	private int partTimeOfDrinktimesOfMonth[] ;

	private PieChart mChart;// ������ʱ��ֲ�ͼ��
	private Typeface tf;// ����

	protected String[] sectionString = new String[] { "����", "����", "����" };
	protected int[] sectionColor;

	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// to title
		setContentView(R.layout.month_report);
		findById();
		initData();

	}

	public void findById()
	{
		sectionColor= new int[] { 
				getResources().getColor(R.color.green_light_more)
				, getResources().getColor(R.color.blue)
				, getResources().getColor(R.color.grey)};
		dateDurationTextView = (TextView) findViewById(R.id.dateDuration);
		noDrinkDaysTextView = (TextView) findViewById(R.id.noDrinkDays);
		longestKeepingDayOfMonthTextView = (TextView) findViewById(R.id.longestKeepingDayOfMonth);
		tipsTextView = (TextView)findViewById(R.id.tips);
		mChart = (PieChart) findViewById(R.id.pie_chart);
	}
	
	public void initData()
	{
		
		calendar = Calendar.getInstance();
		currentTime = calendar.getTime();
		databaseHelper = new DatabaseHelper(this);
		
		dateDuration = DateUtil.getDateDuration(currentTime);
		noDrinkDays = databaseHelper.getNoDrinkDaysNumber(currentTime);
		monthSumOfDrinkTimes = databaseHelper.getMonthSumOfDrinkTimes(currentTime);
		longestKeepingDayOfMonth = databaseHelper.getLongestKeepingDayOfMonth(currentTime);
		partTimeOfDrinktimesOfMonth = databaseHelper.getTimeSectionOfDrinktimesOfMonth(currentTime);

		
		String tempText = dateDurationTextView.getText().toString().replace("%s", dateDuration);
		dateDurationTextView.setText(tempText);

		//tempText = noDrinkDaysTextView.getText().toString().replace("%s", noDrinkDays + "");
		noDrinkDaysTextView.setText(MyTextUtil.getSuperscriptSpan(noDrinkDaysTextView.getText().toString(),noDrinkDays+"",getResources().getColor(R.color.green_light_more)));

		tempText = longestKeepingDayOfMonthTextView.getText().toString().replace("%s", longestKeepingDayOfMonth + "");
		longestKeepingDayOfMonthTextView.setText(MyTextUtil.getSuperscriptSpan(longestKeepingDayOfMonthTextView.getText().toString(),longestKeepingDayOfMonth+"",getResources().getColor(R.color.green_light_more)));

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
		mChart.setCenterText(generateCenterSpannableText());
		mChart.setCenterTextSize(12f);
		mChart.setRotationAngle(270);// �����м俪ʼ����
		mChart.setRotationEnabled(true);// �����ֶ���ת
		mChart.setOnChartValueSelectedListener(this);
		mChart.setUsePercentValues(true);// �Ƿ���ٷֺ�
		mChart.animateX(1400, Easing.EasingOption.EaseInOutQuad);
		
		initChartData();
	}

	private SpannableString generateCenterSpannableText()
	{

		String text = "������ʱ��ֲ�\n�������� " + monthSumOfDrinkTimes + "ƿ";
		int x = 7;
		int y = 7;
		SpannableString spannableString = new SpannableString(text);
		spannableString.setSpan(new RelativeSizeSpan(1.3f), 0, x, 0);
		spannableString.setSpan(new StyleSpan(Typeface.NORMAL), x,spannableString.length() - y, 0);
		spannableString.setSpan(new ForegroundColorSpan(Color.GRAY), x,spannableString.length() - y, 0);
		spannableString.setSpan(new RelativeSizeSpan(.8f), x,spannableString.length() - y, 0);
		spannableString.setSpan(new StyleSpan(Typeface.ITALIC),spannableString.length() - x, spannableString.length(), 0);
		spannableString.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()),
				spannableString.length() - x, spannableString.length(), 0);
		return spannableString;
	}
	
	
	private void initChartData()
	{

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
		//�������
		for(int i = 0 , j = 0;i < partTimeOfDrinktimesOfMonth.length ; i++)
		{
			if(partTimeOfDrinktimesOfMonth[i] != 0 )//˵����ʱ����м�¼
			{
				if(partTimeOfDrinktimesOfMonth[i] > maxDrinkTimes)
				{
					maxDrinkTimes = partTimeOfDrinktimesOfMonth[i] ;
					maxIndex = i;
				}
				yVals.add(new Entry(partTimeOfDrinktimesOfMonth[i], j++));
				yValStringArray.add(sectionString[i]+" "+partTimeOfDrinktimesOfMonth[i] + "ƿ");//ƴ�ӳ� ����:5 ƿ
				legendStringArray.add(sectionString[i]);
				colors.add(sectionColor[i]);
			}
		
		}
		makeTips(maxIndex);//give the suggestion of drink

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
		legend.setXEntrySpace(7f);
		legend.setYEntrySpace(0f);
		legend.setTextSize(12f);
		legend.setYOffset(5f);
		legend.setCustom(colors, legendStringArray);//�Զ���ĵײ�����˵��
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
			default:break;
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
