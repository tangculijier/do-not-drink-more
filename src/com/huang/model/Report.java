/**  
 * �±��Ļ������ݽṹ
 * @author lizheHuang 
 * @Date   time :2015��11��13��  ����11:19:07
 * @version 1.0
 */ 

package com.huang.model;

public class Report
{
	/**
	 * �±����������� �趨Ϊÿ�����һ�� ��2015-11-30
	 */
	String date;
	/**
	 * ����û�к����ϵ�����
	 */
	int noDrinkDays;
	/**
	 * ���±����������
	 */
	int longestKeepDays;
	/**
	 * �±����Ϻ����ϵĴ���
	 */
	int morningtimes;
	int afternoontimes;
	int eveningtimes;
	
	public String getDate()
	{
		return date;
	}
	public void setDate(String date)
	{
		this.date = date;
	}
	public int getNoDrinkDays()
	{
		return noDrinkDays;
	}
	public void setNoDrinkDays(int noDrinkDays)
	{
		this.noDrinkDays = noDrinkDays;
	}
	public int getLongestKeepDays()
	{
		return longestKeepDays;
	}
	public void setLongestKeepDays(int longestKeepDays)
	{
		this.longestKeepDays = longestKeepDays;
	}
	public int getMorningtimes()
	{
		return morningtimes;
	}
	public void setMorningtimes(int morningtimes)
	{
		this.morningtimes = morningtimes;
	}
	public int getAfternoontimes()
	{
		return afternoontimes;
	}
	public void setAfternoontimes(int afternoontimes)
	{
		this.afternoontimes = afternoontimes;
	}
	public int getEveningtimes()
	{
		return eveningtimes;
	}
	public void setEveningtimes(int eveningtimes)
	{
		this.eveningtimes = eveningtimes;
	}
}
