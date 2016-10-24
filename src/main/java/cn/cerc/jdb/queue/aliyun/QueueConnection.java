package cn.cerc.jdb.queue.aliyun;

import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.MNSClient;

import cn.cerc.jdb.core.IConfig;
import cn.cerc.jdb.core.IConnection;

public class QueueConnection implements IConnection {
	private static MNSClient client;
	private static CloudAccount account;

	@Override
	public void setConfig(IConfig config) {
		if (account == null) {
			String server = config.getProperty(QueueSession.AccountEndpoint, null);
			String userCode = config.getProperty(QueueSession.AccessKeyId, null);
			String password = config.getProperty(QueueSession.AccessKeySecret, null);
			String token = config.getProperty(QueueSession.SecurityToken, "");
			account = new CloudAccount(userCode, password, server, token);
			client = account.getMNSClient();
		}
	}

	@Override
	public QueueSession getSession() {
		QueueSession sess = new QueueSession();
		sess.setClient(client);
		return sess;
	}

}
