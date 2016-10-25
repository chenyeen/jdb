package cn.cerc.jdb.sms;

public interface SmsConnection {
	public void sendMessage(String phoneNumber, String content);
}
