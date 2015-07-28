package net.sietseringers.abc;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.util.ArrayList;
import java.util.List;

public class PublicKey {
	public Pairing e;
	public PairingParameters params;
	public int n;
	private Field G1;
	private Field G2;
	private Field Zn;

	public Element Q;

	public Element A;
	public ElementList Ai;
	public Element Z;

	public PublicKey(PairingParameters params, int n) {
		this.params = params;
		this.e = SystemParameters.e;
		this.n = n;
		this.G1 = e.getG1();
		this.G2 = e.getG2();
		this.Zn = e.getZr();
		this.Ai = new ElementList(n);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("PrivateKey: ")
				.append(params.toString("="))
				.append("Z=").append(Z.toString())
				.append("\nA=").append(A.toString());

		for (int i = 0; i <= n; i++) {
			sb.append("\nA").append(i).append("=").append(Ai.get(i).toString());
		}

		return sb.toString();
	}
}
