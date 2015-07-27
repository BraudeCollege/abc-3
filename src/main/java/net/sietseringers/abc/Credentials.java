package net.sietseringers.abc;

import net.sf.scuba.smartcards.CardServiceException;
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
