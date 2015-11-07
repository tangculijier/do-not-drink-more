package com.huang.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.huang.nodrinkmore.R;
import com.huang.service.WidgetService;
import com.huang.util.DatabaseHelper;

/**  
 *  
 * @author lizheHuang 
 * @Date 2015��11��07��   ����16:36:17
 * �����ݷ�ʽ��Provider
 */ 
public class WidgetProvider extends AppWidgetProvider
{

	/**
	 * widget���Ƴ�ʱִ��
	 */
	@Override
	public void onDeleted(Context context, int[] appWidgetIds)
	{
		Log.d("huang", "WidgetProvider onDeleted");
		super.onDeleted(context, appWidgetIds);
	}
	
	
	/**
	 * ��һ��widget��ӵ���Ļ��ִ��
	 */
	@Override
	public void onEnabled(Context context)
	{
		Log.d("huang", "WidgetProvider onEnabled");
		super.onEnabled(context);
		context.startService(new Intent(context,WidgetService.class));
	}
	
	
	/**
	 * ���һ��widget����Ļ�Ƴ�ʱִ��
	 */
	@Override
	public void onDisabled(Context context)
	{
		Log.d("huang", "WidgetProvider onDisabled");
		context.stopService(new Intent(context,WidgetService.class));
		super.onDisabled(context);
	}

	
	/**
	 * �����ĸ���������������
	 */
	@Override
	public void onReceive(Context context, Intent intent)
	{
		Log.d("huang", "WidgetProvider onReceive");
		super.onReceive(context, intent);
		
		DatabaseHelper databaseHelper = new DatabaseHelper(context);
		String[] keepDaysInfo = databaseHelper.getKeepTime();
		String widgetShowStr = "�ѱ���\n"+keepDaysInfo[0]+"��";
		Log.d("huang", "onReceive updateViews"+widgetShowStr);
		RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget);
		rv.setTextViewText(R.id.keeptime_widget, widgetShowStr);
		AppWidgetManager manager = AppWidgetManager.getInstance(context);
		ComponentName cn = new ComponentName(context, WidgetProvider.class);
		manager.updateAppWidget(cn, rv);//����widget.onUpdate()
	}
	
	/**
	 * ˢ�µ�widgetʱ��ִ�� remove��Appwidget
	 */
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds)
	{
		Log.d("huang", "WidgetProvider onUpdate");
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
	
}
