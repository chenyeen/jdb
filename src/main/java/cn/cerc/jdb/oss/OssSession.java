package cn.cerc.jdb.oss;

import org.apache.log4j.Logger;

import com.aliyun.oss.OSSClient;

import cn.cerc.jdb.core.ISession;

public class OssSession implements ISession {
	private static final Logger log = Logger.getLogger(OssSession.class);
	public static final String sessionId = "ossSession";
	private OSSClient ossClient;

	public OSSClient getOssClient() {
		return ossClient;
	}

	public void setOssClient(OSSClient ossClient) {
		this.ossClient = ossClient;
	}

	@Override
	public void closeSession() {
		// 关闭OSSClient
		ossClient.shutdown();
		log.info("关闭ossSession......成功");
	}

}
