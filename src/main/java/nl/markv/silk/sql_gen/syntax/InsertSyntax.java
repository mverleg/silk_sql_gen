package nl.markv.silk.sql_gen.syntax;

import javax.annotation.Nonnull;

import nl.markv.silk.types.Table;

public interface InsertSyntax {

	int rowsPerStatement();

	@Nonnull
	String opening(@Nonnull Table table);



}
