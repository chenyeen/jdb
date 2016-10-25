package cn.cerc.jdb.sms;

import cn.cerc.jdb.core.ISession;

public class SmsSession implements ISession {

	// IHandle中识别码
	public static String sessionId = "smsSession";

	private SmsConnection connection;

	public SmsSession(SmsConnection connection) {
		this.connection = connection;
	}

	@Override
	public void closeSession() {

	}

	public SmsConnection getSmsConnection() {
		return connection;
	}
}
