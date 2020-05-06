package nl.markv.silk.sql_gen;

import nl.markv.silk.example.Examples;
import nl.markv.silk.parse.SilkDb;
import nl.markv.silk.sql_gen.writer.SqlStringWriter;

public class Main {
	public static void main(String[] args) {
		for (SilkDb db : new Examples().jsons()) {
			SqlStringWriter writer = new SqlStringWriter();
			Generator.generate(writer, db, Generator.Dialect.Sqlite);
		}
	}
}
