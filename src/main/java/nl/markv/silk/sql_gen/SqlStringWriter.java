package nl.markv.silk.sql_gen;

import javax.annotation.Nonnull;

public class SqlStringWriter {

	private StringBuilder sql = new StringBuilder();

	public void add(@Nonnull String txt) {
		sql.append(txt);
	}

	@Nonnull
	public String build() {
		return sql.toString();
	}
}
