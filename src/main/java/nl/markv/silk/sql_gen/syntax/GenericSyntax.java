package nl.markv.silk.sql_gen.syntax;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import nl.markv.silk.pojos.v0_1_0.LongColumn;
import nl.markv.silk.sql_gen.writer.SqlWriter;

/**
 * Attempt at a common version of SQL syntax.
 *
 * Dialect implementations can extend this and override only their dialect's peculiarities.
 */
public class GenericSyntax implements Syntax {

	protected String schemaName;
	protected String silkVersion;

	public GenericSyntax(@Nonnull String schemaName, @Nonnull String silkVersion) {
		this.schemaName = schemaName;
		this.silkVersion = silkVersion;
	}

	@Override
	public void prelude(@Nonnull SqlWriter sql) {
		this.schemaName = schemaName;
		this.silkVersion = silkVersion;
		sql.newline();
		sql.comment("start schema " + schemaName);
	}

	@Override
	public void postlude(@Nonnull SqlWriter sql) {
		sql.newline();
		sql.comment("end schema " + schemaName);
		sql.newline();
	}

	@Override
	public void startTable(@Nonnull SqlWriter sql, @Nullable String group, @Nonnull String name, @Nullable String description) {
		if (description != null) {
			sql.comment(description);
		}
		sql.add("create table ");
		sql.add(name);
		sql.addLine(" {");
	}

	@Override
	public void endTable(@Nonnull SqlWriter sql, @Nullable String group, @Nonnull String name) {
		sql.addLine("}");
	}

	@Override
	public String dataTypeName(@Nonnull SqlWriter sql, @Nonnull String type) {
		return null;
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
	public void columnInCreateTable(@Nonnull SqlWriter sql, @Nonnull String name, @Nonnull String dataTypeName, boolean nullable, @Nullable String autoValueName, @Nullable String defaultValue) {

	}

	@Override
	public void autoValueAfterCreation(@Nonnull SqlWriter sql, @Nonnull String columnName, @Nonnull String dataType, @Nonnull String autoValue) {

	}

	@Override
	public void primaryKeyInCreateTable(@Nonnull SqlWriter sql, @Nonnull List<String> primaryKey) {

	}
}
