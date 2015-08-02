/*
 * MainTest.java
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

package net.sietseringers.abc;

import it.unisa.dia.gas.jpbc.Pairing;
import net.sietseringers.abc.issuance.CommitmentIssuanceMessage;
import net.sietseringers.abc.issuance.FinishIssuanceMessage;
import net.sietseringers.abc.issuance.RequestIssuanceMessage;
import net.sietseringers.abc.issuance.StartIssuanceMessage;
import org.irmacard.credentials.Attributes;
import org.irmacard.credentials.CredentialsException;
import org.irmacard.credentials.info.CredentialDescription;
import org.irmacard.credentials.info.DescriptionStore;
import org.irmacard.credentials.info.InfoException;
import org.irmacard.credentials.info.VerificationDescription;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.sound.midi.MidiDevice;
import java.io.File;
import java.io.PrintStream;
import java.math.BigInteger;
import java.net.URI;
import java.util.Arrays;

public class MainTest {
	private static DescriptionStore ds;
	private static PrintStream out;

	private static Credentials card;
	private static PrivateKey sk;

	public static void setDescriptionStore(DescriptionStore store) {
		ds = store;
	}

	public static void setPrintStream(PrintStream stream) {
		out = stream;
	}

	@BeforeClass
	public static void init() {
		card = new Credentials();
		sk = new PrivateKey(6);

		// If ds is not set, we create one on our own.
		if (ds == null) {
			try {
				URI core = new File(System
						.getProperty("user.dir")).toURI()
						.resolve("irma_configuration/");
				DescriptionStore.setCoreLocation(core);
				ds = DescriptionStore.getInstance();
			} catch (InfoException e) {
				e.printStackTrace();
			}
		}

		if (out == null)
			out = System.out;
	}

	@Test
	public void testAgeLowerOver18() throws InfoException {
		issue("MijnOverheid", "ageLower");
		proofAndVerifyTest("IRMATube", "ageLowerOver18");
	}

	@Test
	public void testStudentCardAll() throws InfoException {
		issue("RU", "studentCard");
		proofAndVerifyTest("RU", "studentCardAll");
	}

	public void proofAndVerifyTest(String verifier, String verifierId) throws InfoException {
		BigInteger nonce = Util.generateNonce();
		ProofD proof = getProof(verifier, verifierId, nonce);
		verifyProof(proof, verifier, verifierId);
	}

	public ProofD getProof(String verifier, String verifierId, BigInteger nonce) throws
			InfoException {
		VerificationDescription vd = ds.getVerificationDescriptionByName(verifier, verifierId);
		Credential c = card.get(vd.getCredentialDescription());

		long start = System.currentTimeMillis();

		ProofD proof = c.getDisclosureProof(vd, nonce);

		long stop = System.currentTimeMillis();
		out.println("Disclosing: " + (stop-start) + " ms");

		return proof;
	}

	public void issue(String issuerid, String credid) {
		CredentialDescription cd = ds.getCredentialDescriptionByName(issuerid, credid);

		Attributes attrs = loadNewAttributes(cd);

		CredentialIssuer issuer = new CredentialIssuer(sk);
		CredentialBuilder builder = new CredentialBuilder();

		long start = System.currentTimeMillis();

		RequestIssuanceMessage request = builder.generateRequestIssuanceMessage(cd, attrs);
		StartIssuanceMessage startMessage = issuer.generateStartIssuanceMessage(request);
		CommitmentIssuanceMessage commitMessage = builder.generateCommitmentIssuanceMessage(startMessage);
		FinishIssuanceMessage finishMessage = issuer.generateFinishIssuanceMessage(commitMessage);

		Credential c = builder.generateCredential(finishMessage);

		Assert.assertNotNull(c);

		card.set(c.getId(), c);

		long stop = System.currentTimeMillis();
		out.println("Issuing: " + (stop-start) + " ms");
	}

	public Attributes loadNewAttributes(CredentialDescription cd) {
		Attributes attrs = new Attributes();
		attrs.setCredentialID(cd.getId());
		int i = 0;
		for (String name : cd.getAttributeNames()) {
			attrs.add(name, String.valueOf(i).getBytes());
			++i;
		}

		return attrs;
	}

	public Attributes verifyProof(ProofD proof, String verifier, String verifierId) throws
			InfoException {
		VerificationDescription vd = ds.getVerificationDescriptionByName(verifier, verifierId);

		long start = System.currentTimeMillis();

		Attributes disclosed = proof.verify(vd, sk.publicKey);
		Assert.assertNotNull(disclosed);

		long stop = System.currentTimeMillis();
		out.println("Verifying: " + (stop-start) + " ms");
		out.println("Disclosed attributes: " + disclosed.toString().replace("\n", ""));

		return disclosed;
	}
}
