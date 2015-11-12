/**  
 * text������
 * @author lizheHuang 
 * @Date   time :2015��11��9��  ����3:39:23
 * @version 1.0
 */ 

package com.huang.util;

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

public class MyTextUtil
{

	public static SpannableString getSuperscriptSpan(String text,String highLightStr,int color)
	{
		text = text.replace("%s", highLightStr);//���滻%s
		int start = text.indexOf(highLightStr);
		int end = start + String.valueOf(highLightStr).length();
		
		SpannableString spanText = new SpannableString(text);
		spanText.setSpan(new ForegroundColorSpan(color), start, end,
				Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		spanText.setSpan(new RelativeSizeSpan(3.0f), start,end,
				Spannable.SPAN_INCLUSIVE_EXCLUSIVE);//��Դ�С���ı����壩
		spanText.setSpan(new StyleSpan(Typeface.NORMAL), start, end,
				Spannable.SPAN_INCLUSIVE_EXCLUSIVE);//����
		
//		//highLightStr�ĺ����һ������Ϊ�ϱ�
//		spanText.setSpan(new SuperscriptSpan(), end + 1, end + 2, 
//				Spannable.SPAN_INCLUSIVE_EXCLUSIVE);//�ϱ�
		
		spanText.setSpan(new RelativeSizeSpan(0.8f), end + 1,end + 2,
				Spannable.SPAN_INCLUSIVE_EXCLUSIVE);//��Դ�С���ı����壩
		return spanText;
		
	}
	
}
