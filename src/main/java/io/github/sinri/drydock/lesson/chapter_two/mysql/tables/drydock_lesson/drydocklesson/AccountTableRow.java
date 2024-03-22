package io.github.sinri.drydock.lesson.chapter_two.mysql.tables.drydock_lesson.drydocklesson;

import io.github.sinri.keel.mysql.matrix.AbstractTableRow;
import io.vertx.core.json.JsonObject;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Table comment is empty.
 * (´^ω^`)
 * SCHEMA: drydock_lesson
 * TABLE: account
 * (*￣∇￣*)
 * NOTICE BY KEEL:
 * To avoid being rewritten, do not modify this file manually, unless editable confirmed.
 * It was auto-generated on Fri Mar 22 14:30:08 CST 2024.
 *
 * @see io.github.sinri.keel.mysql.dev.TableRowClassSourceCodeGenerator
 */
public class AccountTableRow extends AbstractTableRow {
    public static final String SCHEMA_AND_TABLE = "drydock_lesson.account";

    public AccountTableRow(JsonObject tableRow) {
        super(tableRow);
    }

    @Override
    @Nonnull
    public String sourceTableName() {
        return "account";
    }

    public String sourceSchemaName() {
        return "drydock_lesson";
    }

    /*
     * 账户的唯一识别号码
     *
     * Field `account_id` of type `bigint(20) unsigned`.
     */
    @Nonnull
    public Long getAccountId() {
        return Objects.requireNonNull(readLong("account_id"));
    }

    /*
     * 账户的登入名
     *
     * Field `account_name` of type `varchar(128)`.
     */
    @Nonnull
    public String getAccountName() {
        return Objects.requireNonNull(readString("account_name"));
    }

    /*
     * 账户的显示名
     *
     * Field `display_name` of type `varchar(128)`.
     */
    @Nonnull
    public String getDisplayName() {
        return Objects.requireNonNull(readString("display_name"));
    }

    /*
     * 账户的邮箱
     *
     * Field `email` of type `varchar(200)`.
     */
    @Nonnull
    public String getEmail() {
        return Objects.requireNonNull(readString("email"));
    }

    /*
     * 密码的哈希值
     *
     * Field `password_hash` of type `varchar(200)`.
     */
    @Nonnull
    public String getPasswordHash() {
        return Objects.requireNonNull(readString("password_hash"));
    }

    /*
     * Enum{ON,OFF}
     *
     * Loose Enum of Field `account_status` of type `varchar(32)`.
     */
    @Nonnull
    public AccountStatusEnum getAccountStatus() {
        return AccountStatusEnum.valueOf(
                Objects.requireNonNull(readString("account_status"))
        );
    }

    /**
     * Enum for Field `account_status`
     */
    public enum AccountStatusEnum {
        ON,
        OFF,
    }


}

/*
CREATE TABLE `account` (
  `account_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '账户的唯一识别号码',
  `account_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '账户的登入名',
  `display_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '账户的显示名',
  `email` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '账户的邮箱',
  `password_hash` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码的哈希值',
  `account_status` varchar(32) NOT NULL COMMENT 'Enum{ON,OFF}',
  PRIMARY KEY (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
 */
