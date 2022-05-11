package net.unicraft.skyresources.utilities;

import java.util.Random;

public class RandomGenerator {

	public static final double getRandomChanceDouble() {
		return new Random().nextInt(1000000)/10000.0000;
	}
	
}
