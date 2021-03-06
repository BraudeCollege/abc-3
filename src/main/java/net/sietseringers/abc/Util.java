/*
 * Util.java
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
import org.irmacard.credentials.Attributes;
import org.irmacard.credentials.info.AttributeDescription;
import org.irmacard.credentials.info.CredentialDescription;
import org.w3c.dom.Attr;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Util {
	public static BigInteger generateNonce() {
		return SystemParameters.e.getZr().newRandomElement().toBigInteger();
	}

	public static Element getChallenge(Element D, BigInteger nonce) {
		byte[] hash = null;
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			digest.update(D.toBytes());
			hash = digest.digest(nonce.toByteArray());
		} catch (Exception e) {}
		return SystemParameters.e.getZr().newElementFromBytes(hash);
	}

	public static ElementList AttributeToElements(CredentialDescription cd, Attributes attributes) {
		return AttributeToElements(cd, attributes, BigInteger.ZERO);
	}

	public static ElementList AttributeToElements(CredentialDescription cd, Attributes attributes, BigInteger secretkey) {
		Field Zn = SystemParameters.e.getZr();

		ElementList ki = new ElementList(attributes.getIdentifiers().size() + 2);
		ki.add(Zn.newElement(secretkey));
		ki.add(Zn.newElement(new BigInteger(attributes.get("metadata"))));

		List<AttributeDescription> l = cd.getAttributes();
		for (int i = 0; i < l.size(); i++) {
			byte[] attribute = attributes.get(l.get(i).getName());
			ki.add(Zn.newElement(new BigInteger(attribute)));
		}

		return ki;
	}

	public static Attributes ElementsToAttributes(CredentialDescription cd, Map<Integer, Element> elements) {
		Attributes attributes = new Attributes();


		attributes.add("metadata", StripZeroBytes(elements.get(1).toBytes()));

		List<AttributeDescription> descs = cd.getAttributes();
		for (int i = 0; i < descs.size(); i++) {
			if (elements.containsKey(i + 2)) {
				attributes.add(descs.get(i).getName(), StripZeroBytes(elements.get(i + 2).toBytes()));
			}
		}

		return attributes;
	}

	public static byte[] StripZeroBytes(byte[] b) {
		return new BigInteger(b).toByteArray();
	}

	public static Attributes ElementsToAttributes(CredentialDescription cd, List<Element> elements) {
		Map<Integer, Element> map = new HashMap<>(elements.size());

		for (int i = 1; i < elements.size(); i++)
			map.put(i, elements.get(i));

		return ElementsToAttributes(cd, map);
	}
}
