# 介绍

数据血缘，支持spark sql,hive sql,pg sql,presto sql,mysql sql,tidb sql, flink sql, datax血缘,Spark/flink jar 运行命令的血缘解析；支持with语法

* 特别提示：presto和spark均支持with语法
* 目前解析到表级别，可以扩展到字段级别
* 支持datax血缘(mysql/pg/s3)

# 编译

mvn antlr4:antlr4

mvn clean package

# 使用

* spark SQL
```kotlin
val sql = "CREATE DATABASE IF NOT EXISTS bigdata"
val statementData = SparkSQLHelper.getStatementData(sql)
println(statementData.statement)
```

* presto SQL
```kotlin
val sql = "INSERT INTO adc.fsfd with recursive t as (select a,b,v from a.x)  select a,b,v from t"
val statementData = PrestoSQLHelper.getStatementData(sql)
println(statementData.statement)
```

* Tidb SQL/MySQL
```kotlin
val sql = "insert into bigdata.user select * from users a left outer join address b on a.address_id = b.id"
val statementData = PrestoSQLHelper.getStatementData(sql)
println(statementData.statement)
```

* Flink SQL
```kotlin
val sql = "CREATE SOURCE TABLE student_scores (\n" +
        "  student_number varchar comment '学号', \n" +
        "  student_name varchar comment '姓名', \n" +
        "  subject varchar comment '学科',\n" +
        "  score INT comment '成绩' \n" +
        ")\n" +
        "WITH (\n" +
        "  type = \"dis\",\n" +
        "  kafka.region = \"cn-north-1\"\n" +
        ") TIMESTAMP BY proctime proctime1"

val statementData = StreamSQLHelper.getStatementData(sql).get(0)
println(statementData.statement)
```
