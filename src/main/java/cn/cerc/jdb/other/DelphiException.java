package cn.cerc.jdb.other;

import java.io.Serializable;

public class DelphiException extends RuntimeException implements Serializable {
	private static final long serialVersionUID = 4284750366499233051L;

	public DelphiException(String msg) {
		super(msg);
	}

	public static DelphiException createFmt(String msg, Object... args) {
		String str = String.format(msg, args);
		DelphiException e = new DelphiException(str);
		return e;
	}

	public static RuntimeException Create(String msg) {
		return new DelphiException(msg);
	}

}
