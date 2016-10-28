package cn.cerc.jdb.mongo;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import cn.cerc.jdb.core.StubHandle;
import cn.cerc.jdb.mysql.SqlQuery;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
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
	 * 压缩保存
	 * 
	 */
	@Test
	public void test1() {
		// 压缩数据
		mongoDs.add("select * from %s", "s_mgToSql.key1");
		mongoDs.open(); // save dataset body data
		mongoDs.append();
		mongoDs.setField("c1", "v1");
		mongoDs.setField("c2", "v2");
		mongoDs.setField("c3", "v3");
		mongoDs.setField("c4", "v4");
		mongoDs.setField("c5", "v5");
		mongoDs.save(MongoSaveModel.reduce);
	}

	/**
	 * 将压缩保存的数据存储到mysql
	 */
	@Test
	public void test2() {
		// 查询mongo key1
		mongoDs.add("select * from %s", "s_mgToSql.key1");
		mongoDs.open();
		// 查询mysql key1
		sqlDs.add("select * from %s where UID_=%s", "s_mgToSql", "'key1'");
		sqlDs.open();
		log.info("查询到的sql数据:" + sqlDs.getJSON());
		if (sqlDs.eof()) {
			log.info("将mongo中的key1对应的压缩后数据倒入mysql key1");
			sqlDs.setJSON(mongoDs.getJSON());
			sqlDs.post();
		}
	}

	/**
	 * 非压缩保存
	 */
	@Test
	public void test3() {
		// 非压缩数据
		mongoDs.add("select * from %s", "s_mgToSql.key2");
		mongoDs.open(); // save dataset body data
		mongoDs.append();
		mongoDs.setField("c1", "v1");
		mongoDs.setField("c2", "v2");
		mongoDs.setField("c3", "v3");
		mongoDs.setField("c4", "v4");
		mongoDs.setField("c5", "v5");
		mongoDs.save(MongoSaveModel.keyValue);
	}

	/**
	 * 将非压缩保存的数据存储到mysql
	 */
	@Test
	public void test4() {
		// 查询mongo key1
		mongoDs.add("select * from %s", "s_mgToSql.key2");
		mongoDs.open();
		// 查询mysql key1
		sqlDs.add("select * from %s where UID_=%s", "s_mgToSql", "'key2'");
		sqlDs.open();
		log.info("查询到的sql数据:" + sqlDs.getJSON());
		if (sqlDs.eof()) {
			log.info("将mongo中的key2对应的非压缩数据倒入mysql key2");
			sqlDs.setJSON(mongoDs.getJSON());
			sqlDs.post();
		}
	}

}
