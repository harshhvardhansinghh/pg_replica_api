package com.example.pg_replica_api.controller;

import com.example.pg_replica_api.model.PostgresConfig;
import com.example.pg_replica_api.service.SetupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for handling PostgreSQL replication setup.
 */
@RestController
@RequestMapping("/api/v1/setup")
public class SetupController {

    private final SetupService setupService;

    @Autowired
    public SetupController(SetupService setupService) {
        this.setupService = setupService;
    }

    /**
     * Sets up the PostgreSQL replication infrastructure and configuration.
     * This method generates Terraform files, runs `terraform plan` and `apply`,
     * and executes the Ansible playbook to configure PostgreSQL.
     *
     * @param config The PostgreSQL configuration.
     * @return A response indicating success or failure of the entire setup process.
     */
    @PostMapping("/full-setup")
    public ResponseEntity<String> setupPostgresReplication(@Validated @RequestBody PostgresConfig config) {
        try {
            // Step 1: Generate Terraform configuration
            String generateResponse = setupService.generateTerraformConfig(config);
            if (generateResponse.contains("Failed")) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Step 1 - Terraform generation failed: " + generateResponse);
            }

            // Step 2: Run `terraform plan`
            String planResponse = setupService.runTerraformPlan();
            if (planResponse.contains("Failed")) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Step 2 - Terraform plan failed: " + planResponse);
            }

            // Step 3: Run `terraform apply`
            String applyResponse = setupService.runTerraformApply();
            if (applyResponse.contains("Failed")) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Step 3 - Terraform apply failed: " + applyResponse);
            }

            // Step 4: Run Ansible playbook
            String ansibleResponse = setupService.runAnsiblePlaybook();
            if (ansibleResponse.contains("Failed")) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Step 4 - Ansible playbook execution failed: " + ansibleResponse);
            }

            return ResponseEntity.ok("PostgreSQL replication setup completed successfully.");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Setup process encountered an error: " + e.getMessage());
        }
    }
}
