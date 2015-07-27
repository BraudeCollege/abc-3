package net.sietseringers.abc;

import java.io.File;
import java.math.BigInteger;
import java.util.*;
import java.lang.*;

import it.unisa.dia.gas.jpbc.*;
import org.irmacard.credentials.Attributes;
import org.irmacard.credentials.info.CredentialDescription;
import org.irmacard.credentials.info.DescriptionStore;
import org.irmacard.credentials.info.VerificationDescription;

class Main
{
	public static void main (String[] args) throws java.lang.Exception
	{
		Pairing e = SystemParameters.e;

		// Generate a new private/public keypair
		PrivateKey sk = new PrivateKey(6);
		System.out.println(sk.toString());
		System.out.println();
		System.out.println(sk.publicKey.toString());

		// Load the "agelower" CredentialDescription from the store
		DescriptionStore.setCoreLocation(
				new File(System.getProperty("user.dir")).toURI().resolve("irma_configuration/"));
		CredentialDescription agelower = DescriptionStore.getInstance().getCredentialDescription((short)10);

		// Build the attributes that we want in our credential
		Attributes attributes = new Attributes();
		for (String name : agelower.getAttributeNames()) {
			attributes.add(name, "yes".getBytes());
		}

		// Build a credential, put it in a card, print its attributes
		Credential c = sk.sign(agelower, attributes, BigInteger.valueOf(100));
		Credentials card = new Credentials();
		card.set(agelower, c);
		System.out.println(card.getAttributes(agelower).toString());

		// Create a disclosure proof
		ProofD proof = c.getDisclosureProof(Util.generateNonce(), Arrays.asList(1, 2, 3));

		// Verify it directly
		System.out.println(proof.isValid(sk.publicKey));

		// Verify it and return the contained attributes using a VerificationDescription
		VerificationDescription vd = DescriptionStore.getInstance()
				.getVerificationDescriptionByName("IRMATube", "ageLowerOver18");
		proof = c.getDisclosureProof(vd, Util.generateNonce());
		Attributes disclosed = proof.verify(vd, sk.publicKey);
		System.out.println(disclosed.toString());
	}
}
