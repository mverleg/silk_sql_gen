package nl.markv.silk.sql_gen.syntax;

import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import nl.markv.silk.sql_gen.sqlparts.ListEntry;
import nl.markv.silk.sql_gen.sqlparts.Statement;
import nl.markv.silk.types.CheckConstraint;
import nl.markv.silk.types.ForeignKey;
import nl.markv.silk.types.UniqueConstraint;

import static java.util.Collections.singletonList;
import static nl.markv.silk.sql_gen.sqlparts.ListEntry.listEntry;
import static nl.markv.silk.sql_gen.sqlparts.Statement.statement;

/**
 * Attempt at a common version of SQL syntax, that attempts to add constraints at the end.
 */
public abstract class GenericAlterSyntax extends GenericSyntax {

	public GenericAlterSyntax(@Nonnull String schemaName, @Nonnull String silkVersion, @Nonnull SyntaxOptions options) {
		super(schemaName, silkVersion, options);
	}

	@Nonnull
	@Override
	public Optional<TableEntrySyntax<CheckConstraint, ListEntry>> checkInCreateTableSyntax() {
		// Checks are added after table creation.
		return Optional.empty();
	}

	@Nonnull
	@Override
	public Optional<TableEntrySyntax<CheckConstraint, Statement>> addCheckToExistingTableSyntax() {
		return Optional.of((table, check) -> singletonList(statement(
				"alter table ",
				quoted(table.name),
				" add constraint ",
				quoted(check.name == null ? nameFromHash("c_" + table.name, check.condition) : check.name),
				" check (",
				check.condition,
				")"
		)));
	}

	@Nonnull
	@Override
	public Optional<TableEntrySyntax<UniqueConstraint, ListEntry>> uniqueInCreateTableSyntax() {
		// Unique constraints are added after table creation.
		return Optional.empty();
	}

	@Nonnull
	@Override
	public Optional<TableEntrySyntax<UniqueConstraint, Statement>> addUniqueToExistingTableSyntax() {
		return Optional.of((table, unique) -> singletonList(statement(
				"alter table ",
				quoted(table.name),
				" add constraint ",
				quoted(unique.name == null ? nameFromCols("u_", table.name, unique.columnsNames) : unique.name),
				" unique(",
				String.join(", ", unique.columnsNames),
				")"
		)));
	}

	@Nonnull
	@Override
	public Optional<TableEntrySyntax<ForeignKey, ListEntry>> referenceInCreateTableSyntax() {
		// Foreign keys are added after table creation.
		return Optional.empty();
	}

	@Nonnull
	@Override
	public Optional<TableEntrySyntax<ForeignKey, Statement>> addReferenceToExistingTableSyntax() {
		return Optional.of((table, fk) -> singletonList(statement(
				"alter table ",
				quoted(fk.sourceTable.name),
				" add constraint ",
				quoted(fk.name != null ? fk.name : nameFromHash(
						"f_" + table.name + "_" + fk.sourceTable,
						String.join("_", fk.sourceColumns(c -> c.name)))),
				" foreign key (",
				String.join(", ", fk.sourceColumns(c -> quoted(c.name))),
				") references ",
				quoted(fk.targetTableName),
				" (",
				String.join(", ", fk.targetColumns(c -> quoted(c.name))),
				")"
		)));
	}

	@Nonnull
	@Override
	public Optional<TableEntrySyntax<ColumnInfo, Statement>> changeColumnForExistingTableSyntax() {
		return Optional.empty();
	}
}
