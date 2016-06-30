package cn.cerc.jdb.core;

public abstract class DataSetNotifyEvent {
	public CustomDataSet dataset;

	public DataSetNotifyEvent(CustomDataSet dataset) {
		this.dataset = dataset;
	}

	abstract public void execute(CustomDataSet dataset);

}
