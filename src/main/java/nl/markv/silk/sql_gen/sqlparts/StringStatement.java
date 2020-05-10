package nl.markv.silk.sql_gen.sqlparts;

import javax.annotation.Nonnull;

public class StringStatement implements Statement {

	@Nonnull
	final String[] messageParts;

	public StringStatement(@Nonnull String... messageParts) {
		this.messageParts = messageParts;
	}

	@Override
	public void statementText(@Nonnull StringBuilder sql) {
		for (String msg : messageParts) {
			sql.append(msg);
		}
		sql.append(";\n");
	}
}
