package cn.cerc.jdb.mongo;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import cn.cerc.jdb.core.Record;
import cn.cerc.jdb.core.StubHandle;

public class MongoQueryProduceTest {
	private static final Logger log = Logger.getLogger(MongoSession.class);

	private MongoQuery ds;
	private StubHandle handle;

	@Before
	public void setUp() {
		handle = new StubHandle();
		ds = new MongoQuery(handle);
	}

	/**
	 * 压缩保存
	 * 
	 * @Description
	 * @author rick_zhou
	 */
	@Test
	public void reduceAdd() {
		ds.add("select * from mongoqueryColl.testkey002");
		ds.open();
		// save dataset head data
		ds.getHead().setField("headdata1", "headdata1-reduce");
		ds.getHead().setField("headdata2", "headdata2-reduce");
		ds.getHead().setField("headdata3", "headdata3-reduce");
		ds.getHead().setField("headdata4", "headdata4-reduce");
		// save dataset body data
		ds.append();
		ds.setField("myname", "zrk-reduce");
		ds.setField("myname1", "zrk1-reduce");
		ds.setField("myname2", "zrk2-reduce");
		ds.setField("myname3", "zrk3-reduce");
		ds.setField("myname4", "zrk4-reduce");
		ds.save(MongoSaveModel.reduce);
	}

	/**
	 * 压缩修改
	 * 
	 * @Description
	 * @author rick_zhou
	 */
	@Test
	@Ignore
	public void reduceUpdate() {
		ds.add("select * from mongoqueryColl.testkey002");
		ds.open();
		// udpate dataset head data
		ds.getHead().setField("headdata1", "headdata1_update-reduce");
		ds.getHead().setField("headdata2", "headdata2_update-reduce");
		ds.getHead().setField("headdata3", "headdata3_update-reduce");
		ds.getHead().setField("headdata4", "headdata4_update-reduce");
		// udpate dataset body data
		log.info("bof:" + ds.bof());
		log.info("eof:" + ds.eof());
		if (!ds.eof()) {
			ds.edit();
			ds.setField("myname", "zrk_update-reduce");
			ds.setField("myname1", "zrk1_update-reduce");
			ds.setField("myname2", "zrk2_update-reduce");
			ds.setField("myname3", "zrk3_update-reduce");
			ds.setField("myname4", "zrk4_update-reduce");
		}
		ds.save(MongoSaveModel.reduce);// 非压缩存储

	}

	@Test
	public void reduceQuery() {
		log.info(StringUtils.center("查询压缩数据(与非压缩数据在查询操作上没有任何区别)", 70, "=="));
		ds.add("select * from mongoqueryColl.testkey002");
		ds.open();
		log.info("查询heand数据");
		log.info(ds.getHead().getField("headdata1"));
		log.info(ds.getHead().getField("headdata2"));
		log.info(ds.getHead().getField("headdata3"));
		log.info(ds.getHead().getField("headdata4"));
		log.info("查询body数据");
		for (Record rs : ds) {
			log.info("_id:" + rs.getString("_id"));
			log.info("personId:" + rs.getString("myname"));
			log.info("personId:" + rs.getString("myname1"));
			log.info("personId:" + rs.getString("myname2"));
			log.info("personId:" + rs.getString("myname3"));
			log.info("personId:" + rs.getString("myname4"));
		}
	}

	@Test
	@Ignore
	public void delete() {
		ds.add("select * from mongoqueryColl.testkey001");
		ds.open();
		if (!ds.eof())
			ds.delete();
	}
}
