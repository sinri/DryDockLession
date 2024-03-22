package io.github.sinri.drydock.lesson.chapter_two.mysql.actions;

import io.github.sinri.drydock.lesson.chapter_two.mysql.LessonMySQLConnection;
import io.github.sinri.drydock.lesson.chapter_two.mysql.tables.drydock_lesson.drydocklesson.OrganizationTableRow;
import io.github.sinri.keel.mysql.AbstractNamedAction;
import io.github.sinri.keel.mysql.statement.AnyStatement;
import io.vertx.core.Future;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class OrganizationAction extends AbstractNamedAction<LessonMySQLConnection> {
    public OrganizationAction(@Nonnull LessonMySQLConnection namedSqlConnection) {
        super(namedSqlConnection);
    }

    public Future<Long> createOrganization(
            @Nonnull String organizationName,
            @Nonnull OrganizationTableRow.OrganizationTypeEnum organizationType,
            @Nonnull OrganizationTableRow.OrganizationStatusEnum organizationStatus
    ) {
        return AnyStatement.insert(writeIntoStatement -> writeIntoStatement
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
                            updateStatement.where(conditionsComponent -> conditionsComponent
                                    .expressionEqualsNumericValue("organization_id", organizationId)
                            );
                        }
                )
                .executeForAffectedRows(getNamedSqlConnection());
    }

    public Future<Void> deleteOrganization(long organizationId) {
        return AnyStatement.delete(deleteStatement -> deleteStatement
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

    public Future<OrganizationTableRow> fetchOrganizationById(long organizationId) {
        return AnyStatement.select(selectStatement -> selectStatement
                        .from(OrganizationTableRow.SCHEMA_AND_TABLE)
                        .where(conditionsComponent -> conditionsComponent
                                .expressionEqualsNumericValue("organization_id", organizationId)
                        )
                        .limit(1)
                )
                .queryForOneRow(getNamedSqlConnection(), OrganizationTableRow.class);
    }

    public Future<List<OrganizationTableRow>> fetchOrganizationsWithStatusOn() {
        return AnyStatement.select(selectStatement -> selectStatement
                        .from(OrganizationTableRow.SCHEMA_AND_TABLE)
                        .where(conditionsComponent -> conditionsComponent
                                .expressionEqualsLiteralValue("organization_status", OrganizationTableRow.OrganizationStatusEnum.ON.name())
                        )
                )
                .queryForRowList(getNamedSqlConnection(), OrganizationTableRow.class);
    }

    public Future<List<OrganizationTableRow>> fetchOrganizationsWithKeyword(@Nonnull String keyword) {
        return AnyStatement.select(selectStatement -> selectStatement
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
}
