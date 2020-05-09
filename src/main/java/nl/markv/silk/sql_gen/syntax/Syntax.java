package nl.markv.silk.sql_gen.syntax;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import nl.markv.silk.sql_gen.writer.SqlWriter;
import nl.markv.silk.types.CheckConstraint;
import nl.markv.silk.types.Column;
import nl.markv.silk.types.DataType;
import nl.markv.silk.types.DatabaseSpecific;
import nl.markv.silk.types.Table;
import nl.markv.silk.types.UniqueConstraint;

/**
 * A syntax converts isolated Silk schema elements to SQL statements.
 */
public interface Syntax {

	@FunctionalInterface
	interface TableEntrySyntax<T> {
		default void begin(@Nonnull SqlWriter sql, @Nonnull Table table) {};
		void entry(@Nonnull SqlWriter sql, @Nonnull Table table, @Nonnull T entry);
		default void end(@Nonnull SqlWriter sql, @Nonnull Table table) {};
	}

	@Nonnull
	String dataTypeName(@Nonnull SqlWriter sql, @Nonnull DataType type);

	@Nonnull
	String autoValueName(@Nonnull SqlWriter sql, @Nonnull Column.AutoOptions autoValue);

	void prelude(@Nonnull SqlWriter sql, @Nullable DatabaseSpecific db);

	void postlude(@Nonnull SqlWriter sql, @Nullable DatabaseSpecific db);

	void startTable(@Nonnull SqlWriter sql, @Nonnull Table table, @Nullable DatabaseSpecific db);

	void endTable(@Nonnull SqlWriter sql, @Nonnull Table table, @Nullable DatabaseSpecific db);

	final class ColumnInfo { Column column; String dataTypeName; String autoValueName; MetaInfo.PrimaryKey primaryKey; boolean isLast; }
	@Nullable
	TableEntrySyntax<ColumnInfo> columnInCreateTableSyntax();

	@Nullable
	TableEntrySyntax<List<Column>> primaryKeyInCreateTableSyntax();

	@Nullable
	TableEntrySyntax<List<Column>> addPrimaryKeyToExistingTableSyntax();

	@Nullable
	TableEntrySyntax<CheckConstraint> checkInCreateTableSyntax();

	@Nullable
	TableEntrySyntax<CheckConstraint> addCheckToExistingTableSyntax();

	@Nullable
	TableEntrySyntax<UniqueConstraint> uniqueInCreateTableSyntax();

	@Nullable
	TableEntrySyntax<UniqueConstraint> addUniqueToExistingTableSyntax();

	@Nullable
	TableEntrySyntax<UniqueConstraint> referenceInCreateTableSyntax();

	@Nullable
	TableEntrySyntax<UniqueConstraint> addReferenceToExistingTableSyntax();

	final class AutoValueInfo { Column column; String dataTypeName; String autoValueName; }
	@Nullable
	TableEntrySyntax<AutoValueInfo> addDefaultValueToExistingTableSyntax();
}