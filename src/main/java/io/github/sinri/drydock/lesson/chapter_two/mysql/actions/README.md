# 2.3 我的MySQL不可能这么简单 之二

上一篇我们已经搞定了把物理世界中的数据库体系映射到指名数据源和指名数据源连接和相应的表结果定义类中，接下来让我们开始搞业务吧。

# 基于指名数据源连接的数据操作集合类

在工业化进程中，一般会将同一目的的、基于同一指名数据源连接的数据操作放到一起维护。在Keel中提供了`io.github.sinri.keel.mysql.AbstractNamedAction`
类以供继承，该类要求提供一个指名数据源连接，并设计为基于此提供数据操作方法。

作为一个案例，在上一篇中我们已经建立了`organization`
表（组织实体表）对应的类`io.github.sinri.drydock.lesson.chapter_two.mysql.tables.drydock_lesson.drydocklesson.OrganizationTableRow`
，现在我们可以围绕这个组织实体的操作构建一个数据操作集合类。

完成继承之后，就是一个如下的空架子。

    package io.github.sinri.drydock.lesson.chapter_two.mysql.actions;
    
    import io.github.sinri.drydock.lesson.chapter_two.mysql.LessonMySQLConnection;
    import io.github.sinri.drydock.lesson.chapter_two.mysql.tables.drydock_lesson.drydocklesson.OrganizationTableRow;
    import io.github.sinri.keel.mysql.AbstractNamedAction;
    import io.github.sinri.keel.mysql.statement.AnyStatement;
    import io.vertx.core.Future;
    
    import javax.annotation.Nonnull;
    
    public class OrganizationAction extends AbstractNamedAction<LessonMySQLConnection> {
      public OrganizationAction(@Nonnull LessonMySQLConnection namedSqlConnection) {
        super(namedSqlConnection);
      }
      // TODO
    }

接下来，让我们开始尝试在里面增加操作能力，顺便了解各种查询的运用。

# 数据操作

## 查询语句

在MySQL中查询通过SQL语句实现，在Keel中则通过`AnyStatement`接口进行基础描述，同时提供静态快速构建方法。

类`io.github.sinri.keel.mysql.statement.AbstractStatement`为该接口提供了一个基础实现，并在其上为增删改查等查询作了相应实现。

同时此类还提供了静态方法`void setSqlAuditIssueRecorder(@Nonnull KeelIssueRecorder<MySQLAuditIssueRecord> sqlAuditIssueRecorder)`
；通过此设置SQL审计记录器后，即可打印所有通过该基类及其实现类的生成SQL，可以方便地在开发过程验证所写的代码的效果。

## INSERT

在现实中新建一个组织，意味着在`organization`表（组织实体表）中需要新增一行。显然，这需要通过INSERT STATEMENT实现。

    public Future<Long> createOrganization(
      @Nonnull String organizationName,
      @Nonnull OrganizationTableRow.OrganizationTypeEnum organizationType,
      @Nonnull OrganizationTableRow.OrganizationStatusEnum organizationStatus
    ) {
      return AnyStatement.insert(
        writeIntoStatement -> writeIntoStatement
        .intoTable(OrganizationTableRow.SCHEMA_AND_TABLE)
        .macroWriteOneRow(rowToWrite -> rowToWrite
                          .put("organization_name", organizationName)
                          .put("organization_type", organizationType)
                          .put("organization_status", organizationStatus)
                          .putNow("create_time")
                          .putExpression("update_time", "now()")
                         )
      )
        .executeForLastInsertedID(getNamedSqlConnection());
    }

上述方法通过AnyStatement的insert方法接收一个写入语句（`io.github.sinri.keel.mysql.statement.WriteIntoStatement`
）的编辑处理过程，生成写入语句后执行之并提取其最后插入的行ID。

这里你能注意到，OrganizationTableRow类中自动为两个字段生成了Enum类。通过观察表中的字段注释和枚举类的定义，我想你一定能搞明白`Enum{X,Y}`
的表字段注释有啥用了。同样，这里的`OrganizationTableRow.SCHEMA_AND_TABLE`也完全可以望文生义。

写入语句提供了macroWriteOneRow方法以处理单行插入的情况，并通过`io.github.sinri.keel.mysql.statement.WriteIntoStatement.RowToWrite`
对插入的行内容进行封装。行中每列内容的填充可以使用`put`、`putExpression`以及`putNow`
三个方法实现，具体可以自己生成SQL进行体验。该类同时还提供了一系列静态方法`fromJson*`，可以便捷地利用现成的JSON数据进行格式化封装。

## REPLACE

众周所知，MySQL提供了replace语法，与insert贼拉类似，可以直接体验AnyStatement的replace方法，和上面实在太像了，不再展开。

## UPDATE

对具有某一ID的组织信息进行修改，就需要动用update语句。

    public Future<Integer> updateOrganization(
      long organizationId,
      @Nullable String organizationName,
      @Nullable OrganizationTableRow.OrganizationTypeEnum organizationType,
      @Nullable OrganizationTableRow.OrganizationStatusEnum organizationStatus
    ) {
      return AnyStatement.update(
        updateStatement -> {
          updateStatement.table(OrganizationTableRow.SCHEMA_AND_TABLE);
          if (organizationName != null) {
            updateStatement.setWithValue("organization_name", organizationName);
          }
          if (organizationType != null) {
            updateStatement.setWithValue("organization_type", organizationType.name());
          }
          if (organizationStatus != null) {
            updateStatement.setWithValue("organization_status", organizationStatus.name());
          }
          updateStatement.setWithExpression("update_time", "now()");
          updateStatement.where(
            conditionsComponent -> conditionsComponent
            .expressionEqualsNumericValue("organization_id", organizationId)
          );
        }
      )
        .executeForAffectedRows(getNamedSqlConnection());
    }

## DELETE

一般地，系统内实体应该通过更新状态进行软删除。但如果某个组织建错了之类的需要毁尸灭迹，就会用到硬删除，即delete语句。

    public Future<Void> deleteOrganization(long organizationId) {
      return AnyStatement.delete
      deleteStatement -> deleteStatement
      .from(OrganizationTableRow.SCHEMA_AND_TABLE)
      .where(conditionsComponent -> conditionsComponent
             .expressionEqualsNumericValue("organization_id", organizationId)
            )
      )
      .executeForAffectedRows(getNamedSqlConnection())
      .compose(afx -> {
        if (afx != 1) return Future.failedFuture("AFX IS NOT 1, DELETION NOT OK");
        return Future.succeededFuture();
      });
    }

## SELECT

囤了数据，总归还是要查出来的。

以下是根据ID查询单行并组装为表的结果行类实例的例子。

    public Future<OrganizationTableRow> fetchOrganizationById(long organizationId) {
      return AnyStatement.select(
      selectStatement -> selectStatement
      .from(OrganizationTableRow.SCHEMA_AND_TABLE)
      .where(conditionsComponent -> conditionsComponent
             .expressionEqualsNumericValue("organization_id", organizationId)
            )
      .limit(1)
    )
      .queryForOneRow(getNamedSqlConnection(), OrganizationTableRow.class);
    }

也可以查询多行组装成列表，比如把所有状态为ON的组织查出来。

    public Future<List<OrganizationTableRow>> fetchOrganizationsWithStatusOn() {
      return AnyStatement.select(
        selectStatement -> selectStatement
        .from(OrganizationTableRow.SCHEMA_AND_TABLE)
        .where(conditionsComponent -> conditionsComponent
               .expressionEqualsLiteralValue("organization_status", OrganizationTableRow.OrganizationStatusEnum.ON.name())
              )
      )
        .queryForRowList(getNamedSqlConnection(), OrganizationTableRow.class);
    }

当然where条件部分，除了数值相等或字符串相等之外，还有别的搞法，例如按照名称模糊查询。

    public Future<List<OrganizationTableRow>> fetchOrganizationsWithKeyword(@Nonnull String keyword) {
      return AnyStatement.select(
        selectStatement -> selectStatement
        .from(OrganizationTableRow.SCHEMA_AND_TABLE)
        .where(conditionsComponent -> conditionsComponent
               .comparison(compareCondition -> compareCondition
                           .compareExpression("organization_name")
                           .contains(keyword)
                          )
              )
      )
        .queryForRowList(getNamedSqlConnection(), OrganizationTableRow.class);
    }

Keel体系下statement的where部分提供了`comparison`（单对象运算判断）和`among`（IN判断）两类基础判断，`intersection`
（AND）和`union`（OR）两类逻辑判断，以及实在不知道怎么写的时候的`raw`
方法；这些判断均由`io.github.sinri.keel.mysql.condition.MySQLCondition`接口实现的各条件类组合来实现，具体可以自行看源代码摸索。

## 其他妖术

此外，select语句所支持的join、union甚至使用Raw SQL等等也是资瓷滴，具体可以慢慢深入的时候再说，好孩子可以先努力不要在业务中搞这种骚操作。当然还有更骚的SQL模板之术，等长大了再学吧。