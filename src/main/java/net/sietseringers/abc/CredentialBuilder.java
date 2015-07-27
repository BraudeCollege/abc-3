package net.sietseringers.abc;


import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import net.sietseringers.abc.issuance.CommitmentIssuanceMessage;
import net.sietseringers.abc.issuance.FinishIssuanceMessage;
import net.sietseringers.abc.issuance.RequestIssuanceMessage;
import net.sietseringers.abc.issuance.StartIssuanceMessage;
import org.irmacard.credentials.Attributes;
import org.irmacard.credentials.info.CredentialDescription;

public class CredentialBuilder {
	private Field Zn = SystemParameters.e.getZr();
	private Attributes attributes;
	private Element K;
	private Element S;
	private Element S0;
	private Element kappa;
	private Element secretkey;
	private CredentialDescription cd;

	public RequestIssuanceMessage generateRequestIssuanceMessage(CredentialDescription cd, Attributes attributes) {
		this.attributes = attributes;
		this.cd = cd;
		return new RequestIssuanceMessage(cd, attributes);
	}

	public CommitmentIssuanceMessage generateCommitmentIssuanceMessage(StartIssuanceMessage start) {
		this.K = start.getK();
		this.S = start.getS();
		this.S0 = start.getS0();

		kappa = Zn.newRandomElement();
		secretkey = Zn.newRandomElement();

		Element w_kappa = Zn.newRandomElement();
		Element w_k0 = Zn.newRandomElement();
		Element W = start.getS().powZn(w_kappa).mul( start.getS0().powZn(w_k0) );
		Element R = start.getS().powZn(kappa).mul( start.getS0().powZn(secretkey) );

		Element c = Util.getChallenge(R, start.getNonce()).getImmutable();

		Element s_kappa = c.mul(kappa).add(w_kappa);
		Element s_k0 = c.mul(secretkey).add(w_k0);

		return new CommitmentIssuanceMessage(start.getSession(), s_kappa, s_k0, R, W);
	}

	public Credential generateCredential(FinishIssuanceMessage msg) {
		ElementList ki = Util.AttributeToElements(cd, attributes, secretkey.toBigInteger());

		Credential c = new Credential(K, S, msg.getSi(), msg.getT(), msg.getKappa().add(kappa), ki);

		return c;
	}
}
