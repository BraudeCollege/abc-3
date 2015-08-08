/*
 * PrivateKey.java
 * Copyright (C) 2015 Sietse Ringers
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 * USA
 */

package net.sietseringers.abc;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;

public class PrivateKey {
	private Pairing e = SystemParameters.e;
	private Field G1;
	private Field G2;
	private Field Zn;

	private PublicKey publicKey;
	private int n;

	private Element a;
	private ElementList ai;
	private Element z;

	public PrivateKey(int n) {
		this.ai = new ElementList(n + 1);

		this.e = e;
		this.n = n;
		this.G1 = e.getG1();
		this.G2 = e.getG2();
		this.Zn = e.getZr();

		Element Q = G2.newRandomElement();

		a = Zn.newRandomElement();
		z = Zn.newRandomElement();

		Element A = Q.duplicate().powZn(geta());
		Element Z = Q.duplicate().powZn(getz());

		ElementList Ai = new ElementList(n+1);
		for (int i = 0; i <= n; i++) {
			Element newai = Zn.newRandomElement();
			Element newAi = Q.duplicate().powZn(newai);
			ai.add(newai);
			Ai.add(newAi);
		}

		publicKey = new PublicKey(Q, A, Ai, Z);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("PrivateKey: ")
				.append("z=").append(z.toString())
				.append("\na=").append(a.toString());

		for (int i = 0; i <= n; i++) {
			sb.append("\na").append(i).append("=").append(ai.get(i).toString());
		}

		return sb.toString();
	}

	public PublicKey getPublicKey() {
		return publicKey;
	}

	public int getN() {
		return n;
	}

	public Element geta() {
		return a.duplicate();
	}

	public Element getai(int i) {
		return ai.get(i).duplicate();
	}

	public Element getz() {
		return z.duplicate();
	}
}
