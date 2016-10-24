package cn.cerc.jdb.queue;

import org.junit.Test;

import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.model.Message;

import cn.cerc.jdb.core.StubConfig;
import cn.cerc.jdb.core.TDateTime;
import cn.cerc.jdb.queue.QueueConnection;
import cn.cerc.jdb.queue.QueueSession;

public class QueueSessionTest {

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
			System.out.println(msg.getMessageBody());
		}
	}
}
