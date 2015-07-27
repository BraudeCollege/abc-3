package net.sietseringers.abc.issuance;

import it.unisa.dia.gas.jpbc.Element;
import net.sietseringers.abc.Util;

import java.math.BigInteger;

public class CommitmentIssuanceMessage {
	private Element s_kappa;
	private Element s_k0;
	private Element R;
	private Element W;
	private int session;

	private BigInteger nonce;
	private Element c;

	public CommitmentIssuanceMessage(int session, Element s_kappa, Element s_k0, Element R, Element W) {
		this.s_kappa = s_kappa;
		this.s_k0 = s_k0;
		this.R = R;
		this.W = W;
		this.session = session;
	}

	public Element get_s_kappa() {
		return s_kappa.duplicate();
	}

	public Element get_s_k0() {
		return s_k0.duplicate();
	}

	public Element getR() {
		return R.duplicate();
	}

	public Element getW() {
		return W.duplicate();
	}

	public int getSession() {
		return session;
	}

	public boolean isValid(BigInteger nonce, Element S, Element S0) {
		Element c = Util.getChallenge(getR(), nonce);
		Element rhs = S.duplicate().powZn(get_s_kappa()).mul( S0.duplicate().powZn(get_s_k0()) );

		return getR().powZn(c).mul(getW()).equals(rhs);
	}

}
