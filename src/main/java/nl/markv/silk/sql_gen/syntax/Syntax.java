package nl.markv.silk.sql_gen.syntax;

import java.util.Collections;
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
import nl.markv.silk.types.Row;
import nl.markv.silk.types.Table;
import nl.markv.silk.types.UniqueConstraint;

/**
 * A syntax converts isolated Silk schema elements to SQL statements.
 */
public interface Syntax {

	class SyntaxOptions {
		/* Whether names should be quoted; affects case-sensitivity. */
		final boolean quoteNames;
		/* Whether to use terse format, if it is supported. May skip comments and extra whitespace. */
		final boolean terse;

		public SyntaxOptions(boolean quoteNames, boolean terse) {
			this.quoteNames = quoteNames;
			this.terse = terse;
		}
	}

	@FunctionalInterface
	interface SyntaxConstructor {
		@Nonnull
		Syntax create(@Nonnull String schemaName, @Nonnull String silkVersion, SyntaxOptions options);
	}

	@FunctionalInterface
	interface TableEntrySyntax<T, U> {
		@Nonnull
		default List<U> begin(@Nonnull Table table) { return Collections.emptyList(); };
		@Nonnull
		List<U> entry(@Nonnull Table table, @Nonnull T entry);
		@Nonnull
		default List<U> end(@Nonnull Table table) { return Collections.emptyList(); };
	}

	interface InsertSyntax {

		int rowsPerStatement();

		@Nonnull
		String insertBegin(@Nonnull Table table);

		@Nonnull
		String dataRowInsert(@Nonnull Table table, @Nonnull Row row, boolean isFirst);

		@Nonnull
		String insertEnd(@Nonnull Table table);
	}

	@Nonnull
	String dataTypeName(@Nonnull DataType type);

	@Nullable
	String autoValueName(@Nonnull Column.AutoOptions autoValue);

	@Nonnull
	List<Statement> prelude(@Nullable DatabaseSpecific db);

	@Nonnull
	List<Statement> postlude(@Nullable DatabaseSpecific db);

	@Nonnull
	String startTable(@Nonnull Table table);

	@Nonnull
	String endTable(@Nonnull Table table);

	@Nonnull
	TableEntrySyntax<ColumnInfo, ListEntry> columnInCreateTableSyntax();

	@Nonnull
	Optional<TableEntrySyntax<List<Column>, ListEntry>> primaryKeyInCreateTableSyntax();

	@Nonnull
	Optional<TableEntrySyntax<List<Column>, Statement>> addPrimaryKeyToExistingTableSyntax();

	@Nonnull
	Optional<TableEntrySyntax<CheckConstraint, ListEntry>> checkInCreateTableSyntax();

	@Nonnull
	Optional<TableEntrySyntax<CheckConstraint, Statement>> addCheckToExistingTableSyntax();

	@Nonnull
	Optional<TableEntrySyntax<UniqueConstraint, ListEntry>> uniqueInCreateTableSyntax();

	@Nonnull
	Optional<TableEntrySyntax<UniqueConstraint, Statement>> addUniqueToExistingTableSyntax();

	@Nonnull
	Optional<TableEntrySyntax<ForeignKey, ListEntry>> referenceInCreateTableSyntax();

	@Nonnull
	Optional<TableEntrySyntax<ForeignKey, Statement>> addReferenceToExistingTableSyntax();

	@Nonnull
	Optional<TableEntrySyntax<ColumnInfo, Statement>> changeColumnForExistingTableSyntax();

	@Nonnull
	Optional<InsertSyntax> insert();

	final class ColumnInfo {
		@Nonnull public final Column column;
		@Nonnull public final String dataTypeName;
		@Nullable public final String autoValueName;
		@Nonnull public final MetaInfo.PrimaryKey primaryKey;

		public ColumnInfo(@Nonnull Column column, @Nonnull String dataTypeName, @Nullable String autoValueName, @Nonnull MetaInfo.PrimaryKey primaryKey) {
			this.column = column;
			this.dataTypeName = dataTypeName;
			this.autoValueName = autoValueName;
			this.primaryKey = primaryKey;
		}
	}
}
