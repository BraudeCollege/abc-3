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
