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

	interface InTableSyntax<T> {
		void begin(@Nonnull SqlWriter sql, @Nonnull Table table);
		void entry(@Nonnull SqlWriter sql, @Nonnull Table table, @Nonnull T entry);
		void end(@Nonnull SqlWriter sql, @Nonnull Table table);
	}

	interface AfterTableSyntax<T> {
		void begin(@Nonnull SqlWriter sql, @Nonnull Table table);
		void entry(@Nonnull SqlWriter sql, @Nonnull Table table, @Nonnull T entry);
		void end(@Nonnull SqlWriter sql, @Nonnull Table table);
	}

	@Nonnull
	String dataTypeName(@Nonnull SqlWriter sql, @Nonnull DataType type);

	@Nonnull
	String autoValueName(@Nonnull SqlWriter sql, @Nonnull Column.AutoOptions autoValue);

	void prelude(@Nonnull SqlWriter sql, @Nullable DatabaseSpecific db);

	void postlude(@Nonnull SqlWriter sql, @Nullable DatabaseSpecific db);

	void startTable(@Nonnull SqlWriter sql, @Nonnull Table table, @Nullable DatabaseSpecific db);

	void endTable(@Nonnull SqlWriter sql, @Nonnull Table table, @Nullable DatabaseSpecific db);

	void columnInCreateTable(@Nonnull SqlWriter sql, @Nonnull Column column, @Nonnull String dataTypeName, MetaInfo.PrimaryKey primaryKey, @Nullable String autoValueName, boolean isLast, @Nullable DatabaseSpecific db);

	void primaryKeyInCreateTable(@Nonnull SqlWriter sql, @Nonnull List<String> primaryKey, @Nullable DatabaseSpecific db);

	void autoValueAfterCreation(@Nonnull SqlWriter sql, @Nonnull Column column, @Nonnull String dataTypeName, @Nonnull String autoValueName, @Nullable DatabaseSpecific db);

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