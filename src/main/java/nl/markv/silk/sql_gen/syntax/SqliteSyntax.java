package nl.markv.silk.sql_gen.syntax;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.NotImplementedException;

import nl.markv.silk.sql_gen.sqlparts.ListEntry;
import nl.markv.silk.types.Column;
import nl.markv.silk.types.DataType;

import static java.util.Collections.singletonList;
import static nl.markv.silk.sql_gen.sqlparts.ListEntry.listEntry;
import static org.apache.commons.lang3.Validate.isTrue;

public class SqliteSyntax extends GenericInlineSyntax {

	public SqliteSyntax(@Nonnull String schemaName, @Nonnull String silkVersion, @Nonnull Syntax.SyntaxOptions options) {
		super(schemaName, silkVersion, options);
	}

	@Nonnull
	@Override
	protected String dialectName() {
		return "sqlite3";
	}

	@Nonnull
	@Override
	public String autoValueName(@Nonnull Column.AutoOptions autoValue) {
		switch (autoValue) {
			case INCREMENT:
				return "autoincrement";
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
			sql.append(info.dataTypeName);
			if (info.primaryKey == MetaInfo.PrimaryKey.Single) {
				sql.append(" primary key");
			}
			if (info.primaryKey == MetaInfo.PrimaryKey.NotPart) {
				if (info.autoValueName != null) {
					isTrue(column.defaultValue == null);
					sql.append(" ");
					sql.append(info.autoValueName);
				} else if (column.defaultValue != null) {
					sql.append(" default ");
					sql.append(valueToSql(column.type, column.defaultValue));
				}
			}
			if (!column.nullable) {
				sql.append(" not null");
			}
			return singletonList(listEntry(sql.toString()));
		};
	}
}
