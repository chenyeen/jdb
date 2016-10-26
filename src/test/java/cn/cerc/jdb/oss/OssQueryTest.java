package cn.cerc.jdb.oss;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import cn.cerc.jdb.core.StubHandle;

public class OssQueryTest {

	private static final Logger log = Logger.getLogger(OssQueryTest.class);
	private OssQuery ds;
	private StubHandle handle;

	@Before
	public void setUp() {
		handle = new StubHandle();
		ds = new OssQuery(handle);
	}

	@After
	public void closeSession() {
		ds.sessionClose();
	}

	/**
	 * 保存文件/覆盖文件
	 * 
	 * @Description
	 * @author rick_zhou
	 */
	@Test
	@Ignore
	public void saveFile() {
		/*
		 * ds.add("select id_00001 from %s",
		 * appdb.get(handle,appdb.bucketName));
		 */
		ds.add("select id_00001 from %s", "zrk-oss-test");
		ds.open();
		String fileContext = "一大串字符串................................................";
		ds.save(fileContext);
	}

	/**
	 * 获取文件内容
	 * 
	 * @Description
	 * @author rick_zhou
	 */
	@Test
	@Ignore
	public void findFile() {
		/*
		 * ds.add("select id_00001 from %s",
		 * appdb.get(handle,appdb.bucketName));
		 */
		ds.add("select id_00001 from %s", "zrk-oss-test");
		ds.open();
		log.info("查询文件");
		log.info("获取到的文件内容为:\n" + ds.getFileContext());
	}

	/**
	 * 删除文件
	 * 
	 * @Description
	 * @author rick_zhou
	 */
	@Test
	public void deleteFile() {
		/*
		 * ds.add("select id_00001 from %s",
		 * appdb.get(handle,appdb.bucketName));
		 */
		ds.add("select id_00001 from %s", "zrk-oss-test");
		ds.open();
		ds.delete();
	}

}
