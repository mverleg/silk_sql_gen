package nl.markv.silk.sql_gen.syntax;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import nl.markv.silk.sql_gen.writer.SqlWriter;
import nl.markv.silk.types.Column;
import nl.markv.silk.types.ColumnMapping;
import nl.markv.silk.types.DataType;
import nl.markv.silk.types.DatabaseSpecific;
import nl.markv.silk.types.Table;

/**
 * A syntax converts isolated Silk schema elements to SQL statements.
 */
public interface Syntax {

	void prelude(@Nonnull SqlWriter sql, @Nullable DatabaseSpecific db);

	void postlude(@Nonnull SqlWriter sql, @Nullable DatabaseSpecific db);

	void startTable(@Nonnull SqlWriter sql, @Nonnull Table table, @Nullable String description, @Nullable DatabaseSpecific db);

	void endTable(@Nonnull SqlWriter sql, @Nonnull Table table, @Nullable DatabaseSpecific db);

	String dataTypeName(@Nonnull SqlWriter sql, @Nonnull DataType type);

	String autoValueName(@Nonnull SqlWriter sql, @Nonnull Column.AutoOptions autoValue);

	void columnInCreateTable(@Nonnull SqlWriter sql, @Nonnull String name, @Nonnull String dataTypeName, boolean nullable, MetaInfo.PrimaryKey primaryKey, @Nullable String autoValueName, @Nullable String defaultValue, boolean isLast, @Nullable DatabaseSpecific db);

	void autoValueAfterCreation(@Nonnull SqlWriter sql, @Nonnull String columnName, @Nonnull String dataType, @Nonnull String autoValue, @Nullable DatabaseSpecific db);

	void primaryKeyInCreateTable(@Nonnull SqlWriter sql, @Nonnull List<String> primaryKey, @Nullable DatabaseSpecific db);

	void startTableUniqueConstraints(@Nonnull SqlWriter sql, @Nonnull Table table, @Nullable DatabaseSpecific databaseSpecific);

	void endTableUniqueConstraints(@Nonnull SqlWriter sql, @Nonnull Table table, @Nullable DatabaseSpecific databaseSpecific);

	void tableUniqueConstraintInline(@Nonnull SqlWriter sql, @Nonnull Table table, @Nullable String constraintName, @Nonnull List<Column> columns, @Nullable DatabaseSpecific databaseSpecific);

	void tableUniqueConstraintAfter(@Nonnull SqlWriter sql, @Nonnull Table table, @Nullable String constraintName, @Nonnull List<Column> columns, @Nullable DatabaseSpecific databaseSpecific);

	void startTableCheckConstraints(@Nonnull SqlWriter sql, @Nonnull Table table, @Nullable DatabaseSpecific databaseSpecific);

	void tableCheckConstraintInline(@Nonnull SqlWriter sql, @Nonnull Table table, @Nullable String constraintName, @Nonnull String condition, @Nullable DatabaseSpecific databaseSpecific);

	void tableCheckConstraintAfter(@Nonnull SqlWriter sql, @Nonnull Table table, @Nullable String constraintName, @Nonnull String condition, @Nullable DatabaseSpecific databaseSpecific);

	void endTableCheckConstraints(@Nonnull SqlWriter sql, @Nonnull Table table, @Nullable DatabaseSpecific databaseSpecific);

	void startTableReferences(@Nonnull SqlWriter sql, @Nonnull Table table, @Nullable DatabaseSpecific databaseSpecific);

	void endTableReferences(@Nonnull SqlWriter sql, @Nonnull Table table, @Nullable DatabaseSpecific databaseSpecific);

	void tableReferenceAfter(@Nonnull SqlWriter sql, @Nonnull String group, @Nonnull String sourceTable, @Nullable String constraintName, @Nonnull Table targetTable, @Nonnull List<ColumnMapping> columns, @Nullable DatabaseSpecific databaseSpecific);
}