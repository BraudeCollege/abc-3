/*
 * CredentialIssuer.java
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
import net.sietseringers.abc.issuance.CommitmentIssuanceMessage;
import net.sietseringers.abc.issuance.FinishIssuanceMessage;
import net.sietseringers.abc.issuance.RequestIssuanceMessage;
import net.sietseringers.abc.issuance.StartIssuanceMessage;
import org.irmacard.credentials.Attributes;
import org.irmacard.credentials.CredentialsException;
import org.irmacard.credentials.info.CredentialDescription;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CredentialIssuer {
	private Field G1 = SystemParameters.e.getG1();
	private Field Zn = SystemParameters.e.getZr();
	private Map<Integer, RequestIssuanceMessage> requests = new HashMap<>();
	private Map<Integer, StartIssuanceMessage> startMessages = new HashMap<>();
	private PrivateKey sk = null;

	public CredentialIssuer(PrivateKey sk) {
		this.sk = sk;
	}

	public StartIssuanceMessage generateStartIssuanceMessage(RequestIssuanceMessage request) {
		Random r = new Random();
		int session = r.nextInt(Integer.MAX_VALUE);

		Element K = G1.newRandomElement();
		Element S = K.duplicate().powZn(sk.geta());
		Element S0 = K.duplicate().powZn(sk.getai(0));

		StartIssuanceMessage msg = new StartIssuanceMessage(session, K, S, S0, Util.generateNonce());

		requests.put(session, request);
		startMessages.put(session, msg);

		return msg;
	}

	public FinishIssuanceMessage generateFinishIssuanceMessage(CommitmentIssuanceMessage msg) {
		RequestIssuanceMessage request = requests.get(msg.getSession());
		StartIssuanceMessage start = startMessages.get(msg.getSession());

		if (request == null || start == null)
			return null;

		if (!msg.isValid(start.getNonce(), start.getS(), start.getS0()))
			return null;

		Element kappa = Zn.newRandomElement();
		Attributes attributes = request.getAttributes();
		attributes.setCredentialID(request.getCredentialDescription().getId());
		attributes.setExpireDate(null);
		ElementList ki = Util.AttributeToElements(request.getCredentialDescription(), attributes);

		int n = attributes.getIdentifiers().size() + 1;
		ElementList Si = new ElementList(n + 1);

		Si.add(start.getS0());
		Element T = start.getK();
		T.mul(start.getS().powZn(kappa).mul(msg.getR()));

		for (int i = 1; i < n; i++) {
			Si.add(start.getK().powZn(sk.getai(i)));
			T.mul(Si.get(i).duplicate().powZn(ki.get(i)));
		}

		T.powZn(sk.getz());

		return new FinishIssuanceMessage(kappa, Si, T);
	}

	public Credential sign(CredentialDescription cd, Attributes attributes, BigInteger secretkey) throws CredentialsException {
		attributes.setExpireDate(null); // default, 6 months
		attributes.setCredentialID(cd.getId());

		return sign(Util.AttributeToElements(cd, attributes, secretkey));
	}

	public Credential sign(ElementList ki) {
		Element K = G1.newRandomElement();
		Element S = K.duplicate().powZn(sk.geta());
		Element kappa = Zn.newRandomElement();

		ElementList Si = new ElementList(sk.getN() +1);

		Element C = K.duplicate().mul(S.duplicate().powZn(kappa));

		for (int i = 0; i < ki.size(); i++) {
			Element newSi = K.duplicate().powZn(sk.getai(i));
			Si.add(newSi);
			C.mul(newSi.duplicate().powZn(ki.get(i)));
		}

		Element T = C.duplicate().powZn(sk.getz());

		return new Credential(K, S, Si, T, kappa, ki);
	}
}
