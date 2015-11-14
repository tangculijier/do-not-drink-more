/**  
 * text������
 * @author lizheHuang 
 * @Date   time :2015��11��9��  ����3:39:23
 * @version 1.0
 */ 

package com.huang.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.TypefaceSpan;
import android.widget.TextView;

public class MyTextUtil
{

	
	/**
	 * ��һ��textview��������ָ���
	 * @param tv ���ؼ�textview
	 * @param number Ҫ����������
	 * @param color	��������ɫ
	 * @return SpannableString
	 */
	public static SpannableString highLightNumber(TextView tv,String number,int color)
	{
		String text = tv.getText().toString().replaceAll("(\\d+)", number);
		Pattern pattern = Pattern.compile("(\\d+)");
		Matcher matcher = pattern.matcher(text);
		int start = 0;
		int end = 0;
		while (matcher.find())
		{
			start = matcher.start();
			end = matcher.end();
		}
		
		SpannableString spanText = new SpannableString(text);
		spanText.setSpan(new ForegroundColorSpan(color), start, end,
				Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		spanText.setSpan(new RelativeSizeSpan(3.0f), start,end,
				Spannable.SPAN_INCLUSIVE_EXCLUSIVE);//��Դ�С���ı����壩
		spanText.setSpan(new StyleSpan(Typeface.NORMAL), start, end,
				Spannable.SPAN_INCLUSIVE_EXCLUSIVE);//����
		spanText.setSpan(new RelativeSizeSpan(0.8f), end + 1,end + 2,
				Spannable.SPAN_INCLUSIVE_EXCLUSIVE);//��Դ�С���ı����壩
		return spanText;
		
	}
	
}
