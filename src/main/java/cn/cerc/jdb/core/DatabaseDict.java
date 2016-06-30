package cn.cerc.jdb.core;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class DatabaseDict {
	public static Map<String, String> tablesKey = null;

	public static String get(String key) {
		if (tablesKey == null) {
			InputStream filepath = DatabaseDict.class.getResourceAsStream("/Database.xml");
			if (filepath == null)
				return null;
			SAXReader reader = new SAXReader();
			Document doc;
			try {
				doc = reader.read(filepath);
			} catch (DocumentException e) {
				throw new RuntimeException(e.getMessage());
			}
			Element root = doc.getRootElement().element("body");
			Element foo;
			Element tempfoo;
			tablesKey = new HashMap<String, String>();
			for (Iterator<?> i = root.elementIterator("TABLEKEY"); i.hasNext();) {
				foo = (Element) i.next();

				StringBuffer values = new StringBuffer();

				if (foo.element("KEYS") != null) {
					for (Iterator<?> j = foo.element("KEYS").elements("KEY").iterator(); j.hasNext();) {
						tempfoo = (Element) j.next();
						values.append(";").append(tempfoo.getText());
					}
				}
				if (values.length() > 0) {
					values.deleteCharAt(0);
				}
				String table = foo.elementText("TABLE");
				tablesKey.put(table, values.toString());
			}
		}
		return tablesKey.get(key);
	}
}
