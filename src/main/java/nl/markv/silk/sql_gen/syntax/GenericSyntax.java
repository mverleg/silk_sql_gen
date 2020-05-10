package nl.markv.silk.sql_gen.syntax;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import nl.markv.silk.sql_gen.writer.SqlWriter;
import nl.markv.silk.types.CheckConstraint;
import nl.markv.silk.types.Column;
import nl.markv.silk.types.DatabaseSpecific;
import nl.markv.silk.types.ForeignKey;
import nl.markv.silk.types.Table;
import nl.markv.silk.types.UniqueConstraint;

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
	public void prelude(@Nonnull SqlWriter sql, @Nullable DatabaseSpecific db) {
		this.schemaName = schemaName;
		this.silkVersion = silkVersion;
		sql.newline();
		sql.comment("start schema " + schemaName);
	}

	@Override
	public void postlude(@Nonnull SqlWriter sql, @Nullable DatabaseSpecific db) {
		sql.newline();
		sql.comment("end schema " + schemaName);
		sql.newline();
	}

	@Override
	public void startTable(@Nonnull SqlWriter sql, @Nonnull Table table) {
		if (table.description != null) {
			sql.comment(table.description);
		}
		sql.add("create table ");
		sql.add(table.name);
		sql.addLine(" {");
	}

	@Override
	public void endTable(@Nonnull SqlWriter sql, @Nonnull Table table) {
		sql.addLine("}");
	}

	@Nonnull
	@Override
	public TableEntrySyntax<ColumnInfo> columnInCreateTableSyntax() {
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
//			sql.add(",");
//			sql.newline();
		};
	}

	@Nonnull
	@Override
	public Optional<TableEntrySyntax<List<Column>>> primaryKeyInCreateTableSyntax() {
		// Primary key is specified inline by default.
		return Optional.empty();
	}

	@Nonnull
	@Override
	public Optional<TableEntrySyntax<List<Column>>> addPrimaryKeyToExistingTableSyntax() {
		// Primary key is specified inline by default.
		return Optional.empty();
	}

	@Nonnull
	@Override
	public Optional<TableEntrySyntax<CheckConstraint>> checkInCreateTableSyntax() {
		return Optional.of((sql, table, check) -> {
			sql.add("\tcheck(");
			sql.add(check.condition);
			sql.addLine(")");
			//TODO @mark: do something with commas
		});
	}

	@Nonnull
	@Override
	public Optional<TableEntrySyntax<CheckConstraint>> addCheckToExistingTableSyntax() {
		// Check is added when creating table by default.
		return Optional.empty();
	}

	@Nonnull
	@Override
	public Optional<TableEntrySyntax<UniqueConstraint>> uniqueInCreateTableSyntax() {
		return Optional.of((sql, table, unique) -> {
			sql.add("\tunique(");
			sql.delimitered(", ", unique.columnsNames);
			sql.addLine(")");
		});
	}

	@Nonnull
	@Override
	public Optional<TableEntrySyntax<UniqueConstraint>> addUniqueToExistingTableSyntax() {
		// Unicity is added when creating table by default,
		// but this hook is used to add an index on unique columns that don't have one.
		return Optional.of((sql, table, unique) -> {
			sql.add("create unique index if not exists ");
			nameFromCols(sql, "i", unique.table.name, unique.columnsNames);
			sql.add(" on ");
			sql.add(unique.table.name);
			sql.add(" (");
			sql.delimitered(", ", unique.columnsNames);
			sql.addLine(")");
		});
	}

	@Nonnull
	@Override
	public Optional<TableEntrySyntax<ForeignKey>> referenceInCreateTableSyntax() {
		return Optional.empty();
	}

	@Nonnull
	@Override
	public Optional<TableEntrySyntax<ForeignKey>> addReferenceToExistingTableSyntax() {
		return Optional.empty();
	}

	@Nonnull
	@Override
	public Optional<TableEntrySyntax<ColumnInfo>> changeColumnForExistingTableSyntax() {
		return Optional.empty();
	}

	protected void nameFromCols(@Nonnull SqlWriter sql, @Nullable String prefix, @Nonnull String table, @Nonnull List<String> columns) {
		if (prefix != null) {
			sql.add(prefix);
			sql.add("_");
		}
		sql.add(table);
		sql.add("_");
		sql.delimitered("_", columns);
	}
}
