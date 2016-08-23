package cn.cerc.jdb.core;

public interface SyncDataSetProcess {
	public void process(Record src, Record tar) throws DataUpdateException;
}
