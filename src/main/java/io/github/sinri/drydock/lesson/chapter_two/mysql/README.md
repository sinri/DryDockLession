# 2.2 我的MySQL不可能这么简单 之一

说起SQL，就是MySQL，这是中小企业的常见认知。话说回来，MySQL以其简单明了的玩法，确实搞定了大量业务。Keel体系针对MySQL也是进行了大量的工作，逐渐构成了自己的MySQL运用体系。

那么，让我们重温一下在第一章时下海见识过的东西，从数据源开始说起吧。

# 指名数据源与指名数据源连接

在Keel体系下，进行MySQL查询操作基于数据源连接。对于不同的MySQL数据库实例（有着不同的连接地址或者连接账号），其对应的数据源连接应当有区别以避免混淆，因此，在Keel体系中，针对一个MySQL数据库实例进行的操作，必须为之建立一个指名数据源连接，对应提供了抽象类`NamedMySQLConnection`
。该类是对底层的`io.vertx.sqlclient.SqlConnection`
类实例的封装，并要求给出其对应数据源的名称以示区别（实现抽象方法`String getDataSourceName()`）。

指名数据源连接由相应的泛型指定为斯的指名数据源维护其连接池：`class NamedMySQLDataSource<C extends NamedMySQLConnection>`
。指名数据源的建立，除利用该类的构造函数配合MySQL数据源配置类`KeelMySQLConfiguration`
进行生成外，一般都可以通过类`KeelMySQLDataSourceProvider`的静态方法们基于Keel的统一配置内容来创建。这些操作在上一章已经给出了例子，这里就不多说了。

## 动态指名数据源连接

在某些时候，为了个一次性任务什么的，要偷懒，可以直接使用`DynamicNamedMySQLConnection`
类，这是一个动态写入数据源名称的实现，免去自定义一个类重载`NamedMySQLConnection`的麻烦。

（好孩子应该规规矩矩搞工业化，这种小作坊的玩法等成仙了再用。）

## 快速单次查询之术

默认的指名数据源是以连接池为基础的。有时候，就是这个数据源连一下查完就散了，没有必要为其维护一个连接池。为此，在MySQL数据源配置类`KeelMySQLConfiguration`
中提供了`Future<ResultMatrix> instantQuery(String sql)`方法，调用时会直接利用Client模式查询SQL并返回结果，然后直接关闭Client。

关于`ResultMatrix`的使用后面再说。

（这个妖术被~~广泛~~应用在动态按需连接数据库的场景中，日常场景用不到。）

# 数据库查询结果的抽象化

进行一次MySQL查询，首先在客户端视角需要看执行是否成功（是否超时、是否报错等）；通过异步方式查询数据库，如果其预期返回为错误，则无法观测到查询结果，而是直接处理异常。

对于成功时的结果，一般会注意到以下方面：

* 受影响的行数（对于增删改等操作）；

* 最后插入行的自增ID主键值（对于插入操作）；

* 获取到的行数（对于查询操作）；

* 获取到的结果（行集合）。

查询结果中的行集合可以进一步抽象为一个二维矩阵，其每一列具备同样的查询预期和返回格式要求。

## 结果矩阵

综上，Keel体系内提供了`io.github.sinri.keel.mysql.matrix.ResultMatrix`作为数据库查询结果的统一封装。该类提供的常用方法有：

* `int getTotalFetchedRows()`获取到的总行数

* `int getTotalAffectedRows()`受影响的总行数

* `long getLastInsertedID()`最后插入的行的ID

* `JsonArray toJsonArray()`将获取到的行集合转为JsonArray

* `List<JsonObject> getRowList()`将获取到的行集合转为一个列表，其元素为每一行的JsonObject表达

* `JsonObject getFirstRow() throws KeelSQLResultRowIndexError`获取第一行的JsonObject表达（不存在时抛出异常）

* `JsonObject getRowByIndex(int var1) throws KeelSQLResultRowIndexError`根据行次序（从0开始）获取相应行的JsonObject表达（不存在时抛出异常）

* `getOneColumnOfFirstRowAs*() throws KeelSQLResultRowIndexError`以特定格式获取第一行的某一列的值（行不存在时抛出异常）

* `List getOneColumnAs*()`以特定格式获取每一行的某一列的值并组装成列表

* `<K> Future<Map<K, JsonObject>> buildUniqueKeyBoundRowMap(Function<JsonObject, K> uniqueKeyGenerator)`
  将行集合中的每一行通过`uniqueKeyGenerator`方法得到一个值，以该值为键以该行为值组装成一个Map

* `<K, T extends ResultRow> Future<Map<K, T>> buildUniqueKeyBoundRowMap(Class<T> classOfTableRow, Function<T, K> uniqueKeyGenerator)`
  将行集合中的每一行转化为一个行的类抽象，再通过`uniqueKeyGenerator`方法得到一个值，以该值为键以该行对应类实例为值组装成一个Map

* `<K, T extends ResultRow> Future<Map<K, List<T>>> buildCategorizedRowsMap(Class<T> classOfTableRow, Function<T, K> categoryGenerator)`
  将行集合中的每一行转化为一个行的类抽象，并运用`categoryGenerator`方法得到一个分类；以分类为键，将这一分类对应的所有行的类实例组成列表为值，组装成一个Map

* `<K, V> Future<Map<K, V>> buildCustomizedMap(BiConsumer<Map<K, V>, JsonObject> rowToMapHandler)`
  生成一个空Map，对每一行的JsonObject表达执行`rowToMapHandler`以按需更新这个Map，最后返回这个丰容完的Map

* `Future<List<JsonObject>> buildShrinkList(Collection<String> shrinkByKeys, String shrinkBodyListKey)`
  通过以下方式收缩矩阵。对每一行的JsonObject表达，根据`shrinkByKeys`
  中的列们作为联合主键，将同一联合主键的行们的非主键列们构成一个JsonArray形式的子矩阵，并作为`shrinkBodyListKey`
  键的值附加到联合主键对应的行中。

在轻快帆船夕张号的案例中，我们已经见识过了结果矩阵的运用，很好。

## 结果行

上面的方法中已经提到了`ResultRow`这个接口，其继承了`JsonifiableEntity`
类因而具备了以各种格式读取内含JsonObject对象的能力，同时还针对MySQL的日期时间格式进行了一些特殊读取支援。

类`SimpleResultRow`实现了这个接口。

## 表的结果行的抽象

实务中存在大量读取表的一部分行的场景。因此，对于实体化设计的表，整行获取以建立或充实实体类是一个常规操作。因此基于结果行的定义，将特定表的整行抽象为了一个抽象类`io.github.sinri.keel.mysql.matrix.AbstractTableRow`
。这个类在继承类`SimpleResultRow`之上，定义了`String sourceSchemaName()`方法和`String sourceTableName()`
方法以验证表名。通常，该类中还可以针对每一个列的名称和类型提供读取方法，以方便业务中的实体渲染。

## 自动基于数据库表更新表的结果行类源代码文件

DryDock基于Keel提供的`io.github.sinri.keel.mysql.dev.TableRowClassSourceCodeGenerator`
工具，封装好了`io.github.sinri.drydock.naval.raider.ClassFileGeneratorForMySQLTables`类。下面给出一个运用的例子。源代码和运行效果可自行实践体验。

1. 建立一个包用于安置生成的表的结果行类们，如`io.github.sinri.drydock.lesson.chapter_two.mysql.tables`。

2. 在配置文件中定义好以`drydock_lesson`为名的数据源配置，可按照轻快帆船篇的例子如法炮制。同时需要追加一行以将（1）中的包对应的绝对路径设置进去。

    table.package.path=/absulte/path/to/src/main/java/io/github/sinri/drydock/lesson/chapter_two/mysql/tables

3. 建立`NamedMySQLConnection`类的实现类`LessonMySQLConnection`，可按照轻快帆船篇的例子如法炮制。

4. 在对应的MySQL数据库的Schema`drydock_lesson`中，建立表，如

    -- drydock_lesson.organization definition
    
    CREATE TABLE `organization` (
      `organization_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
      `organization_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
      `organization_type` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Enum{HOST,PARASITE}',
      `organization_status` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Enum{ON,OFF}',
      `create_time` datetime NOT NULL,
      `update_time` datetime NOT NULL,
      PRIMARY KEY (`organization_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
    
    -- drydock_lesson.account definition
    
    CREATE TABLE `account` (
      `account_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '账户的唯一识别号码',
      `account_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '账户的登入名',
      `display_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '账户的显示名',
      `email` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '账户的邮箱',
      `password_hash` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码的哈希值',
      `account_status` varchar(32) NOT NULL COMMENT 'Enum{ON,OFF}',
      PRIMARY KEY (`account_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
    
    -- drydock_lesson.`session` definition
    
    CREATE TABLE `session` (
      `session_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
      `token` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
      `account_id` bigint(20) NOT NULL,
      `start_time` int(11) NOT NULL,
      `end_time` int(11) NOT NULL,
      PRIMARY KEY (`session_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

5. 在Maven体系下的src/test区构建`ClassFileGeneratorForMySQLTables`
   的实现类，如`io.github.sinri.drydock.lesson.test.chapter_two.mysql.MySQLSourceCodeFileGen`。现在只需要关心下面两条，其他周边可观赏源代码。

    1. 实现`String getTablePackage()`方法，使之返回（1）中的包名。

    2. 构建一个`@TestUnit`注解加持的`Future<Void> generateClassFiles()`
       方法，为每个schema（本例只有一个）调用`rebuildTablesInSchema`方法进行全库表映射类文件构建。

6. 运行本类，输出成功后可以在（1）中的包里见到生成的表对应的结果行类文件。在工业化生产中，通过本方法自动生成的类仅应通过再次运行本类来覆盖，不应手动改写其内容；但当数据库中表被移除时，需要手动删除本类。