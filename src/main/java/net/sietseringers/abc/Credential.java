package net.sietseringers.abc;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Credential {
	public int n;
	private Field G1;
	private Field Zn;

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

	public ProofD getDisclosureProof(Element nonce, Map<Integer,Boolean> disclosed) {
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
		wi.put(-0, Zn.newRandomElement());

		Element W = bC.duplicate().powZn(wi.get(-2));
		W.mul(bS.duplicate().powZn(wi.get(-1)));
		W.mul(bSi.get(0).duplicate().powZn(wi.get(0)));

		Element D = K.duplicate();
		for (Integer i : disclosed.keySet()) {
			if (!disclosed.get(i)) {
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
		si.put(0, c.duplicate().mul(ki.get(0)).add(wi.get(0)));
		for (Integer i : disclosed.keySet())
			if (!disclosed.get(i))
				si.put(i, c.duplicate().mul(ki.get(i)).add(wi.get(i))); // s[i] = c*ki + wi


		return new ProofD(n, nonce, bK, bS, bSi, bC, bT, W, si, attrs);
	}
}
