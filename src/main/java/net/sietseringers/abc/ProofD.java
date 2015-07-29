/*
 * ProofD.java
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
import it.unisa.dia.gas.jpbc.Pairing;
import org.irmacard.credentials.Attributes;
import org.irmacard.credentials.info.CredentialDescription;
import org.irmacard.credentials.info.VerificationDescription;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProofD {
	public int n;
	public BigInteger nonce;

	public Element K;
	public Element S;
	public ElementList Si;
	public Element C;
	public Element T;
	public Element W;

	public Element D;
	public Element c;

	public Map<Integer, Element> ki;
	public Map<Integer, Element> si;

	public ProofD(int n, BigInteger nonce, Element bK, Element bS, ElementList bSi, Element bC, Element bT, Element W, Map<Integer,Element> si, Map<Integer,Element> attrs) {
		this.n = n;
		this.nonce = nonce;

		this.K = bK.getImmutable();
		this.S = bS.getImmutable();
		this.Si = bSi.getImmutable();
		this.C = bC.getImmutable();
		this.T = bT.getImmutable();
		this.W = W.getImmutable();

		this.ki = new HashMap<>(attrs.size());
		this.si = new HashMap<>(si.size());

		for (int i : attrs.keySet())
			this.ki.put(i, attrs.get(i).getImmutable());
		for (int i : si.keySet())
			this.si.put(i, si.get(i).getImmutable());
	}

	public Element getD() {
		if (D != null)
			return D;

		D = K.duplicate();

		for (int i = 1; i <= n; i++)
			if (ki.containsKey(i))
				D.mul( Si.get(i).duplicate().powZn(ki.get(i)) ); // D = D * Si^ki

		D = D.invert().getImmutable();

		return D;
	}

	public Element getChallenge() {
		if (c != null)
			return c;

		c = Util.getChallenge(D, nonce).getImmutable();

		return c;
	}

	public Attributes verify(VerificationDescription vd, PublicKey pk) {
		return verify(vd.getCredentialDescription(), pk, nonce);
	}

	public Attributes verify(VerificationDescription vd, PublicKey pk, BigInteger nonce) {
		return verify(vd.getCredentialDescription(), pk, nonce);
	}

	public Attributes verify(CredentialDescription cd, PublicKey pk, BigInteger nonce) {
		if (!isValid(pk, nonce))
			return null;

		return Util.ElementsToAttributes(cd, ki);
	}

	public boolean isValid(PublicKey pk) {
		return isValid(pk, nonce);
	}


	public boolean isValid(PublicKey pk, BigInteger nonce) {
		Pairing e = SystemParameters.e;

		boolean answer = e.pairing(C, pk.Z).equals(e.pairing(T, pk.Q));
		answer = answer && e.pairing(K, pk.A).equals(e.pairing(S, pk.Q));

		Element rhs = C.duplicate().powZn(si.get(-2)); // rhs = C^si[-2]
		rhs.mul(S.duplicate().powZn(si.get(-1)));      // rhs = rhs * S^si[-1]

		for (int i = 0; i <= n; i++) {
			if (!ki.containsKey(i))
				rhs.mul(Si.get(i).duplicate().powZn(si.get(i))); // rhs = rhs * Si^si
			answer = answer && e.pairing(K, pk.Ai.get(i)).equals(e.pairing(Si.get(i), pk.Q));
		}

		// D^c * W == rhs
		Element c = Util.getChallenge(getD(), nonce);
		if (nonce.equals(BigInteger.ZERO))
			c = getChallenge();

		answer = answer && getD().duplicate().powZn(c).mul(W).isEqual(rhs);
		return answer;
	}
}
