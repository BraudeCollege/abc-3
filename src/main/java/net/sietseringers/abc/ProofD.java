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
import java.util.Map;

public class ProofD {
	private int n;
	private BigInteger nonce;

	private Element K;
	private Element S;
	private ElementList Si;
	private Element C;
	private Element T;
	private Element W;

	private Element D;
	private Element c;

	private Map<Integer, Element> ki;
	private Map<Integer, Element> si;

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

		D = getK();

		for (int i = 1; i <= n; i++)
			if (ki.containsKey(i))
				D.mul( getSi(i).powZn(ki.get(i)) ); // D = D * Si^ki

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

		boolean answer = e.pairing(C, pk.getZ()).equals(e.pairing(T, pk.getQ()));
		answer = answer && e.pairing(K, pk.getA()).equals(e.pairing(S, pk.getQ()));

		Element rhs = getC().powZn(getsi(-2)); // rhs = C^si[-2]
		rhs.mul(getS().powZn(getsi(-1)));      // rhs = rhs * S^si[-1]

		for (int i = 0; i <= n; i++) {
			if (!ki.containsKey(i))
				rhs.mul(getSi(i).powZn(getsi(i))); // rhs = rhs * Si^si
			answer = answer && e.pairing(K, pk.getAi(i)).equals(e.pairing(getSi(i), pk.getQ()));
		}

		// D^c * W == rhs
		Element c = Util.getChallenge(getD(), nonce);
		if (nonce.equals(BigInteger.ZERO))
			c = getChallenge();

		answer = answer && getD().powZn(c).mul(W).isEqual(rhs);
		return answer;
	}

	public int getN() {
		return n;
	}

	public BigInteger getNonce() {
		return nonce;
	}

	public Element getK() {
		return K.duplicate();
	}

	public Element getS() {
		return S.duplicate();
	}

	public Element getSi(int i) {
		return Si.get(i).duplicate();
	}

	public Element getC() {
		return C.duplicate();
	}

	public Element getT() {
		return T.duplicate();
	}

	public Element getW() {
		return W.duplicate();
	}

	public Element getki(int i) {
		return ki.get(i).duplicate();
	}

	public Element getsi(int i) {
		return si.get(i).duplicate();
	}
}
