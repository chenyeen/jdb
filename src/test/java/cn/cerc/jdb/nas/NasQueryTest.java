package cn.cerc.jdb.nas;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import cn.cerc.jdb.core.StubHandle;

public class NasQueryTest {

	private static final Logger log = Logger.getLogger(NasQueryTest.class);
	private NasQuery ds;
	private StubHandle handle;

	@Before
	public void setUp() {
		handle = new StubHandle();
		ds = new NasQuery(handle);
	}

	/**
	 * 保存文件/覆盖文件
	 * 
	 * @Description
	 * @author rick_zhou
	 */
	@Test
	public void saveFile() {
		/*
		 * ds.add("select test.txt from %s",
		 * appdb.get(handle,appdb.NAS_FOLDER));
		 */
		ds.add("select test.txt from %s", "D://testFolder1/testFolder2");
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
	public void findFile() {
		/*
		 * ds.add("select test.txt from %s",
		 * appdb.get(handle,appdb.NAS_FOLDER));
		 */
		ds.add("select test.txt from %s", "D://testFolder1/testFolder2");
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
		 * ds.add("select test.txt from %s",
		 * appdb.get(handle,appdb.NAS_FOLDER));
		 */
		ds.add("select test.txt from %s", "D://testFolder1/testFolder2");
		ds.open();
		ds.delete();
	}

}
