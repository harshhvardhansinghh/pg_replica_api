package com.example.pg_replica_api.controller;

import com.example.pg_replica_api.model.PostgresConfig;
import com.example.pg_replica_api.service.SetupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/setup")
public class SetupController {

    private final SetupService setupService;

    @Autowired
    public SetupController(SetupService setupService) {
        this.setupService = setupService;
    }

    /**
     * Generates the Terraform configuration based on provided PostgreSQL configuration.
     * @param config The PostgreSQL configuration.
     * @return A response indicating success or failure of the configuration generation.
     */
    @PostMapping("/generate")
    public ResponseEntity<String> generateTerraform(@Validated @RequestBody PostgresConfig config) {
        try {
            String response = setupService.generateTerraformConfig(config);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to generate Terraform configuration: " + e.getMessage());
        }
    }

    /**
     * Executes `terraform plan` to plan the infrastructure setup.
     * @return The output of the `terraform plan` command.
     */
    @PostMapping("/plan")
    public ResponseEntity<String> runTerraformPlan() {
        try {
            String response = setupService.runTerraformPlan();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to execute Terraform plan: " + e.getMessage());
        }
    }

    /**
     * Executes `terraform apply` to create the infrastructure.
     * @return The output of the `terraform apply` command.
     */
    @PostMapping("/apply")
    public ResponseEntity<String> runTerraformApply() {
        try {
            String response = setupService.runTerraformApply();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to apply Terraform configuration: " + e.getMessage());
        }
    }

    /**
     * Runs the Ansible playbook to configure PostgreSQL and set up replication.
     * @return The output of the Ansible playbook execution.
     */
    @PostMapping("/configure")
    public ResponseEntity<String> runAnsiblePlaybook() {
        try {
            String response = setupService.runAnsiblePlaybook();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to run Ansible playbook: " + e.getMessage());
        }
    }

    /**
     * Destroys the Terraform-managed infrastructure.
     * @return The output of the `terraform destroy` command.
     */
    @DeleteMapping("/destroy")
    public ResponseEntity<String> destroyInfrastructure() {
        try {
            String response = setupService.destroyInfrastructure();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to destroy infrastructure: " + e.getMessage());
        }
    }
}
