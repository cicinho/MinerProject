package com.cicinho.miner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.cicinho.miner.mining.PrivateNetworkMiner;

@SpringBootApplication
public class MinerExampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(MinerExampleApplication.class, args);
		
		new PrivateNetworkMiner().initMiner();
	}
}
