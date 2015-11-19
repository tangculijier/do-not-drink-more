/**  
 * ���ݿ��еı����Լ��
 * @author lizheHuang 
 * @Date   time :2015��11��19��  ����10:55:45
 * @version 1.0
 */ 

package com.huang.model;

import android.provider.BaseColumns;

public class ModelReaderContract
{

	public ModelReaderContract()
	{
		
	}

	/**
	 * habit��
	 */
	public static abstract class HabitEntry implements BaseColumns
	{
		public static final String TABLE_NAME = "habit";
        public static final String COLUMN_NAME_ENTRY_ID = "id";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_TYPE = "type";
	}
	
	/**
	 * Analysis��
	 */
	public static abstract class AnalysisEntry implements BaseColumns
	{
		public static final String TABLE_NAME = "analysis";
        public static final String COLUMN_NAME_ENTRY_ID = "id";
        public static final String COLUMN_NAME_MONTH = "month";
        public static final String COLUMN_NAME_NODRINKDAYS= "nodrinkdays";
        public static final String COLUMN_NAME_LONGESTKEEPDAY= "longestkeepday";
        public static final String COLUMN_NAME_MORNINGTIMES= "morningtimes";
        public static final String COLUMN_NAME_AFTERNOONTIMES= "afternoontimes";
        public static final String COLUMN_NAME_EVENINGTIMES= "eveningtimes";
	}
	
}
