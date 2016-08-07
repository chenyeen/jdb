package cn.cerc.jdb.core;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class TableOperationTest {
	private StubConnection conn = new StubConnection();
	private TableOperation obj;

	@Before
	public void setUp() throws Exception {
		obj = new TableOperation(conn.getConnection());
	}

	@Test
	public void test_insert() {
		Record record = new Record();
		record.setField("Code_", "AAA");
		obj.setPreview(true);
		obj.setTableName("Dept");
		obj.insert(record);
	}

	@Test
	public void test_update() {
		Record record = new Record();
		record.setState(DataSetState.dsEdit);
		record.setField("Code_", "AAA");
		obj.setPreview(true);
		obj.setTableName("Dept");
		obj.update(record);
	}

	@Test
	public void test_delete() {
		Record record = new Record();
		record.setField("Code_", "AAA");
		obj.setPreview(true);
		obj.setTableName("Dept");
		obj.delete(record);
	}

	@Test
	public void test_findTableName() {
		String sql = "select * from Dept";
		assertEquals(obj.findTableName(sql), "Dept");
		sql = "select * from \r\n Dept";
		assertEquals(obj.findTableName(sql), "Dept");
		sql = "select * from \r\nDept";
		assertEquals(obj.findTableName(sql), "Dept");
		sql = "select * from\r\n Dept";
		assertEquals(obj.findTableName(sql), "Dept");
		sql = "select * FROM Dept";
		assertEquals(obj.findTableName(sql), "Dept");
	}

}
