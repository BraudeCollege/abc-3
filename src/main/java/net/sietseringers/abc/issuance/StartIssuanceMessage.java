/*
 * StartIssuanceMessage.java
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
