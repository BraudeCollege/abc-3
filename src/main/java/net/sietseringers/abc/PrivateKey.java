package net.sietseringers.abc;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.util.ArrayList;
import java.util.List;

public class PrivateKey {
	public Pairing e;
	public PublicKey publicKey;
	public PairingParameters params;
	public int n;
	private Field G1;
	private Field G2;
	private Field Zn;

	public Element a;
	public ElementList ai;
	public Element z;

	public PrivateKey(int n) {
		this.params = SystemParameters.pairingParameters;
		this.e = SystemParameters.e;

		publicKey = new PublicKey(params, n);

		this.ai = new ElementList(n + 1);

		this.e = e;
		this.n = n;
		this.G1 = e.getG1();
		this.G2 = e.getG2();
		this.Zn = e.getZr();

		publicKey.Q = G2.newRandomElement();

		a = Zn.newRandomElement();
		z = Zn.newRandomElement();

		publicKey.A = publicKey.Q.duplicate().powZn(a);
		publicKey.Z = publicKey.Q.duplicate().powZn(z);

		for (int i = 0; i <= n; i++) {
			Element newai = Zn.newRandomElement();
			Element newAi = publicKey.Q.duplicate().powZn(newai);
			ai.add(newai);
			publicKey.Ai.add(newAi);
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("PrivateKey: ")
				.append(params.toString("="))
				.append("z=").append(z.toString())
				.append("\na=").append(a.toString());

		for (int i = 0; i <= n; i++) {
			sb.append("\na").append(i).append("=").append(ai.get(i).toString());
		}

		return sb.toString();
	}

	public Credential sign(ElementList ki) {
		Element K = G1.newRandomElement();
		Element S = K.duplicate().powZn(a);
		Element kappa = Zn.newRandomElement();

		ElementList Si = new ElementList(n+1);

		Element C = K.duplicate().mul(S.duplicate().powZn(kappa));

		for (int i = 0; i <= n; i++) {
			Element newSi = K.duplicate().powZn(ai.get(i));
			Si.add(newSi);
			C.mul(newSi.duplicate().powZn(ki.get(i)));
		}

		Element T = C.duplicate().powZn(z);

		return new Credential(K, S, Si, T, kappa, ki);
	}
}
