package cn.cerc.jdb.core;

public class SqlConnectionException extends Exception {
	private static final long serialVersionUID = 811290515551429913L;
	private String host;

	public SqlConnectionException(String message) {
		super(message);
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

}
