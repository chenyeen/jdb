package cn.cerc.jdb.oss;

import org.apache.log4j.Logger;

import com.aliyun.oss.OSSClient;

import cn.cerc.jdb.core.ISession;

public class OssSession implements ISession {
	private static final Logger log = Logger.getLogger(OssSession.class);
	// 设置连接地址
	public static final String oss_endpoint = "oss.endpoint";
	// 连接id
	public static final String oss_accessKeyId = "oss.accessKeyId";
	// 连接密码
	public static final String oss_accessKeySecret = "oss.accessKeySecret";
	// IHandle 标识
	public static final String sessionId = "ossSession";
	private OSSClient ossClient;

	public OSSClient getClient() {
		return ossClient;
	}

	public void setClient(OSSClient ossClient) {
		this.ossClient = ossClient;
	}

	@Override
	public void closeSession() {
		// 关闭OSSClient
		ossClient.shutdown();
		log.debug("关闭ossSession成功");
	}

}
