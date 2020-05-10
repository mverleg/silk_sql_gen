package nl.markv.silk.sql_gen.sqlparts;

import javax.annotation.Nonnull;

public class StringEmptyLine implements Statement, ListEntry {

	@Nonnull
	public static StringEmptyLine emptyLine() {
		return new StringEmptyLine();
	}

	@Override
	public void entryText(@Nonnull StringBuilder sql, boolean isLast) {
		sql.append("\n");
	}

	@Override
	public void statementText(@Nonnull StringBuilder sql) {
		sql.append("\n");
	}
}
