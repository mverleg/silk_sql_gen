package nl.markv.silk.sql_gen.sqlparts;

import javax.annotation.Nonnull;

public class StringListEntry implements ListEntry {

	@Nonnull
	final String[] messageParts;

	public StringListEntry(@Nonnull String... messageParts) {
		this.messageParts = messageParts;
	}

	@Override
	public void entryText(@Nonnull StringBuilder sql, boolean isLast) {
		sql.append("    ");
		for (String msg : messageParts) {
			sql.append(msg);
		}
		if (!isLast) {
			sql.append(",");
		}
		sql.append("\n");
	}
}
