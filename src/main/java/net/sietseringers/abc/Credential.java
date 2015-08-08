/*
 * Credential.java
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
import org.irmacard.credentials.Attributes;
import org.irmacard.credentials.info.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Credential {
	private int n;
	private short id;
	private Field G1;
	private Field Zn;

	private CredentialDescription desc;
	private Attributes attributes;

	private Element K;
	private Element S;
	private Element R;
	private ElementList Si;
	private ElementList Ri;
	private Element C;
	private Element T;

	private Element kappa;
	private ElementList ki;

	private Credential(Element K, Element S, Element S0, Element secretKey) {
		G1 = SystemParameters.e.getG1();
		Zn = SystemParameters.e.getZr();

		Si = new ElementList(6);
		ki = new ElementList(6);

		this.K = K.getImmutable();
		this.S = S.getImmutable();
		this.Si.add(S0.getImmutable());

		this.kappa = Zn.newRandomElement();
		this.ki.add(secretKey);
	}

	public Credential(Element K, Element S, ElementList Si, Element T, Element kappa, ElementList ki) {
		G1 = K.getField();
		Zn = kappa.getField();

		this.K = K.getImmutable();
		this.S = S.getImmutable();
		this.Si = Si.getImmutable();
		this.T = T.getImmutable();
		this.kappa = kappa.getImmutable();
		this.ki = ki.getImmutable();

		n = Si.size() - 1;
		Ri = new ElementList(n + 1);

		R = getS().powZn(kappa).getImmutable();
		C = getK().mul(R);

		for (int i = 0; i <= getN(); i++) {
			Element newRi = getSi(i).powZn(ki.get(i)).getImmutable();
			Ri.add(newRi);
			C.mul(newRi);
		}

		C = C.getImmutable();
	}

	public ProofD getDisclosureProof(VerificationDescription vd, BigInteger nonce) {
		List<Integer> l = new ArrayList<>();

		List<String> attributeNames = vd.getCredentialDescription().getAttributeNames();

		l.add(1);
		for (int i = 0; i < attributeNames.size(); i++) {
			if (vd.isDisclosed(attributeNames.get(i)))
				l.add(i + 2);
		}

		return getDisclosureProof(nonce, l);
	}

	public ProofD getDisclosureProof(BigInteger nonce, List<Integer> disclosed) {
		Element alpha = Zn.newRandomElement();
		Element beta = Zn.newRandomElement();
		Element alphabeta = alpha.duplicate().div(beta).negate();

		Element bK = getK().powZn(alpha);
		Element bS = getS().powZn(alpha);
		ElementList bSi = Si.duplicate().powZn(alpha);
		Element bC = getC().powZn(alphabeta);
		Element bT = getT().powZn(alphabeta);

		Map<Integer,Element> wi = new HashMap<>();
		Map<Integer,Element> si = new HashMap<>();
		Map<Integer,Element> attrs = new HashMap<>();

		wi.put(-2, Zn.newRandomElement());
		wi.put(-1, Zn.newRandomElement());
		Element W = bC.duplicate().powZn(wi.get(-2));
		W.mul(bS.duplicate().powZn(wi.get(-1)));

		Element D = getK();
		for (int i = 0; i < ki.size(); i++) {
			if (!disclosed.contains(i) || i == 0) {
				wi.put(i, Zn.newRandomElement());
				W.mul(bSi.get(i).duplicate().powZn(wi.get(i))); // W = W*Si^wi
			} else {
				attrs.put(i, ki.get(i));
				D.mul(Ri.get(i));
			}
		}
		D.powZn(alpha.duplicate().negate());

		Element c = Util.getChallenge(D, nonce);

		si.put(-2, c.duplicate().mul(beta).add(wi.get(-2)));
		si.put(-1, c.duplicate().mul(getKappa()).add(wi.get(-1)));
		for (int i = 0; i < ki.size(); i++)
			if (!disclosed.contains(i) || i == 0)
				si.put(i, c.duplicate().mul(ki.get(i)).add(wi.get(i))); // s[i] = c*ki + wi

		return new ProofD(getN(), nonce, bK, bS, bSi, bC, bT, W, si, attrs);
	}

	public short getId() {
		Attributes a = new Attributes();
		a.add("metadata", new BigInteger(ki.get(1).toBytes()).toByteArray());
		return a.getCredentialID();
	}

	public CredentialDescription getCredentialDescription() throws InfoException {
		if (desc == null) {
			desc = DescriptionStore.getInstance().getCredentialDescription(getId());
		}

		return desc;
	}

	public Attributes getAttributes() throws InfoException {
		if (attributes == null) {
			attributes = Util.ElementsToAttributes(getCredentialDescription(), ki);
		}

		return attributes;
	}

	public boolean isValid(PublicKey pk) {
		boolean answer = true;
		Pairing e = SystemParameters.e;

		// C has already been set to its appropriate value by the constructor
		answer = answer && e.pairing(getC(), pk.getZ()).equals( e.pairing(getT(), pk.getQ()) );
		answer = answer && e.pairing(getK(), pk.getA()).equals( e.pairing(getS(), pk.getQ()) );
		for (int i = 0; i < Si.size(); i++)
			answer = answer && e.pairing(getK(), pk.getAi(i)).equals( e.pairing(Si.get(i), pk.getQ()) );

		return answer;
	}

	public int getN() {
		return n;
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

	public Element getR() {
		return R.duplicate();
	}

	public Element getTi(int i) {
		return Ri.get(i).duplicate();
	}

	public Element getC() {
		return C.duplicate();
	}

	public Element getT() {
		return T.duplicate();
	}

	public Element getKappa() {
		return kappa.duplicate();
	}

	public Element getki(int i) {
		return ki.get(i).duplicate();
	}
}
