package com.wulian.routelibrary.http;

import android.content.Context;

import com.wulian.routelibrary.utils.LibraryLoger;

import org.apache.http.conn.ssl.X509HostnameVerifier;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class HttpsUtils {
	static X509TrustManager DefaultXML = new X509TrustManager() {
		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType) {
		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType) {
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			X509Certificate[] x509Certificates = new X509Certificate[0];
			return x509Certificates;
		}
	};

	//忽略服务器证书
	public static SSLSocketFactory initHttps(Context context) {

		SSLContext sslContext = null;
		try {
			sslContext = SSLContext.getInstance("SSL");
			sslContext.init(null, new TrustManager[]{DefaultXML}, new SecureRandom());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
		return sslContext.getSocketFactory();
	}

	//验证服务器证书
	public static SSLSocketFactory initHttps1(Context context) {
		InputStream in = null;
		try {
			if(!LibraryLoger.getLoger()) {
				in = context.getAssets().open("shgg1.cer");
			}else {
				in = context.getAssets().open("test.sh.gg.cer");
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new AssertionError("Please set shgg.cer");
		}
		if (in == null) {
			return null;
		}
		InputStream[] inputArray = { in };
		return HttpsUtils.getSslSocketFactory(inputArray, null, null);
	}

	public static SSLSocketFactory getSslSocketFactory(
			InputStream[] certificates, InputStream bksFile, String password) {
		try {
			TrustManager[] trustManagers = prepareTrustManager(certificates);
			SSLContext sslContext = SSLContext.getInstance("TLS");
			TrustManager trustManager = null;
			if (trustManagers != null) {
				trustManager = new MyTrustManager(
						chooseTrustManager(trustManagers));
			} else {
				throw new AssertionError("No Valid Cer");
			}
			sslContext.init(null, new TrustManager[] { trustManager },
					new SecureRandom());
			return sslContext.getSocketFactory();
		} catch (NoSuchAlgorithmException e) {
			throw new AssertionError(e);
		} catch (KeyManagementException e) {
			throw new AssertionError(e);
		} catch (KeyStoreException e) {
			throw new AssertionError(e);
		}
	}

	private static TrustManager[] prepareTrustManager(
			InputStream... certificates) {
		if (certificates == null || certificates.length <= 0)
			return null;
		try {

			CertificateFactory certificateFactory = CertificateFactory
					.getInstance("X.509");
			KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
			keyStore.load(null);
			int index = 0;
			for (InputStream certificate : certificates) {
				String certificateAlias = Integer.toString(index++);
				keyStore.setCertificateEntry(certificateAlias,
						certificateFactory.generateCertificate(certificate));
				try {
					if (certificate != null)
						certificate.close();
				} catch (IOException e)

				{
				}
			}
			TrustManagerFactory trustManagerFactory = null;

			trustManagerFactory = TrustManagerFactory
					.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			trustManagerFactory.init(keyStore);

			TrustManager[] trustManagers = trustManagerFactory
					.getTrustManagers();

			return trustManagers;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	private static X509TrustManager chooseTrustManager(
			TrustManager[] trustManagers) {
		for (TrustManager trustManager : trustManagers) {
			if (trustManager instanceof X509TrustManager) {
				return (X509TrustManager) trustManager;
			}
		}
		return null;
	}

	private static class MyTrustManager implements X509TrustManager {
		private X509TrustManager localTrustManager;

		public MyTrustManager(X509TrustManager localTrustManager)
				throws NoSuchAlgorithmException, KeyStoreException {
			this.localTrustManager = localTrustManager;
		}

		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
			try {
				localTrustManager.checkClientTrusted(chain, authType);
			} catch (CertificateException ce) {
				throw new CertificateException(ce.getMessage());
			}
		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
			try {
				localTrustManager.checkServerTrusted(chain, authType);
			} catch (CertificateException ce) {
				throw new CertificateException(ce.getMessage());
			}
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[0];
		}
	}
}
