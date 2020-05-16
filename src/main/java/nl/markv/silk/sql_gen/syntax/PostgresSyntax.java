package nl.markv.silk.sql_gen.syntax;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.NotImplementedException;

import nl.markv.silk.sql_gen.sqlparts.ListEntry;
import nl.markv.silk.types.Column;
import nl.markv.silk.types.DataType;

import static java.util.Collections.singletonList;
import static nl.markv.silk.sql_gen.sqlparts.ListEntry.listEntry;
import static org.apache.commons.lang3.Validate.isTrue;

public class PostgresSyntax extends GenericAlterSyntax {
	// Hint: to start a quick docker database for testing, use
	// docker run --tmpfs=/pgtmpfs:size=500M -p5432:5432 -e PGDATA=/pgtmpfs -e POSTGRES_PASSWORD=test postgres:12
	// then connect to jdbc:postgresql://localhost:5432/postgres using credentials postgres:test

	public PostgresSyntax(@Nonnull String schemaName, @Nonnull String silkVersion, @Nonnull Syntax.SyntaxOptions options) {
		super(schemaName, silkVersion, options);
	}

	@Nonnull
	@Override
	protected String dialectName() {
		return "postgres12";
	}

	@Nullable
	@Override
	public String autoValueName(@Nonnull Column.AutoOptions autoValue) {
		switch (autoValue) {
			case INCREMENT:
				return null;
			case CREATED_TIMESTAMP:
				return "default current_timestamp";
			case UPDATED_TIMESTAMP:
				return "default current_timestamp";
		}
		throw new UnsupportedOperationException("unknown auto data value " + autoValue);
	}

	@Nonnull
	@Override
	public String dataTypeName(@Nonnull DataType type) {
		if (type instanceof DataType.Text) {
			return "text";
		}
		if (type instanceof DataType.Int) {
			return "integer";
		}
		if (type instanceof DataType.Decimal) {
			return "real";
		}
		if (type instanceof DataType.Timestamp) {
			return "text";
		}
		throw new NotImplementedException("unknown type: " + type);
	}

	@Nonnull
	@Override
	public TableEntrySyntax<ColumnInfo, ListEntry> columnInCreateTableSyntax() {
		return (table, info) -> {
			StringBuilder sql = new StringBuilder();
			Column column = info.column;
			sql.append(quoted(column.name));
			sql.append(" ");
			if (column.autoValue == Column.AutoOptions.INCREMENT) {
				isTrue(column.type instanceof DataType.Int);
				sql.append("serial");
			} else {
				sql.append(info.dataTypeName);
			}
			if (info.primaryKey == MetaInfo.PrimaryKey.Single) {
				sql.append(" primary key");
			} else if (info.autoValueName != null) {
				isTrue(column.defaultValue == null);
				sql.append(" ");
				sql.append(info.autoValueName);
			} else if (column.defaultValue != null) {
				sql.append(" default ");
				sql.append(valueToSql(column.type, column.defaultValue));
			} else if (!column.nullable) {
				sql.append(" not null");
			}
			return singletonList(listEntry(sql.toString()));
		};
	}
}
