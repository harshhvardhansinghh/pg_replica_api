package com.example.pg_replica_api.service;


import com.example.pg_replica_api.model.PostgresConfig;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.logging.Logger;

@Service
public class SetupService {


    private static final Logger LOGGER = Logger.getLogger(SetupService.class.getName());

    private static final String TERRAFORM_DIR = "terraform";
    private static final String ANSIBLE_DIR = "ansible";
    private static final String TERRAFORM_FILE = "main.tf";

    public String generateTerraformConfig(PostgresConfig config) {
        try {
            // Create Terraform directory if it doesn't exist
            File terraformDir = new File(TERRAFORM_DIR);
            if (!terraformDir.exists()) {
                terraformDir.mkdir();
            }

            // Write the Terraform configuration to a file
            File terraformFile = new File(TERRAFORM_DIR + "/" + TERRAFORM_FILE);
            try (FileWriter writer = new FileWriter(terraformFile)) {
                writer.write(generateTerraformContent(config));
            }

            return "Terraform configuration generated successfully.";
        } catch (IOException e) {
            LOGGER.severe("Error generating Terraform config: " + e.getMessage());
            return "Error generating Terraform configuration.";
        }
    }

    private String generateTerraformContent(PostgresConfig config) {
        // Example template for Terraform EC2 instances and other AWS resources
        return """
        provider "aws" {
          region = "us-west-2"
        }

        resource "aws_instance" "postgres_primary" {
          ami             = "ami-0abcdef1234567890"
          instance_type   = "%s"
          tags = {
            Name = "Postgres Primary"
          }
        }

        resource "aws_instance" "postgres_replica" {
          count           = %d
          ami             = "ami-0abcdef1234567890"
          instance_type   = "%s"
          tags = {
            Name = "Postgres Replica-${count.index}"
          }
        }

        output "primary_instance_id" {
          value = aws_instance.postgres_primary.id
        }

        """.formatted(config.getInstanceType(), config.getNumberOfReplicas(), config.getInstanceType());
    }

    public String runTerraformPlan() {
        return executeCommand("terraform plan", TERRAFORM_DIR);
    }

    public String runTerraformApply() {
        return executeCommand("terraform apply -auto-approve", TERRAFORM_DIR);
    }

    public String runAnsiblePlaybook() {
        return executeCommand("ansible-playbook -i inventory.ini playbook.yml", ANSIBLE_DIR);
    }

    public String destroyInfrastructure() {
        return executeCommand("terraform destroy -auto-approve", TERRAFORM_DIR);
    }

    private String executeCommand(String command, String workingDirectory) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
            processBuilder.directory(new File(workingDirectory));
            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                return output.toString();
            } else {
                LOGGER.severe("Command failed with exit code " + exitCode);
                return "Command failed with exit code " + exitCode;
            }
        } catch (IOException | InterruptedException e) {
            LOGGER.severe("Error executing command: " + e.getMessage());
            return "Error executing command: " + e.getMessage();
        }
    }
}
