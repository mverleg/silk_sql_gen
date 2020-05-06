package nl.markv.silk.sql_gen.syntax;

import javax.annotation.Nonnull;

import nl.markv.silk.pojos.v0_1_0.LongColumn;
import nl.markv.silk.sql_gen.writer.SqlWriter;

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
}
