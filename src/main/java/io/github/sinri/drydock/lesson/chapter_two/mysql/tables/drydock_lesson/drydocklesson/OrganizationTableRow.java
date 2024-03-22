package io.github.sinri.drydock.lesson.chapter_two.mysql.tables.drydock_lesson.drydocklesson;

import io.github.sinri.keel.mysql.matrix.AbstractTableRow;
import io.vertx.core.json.JsonObject;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Table comment is empty.
 * (´^ω^`)
 * SCHEMA: drydock_lesson
 * TABLE: organization
 * (*￣∇￣*)
 * NOTICE BY KEEL:
 * To avoid being rewritten, do not modify this file manually, unless editable confirmed.
 * It was auto-generated on Fri Mar 22 14:30:08 CST 2024.
 *
 * @see io.github.sinri.keel.mysql.dev.TableRowClassSourceCodeGenerator
 */
public class OrganizationTableRow extends AbstractTableRow {
    public static final String SCHEMA_AND_TABLE = "drydock_lesson.organization";

    public OrganizationTableRow(JsonObject tableRow) {
        super(tableRow);
    }

    @Override
    @Nonnull
    public String sourceTableName() {
        return "organization";
    }

    public String sourceSchemaName() {
        return "drydock_lesson";
    }

    /*
     * Field `organization_id` of type `bigint(20) unsigned`.
     */
    @Nonnull
    public Long getOrganizationId() {
        return Objects.requireNonNull(readLong("organization_id"));
    }

    /*
     * Field `organization_name` of type `varchar(128)`.
     */
    @Nonnull
    public String getOrganizationName() {
        return Objects.requireNonNull(readString("organization_name"));
    }

    /*
     * Enum{HOST,PARASITE}
     *
     * Loose Enum of Field `organization_type` of type `varchar(128)`.
     */
    @Nonnull
    public OrganizationTypeEnum getOrganizationType() {
        return OrganizationTypeEnum.valueOf(
                Objects.requireNonNull(readString("organization_type"))
        );
    }

    /*
     * Enum{ON,OFF}
     *
     * Loose Enum of Field `organization_status` of type `varchar(16)`.
     */
    @Nonnull
    public OrganizationStatusEnum getOrganizationStatus() {
        return OrganizationStatusEnum.valueOf(
                Objects.requireNonNull(readString("organization_status"))
        );
    }

    /*
     * Field `create_time` of type `datetime`.
     */
    @Nonnull
    public String getCreateTime() {
        return Objects.requireNonNull(readDateTime("create_time"));
    }

    /*
     * Field `update_time` of type `datetime`.
     */
    @Nonnull
    public String getUpdateTime() {
        return Objects.requireNonNull(readDateTime("update_time"));
    }

    /**
     * Enum for Field `organization_type`
     */
    public enum OrganizationTypeEnum {
        HOST,
        PARASITE,
    }

    /**
     * Enum for Field `organization_status`
     */
    public enum OrganizationStatusEnum {
        ON,
        OFF,
    }


}

/*
CREATE TABLE `organization` (
  `organization_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `organization_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `organization_type` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Enum{HOST,PARASITE}',
  `organization_status` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Enum{ON,OFF}',
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`organization_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
 */
