package com.cicinho.miner.mining;

import java.io.IOException;
import java.util.Properties;

import org.ethereum.config.SystemProperties;
import org.ethereum.facade.EthereumFactory;
import org.ethereum.samples.BasicSample;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import com.cicinho.miner.node.MinerNode;
import com.typesafe.config.ConfigFactory;

public class PrivateNetworkMiner {

	private static String logger = "Miner1";
	
	static Properties properties;

	private static class MinerConfig {

		private final String config =
				// no need for discovery in that small network
				"peer.discovery.enabled = " + properties.getProperty("peer.discovery.enabled") + " \n" + 
						"peer.listen.port = " + properties.getProperty("peer.listen.port") + " \n" +
				// need to have different nodeId's for the peers
						"peer.privateKey = " + properties.getProperty("peer.privateKey") + " \n" +
						// our private net ID
						"peer.networkId = " + properties.getProperty("peer.networkId") + " \n" +
						// we have no peers to sync with
						"sync.enabled = " + properties.getProperty("sync.enabled") + " \n" +
						// genesis with a lower initial difficulty and some predefined known funded
						// accounts
						"genesis = " + properties.getProperty("genesis") + " \n" +
						// two peers need to have separate database dirs
						"database.dir = " + properties.getProperty("database.dir") + " \n" +
						// when more than 1 miner exist on the network extraData helps to identify the
						// block creator
						"mine.extraDataHex = " + properties.getProperty("mine.extraDataHex") + " \n" + 
						"mine.cpuMineThreads = " + properties.getProperty("mine.cpuMineThreads") + " \n"
						+ "cache.flush.blocks = " + properties.getProperty("cache.flush.blocks") + " \n"
						+ "peer.discovery.external.ip = " + properties.getProperty("peer.discovery.external.ip");

		@Bean
		public MinerNode node() {
			return new MinerNode(logger);
		}

		/**
		 * Instead of supplying properties via config file for the peer we are
		 * substituting the corresponding bean which returns required config for this
		 * instance.
		 */
		@Bean
		public SystemProperties systemProperties() {
			SystemProperties props = new SystemProperties();
			props.overrideParams(ConfigFactory.parseString(config.replaceAll("'", "\"")));
			return props;
		}
	}

	public void initMiner() {
		if (Runtime.getRuntime().maxMemory() < (1250L << 20)) {
			MinerNode.sLogger.error("Not enough JVM heap (" + (Runtime.getRuntime().maxMemory() >> 20)
					+ "Mb) to generate DAG for mining (DAG requires min 1G). For this sample it is recommended to set -Xmx2G JVM option");
			return;
		}
		try {
			properties = PropertiesLoaderUtils.loadAllProperties("application.properties");
		} catch (IOException e) {			
		}
		BasicSample.sLogger.info("Starting EthtereumJ miner instance!");
		EthereumFactory.createEthereum(MinerConfig.class);
	}
}
