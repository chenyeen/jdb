package cn.cerc.jdb.core;

public interface Operator {

	public boolean insert(Record record);

	public boolean update(Record record);

	public boolean delete(Record record);

}
