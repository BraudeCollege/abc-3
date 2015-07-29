/*
 * Credentials.java
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
import net.sf.scuba.smartcards.CardServiceException;
import net.sietseringers.abc.issuance.CommitmentIssuanceMessage;
import net.sietseringers.abc.issuance.RequestIssuanceMessage;
import net.sietseringers.abc.issuance.StartIssuanceMessage;
import org.irmacard.credentials.Attributes;
import org.irmacard.credentials.BaseCredentials;
import org.irmacard.credentials.CredentialsException;
import org.irmacard.credentials.info.AttributeDescription;
import org.irmacard.credentials.info.CredentialDescription;
import org.irmacard.credentials.info.InfoException;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Credentials extends BaseCredentials {
	private static final Field Zn = SystemParameters.e.getZr();

	private Map<Short, Credential> credentials = new HashMap<>();

	public Credential get(short id) {
		return credentials.get(id);
	}

	public Credential get(CredentialDescription cd) {
		return credentials.get(cd.getId());
	}

	public void set(short id, Credential cred) {
		credentials.put(id, cred);
	}

	public void set(CredentialDescription cd, Credential cred) {
		credentials.put(cd.getId(), cred);
	}

	@Override
	public List<CredentialDescription> getCredentials() throws CardServiceException, InfoException {
		return null;
	}

	public Attributes getAttributes(CredentialDescription cd) throws CredentialsException, InfoException {
		Credential cred = credentials.get(cd.getId());
		if (cred == null)
			throw new CredentialsException("Credential not found");

		return cred.getAttributes();
	}
}
