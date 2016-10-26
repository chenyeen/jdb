package cn.cerc.jdb.sms;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.cerc.jdb.core.CustomDataSet;
import cn.cerc.jdb.core.DataQuery;
import cn.cerc.jdb.core.IDataOperator;
import cn.cerc.jdb.core.IHandle;
import cn.cerc.jdb.core.Record;

public class SmsQuery extends DataQuery {

	private static final long serialVersionUID = -7094700131251825174L;

	public static final String TEMPLATE = "template";

	private SmsSession smsSession = null;
	private boolean validate = false;
	private String phoneNumber = "";

	public SmsQuery(IHandle handle) {
		super(handle);
		Object object = handle.getProperty(SmsSession.sessionId);
		if (object == null)
			throw new RuntimeException("请先在application.xml配置" + SmsSession.sessionId);
		smsSession = (SmsSession) object;
	}

	public SmsQuery add(String format, String phoneNumber) {
		if (!format.equals("select * from %s"))
			throw new RuntimeException("format必须固定为select * from %s");
		this.phoneNumber = phoneNumber;
		return this;
	}

	@Override
	public DataQuery open() {
		validate = isMobile(phoneNumber);
		if (!validate)
			throw new RuntimeException("手机号码格式不正确，请检查");
		return this;
	}

	@Override
	public CustomDataSet append() {
		if (validate)
			return super.append();
		throw new RuntimeException("手机号码格式不正确，请检查");
	}

	@Override
	public void save() {
		throw new RuntimeException("请不要调用此方法");
	}

	@Override
	public IDataOperator getOperator() {
		return null;
	}

	/**
	 * 默认执行最后一次setField的数据;
	 */
	public void post() {
		if (getRecords().isEmpty())
			throw new RuntimeException("请先append()数据，并且设置模版编码" + TEMPLATE + "和模版里面的填充数据");
		Record record = getRecords().get(getRecords().size() - 1);
		getRecords().clear();
		String template = record.getString(TEMPLATE);
		String content = SmsTemplateUtil.getContentByTemplate(template);
		content = renderString(content, record);
		smsSession.getSmsConnection().sendMessage(phoneNumber, content);
	}

	private static boolean isMobile(String str) {
		Pattern p = null;
		Matcher m = null;
		boolean b = false;
		p = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$"); // 验证手机号
		m = p.matcher(str);
		b = m.matches();
		return b;
	}

	/**
	 * 根据键值对填充字符串 输出：
	 * 
	 * @param content
	 * @param map
	 * @return
	 */
	private static String renderString(String content, Record record) {
		for (String key : record.getItems().keySet()) {
			String regex = "\\$\\{" + key + "\\}";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(content);
			content = matcher.replaceAll(record.getString(key));
		}
		return content;
	}
}
