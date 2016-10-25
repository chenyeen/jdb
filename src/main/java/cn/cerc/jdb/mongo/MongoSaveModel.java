package cn.cerc.jdb.mongo;

public enum MongoSaveModel {
	/**
	 * 压缩模式,会将dataSet的body部分数据采用压缩算法
	 */
	reduce,
	/**
	 * key-value模式,不会压缩,方便在mongodb中进行查询,但是会消耗更多的存储空间
	 */
	keyValue
}
