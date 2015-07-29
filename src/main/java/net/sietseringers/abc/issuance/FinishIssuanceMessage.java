/*
 * FinishIssuanceMessage.java
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
import net.sietseringers.abc.ElementList;

public class FinishIssuanceMessage {
	private Element kappa;
	private ElementList Si;
	private Element T;

	public Element getKappa() {
		return kappa.duplicate();
	}

	public ElementList getSi() {
		return Si.duplicate();
	}

	public Element getT() {
		return T.duplicate();
	}

	public FinishIssuanceMessage(Element kappa, ElementList Si, Element T) {
		this.kappa = kappa;
		this.Si = Si;
		this.T = T;
	}
}
