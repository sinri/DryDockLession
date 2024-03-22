package io.github.sinri.drydock.lesson.chapter_two.mysql.tables.drydock_lesson.drydocklesson;

import io.github.sinri.keel.mysql.matrix.AbstractTableRow;
import io.vertx.core.json.JsonObject;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Table comment is empty.
 * (´^ω^`)
 * SCHEMA: drydock_lesson
 * TABLE: session
 * (*￣∇￣*)
 * NOTICE BY KEEL:
 * To avoid being rewritten, do not modify this file manually, unless editable confirmed.
 * It was auto-generated on Fri Mar 22 14:30:08 CST 2024.
 *
 * @see io.github.sinri.keel.mysql.dev.TableRowClassSourceCodeGenerator
 */
public class SessionTableRow extends AbstractTableRow {
    public static final String SCHEMA_AND_TABLE = "drydock_lesson.session";

    public SessionTableRow(JsonObject tableRow) {
        super(tableRow);
    }

    @Override
    @Nonnull
    public String sourceTableName() {
        return "session";
    }

    public String sourceSchemaName() {
        return "drydock_lesson";
    }

    /*
     * Field `session_id` of type `bigint(20) unsigned`.
     */
    @Nonnull
    public Long getSessionId() {
        return Objects.requireNonNull(readLong("session_id"));
    }

    /*
     * Field `token` of type `varchar(200)`.
     */
    @Nonnull
    public String getToken() {
        return Objects.requireNonNull(readString("token"));
    }

    /*
     * Field `account_id` of type `bigint(20)`.
     */
    @Nonnull
    public Long getAccountId() {
        return Objects.requireNonNull(readLong("account_id"));
    }

    /*
     * Field `start_time` of type `int(11)`.
     */
    @Nonnull
    public Integer getStartTime() {
        return Objects.requireNonNull(readInteger("start_time"));
    }

    /*
     * Field `end_time` of type `int(11)`.
     */
    @Nonnull
    public Integer getEndTime() {
        return Objects.requireNonNull(readInteger("end_time"));
    }


}

/*
CREATE TABLE `session` (
  `session_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `token` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `account_id` bigint(20) NOT NULL,
  `start_time` int(11) NOT NULL,
  `end_time` int(11) NOT NULL,
  PRIMARY KEY (`session_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
 */
