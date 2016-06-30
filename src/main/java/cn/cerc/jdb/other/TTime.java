package cn.cerc.jdb.other;

import cn.cerc.jdb.core.TDateTime;

@Deprecated
public class TTime extends TDateTime {
	private static final long serialVersionUID = 1L;

	public TTime(java.util.Date date) {
		this.setData(date);
	}

	@Override
	public String toString() {
		return this.getTime();
	}

}
