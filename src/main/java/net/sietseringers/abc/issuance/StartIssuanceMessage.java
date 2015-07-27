package net.sietseringers.abc.issuance;

import it.unisa.dia.gas.jpbc.Element;

import java.math.BigInteger;

public class StartIssuanceMessage {
	private Element K;
	private Element S;
	private Element S0;
	private BigInteger nonce;
	private int session;

	public StartIssuanceMessage(int session, Element K, Element S, Element S0, BigInteger nonce) {
		this.K = K;
		this.S = S;
		this.S0 = S0;
		this.nonce = nonce;
		this.session = session;
	}

	public Element getK() {
		return K.duplicate();
	}

	public Element getS() {
		return S.duplicate();
	}

	public Element getS0() {
		return S0.duplicate();
	}

	public BigInteger getNonce() {
		return nonce;
	}

	public int getSession() {
		return session;
	}
}
