package com.huang.nodrinkmore;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.huang.Activity.ActionBarBaseActivity;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar.OnSeekBarChangeListener;

/**  
 * �걨activity  ͨ���걨���Խ����±�����-MonthReportActivity
 * @author lizheHuang 
 * @Date   time :2016��03��08��  ����20:35:21
 * @version 1.0
 */ 
public class YearReportActivity extends ActionBarBaseActivity implements 
OnChartValueSelectedListener
{

    private HorizontalBarChart yearChart;
    private Typeface tf;
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_year_report);
		yearChart = (HorizontalBarChart) findViewById(R.id.year_report_chart);
		yearChart.setOnChartValueSelectedListener(this);
        tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");


	}
	@Override
	public void onValueSelected(Entry e, int dataSetIndex, Highlight h)
	{
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onNothingSelected()
	{
		// TODO Auto-generated method stub
		
	}

}
