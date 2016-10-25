package cn.cerc.jdb.sms;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.cerc.jdb.core.IConfig;
import cn.cerc.jdb.core.IConnection;

public class SmsYunpianConnection implements IConnection, SmsConnection {

	private static final String SMS_YUNPIAN_URL = "sms.yunpian.url";
	private static final String SMS_YUNPIAN_KEY = "sms.yunpian.key";

	private IConfig config;

	private String url = null;
	private String key = null;
	private SmsSession smsSession = new SmsSession(this);

	@Override
	public void setConfig(IConfig config) {
		this.config = config;
	}

	public IConfig getConfig() {
		return config;
	}

	@Override
	public Object getSession() {
		if (url == null) {
			url = config.getProperty(SMS_YUNPIAN_URL, "").trim();
			key = config.getProperty(SMS_YUNPIAN_KEY, "").trim();
		}
		if (url == null || key == null || url.equals("") || key.equals(""))
			throw new RuntimeException("无法连接yuanpian发送短信！请在properties文件配置" + SMS_YUNPIAN_URL + "和" + SMS_YUNPIAN_KEY);
		return smsSession;
	}

	@Override
	public void sendMessage(String phoneNumber, String content) {
		int result = -1;
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("apikey", key);
		paramMap.put("mobile", phoneNumber);
		paramMap.put("text", content);
		String str = HttpClientUtil.post("https://sms.yunpian.com/v1/sms/send.json", paramMap, "UTF-8");
		if (str == null)
			throw new RuntimeException("网络错误，连接不上短信服务商");
		JSONObject obj = JSON.parseObject(str);
		result = obj.getIntValue("code");
		if (result != 0)
			throw new RuntimeException("发送错误，sms.yunpian.com服务商返回不成功,code=" + result);
	}

	@Override
	public void init() {

	}

}
