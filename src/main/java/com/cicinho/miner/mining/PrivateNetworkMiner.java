package com.cicinho.miner.mining;

import org.ethereum.config.SystemProperties;
import org.springframework.context.annotation.Bean;

import com.cicinho.miner.node.MinerNode;
import com.typesafe.config.ConfigFactory;

public class PrivateNetworkMiner {
	private final String config =
			// no need for discovery in that small network
			"peer.discovery.enabled = false \n" + "peer.listen.port = 30335 \n" +
			// need to have different nodeId's for the peers
					"peer.privateKey = 6ef8da380c27cea8fdf7448340ea99e8e2268fc2950d79ed47cbf6f85dc977ec \n" +
					// our private net ID
					"peer.networkId = 555 \n" +
					// we have no peers to sync with
					"sync.enabled = false \n" +
					// genesis with a lower initial difficulty and some predefined known funded
					// accounts
					"genesis = sample-genesis.json \n" +
					// two peers need to have separate database dirs
					"database.dir = sampleDB-1 \n" +
					// when more than 1 miner exist on the network extraData helps to identify the
					// block creator
					"mine.extraDataHex = cccccccccccccccccccc \n" + "mine.cpuMineThreads = 2 \n"
					+ "cache.flush.blocks = 1";

	@Bean
	public MinerNode node() {
		return new MinerNode();
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
