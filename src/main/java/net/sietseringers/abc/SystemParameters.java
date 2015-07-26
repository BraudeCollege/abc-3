package net.sietseringers.abc;

import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.plaf.jpbc.pairing.f.TypeFCurveGenerator;

public class SystemParameters {
	public final static int bits = 20;
	public final static PairingParameters pairingParameters = new TypeFCurveGenerator(bits).generate();
	public final static Pairing e = PairingFactory.getPairing(pairingParameters);
}
