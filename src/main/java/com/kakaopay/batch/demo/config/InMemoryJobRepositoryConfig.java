package com.kakaopay.batch.demo.config;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.SimpleJobExplorer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableBatchProcessing
@Configuration
public class InMemoryJobRepositoryConfig {

	@Bean
	public ResourcelessTransactionManager transactionManager() {
		return new ResourcelessTransactionManager();
	}

	@Bean
	public MapJobRepositoryFactoryBean mapJobRepositoryFactory(ResourcelessTransactionManager transactionManager)
		throws Exception {
		MapJobRepositoryFactoryBean factory = new MapJobRepositoryFactoryBean(transactionManager);
		factory.afterPropertiesSet();
		return factory;
	}

	@Bean
	public JobRepository jobRepository(MapJobRepositoryFactoryBean repositoryFactory) throws Exception {
		return repositoryFactory.getObject();
	}

	@Bean
	public JobExplorer jobExplorer(MapJobRepositoryFactoryBean repositoryFactory) {
		return new SimpleJobExplorer(repositoryFactory.getJobInstanceDao(), repositoryFactory.getJobExecutionDao(),
			repositoryFactory.getStepExecutionDao(), repositoryFactory.getExecutionContextDao());
	}

	@Bean
	public SimpleJobLauncher jobLauncher(JobRepository jobRepository) {
		SimpleJobLauncher launcher = new SimpleJobLauncher();
		launcher.setJobRepository(jobRepository);
		return launcher;
	}
}