/**  
 * ��ѯ�ֻ�������ϵ�˵�ʱ�������� 
 * @author lizheHuang 
 * @Date   time :2015��12��8��  ����4:28:44
 * @version 1.0
 */ 

package com.huang.model;

public class Phone
{

	private String userName;
	
	private String userPhone;
	
	public Phone(String userName, String userPhone)
	{
		super();
		this.userName = userName;
		this.userPhone = userPhone;
	}

	public String getUserName()
	{
		return userName;
	}

	

	public String getUserPhone()
	{
		return userPhone;
	}

	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	public void setUserPhone(String userPhone)
	{
		this.userPhone = userPhone;
	}
	
	
}
