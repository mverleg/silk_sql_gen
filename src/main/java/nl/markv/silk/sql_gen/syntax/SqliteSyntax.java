package nl.markv.silk.sql_gen.syntax;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.NotImplementedException;

import nl.markv.silk.pojos.v0_1_0.LongColumn;
import nl.markv.silk.sql_gen.writer.SqlWriter;
import nl.markv.silk.types.DataType;

public class SqliteSyntax extends GenericSyntax {

	public SqliteSyntax(@Nonnull String schemaName, @Nonnull String silkVersion) {
		super(schemaName, silkVersion);
	}

	@Override
	public String autoValueName(@Nonnull SqlWriter sql, @Nonnull LongColumn.AutoOptions autoValue) {
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

	@Override
	public String dataTypeName(@Nonnull SqlWriter sql, @Nonnull DataType type) {
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
