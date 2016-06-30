package cn.cerc.jdb.other;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

public class TStringList implements Iterable<String> {
	public static final String vbCrLf = "\r\n";
	private ArrayList<String> Items = new ArrayList<String>();
	private ArrayList<Object> objects = new ArrayList<Object>();

	// 分隔符
	private String delimiter = ",";

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	// 加分隔符后返回的字符串
	public String getDelimitedText() {
		StringBuffer result = new StringBuffer();
		for (String key : Items) {
			result.append(key + this.delimiter);
		}
		String str = result.toString();
		if (Items.size() > 0)
			return str.substring(0, str.length() - this.delimiter.length());
		else
			return str;
	}

	public TStringList add(String value) {
		Items.add(value);
		return this;
	}

	public String text() {
		StringBuffer sb = new StringBuffer();
		for (String item : Items) {
			sb.append(item + vbCrLf);
		}
		return sb.toString();
	}

	public int count() {
		return Items.size();
	}

	@Override
	public String toString() {
		return this.text() + "count:" + this.count();
	}

	public Iterator<String> iterator() {
		Iterator<String> iter = Items.iterator();
		return iter;
	}

	public void LoadFromFile(String fileName) {
		/**
		 * f = TextFile(); while(f.next()){ String line = f.readLine();
		 * this.add(line); } f.close();
		 */
		InputStream is = null;
		InputStreamReader isr = null;
		BufferedReader br = null;

		try {
			is = new FileInputStream(fileName);
			isr = new InputStreamReader(is);
			br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null) {
				add(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (Exception e2) {
			}
			try {
				isr.close();
			} catch (Exception e2) {
			}
			try {
				br.close();
			} catch (Exception e2) {
			}
		}
	}

	public int indexOf(String value) {
		return this.Items.indexOf(value);
	}

	public String Strings(int index) {
		return Items.get(index);
	}

	public TStringList AddObject(String value, Object item) {
		Items.add(value + vbCrLf);
		objects.add(item);
		return this;
	}

	public Object getObjects(Integer index) {
		return objects.get(index);
	}

	public void Delete(int index) {
		this.Items.remove(index);

	}

	public static void main(String[] args) {
		TStringList sl = new TStringList();
		sl.setDelimiter("\\");
		sl.add("line1");
		sl.add("line2");
		sl.add("line3");
		// log.info(sl.getDelimitedText());
		// for (String str : sl)
		// {
		// //log.info(str);
		// }
	}

}
