package io.github.sinri.drydock.lesson.chapter_two.mysql.tables.drydock_lesson.drydocklesson;

import io.github.sinri.keel.mysql.matrix.AbstractTableRow;
import io.vertx.core.json.JsonObject;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Table comment is empty.
 * (´^ω^`)
 * SCHEMA: drydock_lesson
 * TABLE: project_action
 * (*￣∇￣*)
 * NOTICE BY KEEL:
 * To avoid being rewritten, do not modify this file manually, unless editable confirmed.
 * It was auto-generated on Fri Mar 22 14:30:09 CST 2024.
 *
 * @see io.github.sinri.keel.mysql.dev.TableRowClassSourceCodeGenerator
 */
public class ProjectActionTableRow extends AbstractTableRow {
    public static final String SCHEMA_AND_TABLE = "drydock_lesson.project_action";

    public ProjectActionTableRow(JsonObject tableRow) {
        super(tableRow);
    }

    @Override
    @Nonnull
    public String sourceTableName() {
        return "project_action";
    }

    public String sourceSchemaName() {
        return "drydock_lesson";
    }

    /*
     * Field `action_id` of type `bigint(20) unsigned`.
     */
    @Nonnull
    public Long getActionId() {
        return Objects.requireNonNull(readLong("action_id"));
    }

    /*
     * Field `project_id` of type `bigint(20)`.
     */
    @Nonnull
    public Long getProjectId() {
        return Objects.requireNonNull(readLong("project_id"));
    }

    /*
     * Field `action_type` of type `varchar(128)`.
     */
    @Nonnull
    public String getActionType() {
        return Objects.requireNonNull(readString("action_type"));
    }

    /*
     * Field `action_title` of type `varchar(128)`.
     */
    @Nonnull
    public String getActionTitle() {
        return Objects.requireNonNull(readString("action_title"));
    }

    /*
     * Field `detail` of type `text`.
     */
    @Nonnull
    public String getDetail() {
        return Objects.requireNonNull(readString("detail"));
    }

    /*
     * Field `action_time` of type `datetime`.
     */
    @Nonnull
    public String getActionTime() {
        return Objects.requireNonNull(readDateTime("action_time"));
    }

    /*
     * Field `act_account` of type `bigint(20)`.
     */
    @Nonnull
    public Long getActAccount() {
        return Objects.requireNonNull(readLong("act_account"));
    }


}

/*
CREATE TABLE `project_action` (
  `action_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `project_id` bigint(20) NOT NULL,
  `action_type` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `action_title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `detail` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `action_time` datetime NOT NULL,
  `act_account` bigint(20) NOT NULL,
  PRIMARY KEY (`action_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
 */
