package nl.markv.silk.sql_gen.syntax;

import javax.annotation.Nonnull;

import nl.markv.silk.sql_gen.writer.SqlWriter;

/**
 * Attempt at a common version of SQL syntax.
 *
 * Dialect implementations can extend this and override only their dialect's peculiarities.
 */
public class GenericSyntax implements Syntax {

	private String schemaName;
	private String silkVersion;

	public GenericSyntax(@Nonnull String schemaName, @Nonnull String silkVersion) {
		this.schemaName = schemaName;
		this.silkVersion = silkVersion;
	}

	@Override
	public void prelude(@Nonnull SqlWriter sql) {
		this.schemaName = schemaName;
		this.silkVersion = silkVersion;
		sql.newline();
		sql.add("-- start schema ");
		sql.addLine(schemaName);
		sql.newline();
	}

	@Override
	public void postlude(@Nonnull SqlWriter sql) {
		sql.add("-- end schema ");
		sql.addLine(schemaName);
	}
}
