package com.huang.model;

import java.util.Date;
/**
 * ϰ��ʵ����
 * 
 */
public class Habit
{
	/**
	 * ϰ��id
	 */
	private int id;

	/**
	 * ����ϰ�ߵ�ʱ��
	 */
	private String date;
	
	/**
	 * ϰ�ߵ�����
	 */
	private int type;

	/**
	 * ϰ�߷�������
	 */
	private int dateDrinkTimes;
	
	public Habit(int id, String date, int type)
	{
		super();
		this.id = id;
		this.date = date;
		this.type = type;
	}
	
	public Habit()
	{
		
	}
	
	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}
	public String getDate()
	{
		return date;
	}
	public void setDate(String date)
	{
		this.date = date;
	}
	public int getType()
	{
		return type;
	}
	public void setType(int type)
	{
		this.type = type;
	}
	public int getDateDrinkTimes()
	{
		return dateDrinkTimes;
	}

	public void setDateDrinkTimes(int dateDrinkTimes)
	{
		this.dateDrinkTimes = dateDrinkTimes;
	}
	//test
	
}
