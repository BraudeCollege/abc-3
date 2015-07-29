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
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.util.ArrayList;
import java.util.List;

public class PublicKey {
	public Pairing e;
	public PairingParameters params;
	public int n;
	private Field G1;
	private Field G2;
	private Field Zn;

	public Element Q;

	public Element A;
	public ElementList Ai;
	public Element Z;

	public PublicKey(PairingParameters params, int n) {
		this.params = params;
		this.e = SystemParameters.e;
		this.n = n;
		this.G1 = e.getG1();
		this.G2 = e.getG2();
		this.Zn = e.getZr();
		this.Ai = new ElementList(n);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("PrivateKey: ")
				.append(params.toString("="))
				.append("Z=").append(Z.toString())
				.append("\nA=").append(A.toString());

		for (int i = 0; i <= n; i++) {
			sb.append("\nA").append(i).append("=").append(Ai.get(i).toString());
		}

		return sb.toString();
	}
}
