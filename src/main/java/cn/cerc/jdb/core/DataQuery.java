package cn.cerc.jdb.core;

public abstract class DataQuery extends DataSet {
	private static final long serialVersionUID = 7316772894058168187L;
	// 批次保存模式，默认为post与delete立即保存
	private boolean batchSave = false;
	private String commandText;
	protected boolean active = false;
	protected IHandle handle;

	public DataQuery(IHandle handle) {
		this.handle = handle;
	}

	// 打开数据集
	public abstract DataQuery open();

	// 批量保存
	public abstract void save();

	// 返回保存操作工具
	public abstract IDataOperator getOperator();

	// 是否批量保存
	public boolean isBatchSave() {
		return batchSave;
	}

	public void setBatchSave(boolean batchSave) {
		this.batchSave = batchSave;
	}

	public DataQuery add(String sql) {
		if (commandText == null)
			commandText = sql;
		else
			commandText = commandText + " " + sql;
		return this;
	}

	public DataQuery add(String format, Object... args) {
		return this.add(String.format(format, args));
	}

	public void setCommandText(String sql) {
		this.commandText = sql;
	}

	public String getCommandText() {
		return this.commandText;
	}

	public boolean getActive() {
		return active;
	}

	public DataQuery setActive(boolean active) {
		this.active = active;
		return this;
	}

}
