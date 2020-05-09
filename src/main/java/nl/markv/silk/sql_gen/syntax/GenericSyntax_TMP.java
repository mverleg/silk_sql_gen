package nl.markv.silk.sql_gen.syntax;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import nl.markv.silk.sql_gen.writer.SqlWriter;
import nl.markv.silk.types.Column;
import nl.markv.silk.types.ColumnMapping;
import nl.markv.silk.types.DataType;
import nl.markv.silk.types.DatabaseSpecific;

import static org.apache.commons.lang3.Validate.isTrue;

/**
 * Attempt at a common version of SQL syntax.
 *
 * Dialect implementations can extend this and override their dialect's peculiarities.
 */
public class GenericSyntax_TMP implements Syntax {

	protected String schemaName;
	protected String silkVersion;

	public GenericSyntax_TMP(@Nonnull String schemaName, @Nonnull String silkVersion) {
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
	public void startTable(@Nonnull SqlWriter sql, @Nullable String group, @Nonnull String name, @Nullable String description, @Nullable DatabaseSpecific db) {
		if (description != null) {
			sql.comment(description);
		}
		sql.add("create table ");
		sql.add(name);
		sql.addLine(" {");
	}

	@Override
	public void endTable(@Nonnull SqlWriter sql, @Nullable String group, @Nonnull String name, @Nullable DatabaseSpecific db) {
		sql.addLine("};");
	}

	@Override
	public String dataTypeName(@Nonnull SqlWriter sql, @Nonnull DataType type) {
		return null;
	}

	@Override
	public String autoValueName(@Nonnull SqlWriter sql, @Nonnull Column.AutoOptions autoValue) {
		return null;
	}

	@Override
	public void columnInCreateTable(
			@Nonnull SqlWriter sql,
			@Nonnull String name,
			@Nonnull String dataTypeName,
			boolean nullable,
			MetaInfo.PrimaryKey primaryKey,
			@Nullable String autoValueName,
			@Nullable String defaultValue,
			boolean isLast,
			@Nullable DatabaseSpecific db
	) {
		sql.add("\t");
		sql.add(name, dataTypeName);
		if (primaryKey != MetaInfo.PrimaryKey.NotPart) {
			sql.add(" primary key");
		} else if (!nullable) {
			sql.add(" not null");
		}
		if (autoValueName != null) {
			isTrue(defaultValue == null);
			sql.add(" ");
			sql.add(autoValueName);
		} else if (defaultValue != null) {
			sql.add(" default ");
			sql.add(defaultValue);
		}
//		if (!isLast) {
			sql.add(",");
//		}
		sql.newline();
	}

	@Override
	public void autoValueAfterCreation(@Nonnull SqlWriter sql, @Nonnull String columnName, @Nonnull String dataType, @Nonnull String autoValue, @Nullable DatabaseSpecific db) {
		// Automatic values are specified inline by default.
	}

	@Override
	public void primaryKeyInCreateTable(@Nonnull SqlWriter sql, @Nonnull List<String> primaryKey, @Nullable DatabaseSpecific db) {
		// Primary key is specified inline by default.
	}

	@Override
	public void startTableUniqueConstraints(@Nonnull SqlWriter sql, @Nonnull String group, @Nonnull String name, @Nullable DatabaseSpecific databaseSpecific) {
		// Usually nothing to do here.
	}

	@Override
	public void endTableUniqueConstraints(@Nonnull SqlWriter sql, @Nonnull String group, @Nonnull String name, @Nullable DatabaseSpecific databaseSpecific) {
		// Usually nothing to do here.
	}

	@Override
	public void tableUniqueConstraintInline(@Nonnull SqlWriter sql, @Nonnull String group, @Nonnull String tableName, @Nullable String constraintName, @Nonnull List<ColumnMapping> columns, @Nullable DatabaseSpecific databaseSpecific) {
		sql.add("\tunique(");
		sql.delimitered(", ", columns);
		sql.addLine("),");
	}

	@Override
	public void tableUniqueConstraintAfter(@Nonnull SqlWriter sql, @Nonnull String group, @Nonnull String tableName, @Nullable String constraintName, @Nonnull List<String> columns, @Nullable DatabaseSpecific databaseSpecific) {
		sql.add("create unique index if not exists ");
		nameFromCols(sql, "i", tableName, columns);
		sql.add(" on ");
		sql.add(tableName);
		sql.add(" (");
		sql.delimitered(", ", columns);
		sql.addLine(");");
	}

	@Override
	public void startTableCheckConstraints(@Nonnull SqlWriter sql, @Nonnull String group, @Nonnull String name, @Nullable DatabaseSpecific databaseSpecific) {
		// Usually nothing to do here.
	}

	@Override
	public void tableCheckConstraintInline(@Nonnull SqlWriter sql, @Nonnull String group, @Nonnull String tableName, @Nullable String constraintName, @Nonnull String condition, @Nullable DatabaseSpecific databaseSpecific) {
		sql.add("\tcheck(");
		sql.add(condition);
		sql.addLine("),");
	}

	@Override
	public void tableCheckConstraintAfter(@Nonnull SqlWriter sql, @Nonnull String group, @Nonnull String tableName, @Nullable String constraintName, @Nonnull String condition, @Nullable DatabaseSpecific databaseSpecific) {
	}

	@Override
	public void endTableCheckConstraints(@Nonnull SqlWriter sql, @Nonnull String group, @Nonnull String name, @Nullable DatabaseSpecific databaseSpecific) {
		// Usually nothing to do here.
	}

	@Override
	public void startTableReferences(@Nonnull SqlWriter sql, @Nonnull String group, @Nonnull String tableName, @Nullable DatabaseSpecific databaseSpecific) {
		// Usually nothing to do here.
	}

	@Override
	public void endTableReferences(@Nonnull SqlWriter sql, @Nonnull String group, @Nonnull String tableName, @Nullable DatabaseSpecific databaseSpecific) {
		// Usually nothing to do here.
	}

	@Override
	public void tableReferenceAfter(@Nonnull SqlWriter sql, @Nonnull String group, @Nonnull String sourceTable, @Nullable String constraintName, @Nonnull String targetTable, @Nonnull List<ColumnMapping> columns, @Nullable DatabaseSpecific databaseSpecific) {
		//TODO @mark:
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
