package cn.cerc.jdb.other;

import java.text.DecimalFormat;
import java.util.UUID;

public class utils {
	public static final String vbCrLf = "\r\n";

	public static double roundTo(double val, int scale) {
		if (scale <= 0) {
			String str = "0.000000000000";
			str = str.substring(0, str.indexOf(".") - scale + 1);
			DecimalFormat df = new DecimalFormat(str);
			return Double.parseDouble(df.format(val));
		} else {
			String str = val + "";
			int pointPosition = str.indexOf(".");
			String tempStr = str.substring(0, pointPosition - scale + 1);
			Double tempD = Double.parseDouble(tempStr) / 10;
			int tempInt = Math.round(tempD.floatValue());
			double ret = tempInt * Math.pow(10, scale);
			return ret;
		}
	}

	public static int random(int value) {
		return (int) (Math.random() * value);
	}

	public static int pos(String sub, String text) {
		return text.indexOf(sub) + 1;
	}

	public static String intToStr(int value) {
		return "" + value;
	}

	public static String intToStr(double value) {
		return "" + value;
	}

	public static int strToIntDef(String str, int def) {
		int result;
		try {
			result = Integer.parseInt(str);
		} catch (Exception e) {
			result = def;
		}
		return result;
	}

	public static double strToFloatDef(String str, double def) {
		double result;
		try {
			result = Double.parseDouble(str);
		} catch (Exception e) {
			result = def;
		}
		return result;
	}

	public static String floatToStr(Double stockNum) {
		return stockNum + "";
	}

	public static String newGuid() {
		UUID uuid = UUID.randomUUID();
		return '{' + uuid.toString() + '}';
	}

	public static String safeString(String value) {
		return value == null ? "" : value.replaceAll("'", "");
	}

	public static String copy(String text, int iStart, int iLength) {
		if (text == null)
			return "";
		if (text != null && iLength >= text.length()) {
			if (iStart > text.length()) {
				return "";
			}
			if (iStart - 1 < 0) {
				return "";
			}
			return text.substring(iStart - 1, text.length());
		} else if (text.equals("")) {
			return "";
		}
		return text.substring(iStart - 1, iStart - 1 + iLength);
	}

	public static String replace(String test, String sub, String rpl) {
		return test.replace(sub, rpl);
	}

	public static String trim(String str) {
		return str.trim();
	}

	// 取得大于等于X的最小的整数，即：进一法
	public static int ceil(double val) {
		int result = (int) val;
		return (val > result) ? result + 1 : result;
	}

	// 取得X的整数部分，即：去尾法
	public static double trunc(double d) {
		return (int) d;
	}

	public static String iif(boolean flag, String val1, String val2) {
		return flag ? val1 : val2;
	}

	public static double iif(boolean flag, double val1, double val2) {
		return flag ? val1 : val2;
	}

	public static int iif(boolean flag, int val1, int val2) {
		return flag ? val1 : val2;
	}

	public static int round(double d) {
		return (int) Math.round(d);
	}

	/**
	 * @param text=要检测的文本
	 * @return 判断字符串是否全部为数字
	 */
	public static boolean isNumeric(String text) {
		if (text == null)
			return false;
		if (".".equals(text))
			return false;
		return text.matches("[0-9,.]*");
	}

	public static boolean assigned(Object object) {
		return object != null;
	}

	public static String isNull(String text, String def) {
		// 判断是否为空如果为空就返回。
		return text.equals("") ? def : text;
	}

	public static String formatFloat(String fmt, double value) {
		DecimalFormat df = new DecimalFormat(fmt);
		fmt = df.format(value);
		return fmt;
	}
}
