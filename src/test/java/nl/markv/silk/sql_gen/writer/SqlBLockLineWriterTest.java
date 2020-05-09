package nl.markv.silk.sql_gen.writer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SqlBLockLineWriterTest {

	@Test
	void noStatement() {
		SqlStringWriter sql = new SqlStringWriter();
		sql.statements(() -> {});
		assertEquals("", sql.build());
	}

	@Test
	void singleStatement() {
		SqlStringWriter sql = new SqlStringWriter();
		sql.statements(() -> sql.add("drop table Users"));
		assertEquals("drop table Users;\n", sql.build());
	}

	@Test
	void multipleStatementsOneWrite() {
		SqlStringWriter sql = new SqlStringWriter();
		sql.statements(() -> sql.add("drop table Users;\ndrop table Payments"));
		assertEquals("drop table Users;\ndrop table Payments;\n", sql.build());
	}

	@Test
	void multipleStatementsMultipleWrite() {
		SqlStringWriter sql = new SqlStringWriter();
		sql.statements(() -> {
			sql.addLine("drop table Users;");
			sql.add("drop table Payments");
		});
		assertEquals("drop table Users;\ndrop table Payments;\n", sql.build());
	}

	@Test
	void lineBlockWithoutLines() {
		SqlStringWriter sql = new SqlStringWriter();
		sql.addLine("(");
		sql.lineBlock(() -> {});
		sql.addLine(")");
		assertEquals("(\n\n)\n", sql.build());
	}

	@Test
	void lineBlockSingleLines() {
		SqlStringWriter sql = new SqlStringWriter();
		sql.add("(\n\t");
		sql.lineBlock(() ->
			sql.line(() -> sql.add("int id auto_increment"))
		);
		sql.addLine(")");
		assertEquals("(\n\tint id auto_increment\n)\n", sql.build());
	}

	@Test
	void lineBlockTwoLines() {
		SqlStringWriter sql = new SqlStringWriter();
		sql.add("(\n\t");
		sql.lineBlock(() -> {
			sql.line(() -> sql.add("int id auto_increment"));
			sql.line(() -> sql.add("text name not null"));
		});
		sql.addLine(")");
		assertEquals("(\n\tint id auto_increment,\n\ttext name not null\n)\n", sql.build());
	}

	@Test
	void lineTwoBlockTwoLines() {
		SqlStringWriter sql = new SqlStringWriter();
		sql.add("(\n\t");
		sql.lineBlock(() -> {
			sql.line(() -> sql.add("int id auto_increment"));
			sql.line(() -> sql.add("text name not null"));
		});
		sql.addLine(")");
		sql.statements(() -> sql.add("drop table Users"));
		sql.add("(\n\t");
		sql.lineBlock(() -> {
			sql.line(() -> sql.add("int age"));
			sql.line(() -> sql.add("int length_cm"));
		});
		sql.addLine(")");
		assertEquals("(\n\tint id auto_increment,\n\ttext name not null\n)\ndrop table Users;\n(\n\tint age,\n\tint length_cm\n)\n", sql.build());
	}
}