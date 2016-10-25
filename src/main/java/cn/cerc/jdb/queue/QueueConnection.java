package cn.cerc.jdb.queue;

import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.MNSClient;

import cn.cerc.jdb.core.IConfig;
import cn.cerc.jdb.core.IConnection;

public class QueueConnection implements IConnection {
	private static MNSClient client;
	private static CloudAccount account;

	private IConfig config;

	@Override
	public void setConfig(IConfig config) {
		this.config = config;
	}

	@Override
	public QueueSession getSession() {
		init();
		QueueSession sess = new QueueSession();
		sess.setClient(client);
		return sess;
	}

	public IConfig getConfig() {
		return config;
	}

	@Override
	public void init() {
		if (account == null) {
			String server = config.getProperty(QueueSession.AccountEndpoint, null);
			String userCode = config.getProperty(QueueSession.AccessKeyId, null);
			String password = config.getProperty(QueueSession.AccessKeySecret, null);
			String token = config.getProperty(QueueSession.SecurityToken, "");
			if (server == null || userCode == null || password == null || token == null)
				throw new RuntimeException("propertys.msn 配置为空");
			account = new CloudAccount(userCode, password, server, token);
			client = account.getMNSClient();
		}
	}

}
