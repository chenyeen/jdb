package cn.cerc.jdb.nas;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;

import cn.cerc.jdb.core.DataQuery;
import cn.cerc.jdb.core.IDataOperator;
import cn.cerc.jdb.core.IHandle;
import cn.cerc.jdb.core.Record;

public class NasQuery extends DataQuery {
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unused")
	private IHandle handle;
	// 文件目录
	private String filePath;
	// 查询语句
	private StringBuffer queryStr = new StringBuffer();
	// 文件名称
	private String fileName;

	public NasQuery(IHandle handle) {
		this.handle = handle;
	}

	@Override
	public DataQuery open() {
		// 字符串截取获取collName和business_id的值
		try {
			this.fileName = queryStr.substring(queryStr.indexOf("select") + 6, queryStr.indexOf("from")).trim();
			this.filePath = queryStr.substring(queryStr.indexOf("from") + 4).trim();
		} catch (Exception e) {
			throw new RuntimeException("语法为: select fileName from filePath");
		}
		// 校验数据
		if (StringUtils.isEmpty(this.filePath))
			throw new RuntimeException("请输入文件路径");
		return this;
	}

	/**
	 * 查询文件
	 * 
	 * @Description
	 * @author rick_zhou
	 * @return
	 */
	public String getFileContext() {
		File file = FileUtils.getFile(this.filePath, this.fileName);
		String fileContext = "";
		try {
			fileContext = FileUtils.readFileToString(file, CharEncoding.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileContext;
	}

	/**
	 * 保存或更新文件
	 * 
	 * @Description
	 * @author rick_zhou
	 * @param fileContext
	 */
	public void save(String fileContext) {
		File file = FileUtils.getFile(this.filePath, this.fileName);
		try {
			FileUtils.writeStringToFile(file, fileContext, CharEncoding.UTF_8, false);// 不存在则创建,存在则不追加到文件末尾
		} catch (IOException e) {
			log.info("文件:" + file.getPath() + "保存失败");
			e.printStackTrace();
		}
		log.info("文件:" + file.getPath() + "保存成功");
	}

	/**
	 * 删除文件或目录 Description
	 * 
	 * @see cn.cerc.jdb.core.CustomDataSet#delete()
	 */
	@Override
	public void delete() {
		File file = FileUtils.getFile(this.filePath, this.fileName);
		FileUtils.deleteQuietly(file);
		log.info("文件:" + file.getPath() + "删除成功");
	}

	@Deprecated
	@Override
	public void save() {
		throw new RuntimeException("本方法不提供服务,请使用save(String fileContext)");
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
	public NasQuery add(String queryString) {
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
	public NasQuery add(String format, Object... args) {
		return this.add(String.format(format, args));
	}

}
