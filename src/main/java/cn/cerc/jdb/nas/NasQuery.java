package cn.cerc.jdb.nas;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;

import cn.cerc.jdb.core.DataQuery;
import cn.cerc.jdb.core.IHandle;
import cn.cerc.jdb.queue.QueueOperator;

public class NasQuery extends DataQuery {
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unused")
	private IHandle handle;
	// 文件目录
	private String filePath;
	// 文件名称
	private String fileName;
	private QueueOperator operator;
	private NasModel nasMode = NasModel.create;

	public NasQuery(IHandle handle) {
		super(handle);
		this.handle = handle;
	}

	@Override
	public DataQuery open() {
		try {
			this.fileName = this.getCommandText()
					.substring(this.getCommandText().indexOf("select") + 6, this.getCommandText().indexOf("from"))
					.trim();
			this.filePath = getOperator().findTableName(this.getCommandText());
		} catch (Exception e) {
			throw new RuntimeException("语法为: select fileName from filePath");
		}
		// 校验数据
		if (StringUtils.isEmpty(this.filePath))
			throw new RuntimeException("请输入文件路径");
		if (nasMode == NasModel.readWrite) {
			File file = FileUtils.getFile(this.filePath, this.fileName);
			try {
				this.setJSON(FileUtils.readFileToString(file, CharEncoding.UTF_8));
				this.setActive(true);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return this;
	}

	// 删除文件或目录
	@Override
	public void delete() {
		File file = FileUtils.getFile(this.filePath, this.fileName);
		FileUtils.deleteQuietly(file);
		log.info("文件:" + file.getPath() + "删除成功");
	}

	@Override
	public void save() {
		File file = FileUtils.getFile(this.filePath, this.fileName);
		try {
			String content = this.getJSON();
			FileUtils.writeStringToFile(file, content, CharEncoding.UTF_8, false);// 不存在则创建,存在则不追加到文件末尾
		} catch (IOException e) {
			log.info("文件:" + file.getPath() + "保存失败");
			e.printStackTrace();
		}
		log.info("文件:" + file.getPath() + "保存成功");
	}

	@Override
	public QueueOperator getOperator() {
		if (operator == null)
			operator = new QueueOperator();
		return operator;
	}

	public NasModel getNasMode() {
		return nasMode;
	}

	public void setNasMode(NasModel nasMode) {
		this.nasMode = nasMode;
	}

}
