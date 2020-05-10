package nl.markv.silk.sql_gen.syntax;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import nl.markv.silk.sql_gen.sqlparts.ListEntry;
import nl.markv.silk.sql_gen.sqlparts.Statement;
import nl.markv.silk.types.CheckConstraint;
import nl.markv.silk.types.Column;
import nl.markv.silk.types.DatabaseSpecific;
import nl.markv.silk.types.ForeignKey;
import nl.markv.silk.types.Table;
import nl.markv.silk.types.UniqueConstraint;

import static java.util.Collections.singletonList;
import static nl.markv.silk.sql_gen.sqlparts.ListEntry.listEntry;
import static nl.markv.silk.sql_gen.sqlparts.Statement.comment;
import static nl.markv.silk.sql_gen.sqlparts.Statement.statement;
import static org.apache.commons.lang3.Validate.isTrue;

/**
 * Attempt at a common version of SQL syntax.
 *
 * Dialect implementations can extend this and override their dialect's peculiarities.
 */
public abstract class GenericSyntax implements Syntax {

	private final boolean quoteNames;
	protected String schemaName;
	protected String silkVersion;

	public GenericSyntax(@Nonnull String schemaName, @Nonnull String silkVersion, @Nonnull Syntax.SyntaxOptions options) {
		this.schemaName = schemaName;
		this.silkVersion = silkVersion;
		this.quoteNames = options.quoteNames;
	}

	@Override
	@Nonnull
	public List<Statement> prelude(@Nullable DatabaseSpecific db) {
		return singletonList(comment("start schema ", schemaName));
	}

	@Override
	@Nonnull
	public List<Statement> postlude(@Nullable DatabaseSpecific db) {
		return singletonList(comment("end schema ", schemaName));
	}

	@Override
	@Nonnull
	public String startTable(@Nonnull Table table) {
		return "create table " + quoted(table.name) + " (";
	}

	@Override
	@Nonnull
	public String endTable(@Nonnull Table table) {
		return ")";
	}

	@Nonnull
	@Override
	public TableEntrySyntax<ColumnInfo, ListEntry> columnInCreateTableSyntax() {
		return (table, info) -> {
			StringBuilder sql = new StringBuilder();
			Column column = info.column;
			sql.append(quoted(column.name));
			sql.append(" ");
			sql.append(info.dataTypeName);
			if (info.primaryKey != MetaInfo.PrimaryKey.NotPart) {
				sql.append(" primary key");
			} else if (!column.nullable) {
				sql.append(" not null");
			}
			if (info.autoValueName != null) {
				isTrue(column.defaultValue == null);
				sql.append(" ");
				sql.append(info.autoValueName);
			} else if (column.defaultValue != null) {
				sql.append(" default ");
				sql.append(column.defaultValue);
			}
			return singletonList(listEntry(sql.toString()));
		};
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

	@Nonnull
	protected String nameFromCols(@Nullable String prefix, @Nonnull String table, @Nonnull List<String> columns) {
		StringBuilder sql = new StringBuilder();
		if (prefix != null) {
			sql.append(prefix);
			sql.append("_");
		}
		sql.append(table);
		sql.append("_");
		boolean isFirst = true;
		for (String col : columns) {
			if (isFirst) {
				isFirst = false;
			} else {
				sql.append("_");
			}
			sql.append(col);
		}
		return sql.toString();
	}

	@Nonnull
	protected String quoted(@Nonnull String name) {
		if (quoteNames) {
			return "'" + name + "'";
		}
		return name;
	}
}
