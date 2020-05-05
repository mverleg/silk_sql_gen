package nl.markv.silk.sql_gen;

import java.nio.file.Paths;

import nl.markv.silk.parse.GsonSilkParser;
import nl.markv.silk.pojos.v0_1_0.SilkSchema;

public class Main {
	public static void main(String[] args) {
		SilkSchema silk = new GsonSilkParser().parse(Paths.get("..", "..", "example", "shop.json"));

	}
}
