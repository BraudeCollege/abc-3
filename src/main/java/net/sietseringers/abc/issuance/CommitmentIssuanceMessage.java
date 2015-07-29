/*
 * CommitmentIssuanceMessage.java
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
