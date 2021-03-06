package nl.markv.silk.sql_gen;

import nl.markv.silk.example.Examples;
import nl.markv.silk.sql_gen.syntax.Syntax;
import nl.markv.silk.types.SilkSchema;

public class Main {
	public static void main(String[] args) {
		for (SilkSchema schema : new Examples().schemas()) {
			StringBuilder sql = new StringBuilder();
			Generator.generate(sql, schema, Generator.Dialect.Sqlite, new Syntax.SyntaxOptions(true, false));
			System.out.println(sql.toString());
		}
	}
}
