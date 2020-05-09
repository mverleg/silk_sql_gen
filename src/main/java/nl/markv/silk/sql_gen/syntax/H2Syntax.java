package nl.markv.silk.sql_gen.syntax;

import javax.annotation.Nonnull;

public abstract class H2Syntax extends GenericSyntax {

	public H2Syntax(@Nonnull String schemaName, @Nonnull String silkVersion) {
		super(schemaName, silkVersion);
	}

}
