package cn.cerc.jdb.queue;

import org.apache.commons.lang.StringUtils;

import com.aliyun.mns.client.CloudQueue;

import cn.cerc.jdb.core.DataQuery;
import cn.cerc.jdb.core.IDataOperator;
import cn.cerc.jdb.core.IHandle;
import cn.cerc.jdb.core.Record;

/**
 * 阿里云mns消息队列操作类
 * 
 * @Description
 * @author rick_zhou
 * @date 2016年10月24日 上午11:52:56
 */
public class QueueQuery extends DataQuery {
	private IHandle handle;
	private QueueSession session;
	// 要操作的队列
	private CloudQueue queque;
	// 查询语句
	private StringBuffer queryStr = new StringBuffer();

	private String topicName;

	/** @Fields serialVersionUID: */

	private static final long serialVersionUID = 1L;

	public QueueQuery(IHandle handle) {
		super();
		this.handle = handle;
		session = (QueueSession) this.handle.getProperty(QueueSession.sessionId);
	}

	@Override
	public DataQuery open() {
		// get collName and business_id
		String from = null;// from 关键字
		// 字符串截取获取collName和business_id的值
		try {
			from = "from";
			this.topicName = queryStr.toString().substring(queryStr.indexOf(from) + from.length()).trim();
		} catch (Exception e) {
			throw new RuntimeException("语法为 tableName.business_id");
		}
		// 校验数据
		if (StringUtils.isEmpty(this.topicName))
			throw new RuntimeException("请输入队列名称");
		this.queque = this.session.openQueue(this.topicName);
		return this;
	}

	@Override
	public void save() {
		// 将json作为消息内容进行发送
		String msgBody = this.getJSON();
		this.session.append(this.queque, msgBody);
		log.info("消息发送成功,消息内容为:" + msgBody);
	}

	/**
	 * 获取消息
	 * 
	 * @Description
	 * @author rick_zhou
	 * @return
	 */
	public void getMsg() {
		String json = this.session.receive(this.queque).getMessageBody();
		this.setJSON(json);
	}

	/**
	 * 删除消息(暂不提供)
	 * 
	 * @Description
	 * @author rick_zhou
	 * @param msgId
	 */
	/*
	 * public void delete(String msgId) { this.session.delete(this.queque,
	 * msgId); }
	 */

	public void delete() {
		throw new RuntimeException("本方法不提供服务,禁止调用");
	}

	@Override
	public IDataOperator getOperator() {
		return (new IDataOperator() {
			@Override
			public boolean insert(Record record) {
				throw new RuntimeException("本方法不提供服务,禁止调用");
			}

			@Override
			public boolean update(Record record) {
				throw new RuntimeException("本方法不提供服务,禁止调用");
			}

			@Override
			public boolean delete(Record record) {
				throw new RuntimeException("本方法不提供服务,禁止调用");
			}
		});
	}

	/**
	 * 拼接查询语句
	 * 
	 * @Description
	 * @author rick_zhou
	 * @param queryString
	 * @return
	 */
	public QueueQuery add(String queryString) {
		if (queryStr.length() == 0)
			queryStr.append(queryString);
		else
			queryStr.append(" ").append(queryString);
		return this;
	}

	/**
	 * 替换拼接查询语句
	 * 
	 * @Description
	 * @author rick_zhou
	 * @param format
	 * @param args
	 * @return
	 */
	public QueueQuery add(String format, Object... args) {
		return this.add(String.format(format, args));
	}
}
