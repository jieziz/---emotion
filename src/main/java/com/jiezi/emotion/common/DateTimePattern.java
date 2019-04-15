package com.jiezi.emotion.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
public enum DateTimePattern {

	/** 年 格式 */
	YEAR("yyyy"),

	/** 月 格式 */
	MONTH("MM"),

	/** 日 格式 */
	DAY("dd"),

	/** 小时 格式 */
	HOUR("HH"),

	/** 分钟 格式 */
	MINUTE("mm"),

	/** 秒 格式 */
	SECOND("ss"),

	/** 毫秒 格式 */
	MILLISECOND("SSS"),

	/**
	 * 日期（年月日）分隔符。
	 */
	DATE_SEPARATOR("-"),

	/**
	 * 时间（时分秒）分隔符。
	 */
	TIME_SEPARATOR(":"),

	/**
	 * 日期与时间之间的分隔符。
	 */
	DATETIME_SEPARATOR(" "),

	/**
	 * 毫秒与时间之间的分隔符。
	 */
	MILLISECOND_SEPARATOR(","),
	
	/** 年月格式。 */
	YEARMONTH(YEAR.getPattern() + DATE_SEPARATOR.getPattern() + MONTH.getPattern()),

	/** 日期（年月日）格式。 */
	DATE(YEAR.getPattern() + DATE_SEPARATOR.getPattern() + MONTH.getPattern() + DATE_SEPARATOR.getPattern()
			+ DAY.getPattern()),

	/**
	 * 日期和小时（年月日时）格式。
	 */
	DATEH(DATE.getPattern() + DATETIME_SEPARATOR.getPattern() + HOUR.getPattern()),

	/**
	 * 日期和小时分钟（年月日时分）格式。
	 */
	DATEHM(DATEH.getPattern() + TIME_SEPARATOR.getPattern() + MINUTE.getPattern()),

	/**
	 * 日期和小时分钟秒（年月日时分秒）格式。
	 */
	DATEHMS(DATEHM.getPattern() + TIME_SEPARATOR.getPattern() + SECOND.getPattern()),
	
	/**
	 * 完整日期和时间（年月日时分秒毫秒）格式。
	 */
	DATETIME(DATEHMS.getPattern() + MILLISECOND_SEPARATOR.getPattern() + MILLISECOND.getPattern()),
	
	/**
	 * 月日格式。
	 */
	DATEMD(MONTH.getPattern() + DAY.getPattern()),
	
	/**
	 * 时分秒格式。
	 */
	HMS(HOUR.getPattern() + TIME_SEPARATOR.getPattern() + MINUTE.getPattern() + TIME_SEPARATOR.getPattern() + SECOND.getPattern());


	private String pattern;

	private DateTimePattern(String pattern) {
		this.pattern = pattern;
	}

	/**
	 * 设置格式。
	 * 
	 * @param pattern
	 *            格式。
	 */
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	/**
	 * 返回格式。
	 * 
	 * @return 格式。
	 */
	public String getPattern() {
		return this.pattern;
	}
	
	/**
	 * 将字符串日期格式转换成Date对象，根据字符串的长度采用相同长度的匹配模式。
	 * @param sdate 字符串日期，如：2002-01-02 08:01:02
	 * @return 日期对象。
	 * @throws java.text.ParseException
	 */
	public static Date parse(String sdate) throws ParseException {
		if(sdate == null || sdate.trim().length() == 0) {
			return null;
		}
		String pattern = DATETIME.getPattern();
		sdate = sdate.trim();
		pattern = pattern.substring(0, sdate.length());
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.parse(sdate);
	}
	
	/**
	 * 将Date对象转换成字符串日期格式
	 * @param date 日期对象
	 * @return 字符串日期，如：2002-01-02 08:01:02
	 */
	public static String format(Date date) {
		if(date == null) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(DateTimePattern.DATEHMS.pattern);
		return sdf.format(date);
	}
	
	/**
	 * 根据提供的年月日时分秒毫秒格式化日历。
	 * @param dtValues 年月日时分秒毫秒，必须按此顺序，可以只传递前一个或几个值。
	 * 如果出现null值则忽略后面所有值。
	 * @return 由dtValues决定的日期对象。
	 */
	public static Date parse(Integer[] dtValues) {
		if(dtValues == null || dtValues.length == 0) {
			return null;
		}
		//年月日时分秒毫秒的格式
		String[] patterns = new String[]{
				YEAR.getPattern(),
				MONTH.getPattern(),
				DAY.getPattern(),
				HOUR.getPattern(),
				MINUTE.getPattern(),
				SECOND.getPattern(),
				MILLISECOND.getPattern(),
		};
		//依据年月日时分秒毫秒的顺序出现的分隔符
		String[] separators = new String[]{
				DATE_SEPARATOR.getPattern(),
				DATE_SEPARATOR.getPattern(),
				DATETIME_SEPARATOR.getPattern(),
				TIME_SEPARATOR.getPattern(),
				TIME_SEPARATOR.getPattern(),
				MILLISECOND_SEPARATOR.getPattern()
		};
		StringBuffer sbp = new StringBuffer();
		StringBuffer sbdt = new StringBuffer();
		for(int i = 0;i < dtValues.length && i < patterns.length;i ++) {
			if(dtValues[i] == null) {
				break;
			}
			sbdt.append(dtValues[i]);
			sbp.append(patterns[i]);
			if((i+1) < dtValues.length && i < separators.length ) {
				sbdt.append(separators[i]);
				sbp.append(separators[i]);
			}
		}
		SimpleDateFormat sdf = new SimpleDateFormat(sbp.toString());
		if (sbdt.length() == 0) {
			return null;
		}
		try {
			return sdf.parse(sbdt.toString());
		} catch (ParseException e) {
			throw new RuntimeException("ParseException:" + e.toString());
		}
		
	}//end parse

	public static void main(String[] args) {
		Integer[] dtValues = new Integer[] { 2010, 1, 2, 3, 4, 5, 6, 7 };
		Date date = parse(dtValues);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
		System.out.println(sdf.format(date));
	}
}
