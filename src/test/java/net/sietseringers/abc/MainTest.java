package net.sietseringers.abc;

import it.unisa.dia.gas.jpbc.Pairing;
import net.sietseringers.abc.issuance.CommitmentIssuanceMessage;
import net.sietseringers.abc.issuance.FinishIssuanceMessage;
import net.sietseringers.abc.issuance.RequestIssuanceMessage;
import net.sietseringers.abc.issuance.StartIssuanceMessage;
import org.irmacard.credentials.Attributes;
import org.irmacard.credentials.info.CredentialDescription;
import org.irmacard.credentials.info.DescriptionStore;
import org.irmacard.credentials.info.VerificationDescription;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;

public class MainTest {
	@Test
	public void test() throws Exception {
		Pairing e = SystemParameters.e;
		long start;
		long stop;

		// Generate a new private/public keypair
		PrivateKey sk = new PrivateKey(6);

		// Load the "agelower" CredentialDescription from the store
		DescriptionStore.setCoreLocation(
				new File(System.getProperty("user.dir")).toURI().resolve("irma_configuration/"));
		CredentialDescription agelower = DescriptionStore.getInstance().getCredentialDescription((short)10);

		// Build the attributes that we want in our credential
		Attributes attributes = new Attributes();
		for (String name : agelower.getAttributeNames()) {
			attributes.add(name, "yes".getBytes());
		}

		start = System.currentTimeMillis();

		// Build a credential, put it in a card, print its attributes
		CredentialIssuer issuer = new CredentialIssuer(sk);
		CredentialBuilder builder = new CredentialBuilder();

		RequestIssuanceMessage request = builder.generateRequestIssuanceMessage(agelower, attributes);
		StartIssuanceMessage startMessage = issuer.generateStartIssuanceMessage(request);
		CommitmentIssuanceMessage commitMessage = builder.generateCommitmentIssuanceMessage(startMessage);
		FinishIssuanceMessage finishMessage = issuer.generateFinishIssuanceMessage(commitMessage);

		Credential c = builder.generateCredential(finishMessage);

		Credentials card = new Credentials();
		card.set(agelower, c);
		System.out.println(card.getAttributes(agelower).toString());
		stop = System.currentTimeMillis();
		System.out.println("Issuing: " + (stop-start) + " ms");

		// Create a disclosure proof
		start = System.currentTimeMillis();
		ProofD proof = c.getDisclosureProof(Util.generateNonce(), Arrays.asList(1, 2, 3));
		stop = System.currentTimeMillis();
		System.out.println("Disclosing: " + (stop-start) + " ms");

		// Verify it directly
		start = System.currentTimeMillis();
		System.out.println(proof.isValid(sk.publicKey));
		stop = System.currentTimeMillis();
		System.out.println("Verify 1: " + (stop-start) + " ms");

		// Verify it and return the contained attributes using a VerificationDescription
		start = System.currentTimeMillis();
		VerificationDescription vd = DescriptionStore.getInstance()
				.getVerificationDescriptionByName("IRMATube", "ageLowerOver18");
		proof = c.getDisclosureProof(vd, Util.generateNonce());
		Attributes disclosed = proof.verify(vd, sk.publicKey);
		System.out.println(disclosed.toString());
		stop = System.currentTimeMillis();
		System.out.println("Verify 2: " + (stop-start) + " ms");
	}
}
