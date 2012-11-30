package com.morlunk.mumbleclient.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.sql.Date;

import org.spongycastle.asn1.x500.X500Name;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.cert.X509CertificateHolder;
import org.spongycastle.cert.X509v3CertificateBuilder;
import org.spongycastle.cert.jcajce.JcaX509CertificateConverter;
import org.spongycastle.operator.ContentSigner;
import org.spongycastle.operator.OperatorCreationException;
import org.spongycastle.operator.jcajce.JcaContentSignerBuilder;

public class CertificateManager {
	
	static {
	    Security.addProvider(new org.spongycastle.jce.provider.BouncyCastleProvider());
	}
	
	private static final String issuer = "CN=Plumble";
	private static final String DEFAULT_PASSWORD_STRING = "plumblepass";
	
	public static X509Certificate createCertificate(File path) throws NoSuchAlgorithmException, OperatorCreationException, CertificateException, KeyStoreException, NoSuchProviderException, IOException {
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
		generator.initialize(2048, new SecureRandom());
		
		KeyPair keyPair = generator.generateKeyPair();
		
		SubjectPublicKeyInfo publicKeyInfo = new SubjectPublicKeyInfo(AlgorithmIdentifier.getInstance("RSA"), keyPair.getPublic().getEncoded());
		ContentSigner signer = new JcaContentSignerBuilder("SHA1withRSA").setProvider("SC").build(keyPair.getPrivate());
		
		Date startDate = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
	    Date endDate = new Date(System.currentTimeMillis() + 365 * 24 * 60 * 60 * 1000);
		
		X509v3CertificateBuilder certBuilder = new X509v3CertificateBuilder(new X500Name(issuer), 
				BigInteger.ONE, 
				startDate, endDate, new X500Name(issuer), 
				publicKeyInfo);
		
		X509CertificateHolder certificateHolder = certBuilder.build(signer);
		
		X509Certificate certificate = new JcaX509CertificateConverter().setProvider("SC").getCertificate(certificateHolder);
		
		KeyStore keyStore = KeyStore.getInstance("PKCS12", "SC");
		keyStore.load(null, null);
		keyStore.setKeyEntry("Plumble Key", keyPair.getPrivate(), null, new X509Certificate[] { certificate });
		
		keyStore.store(new FileOutputStream(path), DEFAULT_PASSWORD_STRING.toCharArray());
		
		return certificate;
	}
}
