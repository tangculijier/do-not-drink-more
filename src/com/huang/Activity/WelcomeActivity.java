package com.huang.Activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.huang.nodrinkmore.R;
import com.huang.util.AnimationUtil;
import com.huang.util.AppConst;
import com.huang.util.DatabaseHelper;
import com.huang.util.DateUtil;
import com.huang.util.LogUtil;
import com.huang.views.CircleIndicator.CircleIndicator;
import com.nineoldandroids.animation.ObjectAnimator;

/**
 * ��ӭ�������� ������2��viewpagerҳ�� ������һЩ����
 * @author lizheHuang
 * @Date   time :2015��11��
 * @version 1.0
 */
public class WelcomeActivity extends Activity 
{
	/**
	 * ��ʼgo��ť
	 */
	private Button beginButton;
	
	/**
	 * ����ֵ
	 */
	private int balance;
	
	private List<View> viewPagerList;
	
	private ViewPager viewPager;
	
	/**
	 * viewpager ������ʾ�л���Բ��
	 */
	private CircleIndicator circleIndicator;
	
	
	//viewpager2 images
	private ImageView man;
	private ImageView drink;
	private ImageView blance_example;
	
	/**
	 * ���ϵĶ���
	 */
	private Animation jumpAnimation; 
	private Animation dwonAnimation; 
	
	/**
	 * �����Ƿ�ִ�����
	 */
	private boolean isAnimationShowOver = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//to title
		setContentView(R.layout.activity_welcome);
		viewPager = (ViewPager)findViewById(R.id.viewPager);
		initViewPager();
		circleIndicator = (CircleIndicator)findViewById(R.id.circleIndicator);
		circleIndicator.bindViewPage(viewPager);
		init();
		
	}

	public void initViewPager()
	{
		viewPagerList = new ArrayList<View>();
		LayoutInflater inflater = getLayoutInflater().from(this);
		View viewPager1 = inflater.inflate(R.layout.viewpage_1, null);
		View viewPager2 = inflater.inflate(R.layout.viewpage_2, null);
		viewPagerList.add(viewPager1);
		viewPagerList.add(viewPager2);
		viewPager.setAdapter(new MyViewPagerAdapter());
	    beginButton = (Button)viewPager2.findViewById(R.id.begin);

	    man = (ImageView)viewPager2.findViewById(R.id.man);
	    drink = (ImageView)viewPager2.findViewById(R.id.drink);
	    blance_example = (ImageView)viewPager2.findViewById(R.id.blance_example2);
	    viewPager.setOnPageChangeListener(new OnPageChangeListener()
		{
			
			@Override
			public void onPageSelected(int position)
			{
				circleIndicator.trgger(position, 0);
				if(position == 1)
				{
					if(isAnimationShowOver == true)
					{
						isAnimationShowOver = false;
						man.clearAnimation();
						drink.clearAnimation();
						blance_example.clearAnimation();
					
						final float fromY = 0f;
						final float toY = -80f;
						final int duration = 1000;
						final int animationDelayTime = 1000;
						jumpAnimation = AnimationUtil.getJumpUpAnimation(fromY, toY, duration);
						dwonAnimation = AnimationUtil.getDownAnimation(toY, fromY, duration);
						
						int timing = 1;
						targetViewStartAnimation(man,jumpAnimation,animationDelayTime);
						targetViewStartAnimation(man,dwonAnimation,animationDelayTime + (timing * duration));
						targetViewStartAnimation(drink,jumpAnimation,animationDelayTime);
						targetViewStartAnimation(drink,dwonAnimation,animationDelayTime + (timing * duration));
						timing++;
						targetViewStartAnimation(blance_example,AnimationUtil.getSubBalanceAnimation(0f),animationDelayTime + (timing++ * duration));
						timing++;
						targetViewStartAnimation(beginButton,jumpAnimation,animationDelayTime + (timing++ * duration));
					
					}
				}
				else
				{
					//beginButton�ع�λ��
					ObjectAnimator buttonDownAnimaor = ObjectAnimator.ofFloat(beginButton, "translationY", 40f, 0f);
					buttonDownAnimaor.setDuration(0);
					buttonDownAnimaor.start();
				}
				
			}
			
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
			{
				circleIndicator.trgger(position, positionOffset);
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0)
			{
				// TODO Auto-generated method stub
				
			}
		});
	}

	/**
	 * 
	 * @param view target view
	 * @param animation which animation 
	 * @param delayTime after this time execute animation
	 */
	private void targetViewStartAnimation(final View view,final Animation animation ,int delayTime )
	{
		new Handler().postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				if(view.getId() != R.id.begin)
				{
					view.startAnimation(animation);
				}
				else
				{
					//�˴���ObjectAnimator ���ĵ�������λ
					ObjectAnimator buttonUpAnimaor = ObjectAnimator.ofFloat(beginButton, "translationY", 0f, -40f);
					buttonUpAnimaor.setDuration(2000);
					buttonUpAnimaor.start();
					isAnimationShowOver = true;//��������ִ�����
				}
				
			
			}
		},delayTime);
	}
	
	
	@Override
	protected void onResume()
	{
		init();
		super.onResume();
	}
	public void init()
	{
		final SharedPreferences  setting = getSharedPreferences(AppConst.SHARE_PS_Name, MODE_PRIVATE);
		boolean isFirst= setting.getBoolean(AppConst.IS_FIRST, true);

		//if first into app then make database
		if(isFirst == true)
		{
			beginButton.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					//�������ʼ�����ݿ�
					DatabaseHelper databaseHelper = DatabaseHelper.getInstance(WelcomeActivity.this);
					SQLiteDatabase db = databaseHelper.getWritableDatabase();
					
					setting.edit().putBoolean(AppConst.IS_FIRST, false).commit();
					
					java.util.Date utilDate = new java.util.Date();  
					java.sql.Date nowDay = new java.sql.Date(utilDate.getTime());  
					setting.edit().putString(AppConst.FIRST_DAY, nowDay.toString()).commit();//��һ��ʹ��Ӧ�õ�����
					
					setting.edit().putInt(AppConst.BALANCE, AppConst.BALANCE_INIT_VALUE).commit();//��ʼ���Ծ�ֵ
					
					setting.edit().putInt(AppConst.ROUND_DAY, 0).commit();// �����ļ��� 
					setting.edit().putString(AppConst.Last_LOGIN_DATE, DateUtil.DateToStringNoHour(new Date())).commit();//��¼��½ʱ��

					jumptoMainActivity(AppConst.BALANCE_INIT_VALUE);
					
					
				}
			});
		}
		else//else get the balance and go to MainActivity
		{
			balance = setting.getInt("balance", AppConst.BALANCE_INIT_VALUE);
			jumptoMainActivity(balance);
		
		
		}
	}
	
	private void jumptoMainActivity(int balance)
	{
		Intent toHomeIntent = new Intent(WelcomeActivity.this,MainActivity.class);
		toHomeIntent.putExtra(AppConst.BALANCE, balance);
		startActivity(toHomeIntent);
		finish();
	}
	

	class MyViewPagerAdapter extends PagerAdapter
	{


		@Override
		public Object instantiateItem(ViewGroup container, int position)
		{   
			View currentView = viewPagerList.get(position);
			container.addView(currentView);
			return currentView;
		}
		
		@Override
		public int getCount()
		{
			return viewPagerList.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1)
		{
			return arg0 == arg1;
		}
		
		@Override
		public void destroyItem(ViewGroup container, int position, Object object)
		{
			container.removeView(viewPagerList.get(position));
		}
		

	}

	/**
	 * �õ���ǰ�İ汾��Ϣ
	 * ��ʱû����
	 * @return versionCode
	 */
	public int getVersionCode()
	{
		int versionCode = 0;
		PackageManager pm = getPackageManager();
		PackageInfo pinfo = null;
		try
		{
			pinfo = pm.getPackageInfo(getPackageName(),PackageManager.GET_CONFIGURATIONS);
		} catch (NameNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 versionCode = pinfo.versionCode;
		 return versionCode;
	}
	
	
}
