package net.sietseringers.abc;

import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.jpbc.PairingParametersGenerator;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.plaf.jpbc.pairing.f.TypeFCurveGenerator;
import it.unisa.dia.gas.plaf.jpbc.pairing.parameters.PropertiesParameters;
import it.unisa.dia.gas.plaf.jpbc.pbc.PBCPairingFactory;
import it.unisa.dia.gas.plaf.jpbc.pbc.curve.PBCTypeDCurveGenerator;

public class SystemParameters {
	public static PairingParameters pairingParameters = null;

	public final static Pairing e = PBCPairingFactory.getPairing(getPairingParameters());

	public static PairingParameters getPairingParameters() {
		if (pairingParameters == null) {
			PropertiesParameters curveParams = new PropertiesParameters();
			pairingParameters = curveParams.load("d1003-291-247.param");
		}

		return pairingParameters;
	}
}
