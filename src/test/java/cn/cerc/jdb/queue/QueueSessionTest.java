package cn.cerc.jdb.queue;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.model.Message;

import cn.cerc.jdb.core.StubConfig;
import cn.cerc.jdb.core.TDateTime;

public class QueueSessionTest {
	private static final Logger log = Logger.getLogger(QueueSessionTest.class);

	@Test
	public void testQueue() {
		QueueConnection conn = new QueueConnection();
		conn.setConfig(new StubConfig());
		QueueSession sess = conn.getSession();
		// 打开消息表
		CloudQueue queue = sess.openQueue("test");
		// 增加消息记录
		sess.append(queue, "hello world, sendTime: " + TDateTime.Now());
		while (true) {
			// 请求消息记录
			Message msg = sess.receive(queue);
			if (msg == null)
				break;
			// 由于网络延迟或者不可达的原因,消息很可能出现重复消费,编码或设计时需要考虑
			log.info("消息ID" + msg.getMessageId());
			log.info("消息主体" + msg.getMessageBody());
		}
	}
}
