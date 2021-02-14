package dev.tunks.taxitrips.batch.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import dev.tunks.taxitrips.batch.BatchDataProcessorApplication;

@SpringBatchTest
@ContextConfiguration(classes = { BatchJobDataSourceConfig.class, BatchDataProcessorApplication.class })
@TestPropertySource("classpath:application-dev.properties")
@ActiveProfiles("dev")
public class BatchJobIntegrationTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;
  
    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

	
	@BeforeEach
	public void setUp() throws Exception {
	}

	@AfterEach
	public void tearDown() throws Exception {
	}

	@Test
	public void testBatchJob() {
		//TODO
	}

	@Test
	public void testYellowTaxiMasterStep() {
		//TODO
		//jobLauncherTestUtils.launchStep("taxiZoneMasterStep");
	}

	@Test
	public void testGreenTaxiMasterStep() {
		//TODO
	}

	@Test
	public void testRfvTaxiMasterStep() {
		//TODO
	}

}
