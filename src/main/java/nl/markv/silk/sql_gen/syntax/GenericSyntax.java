package nl.markv.silk.sql_gen.syntax;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.NotImplementedException;

import nl.markv.silk.sql_gen.writer.SqlWriter;
import nl.markv.silk.types.DataType;

import static org.apache.commons.lang3.Validate.isTrue;

/**
 * Attempt at a common version of SQL syntax.
 *
 * Dialect implementations can extend this and override only their dialect's peculiarities.
 */
public abstract class GenericSyntax implements Syntax {

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
	public void columnInCreateTable(@Nonnull SqlWriter sql, @Nonnull String name, @Nonnull String dataTypeName, boolean nullable, @Nullable String autoValueName, @Nullable String defaultValue) {
		sql.add("\t");
		sql.add(name, dataTypeName);
		if (!nullable) {
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
		sql.addLine(",");
	}

	@Override
	public void autoValueAfterCreation(@Nonnull SqlWriter sql, @Nonnull String columnName, @Nonnull String dataType, @Nonnull String autoValue) {

	}

	@Override
	public void primaryKeyInCreateTable(@Nonnull SqlWriter sql, @Nonnull List<String> primaryKey) {

	}
}
