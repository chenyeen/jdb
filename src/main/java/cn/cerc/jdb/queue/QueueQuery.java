package cn.cerc.jdb.queue;

import org.apache.commons.lang.StringUtils;

import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.model.Message;

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
		this.session.append(this.queque, this.getJSON());
	}

	@Override
	public Object getField(String field) {
		if (!"msg".equals(field)) {
			throw new RuntimeException("参数值只能是:msg");
		}
		return this.session.receive(this.queque).getMessageBody();
	}

	/**
	 * 少数情况下可能需要操作message对象,使用此方法
	 * 
	 * @Description
	 * @author rick_zhou
	 * @return
	 */
	public Message getField() {
		return this.session.receive(this.queque);
	}

	/**
	 * 删除消息
	 * 
	 * @Description
	 * @author rick_zhou
	 * @param msgId
	 */
	public void delete(String msgId) {
		this.session.delete(this.queque, msgId);
	}

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

}
