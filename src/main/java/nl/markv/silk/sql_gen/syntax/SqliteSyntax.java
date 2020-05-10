package nl.markv.silk.sql_gen.syntax;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.NotImplementedException;

import nl.markv.silk.types.Column;
import nl.markv.silk.types.DataType;

public class SqliteSyntax extends GenericSyntax {

	public SqliteSyntax(@Nonnull String schemaName, @Nonnull String silkVersion, @Nonnull Syntax.SyntaxOptions options) {
		super(schemaName, silkVersion, options);
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
}
