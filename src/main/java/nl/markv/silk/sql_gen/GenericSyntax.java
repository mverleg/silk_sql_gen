package nl.markv.silk.sql_gen;

import javax.annotation.Nonnull;

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
