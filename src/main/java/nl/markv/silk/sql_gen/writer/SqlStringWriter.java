package nl.markv.silk.sql_gen.writer;

import javax.annotation.Nonnull;

public class SqlStringWriter extends SqlBLockLineWriter {

	private final StringBuilder sql = new StringBuilder();

	@Override
	public void add(@Nonnull String txt) {
		writeCount++;
		sql.append(txt);
	}

	@Nonnull
	public String build() {
		return sql.toString();
	}
}
