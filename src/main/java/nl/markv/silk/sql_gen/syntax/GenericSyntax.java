package nl.markv.silk.sql_gen.syntax;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import nl.markv.silk.sql_gen.writer.SqlWriter;
import nl.markv.silk.types.CheckConstraint;
import nl.markv.silk.types.Column;
import nl.markv.silk.types.DatabaseSpecific;
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
	public void startTable(@Nonnull SqlWriter sql, @Nonnull Table table, @Nullable DatabaseSpecific db) {
		if (table.description != null) {
			sql.comment(table.description);
		}
		sql.add("create table ");
		sql.add(table.name);
		sql.addLine(" {");
	}

	@Override
	public void endTable(@Nonnull SqlWriter sql, @Nonnull Table table, @Nullable DatabaseSpecific db) {
		sql.addLine("};");
	}

	@Nullable
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
			sql.add(",");
			sql.newline();
		};
	}

	@Nullable
	@Override
	public TableEntrySyntax<List<Column>> primaryKeyInCreateTableSyntax() {
		// Primary key is specified inline by default.
		return null;
	}

	@Nullable
	@Override
	public TableEntrySyntax<List<Column>> addPrimaryKeyToExistingTableSyntax() {
		// Primary key is specified inline by default.
		return null;
	}

	@Nullable
	@Override
	public TableEntrySyntax<CheckConstraint> checkInCreateTableSyntax() {
		return (sql, table, check) -> {
			sql.add("\tcheck(");
			sql.add(check.condition);
			sql.addLine("),");
			//TODO @mark: do something with those commas
		};
	}

	@Nullable
	@Override
	public TableEntrySyntax<CheckConstraint> addCheckToExistingTableSyntax() {
		return null;
	}

	@Nullable
	@Override
	public TableEntrySyntax<UniqueConstraint> uniqueInCreateTableSyntax() {
		return null;
	}

	@Nullable
	@Override
	public TableEntrySyntax<UniqueConstraint> addUniqueToExistingTableSyntax() {
		return null;
	}

	@Nullable
	@Override
	public TableEntrySyntax<UniqueConstraint> referenceInCreateTableSyntax() {
		return null;
	}

	@Nullable
	@Override
	public TableEntrySyntax<UniqueConstraint> addReferenceToExistingTableSyntax() {
		return null;
	}

	@Nullable
	@Override
	public TableEntrySyntax<AutoValueInfo> addDefaultValueToExistingTableSyntax() {
		return null;
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
