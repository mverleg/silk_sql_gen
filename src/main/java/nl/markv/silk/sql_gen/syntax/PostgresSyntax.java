package nl.markv.silk.sql_gen.syntax;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.NotImplementedException;

import nl.markv.silk.types.Column;
import nl.markv.silk.types.DataType;

public class PostgresSyntax extends GenericAlterSyntax {
	// Hint: to start a quick docker database for testing, use
	// docker run --tmpfs=/pgtmpfs:size=500M -p5432:5432 -e PGDATA=/pgtmpfs -e POSTGRES_PASSWORD=test postgres:12
	// then connect to jdbc:postgresql://localhost:5432/postgres using credentials postgres:test

	public PostgresSyntax(@Nonnull String schemaName, @Nonnull String silkVersion, @Nonnull Syntax.SyntaxOptions options) {
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

	@Nonnull
	@Override
	protected String quoted(@Nonnull String name) {
		if (quoteNames) {
			return "\"" + name + "\"";
		}
		return name;
	}
}
