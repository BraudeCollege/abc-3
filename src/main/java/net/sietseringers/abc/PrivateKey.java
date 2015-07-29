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
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.plaf.jpbc.field.z.ZrField;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import net.sietseringers.abc.issuance.RequestIssuanceMessage;
import net.sietseringers.abc.issuance.StartIssuanceMessage;
import org.irmacard.credentials.Attributes;
import org.irmacard.credentials.CredentialsException;
import org.irmacard.credentials.info.AttributeDescription;
import org.irmacard.credentials.info.CredentialDescription;
import org.irmacard.credentials.info.DescriptionStore;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class PrivateKey {
	public Pairing e;
	public PublicKey publicKey;
	public PairingParameters params;
	public int n;
	private Field G1;
	private Field G2;
	private Field Zn;

	public Element a;
	public ElementList ai;
	public Element z;

	public PrivateKey(int n) {
		this.params = SystemParameters.pairingParameters;
		this.e = SystemParameters.e;

		publicKey = new PublicKey(params, n);

		this.ai = new ElementList(n + 1);

		this.e = e;
		this.n = n;
		this.G1 = e.getG1();
		this.G2 = e.getG2();
		this.Zn = e.getZr();

		publicKey.Q = G2.newRandomElement();

		a = Zn.newRandomElement();
		z = Zn.newRandomElement();

		publicKey.A = publicKey.Q.duplicate().powZn(a);
		publicKey.Z = publicKey.Q.duplicate().powZn(z);

		for (int i = 0; i <= n; i++) {
			Element newai = Zn.newRandomElement();
			Element newAi = publicKey.Q.duplicate().powZn(newai);
			ai.add(newai);
			publicKey.Ai.add(newAi);
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("PrivateKey: ")
				.append(params.toString("="))
				.append("z=").append(z.toString())
				.append("\na=").append(a.toString());

		for (int i = 0; i <= n; i++) {
			sb.append("\na").append(i).append("=").append(ai.get(i).toString());
		}

		return sb.toString();
	}
}
