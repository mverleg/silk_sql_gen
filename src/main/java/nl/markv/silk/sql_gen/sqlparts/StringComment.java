package nl.markv.silk.sql_gen.sqlparts;

import javax.annotation.Nonnull;

public class StringComment implements Statement, ListEntry {

	@Nonnull
	final String[] messageParts;

	public StringComment(@Nonnull String... messageParts) {
		this.messageParts = messageParts;
	}

	@Override
	public void entryText(@Nonnull StringBuilder sql, boolean isLast) {
		sql.append("    -- ");
		for (String msg : messageParts) {
			sql.append(msg);
		}
		sql.append("\n");
	}

	@Override
	public void statementText(@Nonnull StringBuilder sql) {
		for (String msg : messageParts) {
			sql.append(msg);
		}
		sql.append(";\n");
	}
}
