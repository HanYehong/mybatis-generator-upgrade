# mybatis-generator-upgrade
#### mybatis自动生成代码（自定义模板） + 图形化界面
##### 区别于普通mybatis代码生成器，这里加入了：
##### 1）saveBatch（批量插入）
##### 2）delete（条件物理删除）
##### 3）remove（条件逻辑删除）
##### 4）list（条件查询）

ps：若存在更新、插入者的信息和时间等字段（update_id、creator_id、row_status等），可以在org.mybatis.generator.codegen.mybatis3.custom.CustomColumnField类中指定对应的数据库列名称，在生成语句的时候会自动生成对应的语句，例如自动增加 row_status = 1 条件、在list的where查询中不对这些的字段进行查询（若需要查询则可以不指定）