package com.cicinho.miner.mining;

import org.ethereum.config.SystemProperties;
import org.ethereum.facade.EthereumFactory;
import org.ethereum.samples.BasicSample;
import org.springframework.context.annotation.Bean;

import com.cicinho.miner.node.MinerNode;
import com.typesafe.config.ConfigFactory;

public class PrivateNetworkMiner {

	private static String logger = "Miner1";

	private static class MinerConfig {

		private final String config =
				// no need for discovery in that small network
				"peer.discovery.enabled = false \n" + "peer.listen.port = 30335 \n" +
				// need to have different nodeId's for the peers
						"peer.privateKey = 6ef8da380c27cea8fdf7448340ea99e8e2268fc2950d79ed47cbf6f85dc977ec \n" +
						// our private net ID
						"peer.networkId = 15 \n" +
						// we have no peers to sync with
						"sync.enabled = false \n" +
						// genesis with a lower initial difficulty and some predefined known funded
						// accounts
						"genesis = genesis.json \n" +
						// two peers need to have separate database dirs
						"database.dir = sampleDB-1 \n" +
						// when more than 1 miner exist on the network extraData helps to identify the
						// block creator
						"mine.extraDataHex = cccccccccccccccccccc \n" + "mine.cpuMineThreads = 2 \n"
						+ "cache.flush.blocks = 1";

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

		BasicSample.sLogger.info("Starting EthtereumJ miner instance!");
		EthereumFactory.createEthereum(MinerConfig.class);
	}
}
