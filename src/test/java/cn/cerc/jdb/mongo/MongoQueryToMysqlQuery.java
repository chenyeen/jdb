package cn.cerc.jdb.mongo;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import cn.cerc.jdb.core.StubHandle;
import cn.cerc.jdb.mysql.SqlQuery;

public class MongoQueryToMysqlQuery {
	private static final Logger log = Logger.getLogger(MongoSession.class);

	private MongoQuery mongoDs;
	private SqlQuery sqlDs;
	private StubHandle handle;

	@Before
	public void setUp() {
		handle = new StubHandle();
		mongoDs = new MongoQuery(handle);
		sqlDs = new SqlQuery(handle);
	}

	@After
	public void closeSession() {
		mongoDs.sessionClose();
	}

	/**
	 * 非压缩保存
	 * 
	 * @Description
	 * @author rick_zhou
	 */
	@Test
	public void add() {
		mongoDs.add("select * from %s", "s_mgToSql.key1");
		mongoDs.open(); // save dataset body data
		mongoDs.append();
		mongoDs.setField("c1", "v1");
		mongoDs.setField("c2", "v2");
		mongoDs.setField("c3", "v3");
		mongoDs.setField("c4", "v4");
		mongoDs.setField("c5", "v5");
		mongoDs.save(MongoSaveModel.keyValue);

		sqlDs.add("select * from %s where business_id=%s", "s_mgToSql", "'key1'");
		sqlDs.open();
		sqlDs.append();
		sqlDs.setField("business_id", "key1");
		sqlDs.setField("c1", "v1");
		sqlDs.setField("c2", "v2");
		sqlDs.setField("c3", "v3");
		sqlDs.setField("c4", "v4");
		sqlDs.setField("c5", "v5");
		sqlDs.setBatchSave(true);
		sqlDs.save();
	}

	@Test
	public void sqlQueryToMongoQuery() {
		mongoDs.add("select * from %s", "s_mgToSql.key1");
		mongoDs.open();
		sqlDs.setJSON(mongoDs.getJSON());
		log.info(sqlDs.getJSON());

	}

	@Test
	@Ignore
	public void delete() {
		mongoDs.add("select * from mongoqueryColl.testkey001");
		mongoDs.open();
		if (!mongoDs.eof())
			mongoDs.delete();
	}
}
