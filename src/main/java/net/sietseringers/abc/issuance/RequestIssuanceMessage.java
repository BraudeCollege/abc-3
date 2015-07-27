package net.sietseringers.abc.issuance;

import org.irmacard.credentials.Attributes;
import org.irmacard.credentials.info.CredentialDescription;

public class RequestIssuanceMessage {
	private Attributes attributes;
	private CredentialDescription cd;

	public RequestIssuanceMessage(CredentialDescription cd, Attributes attributes) {
		this.cd = cd;
		this.attributes = attributes;
	}

	public Attributes getAttributes() {
		return attributes;
	}

	public CredentialDescription getCredentialDescription() {
		return cd;
	}
}
