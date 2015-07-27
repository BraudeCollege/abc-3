package net.sietseringers.abc;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import org.irmacard.credentials.Attributes;
import org.irmacard.credentials.info.*;

import javax.security.auth.login.CredentialException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Credential {
	public int n;
	public short id;
	private Field G1;
	private Field Zn;

	private CredentialDescription desc;
	private Attributes attributes;

	public Element K;
	public Element S;
	public Element R;
	public ElementList Si;
	public ElementList Ri;
	public Element C;
	public Element T;

	public Element kappa;
	public List<Element> ki;

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
		Ri = new ElementList(n+1);

		R = S.duplicate().powZn(kappa).getImmutable();
		C = K.duplicate().mul(R);

		for (int i = 0; i <= n; i++) {
			Element newRi = Si.get(i).duplicate().powZn(ki.get(i)).getImmutable();
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

		Element bK = K.duplicate().powZn(alpha);
		Element bS = S.duplicate().powZn(alpha);
		ElementList bSi = Si.duplicate().powZn(alpha);
		Element bC = C.duplicate().powZn(alphabeta);
		Element bT = T.duplicate().powZn(alphabeta);

		Map<Integer,Element> wi = new HashMap<>();
		Map<Integer,Element> si = new HashMap<>();
		Map<Integer,Element> attrs = new HashMap<>();

		wi.put(-2, Zn.newRandomElement());
		wi.put(-1, Zn.newRandomElement());
		Element W = bC.duplicate().powZn(wi.get(-2));
		W.mul(bS.duplicate().powZn(wi.get(-1)));

		Element D = K.duplicate();
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
		si.put(-1, c.duplicate().mul(kappa).add(wi.get(-1)));
		for (int i = 0; i < ki.size(); i++)
			if (!disclosed.contains(i) || i == 0)
				si.put(i, c.duplicate().mul(ki.get(i)).add(wi.get(i))); // s[i] = c*ki + wi

		return new ProofD(n, nonce, bK, bS, bSi, bC, bT, W, si, attrs);
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
}
