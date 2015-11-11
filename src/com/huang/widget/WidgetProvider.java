package com.huang.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.huang.Activity.MainActivity;
import com.huang.nodrinkmore.R;
import com.huang.service.WidgetService;
import com.huang.util.DatabaseHelper;
import com.huang.util.LogUtil;

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
		LogUtil.d("huang", "WidgetProvider onDeleted");
		super.onDeleted(context, appWidgetIds);
	}
	
	
	/**
	 * ��һ��widget��ӵ���Ļ��ִ��
	 */
	@Override
	public void onEnabled(Context context)
	{
		LogUtil.d("huang", "WidgetProvider onEnabled");
		super.onEnabled(context);
		context.startService(new Intent(context,WidgetService.class));
	}
	
	
	/**
	 * ���һ��widget����Ļ�Ƴ�ʱִ��
	 */
	@Override
	public void onDisabled(Context context)
	{
		LogUtil.d("huang", "WidgetProvider onDisabled");
		context.stopService(new Intent(context,WidgetService.class));
		super.onDisabled(context);
	}

	
	/**
	 * �����ĸ���������������
	 */
	@Override
	public void onReceive(Context context, Intent intent)
	{
		LogUtil.d("huang", "WidgetProvider onReceive");
		super.onReceive(context, intent);
		
		DatabaseHelper databaseHelper = new DatabaseHelper(context);
		String[] keepDaysInfo = databaseHelper.getKeepTime();
		String widgetShowStr = "�ѱ���\n"+keepDaysInfo[0]+"��";
		LogUtil.d("huang", "onReceive updateViews"+widgetShowStr);
		RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget);
		rv.setTextViewText(R.id.keeptime_widget, widgetShowStr);
		AppWidgetManager manager = AppWidgetManager.getInstance(context);
		ComponentName cn = new ComponentName(context, WidgetProvider.class);
		manager.updateAppWidget(cn, rv);//����widget.onUpdate()
	}
	
	/**
	 * ˢ�µ�widgetʱ��ִ�� 
	 * �ڽ����ϰ��¼� ����󴥷�app������
	 */
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds)
	{
		final int N = appWidgetIds.length;
		LogUtil.d("huang", "WidgetProvider onUpdate N="+N);
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		for (int i = 0; i < N; i++) 
		{
			int appWidgetId = appWidgetIds[i];
			Intent intent = new Intent(context, MainActivity.class);
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,intent, 0);
			RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.widget);
			views.setOnClickPendingIntent(R.id.widget_layout, pendingIntent);
			appWidgetManager.updateAppWidget(appWidgetId, views);
		}
	}
	
}
