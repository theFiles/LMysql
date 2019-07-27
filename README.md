# lmysql使用说明

---

> ### 简述：
> 基于 Jdk1.8+Mysql8+<font color="red" >**ljson**</font>，主要组成部分`Mysql`配置类、`LMysql`父类、`Select`查操作类、`Insert`增操作类、`Update`改操作类、`Delete`删操作类、`Query`sql执行类。（ps:暂时未封装case判断功能，敬请期待！）

<font color="red" >**重复提示：当前版本由项目需要修改后，需要依赖 ljson 运行**</font>
#### ljson的操作说明：
[ljson下载（当前版本lmysql以包含）][1]
[ljson使用说明][2]

### 配置文件配置
* 配置文件位置默认在`lmysql/db.properties`
```txt
# 驱动，这里是1.8+的驱动
mysql.driver = com.mysql.cj.jdbc.Driver
# 地址
mysql.host = localhost
# 端口号
mysql.port = 3306
# 用户名
mysql.root = root
# 密码
mysql.pwd = root
# 库名
mysql.dbName = project
# 编码
mysql.characterEncoding = utf8
# 是否启动ssl
mysql.useSSL = false
# 时区
mysql.serverTimezone = CST
```

### Mysql配置类
* 所有操作类的入口类，读取配置连接数据库，并创建数据库对象
* 该类依赖于`db.properties`文件，文件位置改变时，可能要修改以下路径
![image_1dghgjfd32j9go31pkk1p3kptl9.png-14.5kB][3]

```java
// 包导好了
// 数据库启动了
// 配置和路径都设置好了
// 假设一切都准备好了

// 实例化一个配置类，没报错就可以了
Mysql mysql = new Mysql();

// 用完记得关掉连接
mysql.close();
```

> #### 事务功能


##### 1. 启动一个事务

```java
// 实例化配置类时传入 true 启动事务
Mysql mysql = new Mysql(true);

// 或者通过通过 begin() 启动
Mysql mysql2 = new Mysql();
mysql2.begin();
```
##### 2. 创建一个保存点
* 第一个字符必须是字母！
```java
// 实例化一个事务配置类
Mysql mysql = new Mysql(true);

// 通过 save() 设置保存点
mysql.save("a"); // 第一个字符必须是字母
```
##### 3. 提交事务
```java
// 实例化一个事务配置类
Mysql mysql = new Mysql(true);

// 这里执行了一堆sql
...

// 通过 commit() 提交
mysql.commit();

// 也可以通过 close() 关闭连接并提交
mysql.close()
```
##### 4. 事务回滚
```java
// 实例化一个事务配置类
Mysql mysql = new Mysql(true);

// 执行一堆sql
...sql1

// 创建了一个保存点
mysql.save("lidaye");

// 再执行一堆sql
...sql2

// 通过 back() 回滚到保存点 lidaye
// back() 不传参则全部回滚
mysql.back("lidaye");

// 提交事务并关闭连接
mysql.close();
// 只执行了 sql1 的sql命令

```

### LMysql父类
* `abstract public class LMysql<T,R>`主要逻辑共用抽象类，平时调用不会直接用到，需要依靠其他操作类的继承调用。

```java
// from
.from("表名")

// where
.where("字段名","条件值")
.where("字段名","条件值",换位符模式(默认true))
.where("字段名","条件值","逻辑运算符(默认=)")
.where("字段名","条件值","逻辑运算符(默认=)","拼接条件符(默认AND)",换位符模式(默认true))

// 大于判断
.whereGt("字段名","条件值")
.whereGt("字段名","条件值",换位符模式(默认true))
.whereGt("字段名","条件值","拼接条件符(默认AND)")
.whereGt("字段名","条件值","拼接条件符(默认AND)",换位符模式(默认true))

// 小于判断
.whereLt("字段名","条件值")
.whereLt("字段名","条件值",换位符模式(默认true))
.whereLt("字段名","条件值","拼接条件符(默认AND)")
.whereLt("字段名","条件值","拼接条件符(默认AND)",换位符模式(默认true))

// IN包含判断
.whereIn("字段名",多个可能包含的值...)

// IN包含判断（换位符模式）
.whereInPre("字段名",多个可能包含的值...)

// IN包含判断（OR 拼接条件）
.whereOrIn("字段名",多个可能包含的值...)

// IN包含判断（OR 拼接条件 + 换位符模式）
.whereOrInPre("字段名",多个可能包含的值...)

// 模糊查询条件
.whereLike("字段名","条件值")
.whereLike("字段名","条件值",换位符模式(默认true))
.whereLike("字段名","条件值","拼接条件符(默认AND)")
.whereLike("字段名","条件值","拼接条件符(默认AND)",换位符模式(默认true))

// 范围条件
.between("字段名","开始值","结束值")
.between("字段名","开始值","结束值",换位符模式(默认true))
.between("字段名","开始值","结束值","拼接条件符(默认AND)")
.between("字段名","开始值","结束值","拼接条件符(默认AND)",换位符模式(默认true))

// 群组
.group("字段名")

// 查询限制
.limit("显示条目数")
.limit("起始条目数","显示条目数")

// 升序
.asc(一个或多个排序字段名...)

// 降序
.desc(一个或多个排序字段名...)

// 执行操作对象（该方法必须作为最后一个方法执行）
.query()
.query(显示条目数)
.query(起始条目数,显示条目数)
.query("表名")
.query("表名",显示条目数)
.query("表名",起始条目数,显示条目数)

// 返回sql语句字符串（该方法必须作为最后一个方法执行）
.getSql()
```

### Select查操作类
* 方法主要继承于`LMysql`
* `public class Select extends LMysql<Select,List<Map>>`运行结果返回以List<Map>的集合

```java
// 创建配置类对象实例
Mysql mysql = new Mysql();

// 普通的全表全字段查询
List<Map> res = mysql.select().query("user");
```

### Update改操作类
* 方法主要继承于`LMysql`
* `public class Update extends LMysql<Update,Integer>`运行结果返回修改的条目数

```java
// 创建配置类对象实例
Mysql mysql = new Mysql();

// 修改一个指定id的指定字段的值
int res = mysql
    // update()可以传 ILJson 对象,然后还要指定字段名
    // 传 ILJson 可以不调用set(),但是where()还要写（有点鸡肋，可以不用）
    .update("user")
    .set("user_name","李大爷")
    .where("id",1)
    .query();
```

### Insert增操作类
* 方法主要继承于`LMysql`
* `public class Insert extends LMysql<Insert,Integer>`运行结果返回插入的条目数

```java
// 创建配置类对象实例
Mysql mysql = new Mysql();

// 随便插入两条条数据
int res = mysql
    // insert()可以传 Map 和 ILJson 对象，传对象默认插入一条
    // 后期可能考虑传list<Map>插入多条
    .insert("user_name","user_pwd","age")
    .value("李大爷","123456",25)
    .vlaue("李学霸","654321",18)
    .query("user");
```

### Delete删操作类
* 方法主要继承于`LMysql`
* `public class Delete extends LMysql<Delete,Integer>`运行结果返回删除的条目数

```java
// 创建配置类对象实例
Mysql mysql = new Mysql();

// 随便删除一条记录
int res = mysql
    .delete("user")
    .where("id",1)
    .query();
```

### Query执行sql类
* 方法主要继承于`LMysql`
* `public class Query extends LMysql<Query,Object>`该方法不同于操作类，用于直接运行sql

```java
// 创建配置类对象实例
Mysql mysql = new Mysql();

// 执行有结果集的sql
mysql.query("SELECT * FROM `user`");
// 带占位符
mysql.query("SELECT ? FROM `user` WHERE `user_name`=?","*","李大爷");

// 执行无结果集的sql
mysql.execute("UPDATE `user` SET `user_name`='李大爷'");
// 带占位符同上
...
```


  [1]: https://github.com/theFiles/LJson
  [2]: https://www.zybuluo.com/File/note/1511726
  [3]: http://static.zybuluo.com/File/cp20ophok8umk3n328285a8d/image_1dghgjfd32j9go31pkk1p3kptl9.png
