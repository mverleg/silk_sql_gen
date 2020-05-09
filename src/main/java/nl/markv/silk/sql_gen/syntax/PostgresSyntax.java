package nl.markv.silk.sql_gen.syntax;

import javax.annotation.Nonnull;

public abstract class PostgresSyntax extends GenericSyntax {

	public PostgresSyntax(@Nonnull String schemaName, @Nonnull String silkVersion) {
		super(schemaName, silkVersion);
	}

}
