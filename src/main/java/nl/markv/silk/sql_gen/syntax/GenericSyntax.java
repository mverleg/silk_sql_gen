package nl.markv.silk.sql_gen.syntax;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import nl.markv.silk.sql_gen.sqlparts.ListEntry;
import nl.markv.silk.sql_gen.sqlparts.Statement;
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

	protected final boolean quoteNames;
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
			} else if (info.autoValueName != null) {
				isTrue(column.defaultValue == null);
				sql.append(" ");
				sql.append(info.autoValueName);
			} else if (column.defaultValue != null) {
				sql.append(" default ");
				sql.append(column.defaultValue);
			} else if (!column.nullable) {
				sql.append(" not null");
			}
			return singletonList(listEntry(sql.toString()));
		};
	}

	@Nonnull
	@Override
	public Optional<TableEntrySyntax<List<Column>, ListEntry>> primaryKeyInCreateTableSyntax() {
		// Primary key is specified inline by default.
		//TODO: perhaps this will need to change when composite primary keys are supported
		return Optional.empty();
	}

	@Nonnull
	@Override
	public Optional<TableEntrySyntax<List<Column>, Statement>> addPrimaryKeyToExistingTableSyntax() {
		// Primary key is specified inline by default.
		//TODO: perhaps this will need to change when composite primary keys are supported
		return Optional.empty();
	}

	@Nonnull
	@Override
	public Optional<TableEntrySyntax<UniqueConstraint, Statement>> addUniqueToExistingTableSyntax() {
		// Unicity is added when creating table by default,
		// but this hook is used to add an index on unique columns that don't have one.
		return Optional.of((table, unique) -> singletonList(statement(
				"create unique index if not exists ",
				quoted(nameFromCols("i", unique.table.name, unique.columnsNames)),
				" on ",
				quoted(table.name),
				" (",
				unique.columnsNames.stream().map(n -> quoted(n)).collect(Collectors.joining(", ")),
				")"
		)));
	}

	@Nonnull
	@Override
	public Optional<TableEntrySyntax<ForeignKey, Statement>> addReferenceToExistingTableSyntax() {
		// Foreign keys are added when creating table by default,
		// but this hook is used to add an index on columns that are references by a foreign key.
		// This happens after unique constraint indices, so if both apply, the unique index prevails.
		return Optional.of((table, fk) -> singletonList(statement(
				"create index if not exists ",
				quoted(nameFromCols("i", fk.targetTableName, fk.targetColumns(c -> c.name))),
				" on ",
				quoted(table.name),
				" (",
				String.join(", ", fk.targetColumns(c1 -> quoted(c1.name))),
				")"
		)));
	}

	@Nonnull
	protected static String nameFromCols(@Nullable String prefix, @Nonnull String table, @Nonnull List<String> columns) {
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
	protected static String nameFromHash(@Nonnull String prefix, @Nonnull String hashInput) {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException ex) {
			throw new IllegalStateException(ex);
		}
		byte[] bytes = digest.digest(hashInput.getBytes(StandardCharsets.UTF_8));
		String hash = new String(Base64.getEncoder().encode(bytes))
				.replace("+", "")
				.replace("/", "")
				.replace("=", "");
		return prefix + hash.substring(0, 16);
	}

	@Nonnull
	protected String quoted(@Nonnull String name) {
		if (quoteNames) {
			return "'" + name + "'";
		}
		return name;
	}
}
