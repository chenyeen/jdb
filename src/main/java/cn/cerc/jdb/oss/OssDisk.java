package cn.cerc.jdb.oss;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.apache.log4j.Logger;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.Bucket;
import com.aliyun.oss.model.CopyObjectResult;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.ObjectMetadata;

import cn.cerc.jdb.core.IHandle;

public class OssDisk {
	private static final Logger log = Logger.getLogger(OssDisk.class);
	private OSSClient client;
	private String bucketName;
	private String localPath;

	public OssDisk(IHandle handle) {
		OssSession sess = (OssSession) handle.getProperty(OssSession.sessionId);
		client = sess.getClient();
	}

	public OssDisk(IHandle handle, String bucketName) {
		this(handle);
		this.bucketName = bucketName;
	}

	public boolean upload(String localFile, String remoteFile) {
		// 上传本地文件到服务器
		// 例：upload("D:\\oss\\temp.png", "131001/Default/131001/temp.png")
		File file = new File(localFile);
		if (!file.exists())
			throw new RuntimeException("文件不存在：" + localFile);
		try {
			ObjectMetadata summary = client.getObjectMetadata(bucketName, remoteFile);
			if (summary != null && summary.getContentLength() == file.length()) {
				log.info("本地文件与云端文件大小一致，忽略上传请求");
				return true;
			}
		} catch (OSSException e) {
			log.info("服务器上无此文件，开始上传");
		}

		client.putObject(bucketName, remoteFile, file);
		ObjectMetadata metadata = client.getObjectMetadata(bucketName, remoteFile);
		return file.exists() && metadata.getContentLength() == file.length();
	}

	public void upload(InputStream inputStream, String remoteFile) {
		// 上传数据流到服务器
		// 例：upload(inputStream, "131001/Default/131001/temp.txt")
		client.putObject(bucketName, remoteFile, inputStream);
	}

	// 下载Object到文件
	public boolean download(String fileName) {
		if (localPath == null || "".equals(localPath))
			throw new RuntimeException("localPath 必须先进行设置！");

		String localFile = localPath + fileName.replace('/', '\\');
		createFolder(localFile); // 创建本地目录

		// 新建GetObjectRequest
		GetObjectRequest param = new GetObjectRequest(this.bucketName, fileName);
		File file = new File(localFile);
		ObjectMetadata metadata = client.getObject(param, file);
		return file.exists() && metadata.getContentLength() == file.length();
	}

	// 如果文件所在的文件目录不存在，则创建之
	private void createFolder(String fileName) {
		String tmpPath = fileName.substring(0, fileName.lastIndexOf("\\") + 1);
		String subPath = tmpPath;
		int fromIndex = 0;
		while (tmpPath.indexOf("\\", fromIndex) > -1) {
			int beginIndex = tmpPath.indexOf("\\", fromIndex);
			if (fromIndex == -1)
				break;
			subPath = tmpPath.substring(0, beginIndex);
			fromIndex = subPath.length() + 1;
			if (subPath.length() > 2) {
				File file = new File(subPath);
				// 如果文件夹不存在则创建
				if (!file.exists() && !file.isDirectory())
					file.mkdir();
			}
		}
	}

	// 删除Object
	public void delete(String fileName) {
		client.deleteObject(this.bucketName, fileName);
	}

	// 拷贝Object
	public void copyObject(String srcBucketName, String srcKey, String destBucketName, String destKey) {
		/*
		 * sample: srcBucketName = "scmfiles" srcKey = "Products\010001\钻石.jpg";
		 * destBucketName = "vinefiles"; destKey =
		 * "131001\product\0100001\钻石.jpg";
		 */
		CopyObjectResult result = client.copyObject(srcBucketName, srcKey, destBucketName, destKey);

		// 打印结果
		log.info("ETag: " + result.getETag() + " LastModified: " + result.getLastModified());
	}

	public OSSClient getClient() {
		return client;
	}

	// 获取Bucket的存在信息
	public boolean existBucket(String bucketName) {
		return client.doesBucketExist(bucketName);
	}

	// 获取用户的Bucket列表
	public List<Bucket> getBuckets() {
		return client.listBuckets();
		// // 遍历Bucket
		// List<Bucket> buckets = disk.getBuckets();
		// for (Bucket bucket : buckets)
		// {
		// log.info(bucket.getName());
		// }
	}

	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	public String getLocalPath() {
		return localPath;
	}

	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}

	public static void main(String[] args) {
		// WebDisk disk = new WebDisk("vinetest");
		//
		// // 上传指定文件
		// disk.upload("D:\\oss\\temp.png", "131001/Default/131001/temp.png");
		//
		// // 上传数据流
		// String content = "Thank you for using OSS SDK for Java";
		// InputStream inputStream = new
		// ByteArrayInputStream(content.getBytes());
		// disk.upload(inputStream, "131001/Default/131001/temp.txt");
		//
		// // 列出指定目录下所有文件
		// WebFolder folder = new WebFolder(disk);
		// folder.open("131001/Default/131001/");
		// for (String folderName : folder.getSubItems())
		// {
		// System.out.println(folderName);
		// }
		//
		// disk.setLocalPath("D:\\oss\\"); //设置下载本地目录
		// for (String fileName : folder.getFiles().keySet())
		// {
		// System.out.println(fileName);
		// disk.download(fileName); //下载指定文件到本地目录
		// }
	}
}
