package nl.markv.silk.sql_gen.syntax;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import nl.markv.silk.sql_gen.sqlparts.ListEntry;
import nl.markv.silk.sql_gen.sqlparts.Statement;
import nl.markv.silk.types.CheckConstraint;
import nl.markv.silk.types.Column;
import nl.markv.silk.types.ForeignKey;
import nl.markv.silk.types.UniqueConstraint;

import static java.util.Collections.singletonList;
import static nl.markv.silk.sql_gen.sqlparts.ListEntry.listEntry;
import static nl.markv.silk.sql_gen.sqlparts.Statement.statement;

/**
 * Attempt at a common version of SQL syntax, that creates as much as possible within the create table statement.
 */
public abstract class GenericInlineSyntax extends GenericSyntax {

	public GenericInlineSyntax(@Nonnull String schemaName, @Nonnull String silkVersion, @Nonnull SyntaxOptions options) {
		super(schemaName, silkVersion, options);
	}

	@Nonnull
	@Override
	public Optional<TableEntrySyntax<List<Column>, ListEntry>> primaryKeyInCreateTableSyntax() {
		// Primary key is specified inline by default.
		return Optional.empty();
	}

	@Nonnull
	@Override
	public Optional<TableEntrySyntax<List<Column>, Statement>> addPrimaryKeyToExistingTableSyntax() {
		// Primary key is specified inline by default.
		return Optional.empty();
	}

	@Nonnull
	@Override
	public Optional<TableEntrySyntax<CheckConstraint, ListEntry>> checkInCreateTableSyntax() {
		return Optional.of((table, check) -> singletonList(listEntry(
				"check(",
				check.condition,
				")"
		)));
	}

	@Nonnull
	@Override
	public Optional<TableEntrySyntax<CheckConstraint, Statement>> addCheckToExistingTableSyntax() {
		// Check is added when creating table by default.
		return Optional.empty();
	}

	@Nonnull
	@Override
	public Optional<TableEntrySyntax<UniqueConstraint, ListEntry>> uniqueInCreateTableSyntax() {
		return Optional.of((table, unique) -> singletonList(listEntry(
				"unique(",
				String.join(", ", unique.columnsNames),
				")"
		)));
	}

	@Nonnull
	@Override
	public Optional<TableEntrySyntax<UniqueConstraint, Statement>> addUniqueToExistingTableSyntax() {
		// Unicity is added when creating table by default,
		// but this hook is used to add an index on unique columns that don't have one.
		return Optional.of((table, unique) -> singletonList(statement(
			"create unique index if not exists ",
			nameFromCols("i", unique.table.name, unique.columnsNames),
			" on ",
			quoted(table.name),
			" (",
			unique.columnsNames.stream().map(n -> quoted(n)).collect(Collectors.joining(", ")),
			")"
		)));
	}

	@Nonnull
	@Override
	public Optional<TableEntrySyntax<ForeignKey, ListEntry>> referenceInCreateTableSyntax() {
		return Optional.of((table, fk) -> singletonList(listEntry(
				"foreign key (",
				fk.fromColumns().stream()
						.map(c -> quoted(c.name))
						.collect(Collectors.joining(", ")),
				") references ",
				quoted(fk.targetTableName),
				" (",
				fk.toColumns().stream()
						.map(c -> quoted(c.name))
						.collect(Collectors.joining(", ")),
				")"
		)));
	}

	@Nonnull
	@Override
	public Optional<TableEntrySyntax<ForeignKey, Statement>> addReferenceToExistingTableSyntax() {
		return Optional.empty();
	}

	@Nonnull
	@Override
	public Optional<TableEntrySyntax<ColumnInfo, Statement>> changeColumnForExistingTableSyntax() {
		return Optional.empty();
	}
}
