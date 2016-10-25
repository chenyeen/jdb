package cn.cerc.jdb.sms;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class SmsTemplateUtil {

	private static final String SMSFILE	= "app-sms.xml";
	private static Map<String, String> map = new HashMap<String, String>();

	static {
		try {
			String filepath = SmsTemplateUtil.class.getClassLoader().getResource("").toURI().getPath() + SMSFILE;
			File f = new File(filepath);
			SAXReader reader = new SAXReader();
			Document doc = reader.read(f);

			Element body = doc.getRootElement().element("body");
			if (body == null)
				throw new RuntimeException(SMSFILE + " 格式不正确！");

			for (Iterator<?> j = body.elementIterator("template"); j.hasNext();) {
				Element item = (Element) j.next();
				String name = item.attributeValue("name");
				String content = item.attributeValue("content");
				if (name == null || content == null)
					throw new RuntimeException(SMSFILE + " 格式不正确！");
				if (content.length() < 5)
					throw new RuntimeException("template 为" + name + "的content的长度必须大于5");
				if (map.get(name) != null)
					throw new RuntimeException("template 为" + name + "的name有重复，请检查");
				map.put(name, content);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getContentByTemplate(String template) {
		String content = map.get(template);
		if (content == null)
			throw new RuntimeException("template 为" + template + "的短信模版未在app-sms.xml配置");
		return content;
	}

}
