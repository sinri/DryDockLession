package io.github.sinri.drydock.lesson.chapter_two.mysql.tables.drydock_lesson.drydocklesson;

import io.github.sinri.keel.mysql.matrix.AbstractTableRow;
import io.vertx.core.json.JsonObject;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Table comment is empty.
 * (´^ω^`)
 * SCHEMA: drydock_lesson
 * TABLE: project_member
 * (*￣∇￣*)
 * NOTICE BY KEEL:
 * To avoid being rewritten, do not modify this file manually, unless editable confirmed.
 * It was auto-generated on Fri Mar 22 14:30:08 CST 2024.
 *
 * @see io.github.sinri.keel.mysql.dev.TableRowClassSourceCodeGenerator
 */
public class ProjectMemberTableRow extends AbstractTableRow {
    public static final String SCHEMA_AND_TABLE = "drydock_lesson.project_member";

    public ProjectMemberTableRow(JsonObject tableRow) {
        super(tableRow);
    }

    @Override
    @Nonnull
    public String sourceTableName() {
        return "project_member";
    }

    public String sourceSchemaName() {
        return "drydock_lesson";
    }

    /*
     * Field `member_id` of type `bigint(20) unsigned`.
     */
    @Nonnull
    public Long getMemberId() {
        return Objects.requireNonNull(readLong("member_id"));
    }

    /*
     * Field `project_id` of type `bigint(20)`.
     */
    @Nonnull
    public Long getProjectId() {
        return Objects.requireNonNull(readLong("project_id"));
    }

    /*
     * Field `account_id` of type `bigint(20)`.
     */
    @Nonnull
    public Long getAccountId() {
        return Objects.requireNonNull(readLong("account_id"));
    }


}

/*
CREATE TABLE `project_member` (
  `member_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `project_id` bigint(20) NOT NULL,
  `account_id` bigint(20) NOT NULL,
  PRIMARY KEY (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
 */
