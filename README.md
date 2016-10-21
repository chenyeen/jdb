# jdb 项目简介：
创建用于java使用的数据表操作工具，其核心为DataSet类，用于建立内存表。
基于DataSet所派生的DataQuery，为操作各类数据仓库的基类，其派生有：

### rds数据库操作：
* SqlQuery：用于操作mysql数据表，可取代hibernate/mybatis，特别适用各类组合条件查询，支持批处理与事务，也支持与hibernate互转。
* MssQuery：用于操作mssql数据表，可取代hibernate/mybatis，特别适用各类组合条件查询，支持批处理与事务，也支持与hibernate互转。

### nosql数据库操作：
* NasQuery：用于以类似mysql的操作方式，操作网络文件或本地文件，降低学习成本，以及由mysql迁移到nas的成本。
* MongoQuery：用于以类似mysql的操作方式，操作MongoDB数据，降低学习成本，以及由mysql迁移到mongo的成本。
* RedisQuery：用于以类似mysql的操作方式，操作Redis数据，降低学习成本，以及由mysql迁移到Redis的成本。

### 其它对象存储操作：
* OssQuery：用于以类似mysql的操作方式，操作aliyun-oss数据，降低学习成本，以及由mysql迁移到aliyun-oss的成本。
* CacheQuery：用于以类似mysql的操作方式，操作Memcached数据，降低学习成本，并方便与其它DataQuery结合。

### 专用数据操作：
* JsonQuery：用于以类似mysql的操作方式，操作json文件，适用于大量存档型数据处理，其实际存储将调用NasQuery或MongoQuery或RedisQuery。

欢迎大家使用，同时反馈更多的建议与意见，也欢迎其它业内人士，对此项目进行协同改进！

