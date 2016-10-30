package cn.cerc.jdb.mongo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.bson.Document;
import org.bson.types.ObjectId;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

import cn.cerc.jdb.core.DataQuery;
import cn.cerc.jdb.core.IDataOperator;
import cn.cerc.jdb.core.IHandle;
import cn.cerc.jdb.core.Record;

public class MongoQuery extends DataQuery {
	private static final long serialVersionUID = 1L;
	// private static final Logger log = Logger.getLogger(MongoDataQuery.class);
	private IHandle handle;
	private MongoSession session = null;

	private String collName;
	private String businessIdValue = null;
	private final String businessId = "business_id";
	private String _id = null;// mongodb object id
	private boolean isUpdate = false;

	public MongoQuery(IHandle handle) {
		super(handle);
		this.handle = handle; // 暂时无用
		session = (MongoSession) this.handle.getProperty(MongoSession.sessionId);
	}

	@Override
	public DataQuery open() {
		// get collName and business_id
		String afterFromStr = null;// from之后的字符串
		String[] arr = null;// arr[0] collName arr[1] business_id value
		this.isUpdate = false;

		// 字符串截取获取collName和business_id的值
		try {
			String from = "from";
			String queryStr = this.getCommandText();
			afterFromStr = queryStr.toString().substring(queryStr.indexOf(from) + from.length()).trim();
			arr = afterFromStr.split("\\.");
		} catch (Exception e) {
			throw new RuntimeException("语法为 tableName.business_id");
		}
		// 校验数据
		if (StringUtils.isEmpty(arr[0]))
			throw new RuntimeException("请输入表名");
		if (StringUtils.isEmpty(arr[1]))
			throw new RuntimeException("请输入字段名");

		// 设置本次open操作对应的操作coll和业务id值
		this.collName = arr[0];
		this.businessIdValue = arr[1];

		// 查找业务ID对应的数据
		MongoCollection<Document> coll = session.getDatabase().getCollection(arr[0]);
		log.info("打开数据表" + arr[0]);
		BasicDBObject filter = new BasicDBObject(businessId, arr[1]);
		Document res = null;
		ArrayList<Document> list = coll.find(filter).into(new ArrayList<Document>());
		// 数据不存在,则状态不为更新,并返回一个空数据
		if (list == null || list.isEmpty()) {
			this.isUpdate = false;
			this.setJSON("{}");
			return this;
		}
		// 数据存在多条,则状态不为更新,并抛出一个异常
		if (list.size() > 1) {
			this.isUpdate = false;
			throw new RuntimeException("返回了不止一条数据");
		}
		// 数据存在,则状态为更新
		res = list.get(0);
		// 记录查询后记录业务ID
		this.businessIdValue = res.get(businessId).toString();
		// 状态为更新
		this.isUpdate = true;
		// 记录mongodb 自生成ID
		this._id = res.get("_id").toString();
		res.remove(businessId);
		res.remove("_id");
		// mongodb document to data set
		// this.setJSON(res.toJson());
		log.info("查询到的数据为:" + res.toJson());
		if ("reduce".equals(res.getString("MongoSaveModel"))) {// 压缩还原
			res.remove("MongoSaveModel");
			this.setJSON(res.toJson());
			log.info("压缩还原后的数据为:" + this.getJSON());
		} else if ("keyValue".equals(res.getString("MongoSaveModel"))) {// keyvalue还原
			res.remove("MongoSaveModel");
			this.setBusJson(res.toJson());
			log.info("keyVlaue还原后的数据为:" + this.getJSON());
		}

		return this;
	}

	public void save(MongoSaveModel model) {
		if (model == MongoSaveModel.reduce) {// 压缩保存
			Document doc = Document.parse(super.getJSON());
			doc.put("MongoSaveModel", "reduce");
			saveJson(doc);
		} else if (model == MongoSaveModel.keyValue) {// kevy-value形式保存
			Document doc = Document.parse(this.getBusJson());
			doc.put("MongoSaveModel", "keyValue");
			saveJson(doc);
		}
	}

	private void saveJson(Document doc) {
		doc.append(businessId, businessIdValue);
		// 执行流程为 open --> save/delete
		if (StringUtils.isBlank(collName))
			throw new RuntimeException("请先执行 open()方法");

		MongoCollection<Document> coll = session.getDatabase().getCollection(collName);
		if (this.isUpdate) {// update
			UpdateResult res = coll.replaceOne(Filters.eq("_id", new ObjectId(this._id)), doc);// 通过自生成id替换
			log.info("修改了" + res.getModifiedCount() + "条数据" + doc.toJson());
		} else {// insert
			coll.insertOne(doc);
			log.info("添加数据成功" + doc.toJson());
		}
	}

	public void delete() {
		// 执行流程为 open --> save/delete
		if (StringUtils.isBlank(collName))
			throw new RuntimeException("请先执行 open()方法");
		MongoCollection<Document> coll = session.getDatabase().getCollection(collName);
		Document filterDoc = new Document(businessId, this.businessIdValue);// 通过业务id查找
		DeleteResult res = coll.deleteOne(filterDoc);
		log.info("删除了" + res.getDeletedCount() + "条数据");
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

	private String getBusJson() {
		Map<String, Object> jsonMap = new HashMap<String, Object>();
		jsonMap.put("head", this.getHead().getItems());
		List<Map<String, Object>> data = new ArrayList<>();
		for (Record record : this.getRecords()) {
			data.add(record.getItems());
		}
		jsonMap.put("data", data);
		return new Gson().toJson(jsonMap);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void setBusJson(String json) {
		Gson gson = new Gson();
		HashMap jsonMap = gson.fromJson(json, HashMap.class);
		Map<String, Object> head = (Map<String, Object>) jsonMap.get("head");
		// 将mongodb的head数据装换为dataset的head数据
		for (String key : head.keySet()) {
			this.getHead().setField(key, head.get(key));
		}
		// 将mongodb查出的data数据装换为list<record>
		List<Map<String, String>> data = (List<Map<String, String>>) jsonMap.get("data");
		for (Map<String, String> map : data) {
			this.append();
			for (String key : map.keySet()) {
				this.setField(key, map.get(key));
			}
		}
		// 将list<record> 装换为DataSet的records
		this.setRecNo(this.getRecords().size());// 修改当前records元素总数
	}

	@Deprecated
	@Override
	public void save() {
		throw new RuntimeException("本方法不提供服务,请使用save(MongoSaveModel model)");
	}

	public void sessionClose() {
		this.session.closeSession();
	}
}
