package nl.markv.silk.sql_gen.syntax;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import nl.markv.silk.sql_gen.sqlparts.ListEntry;
import nl.markv.silk.sql_gen.sqlparts.Statement;
import nl.markv.silk.types.CheckConstraint;
import nl.markv.silk.types.Column;
import nl.markv.silk.types.DataType;
import nl.markv.silk.types.DatabaseSpecific;
import nl.markv.silk.types.ForeignKey;
import nl.markv.silk.types.Table;
import nl.markv.silk.types.UniqueConstraint;

/**
 * A syntax converts isolated Silk schema elements to SQL statements.
 */
public interface Syntax {

	@FunctionalInterface
	interface TableEntrySyntax<T, U> {
		default List<U> begin(@Nonnull Table table) {};
		List<U> entry(@Nonnull Table table, @Nonnull T entry);
		default List<U> end(@Nonnull Table table) {};
	}

	@Nonnull
	String dataTypeName(@Nonnull DataType type);

	@Nonnull
	String autoValueName(@Nonnull Column.AutoOptions autoValue);

	@Nonnull
	List<Statement> prelude(@Nullable DatabaseSpecific db);

	@Nonnull
	List<Statement> postlude(@Nullable DatabaseSpecific db);

	@Nonnull
	List<String> startTable(@Nonnull Table table);

	@Nonnull
	List<String> endTable(@Nonnull Table table);

	final class ColumnInfo { public Column column; public String dataTypeName; public String autoValueName; public MetaInfo.PrimaryKey primaryKey; public boolean isLast; }
	@Nonnull
	TableEntrySyntax<ColumnInfo, ListEntry> columnInCreateTableSyntax();

	@Nonnull
	Optional<TableEntrySyntax<List<Column>, ListEntry>> primaryKeyInCreateTableSyntax();

	@Nonnull
	Optional<TableEntrySyntax<List<Column>, List<Statement>>> addPrimaryKeyToExistingTableSyntax();

	@Nonnull
	Optional<TableEntrySyntax<CheckConstraint, ListEntry>> checkInCreateTableSyntax();

	@Nonnull
	Optional<TableEntrySyntax<CheckConstraint, List<Statement>>> addCheckToExistingTableSyntax();

	@Nonnull
	Optional<TableEntrySyntax<UniqueConstraint, ListEntry>> uniqueInCreateTableSyntax();

	@Nonnull
	Optional<TableEntrySyntax<UniqueConstraint, List<Statement>>> addUniqueToExistingTableSyntax();

	@Nonnull
	Optional<TableEntrySyntax<ForeignKey, ListEntry>> referenceInCreateTableSyntax();

	@Nonnull
	Optional<TableEntrySyntax<ForeignKey, List<Statement>>> addReferenceToExistingTableSyntax();

	@Nonnull
	Optional<TableEntrySyntax<ColumnInfo, List<Statement>>> changeColumnForExistingTableSyntax();
}
