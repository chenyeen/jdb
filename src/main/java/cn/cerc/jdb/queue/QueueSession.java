package cn.cerc.jdb.queue;

import org.apache.log4j.Logger;

import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.model.Message;
import com.aliyun.mns.model.QueueMeta;

import cn.cerc.jdb.core.ISession;

public class QueueSession implements ISession {
	private static final Logger log = Logger.getLogger(QueueSession.class);

	public static final String AccessKeyId = "mns.accesskeyid";
	public static final String AccessKeySecret = "mns.accesskeysecret";
	public static final String AccountEndpoint = "mns.accountendpoint";
	public static final String SecurityToken = "mns.securitytoken";
	// IHandle中识别码
	public static String sessionId = "aliyunQueueSession";
	// 默认不可见时间
	private static int VisibilityTimeout = 50;
	private MNSClient client;

	public MNSClient getClient() {
		return client;
	}

	public void setClient(MNSClient client) {
		this.client = client;
	}

	@Override
	public void closeSession() {
		// client 为线程安全，不需要多实例
		if (client != null) {
			client = null;
		}
	}

	/**
	 * 
	 * @param queueCode
	 *            队列代码
	 * @return 返回具体的消息队列
	 */
	public CloudQueue openQueue(String queueCode) {
		return client.getQueueRef(queueCode);
	}

	/**
	 * 
	 * @param queueCode
	 *            队列代码
	 * @return 返回创建的队列
	 */
	public CloudQueue createQueue(String queueCode) {
		QueueMeta meta = new QueueMeta();
		meta.setQueueName(queueCode);
		meta.setPollingWaitSeconds(15);
		meta.setMaxMessageSize(2048L);
		return client.createQueue(meta);
	}

	/**
	 * 
	 * @param queue
	 *            消息队列
	 * @param content
	 *            消息内容
	 * @return 发送消息
	 */
	public boolean append(CloudQueue queue, String content) {
		Message message = new Message();
		message.setMessageBody(content);
		queue.putMessage(message);
		return true;
	}

	/**
	 * 
	 * @param queue
	 *            消息队列
	 * @return 请求接受消息
	 */
	public Message receive(CloudQueue queue) {
		Message msg = queue.popMessage();
		if (msg != null) {
			log.debug("消息内容：" + msg.getMessageBodyAsString());
			log.debug("消息编号：" + msg.getMessageId());
			log.debug("访问代码：" + msg.getReceiptHandle());
		} else {
			log.debug("msg is null");
		}
		return msg;
	}

	/**
	 * 删除消息
	 * 
	 * @param queue
	 *            队列
	 * @param msgId
	 *            消息Id
	 */
	public void delete(CloudQueue queue, String msgId) {
		queue.deleteMessage(msgId);
	}

	/**
	 * 
	 * @param queue
	 *            队列
	 * @return 检查消息
	 */
	public Message peek(CloudQueue queue) {
		return queue.peekMessage();
	}

	/**
	 * 延长消息不可见时间
	 * 
	 * @param queue
	 *            队列
	 * @param msgId
	 *            消息Id
	 */
	public void changeVisibility(CloudQueue queue, String msgId) {
		// 第一个参数为旧的ReceiptHandle值，第二个参数为新的不可见时间（VisibilityTimeout）
		String newId = queue.changeMessageVisibilityTimeout(msgId, VisibilityTimeout);
		log.debug("新的 msgId: " + newId);
	}

}
