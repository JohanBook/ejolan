package util;

import java.util.Random;

// A class for generating random city names
public class NameGenerator {
	private final static String[] a = { "Am", "Arl", "Ash", "Ax", "Bark",
			"Bed", "Bell", "Bod", "Brid", "Bris", "Bucking", "Cam", "Car",
			"Cast", "Coles", "Down", "Fare", "Green", "Grims", "Guild", "Hamp",
			"Hart", "Hedge", "Hog", "Jol", "Kern", "Kimber", "Lon", "Mary",
			"Mid", "Mil", "Nor", "Oak", "Ox", "Perl", "Pock", "Port", "Red",
			"Ring", "Rocking", "Sea", "Skyl", "Stock", "Tel", "Va", "Wash",
			"Wey", "Wilm", "Win", "Winter", "York" };
	private final static String[] b = { "abri", "bog", "bury", "brook",
			"chine", "cleft", "cliff", "bridge", "burg", "by", "dale", "dam",
			"deen", "don", "dorp", "fen", "field", "ford", "gate", "glade",
			"gust", "ham", "haven", "hill", "ing", "ington", "leigh", "ley",
			"lorn", "mere", "mill", "more", "palace", "ridge", "rill", "rim",
			"scaur", "shire", "ton", "town", "ty", "vale", "ville", "wartz",
			"way", "wich", "wick" };
	private final static String[] prefix = { "East", "French", "Lake", "New",
			"North", "Old", "South", "West" };
	private final static String[] sufix = { "Castle", "City", "Island",
			"Harbour", "Royd" };

	private final static Random random = new Random(0);

	public static String getName() {
		boolean bool = false;
		String name = "";

		// Add prefix
		if (random.nextInt(10) < 1) {
			name += prefix[random.nextInt(prefix.length)] + " ";
			bool = true;
		}
		if (!bool && random.nextInt(10) < 1) {
			name += prefix[random.nextInt(prefix.length)];
			bool = true;
		} else
			name += a[random.nextInt(a.length)];

		// Add second part of name
		name += b[random.nextInt(b.length)];

		// Add suffix
		if (!bool && random.nextInt(10) < 1)
			name += " " + sufix[random.nextInt(sufix.length)];

		return name;
	}
}
