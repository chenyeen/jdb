package cn.cerc.jdb.oss;

import org.apache.log4j.Logger;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSSClient;

import cn.cerc.jdb.core.IConfig;
import cn.cerc.jdb.core.IConnection;

public class OssConnection implements IConnection {
	private static final Logger log = Logger.getLogger(OssConnection.class);
	private static OSSClient ossClient;
	private IConfig config;

	@Override
	public void setConfig(IConfig config) {
		this.config = config;
	}

	public IConfig getConfig() {
		return config;
	}

	@Override
	public OssSession getSession() {
		init();// 如果连接被意外断开了,那么重新建立连接
		OssSession sess = new OssSession();
		sess.setClient(ossClient);
		return sess;
	}

	@Override
	public void init() {
		if (null == ossClient) {
			String endPoint = config.getProperty(OssSession.oss_endpoint, null);
			String acId = config.getProperty(OssSession.oss_accessKeyId, null);
			String secret = config.getProperty(OssSession.oss_accessKeySecret, null);
			// 创建ClientConfiguration实例
			ClientConfiguration conf = new ClientConfiguration();
			// 设置OSSClient使用的最大连接数，默认1024
			conf.setMaxConnections(1024);
			// 设置请求超时时间，默认50秒
			conf.setSocketTimeout(50);
			// 设置失败请求重试次数，默认3次
			conf.setMaxErrorRetry(3);
			// 创建OSSClient实例
			ossClient = new OSSClient(endPoint, acId, secret, conf);
			log.debug("建立oss连接成功");
		}
	}

}
