package org.terifan.util;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public final class Calendar implements Cloneable, Comparable<Calendar>, Serializable
{
	public final static String[] LONG_MONTH_NAMES =
	{
		"Januari", "Februari", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"
	};
	public final static String[] SHORT_MONTH_NAMES =
	{
		"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
	};
	public final static String[] LONG_DAY_NAMES =
	{
		"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
	};
	public final static String[] SHORT_DAY_NAMES =
	{
		"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"
	};

	private final static Object[][] REGEX_PATTERNS = {
		{Pattern.compile("^([0-9]{4})-([0-9]{2})-([0-9]{2})$"), "YMD"},
		{Pattern.compile("^([0-9]{4})-([0-9]{2})-([0-9]{2}) ([0-9]{2}):([0-9]{2})$"), "YMDhm"},
		{Pattern.compile("^([0-9]{4})-([0-9]{2})-([0-9]{2}) ([0-9]{2}):([0-9]{2}):([0-9]{2})$"), "YMDhms"},
		{Pattern.compile("^([0-9]{4})-([0-9]{2})-([0-9]{2}) ([0-9]{2}):([0-9]{2}):([0-9]{2})\\.([0-9]{1,3})$"), "YMDhmsz"},
		{Pattern.compile("^([0-9]{2}):([0-9]{2})$"), "hm"},
		{Pattern.compile("^([0-9]{2}):([0-9]{2}):([0-9]{2})$"), "hms"},
		{Pattern.compile("^([0-9]{2}):([0-9]{2}):([0-9]{2})\\.([0-9]{1,3})$"), "hmsz"},
		{Pattern.compile("^([0-9]{4})([0-9]{2})([0-9]{2})$"), "YMD"},
		{Pattern.compile("^([0-9]{4})([0-9]{2})([0-9]{2})([0-9]{2})([0-9]{2})$"), "YMDhm"},
		{Pattern.compile("^([0-9]{4})([0-9]{2})([0-9]{2})([0-9]{2})([0-9]{2})([0-9]{2})$"), "YMDhms"},
		{Pattern.compile("^([0-9]{4})([0-9]{2})([0-9]{2})([0-9]{2})([0-9]{2})([0-9]{2})([0-9]{1,3})$"), "YMDhmsz"},
		{Pattern.compile("^([0-9]{2})([0-9]{2})$"), "hm"},
		{Pattern.compile("^([0-9]{2})([0-9]{2})([0-9]{2})$"), "hms"},
		{Pattern.compile("^([0-9]{2})([0-9]{2})([0-9]{2})([0-9]{1,3})$"), "hmsz"}
	};

	private final static long serialVersionUID = 1;

	private final static String DEFAULT_DATE_FORMAT_STRING = "yyyy-MM-dd";
	private final static String DEFAULT_TIME_FORMAT_STRING = "hh:mm:ss";
	private final static String DEFAULT_DATETIME_FORMAT_STRING = "yyyy-MM-dd HH:mm:ss";

	private final static SimpleDateFormat DEFAULT_TIME_FORMAT = new SimpleDateFormat(DEFAULT_TIME_FORMAT_STRING);
	private final static SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat(DEFAULT_DATE_FORMAT_STRING);
	private final static SimpleDateFormat DEFAULT_DATETIME_FORMAT = new SimpleDateFormat(DEFAULT_DATETIME_FORMAT_STRING);

	private final static Calendar STATIC_CALENDAR_INSTANCE = new Calendar();


	private int mYear;
	private int mMonth;
	private int mDay;
	private int mHour;
	private int mMinute;
	private int mSecond;
	private int mMilliSecond;


	public enum Field
	{
		YEAR,
		MONTH,
		DAY,
		HOUR,
		MINUTE,
		SECOND,
		MILLISECOND;
	}


	public Calendar()
	{
		set(new GregorianCalendar());
	}


	public Calendar(Calendar aCalendar)
	{
		set(aCalendar.get());
	}


	public Calendar(Date aDate)
	{
		set(aDate.getTime());
	}


	public Calendar(long aMillisecond)
	{
		set(aMillisecond);
	}


	public Calendar(GregorianCalendar aGregorianCalendar)
	{
		set(aGregorianCalendar);
	}


	public Calendar(int aYear, int aMonth, int aDay)
	{
		this(aYear, aMonth, aDay, 0, 0, 0, 0);
	}


	public Calendar(int aYear, int aMonth, int aDay, int aHour, int aMinute, int aSecond, int aMilliSecond)
	{
		setYear(aYear);
		setMonth(aMonth);
		setDay(aDay);
		setHour(aHour);
		setMinute(aMinute);
		setSecond(aSecond);
		setMilliSecond(aMilliSecond);
	}


	/**
	 * @param aDateTime
	 *    a date/time string. Supported formats:
	 *    <ul>
	 *    <li>2004-07-28</li>
	 *    <li>2004-07-28 15:50</li>
	 *    <li>2004-07-28 15:50:40</li>
	 *    <li>2004-07-28 15:50:40.11</li>
	 *    <li>15:50</li>
	 *    <li>15:50:40</li>
	 *    <li>15:50:40.11</li>
	 *    </ul>
	 */
	public Calendar(String aDateTime)
	{
		set(aDateTime);
	}


	/**
	 * Creates a Calendar and initializes it first with the current date and
	 * time and then updates it using the date and time value provided using the
	 * format specified.
	 *
	 * @param aDateTime
	 * @param aFormat
	 * @see #decode
	 */
	public Calendar(String aDateTime, String aFormat)
	{
		if (!decode(aDateTime, aFormat))
		{
			throw new IllegalArgumentException("Bad date/time format: " + aFormat + ", date: " + aDateTime);
		}
	}


	/**
	 * Decodes the date and time provided using the format specified. The format
	 * contain codes that specify the format.
	 *
	 * @param aDateTime
	 *   a date time value. E.g. "2011-10-09 09:08:50"
	 * @param aFormat
	 *   the format of the date and time string. E.g. "yyyy-MM-dd HH:mm:ss.SSS"
	 * @see java.text.SimpleDateFormat
	 */
	public boolean decode(String aDateTime, String aFormat)
	{
		try
		{
			set(new SimpleDateFormat(aFormat).parse(aDateTime).getTime());

			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace(System.err);
			return false;
		}
	}


//	private int grab(String aDateTime, StringBuilder aFormat, String aPart)
//	{
//		int i = aFormat.indexOf(aPart);
//
//		if (i == -1)
//		{
//			throw new IllegalDateTimeFormatException("Part not found: " + aPart + ", format: " + aFormat + ", datetime: " + aDateTime);
//		}
//
//		//System.out.println(i+" "+aDateTime+" "+aFormat+" "+aPart);
//
//		int v = Integer.parseInt(aDateTime.substring(i,i+aPart.length()));
//
//		for (int j = 0; j < aPart.length(); j++)
//		{
//			aFormat.setCharAt(i+j, '*');
//		}
//
//		return v;
//	}


	/**
	 * @param aDateTime
	 *    a date/time string. Supported formats:
	 *    <ul>
	 *    <li>2004-07-28</li>
	 *    <li>2004-07-28 15:50</li>
	 *    <li>2004-07-28 15:50:40</li>
	 *    <li>2004-07-28 15:50:40.11</li>
	 *    <li>15:50</li>
	 *    <li>15:50:40</li>
	 *    <li>15:50:40.11</li>
	 *    </ul>
	 */
	public void set(String aDateTime)
	{
		if (Strings.isEmptyOrNull(aDateTime))
		{
			throw new IllegalArgumentException("Zero length datetime value.");
		}

		mYear = 1970;
		mMonth = mDay = 1;
		mHour = mMinute = mSecond = mMilliSecond = 0;

		for (Object[] format : REGEX_PATTERNS)
		{
			if (parseRegex((Pattern)format[0], aDateTime, (String)format[1]))
			{
				return;
			}
		}

		throw new IllegalArgumentException("Failed to parse: " + aDateTime);
	}


	private boolean parseRegex(Pattern aRegex, String aDateTime, String aFormat)
	{
		try
		{
			Matcher matcher = aRegex.matcher(aDateTime);

			if (matcher.find())
			{
				for (int i = 0; i < aFormat.length(); i++)
				{
					int v = Integer.parseInt(matcher.group(1 + i));

					switch (aFormat.charAt(i))
					{
						case 'Y': mYear = v; break;
						case 'M': mMonth = v; break;
						case 'D': mDay = v; break;
						case 'h': mHour = v; break;
						case 'm': mMinute = v; break;
						case 's': mSecond = v; break;
						case 'z': mMilliSecond = v; break;
						default: throw new IllegalArgumentException();
					}
				}

				return true;
			}
		}
		catch (Exception e)
		{
			// ignore
		}

		return false;
	}


	public int getYear()
	{
		return mYear;
	}


	public Calendar setYear(int aValue)
	{
		mYear = aValue;

		return this;
	}


	public int getMonth()
	{
		return mMonth;
	}


	public Calendar setMonth(int aValue)
	{
		if (aValue < 1 || aValue > 12)
		{
			throw new IllegalDateTimeFormatException("Provided value is out of bounds: month: " + aValue);
		}

		mMonth = aValue;

		return this;
	}


	public int getDay()
	{
		return mDay;
	}


	public Calendar setDay(int aValue)
	{
		if (aValue < 1 || aValue > getDaysInMonth(getYear(), getMonth()))
		{
			throw new IllegalDateTimeFormatException("Provided value is out of bounds: aValue:" + aValue + ", year: " + getYear() + ", month: " + getMonth() + ", days in month: " + getDaysInMonth(getYear(), getMonth()));
		}

		mDay = aValue;

		return this;
	}


	public int getHour()
	{
		return mHour;
	}


	public Calendar setHour(int aValue)
	{
		if (aValue < 0 || aValue > 23)
		{
			throw new IllegalDateTimeFormatException("Provided value is out of bounds: hour: " + aValue);
		}

		mHour = aValue;

		return this;
	}


	public int getMinute()
	{
		return mMinute;
	}


	public final Calendar setMinute(int aValue)
	{
		if (aValue < 0 || aValue > 59)
		{
			throw new IllegalDateTimeFormatException("Provided value is out of bounds: minute: " + aValue);
		}

		mMinute = aValue;

		return this;
	}


	public int getSecond()
	{
		return mSecond;
	}


	public final Calendar setSecond(int aValue)
	{
		if (aValue < 0 || aValue > 59)
		{
			throw new IllegalDateTimeFormatException("Provided value is out of bounds: second: " + aValue);
		}

		mSecond = aValue;

		return this;
	}


	public int getMilliSecond()
	{
		return mMilliSecond;
	}


	public Calendar setMilliSecond(int aValue)
	{
		if (aValue < 0 || aValue > 999)
		{
			throw new IllegalDateTimeFormatException("Provided value is out of bounds: millisecond: " + aValue);
		}

		mMilliSecond = aValue;

		return this;
	}


	public Calendar clearTime()
	{
		mHour = 0;
		mMinute = 0;
		mSecond = 0;
		mMilliSecond = 0;

		return this;
	}


	public int getWeek()
	{
		return toGregorianCalendar().get(GregorianCalendar.WEEK_OF_YEAR);
	}


	public Calendar roll(String aField, int aAmount)
	{
		return roll(Enum.valueOf(Field.class, aField.toUpperCase()), aAmount);
	}


	public Calendar roll(Field aField, int aAmount)
	{
		if (aAmount == 0)
		{
			return this;
		}

		int amount = aAmount > 0 ? 1 : -1;

		while (aAmount != 0)
		{
			switch (aField)
			{
				case YEAR:
					mYear += amount;
					break;
				case MONTH:
					mMonth += amount;
					break;
				case DAY:
					mDay += amount;
					break;
				case HOUR:
					mHour += amount;
					break;
				case MINUTE:
					mMinute += amount;
					break;
				case SECOND:
					mSecond += amount;
					break;
				case MILLISECOND:
					mMilliSecond += amount;
					break;
				default:
					throw new IllegalArgumentException("Unsupported field " + aField);
			}

			if (mMonth == 0)
			{
				setMonth(12);
				roll(Field.YEAR, -1);
			}
			else if (mMonth > 12)
			{
				setMonth(1);
				roll(Field.YEAR, 1);
			}
			if (mDay == 0)
			{
				// setDay may fail because the roll method is called recursivly,
				// so I'm setting the member directly to avoid argument
				// validation exceptions
				mDay = getDaysInMonth(mMonth == 1 ? mYear - 1 : mYear, mMonth == 1 ? 12 : mMonth - 1);
				roll(Field.MONTH, -1);
			}
			else if (mDay > getDaysInMonth(mYear, mMonth))
			{
				setDay(1);
				roll(Field.MONTH, 1);
			}
			if (mHour < 0)
			{
				setHour(23);
				roll(Field.DAY, -1);
			}
			else if (mHour > 23)
			{
				setHour(0);
				roll(Field.DAY, 1);
			}
			if (mMinute < 0)
			{
				setMinute(59);
				roll(Field.HOUR, -1);
			}
			else if (mMinute > 59)
			{
				setMinute(0);
				roll(Field.HOUR, 1);
			}
			if (mSecond < 0)
			{
				setSecond(59);
				roll(Field.MINUTE, -1);
			}
			else if (mSecond > 59)
			{
				setSecond(0);
				roll(Field.MINUTE, 1);
			}
			if (mMilliSecond < 0)
			{
				setMilliSecond(999);
				roll(Field.SECOND, -1);
			}
			else if (mMilliSecond > 999)
			{
				setMilliSecond(0);
				roll(Field.SECOND, 1);
			}

			aAmount -= amount;
		}

		return this;
	}


	public Calendar set(long aMillisecond)
	{
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(new Date(aMillisecond));
		set(gc);

		return this;
	}


	public Calendar set(GregorianCalendar aGregorianCalendar)
	{
		setYear(aGregorianCalendar.get(GregorianCalendar.YEAR));
		setMonth(aGregorianCalendar.get(GregorianCalendar.MONTH) + 1);
		setDay(aGregorianCalendar.get(GregorianCalendar.DATE));
		setHour(aGregorianCalendar.get(GregorianCalendar.HOUR) + (aGregorianCalendar.get(GregorianCalendar.AM_PM) == GregorianCalendar.AM ? 0 : 12));
		setMinute(aGregorianCalendar.get(GregorianCalendar.MINUTE));
		setSecond(aGregorianCalendar.get(GregorianCalendar.SECOND));
		setMilliSecond(aGregorianCalendar.get(GregorianCalendar.MILLISECOND));

		return this;
	}


	/**
	 * Gets the current time in milliseconds.
	 *
	 * @return
	 *   current time in milliseconds.
	 */
	public synchronized long get()
	{
		GregorianCalendar gc = new GregorianCalendar();

		gc.setMinimalDaysInFirstWeek(4);
		gc.setFirstDayOfWeek(GregorianCalendar.MONDAY);
		gc.set(GregorianCalendar.YEAR, mYear);
		gc.set(GregorianCalendar.MONTH, mMonth - 1);
		gc.set(GregorianCalendar.DATE, mDay);
		gc.set(GregorianCalendar.HOUR, mHour % 12);
		gc.set(GregorianCalendar.AM_PM, mHour >= 12 ? GregorianCalendar.PM : GregorianCalendar.AM);
		gc.set(GregorianCalendar.MINUTE, mMinute);
		gc.set(GregorianCalendar.SECOND, mSecond);
		gc.set(GregorianCalendar.MILLISECOND, mMilliSecond);

		return gc.getTimeInMillis();
	}


	public GregorianCalendar toGregorianCalendar()
	{
		GregorianCalendar gc = new GregorianCalendar(mYear, mMonth - 1, mDay, mHour, mMinute, mSecond);
		gc.set(GregorianCalendar.MILLISECOND, mMilliSecond);
		gc.setMinimalDaysInFirstWeek(4);
		gc.setFirstDayOfWeek(GregorianCalendar.MONDAY);
		return gc;
	}


	/**
	 * Return the day of week with Monday as day 0 (zero) and Sunday as day 6.
	 *
	 * @return
	 *   the day number
	 */
	public int getDayOfWeek()
	{
		GregorianCalendar gc = toGregorianCalendar();

		switch (gc.get(GregorianCalendar.DAY_OF_WEEK))
		{
			case GregorianCalendar.MONDAY: return 0;
			case GregorianCalendar.TUESDAY: return 1;
			case GregorianCalendar.WEDNESDAY: return 2;
			case GregorianCalendar.THURSDAY: return 3;
			case GregorianCalendar.FRIDAY: return 4;
			case GregorianCalendar.SATURDAY: return 5;
			case GregorianCalendar.SUNDAY: return 6;
			default: throw new IllegalArgumentException("Only seven day weeks supported");
		}
	}


	@Override
	public String toString()
	{
		return DEFAULT_DATETIME_FORMAT.format(new Date(get()));
	}


	public String format(String aFormat)
	{
		if (aFormat.equals(DEFAULT_DATE_FORMAT_STRING))
		{
			return DEFAULT_DATE_FORMAT.format(new Date(get()));
		}
		if (aFormat.equals(DEFAULT_TIME_FORMAT_STRING))
		{
			return DEFAULT_TIME_FORMAT.format(new Date(get()));
		}
		if (aFormat.equals(DEFAULT_DATETIME_FORMAT_STRING))
		{
			return DEFAULT_DATETIME_FORMAT.format(new Date(get()));
		}

		return new SimpleDateFormat(aFormat).format(new Date(get()));
	}


	public static int getDaysInMonth(int aYear, int aMonth)
	{
		if (aYear < 1600 || aYear > 2400)
		{
			throw new IllegalDateTimeFormatException("Provided 'year' value is out of bounds: year: " + aYear);
		}
		if (aMonth < 1 || aMonth > 12)
		{
			throw new IllegalDateTimeFormatException("Provided 'month' value is out of bounds: month: " + aMonth);
		}

		GregorianCalendar gc = new GregorianCalendar(aYear, aMonth - 1, 1, 0, 0);
		return gc.getActualMaximum(GregorianCalendar.DATE);
	}


	/**
	 * Return the provided time using standard format (yyyy-MM-dd HH:mm:ss).
	 *
	 * @return
	 *   the time as a String
	 */
	public static synchronized String format(long aTimeInMillis)
	{
		return format(aTimeInMillis, "yyyy-MM-dd HH:mm:ss");
	}


	/**
	 * Returns the provided time according to the format specified.
	 *
	 * @return
	 *   the time as a String
	 */
	public static synchronized String format(long aTimeInMillis, String aFormat)
	{
		return STATIC_CALENDAR_INSTANCE.set(aTimeInMillis).format(aFormat);
	}


	/**
	 * Returns the current date and time using format yyyy-MM-dd HH:mm:ss
	 *
	 * @return
	 *   the current date and time as a String.
	 */
	public static synchronized String now()
	{
		return format(System.currentTimeMillis());
	}


	/**
	 * Returns the current date and time using format yyyy-MM-dd HH:mm:ss.SSS
	 *
	 * @return
	 *   the current date and time as a String.
	 */
	public static synchronized String exact()
	{
		return format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss.SSS");
	}


	/**
	 * Returns the current date using format yyyy-MM-dd
	 *
	 * @return
	 *   the current date as a String.
	 */
	public static synchronized String date()
	{
		return STATIC_CALENDAR_INSTANCE.set(System.currentTimeMillis()).format(DEFAULT_DATE_FORMAT_STRING);
	}


	/**
	 * Returns the current time using format HH:mm:ss
	 *
	 * @return
	 *   the current time as a String.
	 */
	public static synchronized String time()
	{
		return STATIC_CALENDAR_INSTANCE.set(System.currentTimeMillis()).format("HH:mm:ss");
	}


	/**
	 * Return the difference between this Calendar and the provided Calendar. The
	 * returned difference equals <code>this</code> minus <code>other</code>.
	 */
	public long difference(String aField, long aTimeMillis)
	{
		return difference(Enum.valueOf(Field.class, aField.toUpperCase()), new Calendar(aTimeMillis));
	}


	/**
	 * Return the difference between this Calendar and the provided Calendar. The
	 * returned difference equals <code>this</code> minus <code>aTimeMillis</code>.
	 */
	public long difference(Field aField, long aTimeMillis)
	{
		return difference(aField, new Calendar(aTimeMillis));
	}


	/**
	 * Return the difference between this Calendar and the provided Calendar. The
	 * returned difference equals <code>this</code> minus <code>aTimeMillis</code>.
	 */
	public long difference(String aField, Calendar aCalendar)
	{
		return difference(Field.valueOf(aField.toUpperCase()), aCalendar);
	}


	/*
	 * Return the difference between this Calendar and the provided Calendar. The
	 * returned difference equals <code>this</code> minus <code>other</code>.
	 */
	public long difference(Field aField, Calendar aOther)
	{
		if (before(aOther))
		{
			return -aOther.difference(aField, this);
		}

		long d = get() - aOther.get();

		switch (aField)
		{
			case YEAR:
				d /= 12;
			case MONTH:
				d /= 31;
			case DAY:
				d /= 24;
			case HOUR:
				d /= 60;
			case MINUTE:
				d /= 60;
			case SECOND:
				d /= 1000;
			case MILLISECOND:
				break;
		}

		return d;
	}


	public boolean after(Calendar aCalendar)
	{
		return !before(aCalendar);
	}


	public boolean before(Calendar aCalendar)
	{
		if (mYear < aCalendar.mYear) return true;
		if (mYear > aCalendar.mYear) return false;

		if (mMonth < aCalendar.mMonth) return true;
		if (mMonth > aCalendar.mMonth) return false;

		if (mDay < aCalendar.mDay) return true;
		if (mDay > aCalendar.mDay) return false;

		if (mHour < aCalendar.mHour) return true;
		if (mHour > aCalendar.mHour) return false;

		if (mMinute < aCalendar.mMinute) return true;
		if (mMinute > aCalendar.mMinute) return false;

		if (mSecond < aCalendar.mSecond) return true;
		if (mSecond > aCalendar.mSecond) return false;

		if (mMilliSecond < aCalendar.mMilliSecond) return true;
		if (mMilliSecond > aCalendar.mMilliSecond) return false;

		return false;
	}


	/**
	 * Roll to a specific day of current week.
	 *
	 * @param aWeekday
	 *   0 is Monday, 6 is Sunday
	 */
	public Calendar rollToWeekday(int aWeekday)
	{
		if (aWeekday < 0 || aWeekday > 6)
		{
			throw new IllegalArgumentException("Weekday must be between 0 and 6.");
		}

		int n = aWeekday - getDayOfWeek();
		if (n != 0)
		{
			roll(Field.DAY, n);
		}
		return this;
	}


	public Calendar rollToYearWeekDay(int aYear, int aWeek, int aWeekday)
	{
		if (aYear < 1980 || aYear > 2050 || aWeek < 1 || aWeek > 53 || aWeekday < 0 || aWeekday > 6)
		{
			throw new IllegalDateTimeFormatException("Bad input: aYear: " + aYear + ", aWeek: " + aWeek + ", aWeekday: " + aWeekday);
		}

		// start at january first

		mYear = aYear;
		mMonth = 1;
		mDay = 1;

		// roll to monday

		rollToWeekday(0);

		// check if were at the last week of previous year

		if (getWeek() > 1)
		{
			// roll one week ahead

			roll(Field.DAY, 7);
		}

		// at this point we are at first monday first week specified year.

		// roll desired number of weeks and days

		roll(Field.DAY, 7 * (aWeek - 1) + aWeekday);

		// selftest

		if (getWeekYear() != aYear)
		{
			throw new IllegalStateException("Failed to compute 'year' value: aYear: " + aYear + ", aWeek: " + aWeek + ", aWeekday: " + aWeekday + ", mYear: " + getWeekYear() + ", mWeek: " + getWeek() + ", mWeekday: " + getDayOfWeek());
		}
		if (getWeek() != aWeek)
		{
			throw new IllegalStateException("Failed to compute 'week' value: aYear: " + aYear + ", aWeek: " + aWeek + ", aWeekday: " + aWeekday + ", mYear: " + getWeekYear() + ", mWeek: " + getWeek() + ", mWeekday: " + getDayOfWeek());
		}
		if (getDayOfWeek() != aWeekday)
		{
			throw new IllegalStateException("Failed to compute 'weekday' value: aYear: " + aYear + ", aWeek: " + aWeek + ", aWeekday: " + aWeekday + ", mYear: " + getWeekYear() + ", mWeek: " + getWeek() + ", mWeekday: " + getDayOfWeek());
		}

		return this;
	}


	@Override
	public Calendar clone()
	{
		try
		{
			Calendar newInstance = (Calendar)super.clone();
			newInstance.mDay = mDay;
			newInstance.mHour  = mHour;
			newInstance.mMilliSecond = mMilliSecond;
			newInstance.mMinute = mMinute;
			newInstance.mMonth = mMonth;
			newInstance.mSecond = mSecond;
			newInstance.mYear = mYear;
			return newInstance;
		}
		catch (CloneNotSupportedException e)
		{
			throw new InternalError();
		}
	}


	public int getWeekYear()
	{
		if (mMonth == 1 && mDay < 7)
		{
			if (getWeek() > 30)
			{
				return mYear - 1;
			}
			else
			{
				return mYear;
			}
		}
		else
		{
			if (mMonth == 12 && mDay > 24)
			{
				if (getWeek() < 30)
				{
					return mYear + 1;
				}
				else
				{
					return mYear;
				}
			}
			else
			{
				return mYear;
			}
		}
	}


	/**
	 * Returns an array with all week number for <i>this</i> month and year.
	 */
	public int[] getWeekNumbers()
	{
		int[] weeks = new int[6];
		int position = 0;

		Calendar tmp = clone();
		tmp.setDay(1);

		int d = tmp.getDayOfWeek();

		if (d < 4)
		{
			weeks[position++] = tmp.getWeek();
		}

		for (;;)
		{
			tmp.rollToWeekday(0).roll(Field.DAY, 7).rollToWeekday(3);

			if (tmp.getMonth() != getMonth())
			{
				int[] t = new int[position];
				System.arraycopy(weeks, 0, t, 0, position);
				return t;
			}

			weeks[position++] = tmp.getWeek();
		}
	}


	@Override
	public int hashCode()
	{
		long value = get();
        return (int)(value ^ (value >>> 32));
	}


	@Override
	public boolean equals(Object aObject)
	{
		if (aObject == null || getClass() != aObject.getClass())
		{
			return false;
		}
		Calendar other = (Calendar) aObject;
		if (this.mMilliSecond != other.mMilliSecond)
		{
			return false;
		}
		if (this.mSecond != other.mSecond)
		{
			return false;
		}
		if (this.mMinute != other.mMinute)
		{
			return false;
		}
		if (this.mHour != other.mHour)
		{
			return false;
		}
		if (this.mDay != other.mDay)
		{
			return false;
		}
		if (this.mMonth != other.mMonth)
		{
			return false;
		}
		if (this.mYear != other.mYear)
		{
			return false;
		}
		return true;
	}


	@Override
	public int compareTo(Calendar aOther)
	{
		if (aOther == null)
		{
			return 1;
		}
		if (aOther.mYear != this.mYear)
		{
			return this.mYear < aOther.mYear ? -1 : 1;
		}
		if (aOther.mMonth != this.mMonth)
		{
			return this.mMonth < aOther.mMonth ? -1 : 1;
		}
		if (aOther.mDay != this.mDay)
		{
			return this.mDay < aOther.mDay ? -1 : 1;
		}
		if (aOther.mHour != this.mHour)
		{
			return this.mHour < aOther.mHour ? -1 : 1;
		}
		if (aOther.mMinute != this.mMinute)
		{
			return this.mMinute < aOther.mMinute ? -1 : 1;
		}
		if (aOther.mSecond != this.mSecond)
		{
			return this.mSecond < aOther.mSecond ? -1 : 1;
		}
		if (aOther.mMilliSecond != this.mMilliSecond)
		{
			return this.mMilliSecond < aOther.mMilliSecond ? -1 : 1;
		}
		return 0;
	}


	/**
	 * Constructs a new Calendar object, return null if the input provided is null.
	 */
	public static Calendar parse(Object aInput)
	{
		if (aInput != null)
		{
			if (aInput instanceof String)
			{
				String s = (String)aInput;
				if (s.isEmpty())
				{
					return null;
				}
				return new Calendar(s);
			}
			if (aInput instanceof Date)
			{
				return new Calendar((Date)aInput);
			}
			if (aInput instanceof GregorianCalendar)
			{
				return new Calendar((GregorianCalendar)aInput);
			}
			if (aInput instanceof Long)
			{
				return new Calendar((Long)aInput);
			}
		}

		return null;
	}


	public Date toDate()
	{
		return new Date(get());
	}


	public static class IllegalDateTimeFormatException extends IllegalArgumentException
	{
		private final static long serialVersionUID = 1L;

		public IllegalDateTimeFormatException(String aMessage)
		{
			super(aMessage);
		}
	}


//	public static void main(String ... args)
//	{
//		try
//		{
//			System.out.println(new Calendar("2021-05-01").format("yyyy-MM-dd HH:mm:ss.SSS"));
//			System.out.println(new Calendar("2021-05-01 12:30").format("yyyy-MM-dd HH:mm:ss.SSS"));
//			System.out.println(new Calendar("2021-05-01 12:30:45").format("yyyy-MM-dd HH:mm:ss.SSS"));
//			System.out.println(new Calendar("2021-05-01 12:30:45.870").format("yyyy-MM-dd HH:mm:ss.SSS"));
//			System.out.println(new Calendar("12:30").format("yyyy-MM-dd HH:mm:ss.SSS"));
//			System.out.println(new Calendar("12:30:45").format("yyyy-MM-dd HH:mm:ss.SSS"));
//			System.out.println(new Calendar("12:30:45.870").format("yyyy-MM-dd HH:mm:ss.SSS"));
//			System.out.println(new Calendar("20210501").format("yyyy-MM-dd HH:mm:ss.SSS"));
//			System.out.println(new Calendar("202105011230").format("yyyy-MM-dd HH:mm:ss.SSS"));
//			System.out.println(new Calendar("20210501123045").format("yyyy-MM-dd HH:mm:ss.SSS"));
//			System.out.println(new Calendar("20210501123045870").format("yyyy-MM-dd HH:mm:ss.SSS"));
//			System.out.println(new Calendar("1230").format("yyyy-MM-dd HH:mm:ss.SSS"));
//			System.out.println(new Calendar("123045").format("yyyy-MM-dd HH:mm:ss.SSS"));
//			System.out.println(new Calendar("123045870").format("yyyy-MM-dd HH:mm:ss.SSS"));
//		}
//		catch (Throwable e)
//		{
//			e.printStackTrace(System.out);
//		}
//	}
}