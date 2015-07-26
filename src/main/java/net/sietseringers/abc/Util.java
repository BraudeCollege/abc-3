package net.sietseringers.abc;

import it.unisa.dia.gas.jpbc.Element;

import java.math.BigInteger;
import java.security.MessageDigest;

public class Util {
	public static Element getChallenge(Element D, BigInteger nonce) {
		byte[] hash = null;
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			digest.update(D.toBytes());
			hash = digest.digest(nonce.toByteArray());
		} catch (Exception e) {}
		return SystemParameters.e.getZr().newElementFromBytes(hash);
	}
}
