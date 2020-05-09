package nl.markv.silk.sql_gen.writer;

import javax.annotation.Nonnull;

public class SqlStringWriter implements SqlWriter {

	private final StringBuilder sql = new StringBuilder();
	private int writeCount = 0;
	private boolean needsLeadingComma = false;

	@Override
	public void add(@Nonnull String txt) {
		writeCount++;
		sql.append(txt);
	}

	@Override
	public void statements(@Nonnull Runnable writeAction) {
		int oldWriteCount = writeCount;
		writeAction.run();
		if (writeCount != oldWriteCount) {
			addLine(";");
		}
	}

	@Override
	public void lineBlock(@Nonnull Runnable writeAction) {
		needsLeadingComma = true;
		writeAction.run();
		newline();
		needsLeadingComma = false;
	}

	@Override
	public void line(@Nonnull Runnable writeAction) {
		if (needsLeadingComma) {
			needsLeadingComma = false;
		} else {
			add(",\n\t");
		}
		int oldWriteCount = writeCount;
		writeAction.run();
		if (writeCount != oldWriteCount) {
			needsLeadingComma = false;
		}
	}

	@Override
	public void comment(@Nonnull String text) {
		for (String line : text.split("\\n")) {
			add("-- ");
			addLine(line);
		}
	}

	@Nonnull
	public String build() {
		return sql.toString();
	}
}
