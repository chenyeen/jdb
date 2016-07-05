package cn.cerc.jdb.core;

import java.util.Iterator;

public class CustomDataSetIteratior implements Iterator<Record> {
	private CustomDataSet ds;
	private int recNo = 0;

	public CustomDataSetIteratior(CustomDataSet customDataSet) {
		this.ds = customDataSet;
		recNo = 0;
	}

	@Override
	public boolean hasNext() {
		return ds.size() > 0 && recNo < ds.size();
	}

	@Override
	public Record next() {
		recNo++;
		ds.setRecNo(recNo);
		return ds.getCurrent();
	}

}
