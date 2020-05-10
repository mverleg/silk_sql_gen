package nl.markv.silk.sql_gen.syntax;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import static nl.markv.silk.sql_gen.sqlparts.Statement.comment;
import static org.apache.commons.lang3.Validate.isTrue;

/**
 * Attempt at a common version of SQL syntax.
 *
 * Dialect implementations can extend this and override their dialect's peculiarities.
 */
public abstract class GenericSyntax implements Syntax {

	protected String schemaName;
	protected String silkVersion;

	public GenericSyntax(@Nonnull String schemaName, @Nonnull String silkVersion) {
		this.schemaName = schemaName;
		this.silkVersion = silkVersion;
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
	public List<String> startTable(@Nonnull Table table) {
		ArrayList<String> list = new ArrayList<>();
		if (table.description != null) {
			list.add("-- " + table.description);
		}
		list.add("create table '" + table.name + "' {");
		return list;
	}

	@Override
	@Nonnull
	public List<String> endTable(@Nonnull Table table) {
		return singletonList("};\n");
	}

	@Nonnull
	@Override
	public TableEntrySyntax<ColumnInfo, ListEntry> columnInCreateTableSyntax() {
		return (sql, table, info) -> {
			Column column = info.column;
			sql.add("\t");
			sql.add(column.name, info.dataTypeName);
			if (info.primaryKey != MetaInfo.PrimaryKey.NotPart) {
				sql.add(" primary key");
			} else if (!column.nullable) {
				sql.add(" not null");
			}
			if (info.autoValueName != null) {
				isTrue(column.defaultValue == null);
				sql.add(" ");
				sql.add(info.autoValueName);
			} else if (column.defaultValue != null) {
				sql.add(" default ");
				sql.add(column.defaultValue);
			}
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
	public Optional<TableEntrySyntax<List<Column>, List<Statement>>> addPrimaryKeyToExistingTableSyntax() {
		// Primary key is specified inline by default.
		return Optional.empty();
	}

	@Nonnull
	@Override
	public Optional<TableEntrySyntax<CheckConstraint, ListEntry>> checkInCreateTableSyntax() {
		return Optional.of((sql, table, check) -> {
			sql.add("\tcheck(");
			sql.add(check.condition);
			sql.addLine(")");
			//TODO @mark: do something with commas
		});
	}

	@Nonnull
	@Override
	public Optional<TableEntrySyntax<CheckConstraint, List<Statement>>> addCheckToExistingTableSyntax() {
		// Check is added when creating table by default.
		return Optional.empty();
	}

	@Nonnull
	@Override
	public Optional<TableEntrySyntax<UniqueConstraint, ListEntry>> uniqueInCreateTableSyntax() {
		return Optional.of((sql, table, unique) -> {
			sql.add("\tunique(");
			sql.delimitered(", ", unique.columnsNames);
			sql.addLine(")");
		});
	}

	@Nonnull
	@Override
	public Optional<TableEntrySyntax<UniqueConstraint, List<Statement>>> addUniqueToExistingTableSyntax() {
		// Unicity is added when creating table by default,
		// but this hook is used to add an index on unique columns that don't have one.
		return Optional.of((sql, table, unique) -> {
			sql.add("create unique index if not exists ");
			nameFromCols(sql, "i", unique.table.name, unique.columnsNames);
			sql.add(" on ");
			sql.add(unique.table.name);
			sql.add(" (");
			sql.delimitered(", ", unique.columnsNames);
			sql.add(")");
		});
	}

	@Nonnull
	@Override
	public Optional<TableEntrySyntax<ForeignKey, ListEntry>> referenceInCreateTableSyntax() {
		return Optional.empty();
	}

	@Nonnull
	@Override
	public Optional<TableEntrySyntax<ForeignKey, List<Statement>>> addReferenceToExistingTableSyntax() {
		return Optional.empty();
	}

	@Nonnull
	@Override
	public Optional<TableEntrySyntax<ColumnInfo, List<Statement>>> changeColumnForExistingTableSyntax() {
		return Optional.empty();
	}

	protected void nameFromCols(@Nullable String prefix, @Nonnull String table, @Nonnull List<String> columns) {
		if (prefix != null) {
			sql.add(prefix);
			sql.add("_");
		}
		sql.add(table);
		sql.add("_");
		sql.delimitered("_", columns);
	}
}
