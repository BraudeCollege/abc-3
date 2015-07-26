package net.sietseringers.abc;

import java.util.*;
import java.lang.*;

import it.unisa.dia.gas.jpbc.*;
import sun.security.util.BigInt;

class Main
{
	public static void main (String[] args) throws java.lang.Exception
	{
		Pairing e = SystemParameters.e;

		PrivateKey sk = new PrivateKey(4);

		System.out.println(sk.toString());
		System.out.println();
		System.out.println(sk.publicKey.toString());

		Field Zn = e.getZr();
		ElementList ki = new ElementList(5);

		for (int i = 0; i <= 4; i++) {
			ki.add(Zn.newElement(i));
		}

		Credential c = sk.sign(ki);

		Map<Integer,Boolean> disclosed = new HashMap<>(4);
		disclosed.put(1, false);
		disclosed.put(2, true);
		disclosed.put(3, false);
		disclosed.put(4, true);

		ProofD proof = c.getDisclosureProof(e.getZr().newRandomElement().toBigInteger(), disclosed);

		System.out.println(proof.isValid(sk.publicKey));
	}
}
