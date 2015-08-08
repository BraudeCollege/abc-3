/*
 * PublicKey.java
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

public class PublicKey {
	private int n;
	private Pairing e = SystemParameters.e;
	private Field G1;
	private Field G2;
	private Field Zn;

	private Element Q;
	private Element A;
	private ElementList Ai;
	private Element Z;

	public PublicKey(int n) {
		this.n = n;
		this.G1 = e.getG1();
		this.G2 = e.getG2();
		this.Zn = e.getZr();
		this.Ai = new ElementList(n);
	}

	public PublicKey(Element Q, Element A, ElementList Ai, Element Z) {
		this(Ai.size());
		this.Q = Q;
		this.A = A;
		this.Ai = Ai;
		this.Z = Z;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("PrivateKey: ")
				.append("Z=").append(Z.toString())
				.append("\nA=").append(A.toString());

		for (int i = 0; i <= n; i++) {
			sb.append("\nA").append(i).append("=").append(Ai.get(i).toString());
		}

		return sb.toString();
	}

	public Element getQ() {
		return Q.duplicate();
	}

	public Element getA() {
		return A.duplicate();
	}

	public Element getAi(int i) {
		return Ai.get(i).duplicate();
	}

	public Element getZ() {
		return Z.duplicate();
	}
}
