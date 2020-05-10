package nl.markv.silk.sql_gen.syntax;

import javax.annotation.Nonnull;

public abstract class DB2Syntax extends GenericSyntax {

	public DB2Syntax(@Nonnull String schemaName, @Nonnull String silkVersion, @Nonnull Syntax.SyntaxOptions options) {
		super(schemaName, silkVersion, options);
	}

}
