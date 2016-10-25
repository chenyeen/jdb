package cn.cerc.jdb.queue;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import cn.cerc.jdb.core.StubHandle;
import cn.cerc.jdb.mongo.MongoSession;

public class QueueQueryTest {
	private static final Logger log = Logger.getLogger(MongoSession.class);

	private QueueQuery ds;
	private StubHandle handle;

	@Before
	public void setUp() {
		handle = new StubHandle();
		ds = new QueueQuery(handle);
	}

	/**
	 * 发送消息
	 * 
	 * @Description
	 * @author rick_zhou
	 */
	@Test
	public void sendMsg() {
		// ds.add("select * from %s", appdb.get(handle, appdb.MQ_TOPIC_NAME));
		ds.add("select * from test");
		ds.open();
		// save dataset head data
		ds.getHead().setField("queueHeadData1", "queueHeadData1");
		ds.getHead().setField("queueHeadData2", "queueHeadData2");
		ds.getHead().setField("queueHeadData3", "queueHeadData3");
		ds.getHead().setField("queueHeadData4", "queueHeadData4");
		// save dataset body data
		ds.append();
		ds.setField("queueBodyData1", "queueBodyData1");
		ds.setField("queueBodyData2", "queueBodyData2");
		ds.setField("queueBodyData3", "queueBodyData3");
		ds.setField("queueBodyData4", "queueBodyData4");
		ds.setField("queueBodyData5", "queueBodyData5");
		ds.save();

	}

	/**
	 * 获取一个队列中的一条消息(注意!这条消息有可能会被消费多次)
	 * 
	 * @Description
	 * @author rick_zhou
	 */
	@Test
	public void query() {
		// ds.add("select * from %s", appdb.get(handle, appdb.MQ_TOPIC_NAME));
		ds.add("select * from test");
		ds.open();// 获取消息

		// 获取消息中DataSet的head内容
		log.info(StringUtils.center("heand data", 70, "=="));
		log.info(ds.getHead().getField("queueHeadData1"));
		log.info(ds.getHead().getField("queueHeadData2"));
		log.info(ds.getHead().getField("queueHeadData3"));
		log.info(ds.getHead().getField("queueHeadData4"));
		log.info(StringUtils.center("body data", 70, "=="));
		log.info(ds.getField("queueBodyData1"));
		log.info(ds.getField("queueBodyData2"));
		log.info(ds.getField("queueBodyData3"));
		log.info(ds.getField("queueBodyData4"));
	}

}
