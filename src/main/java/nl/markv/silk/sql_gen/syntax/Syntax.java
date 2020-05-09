package nl.markv.silk.sql_gen.syntax;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import nl.markv.silk.pojos.v0_1_0.Columns;
import nl.markv.silk.pojos.v0_1_0.DatabaseSpecific;
import nl.markv.silk.pojos.v0_1_0.LongColumn;
import nl.markv.silk.sql_gen.writer.SqlWriter;
import nl.markv.silk.types.DataType;

/**
 * A syntax converts isolated Silk schema elements to SQL statements.
 */
public interface Syntax {

	void prelude(@Nonnull SqlWriter sql, @Nullable DatabaseSpecific db);

	void postlude(@Nonnull SqlWriter sql, @Nullable DatabaseSpecific db);

	void startTable(@Nonnull SqlWriter sql, @Nullable String group, @Nonnull String name, @Nullable String description, @Nullable DatabaseSpecific db);

	void endTable(@Nonnull SqlWriter sql, @Nullable String group, @Nonnull String name, @Nullable DatabaseSpecific db);

	String dataTypeName(@Nonnull SqlWriter sql, @Nonnull DataType type);

	String autoValueName(@Nonnull SqlWriter sql, @Nonnull LongColumn.AutoOptions autoValue);

	void columnInCreateTable(@Nonnull SqlWriter sql, @Nonnull String name, @Nonnull String dataTypeName, boolean nullable, MetaInfo.PrimaryKey primaryKey, @Nullable String autoValueName, @Nullable String defaultValue, boolean isLast, @Nullable DatabaseSpecific db);

	void autoValueAfterCreation(@Nonnull SqlWriter sql, @Nonnull String columnName, @Nonnull String dataType, @Nonnull String autoValue, @Nullable DatabaseSpecific db);

	void primaryKeyInCreateTable(@Nonnull SqlWriter sql, @Nonnull List<String> primaryKey, @Nullable DatabaseSpecific db);

	void startTableUniqueConstraints(@Nonnull SqlWriter sql, @Nonnull String group, @Nonnull String name, @Nullable DatabaseSpecific databaseSpecific);

	void endTableUniqueConstraints(@Nonnull SqlWriter sql, @Nonnull String group, @Nonnull String name, @Nullable DatabaseSpecific databaseSpecific);

	void tableUniqueConstraintInline(@Nonnull SqlWriter sql, @Nonnull String group, @Nonnull String tableName, @Nullable String constraintName, @Nonnull List<String> columns, @Nullable DatabaseSpecific databaseSpecific);

	void tableUniqueConstraintAfter(@Nonnull SqlWriter sql, @Nonnull String group, @Nonnull String tableName, @Nullable String constraintName, @Nonnull List<String> columns, @Nullable DatabaseSpecific databaseSpecific);

	void startTableCheckConstraints(@Nonnull SqlWriter sql, @Nonnull String group, @Nonnull String name, @Nullable DatabaseSpecific databaseSpecific);

	void tableCheckConstraintInline(@Nonnull SqlWriter sql, @Nonnull String group, @Nonnull String tableName, @Nullable String constraintName, @Nonnull String condition, @Nullable DatabaseSpecific databaseSpecific);

	void tableCheckConstraintAfter(@Nonnull SqlWriter sql, @Nonnull String group, @Nonnull String tableName, @Nullable String constraintName, @Nonnull String condition, @Nullable DatabaseSpecific databaseSpecific);

	void endTableCheckConstraints(@Nonnull SqlWriter sql, @Nonnull String group, @Nonnull String name, @Nullable DatabaseSpecific databaseSpecific);

	void startTableReferences(@Nonnull SqlWriter sql, @Nonnull String group, @Nonnull String tableName, @Nullable DatabaseSpecific databaseSpecific);

	void endTableReferences(@Nonnull SqlWriter sql, @Nonnull String group, @Nonnull String tableName, @Nullable DatabaseSpecific databaseSpecific);

	void tableReferenceAfter(@Nonnull SqlWriter sql, @Nonnull String group, @Nonnull String sourceTable, @Nullable String constraintName, @Nonnull String targetTable, @Nonnull Columns columns, @Nullable DatabaseSpecific databaseSpecific);
}