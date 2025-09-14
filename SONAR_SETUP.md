# SonarQube Setup Guide for MoveTrip Backend

## Prerequisites
- Java 17 or higher
- Docker (recommended for SonarQube)
- Maven

## 1. Start SonarQube Server

### Option A: Using Docker (Recommended)
```bash
# Pull and run SonarQube
docker run -d --name sonarqube -p 9000:9000 sonarqube:community

# Wait for SonarQube to start (check logs)
docker logs sonarqube -f
```

### Option B: Download and Run Locally
1. Download SonarQube from https://www.sonarqube.org/downloads/
2. Extract and run:
   ```bash
   bin/windows-x86-64/StartSonar.bat
   ```

## 2. Initial SonarQube Setup
1. Open http://localhost:9000
2. Login with default credentials: `admin/admin`
3. Change the default password when prompted
4. Create a new project manually:
   - Project Key: `movetrip-backend`
   - Display Name: `MoveTrip Backend`
5. Generate a token for analysis

## 3. Run Analysis

### Method 1: Using Maven
```bash
# Generate test reports and coverage
.\mvnw.cmd clean test jacoco:report

# Run SonarQube analysis
.\mvnw.cmd sonar:sonar -Dsonar.login=YOUR_TOKEN_HERE
```

### Method 2: Using SonarScanner CLI
```bash
# Install SonarScanner CLI first, then:
sonar-scanner -Dsonar.login=YOUR_TOKEN_HERE
```

### Method 3: One-command analysis
```bash
# Run tests, generate reports, and analyze in one command
.\mvnw.cmd clean test jacoco:report sonar:sonar -Dsonar.login=YOUR_TOKEN_HERE
```

## 4. View Results
1. Go to http://localhost:9000
2. Navigate to your project: `movetrip-backend`
3. Review:
   - **Bugs**: Code issues that should be fixed
   - **Vulnerabilities**: Security issues
   - **Code Smells**: Maintainability issues
   - **Coverage**: Test coverage percentage
   - **Duplications**: Code duplication

## 5. CI/CD Integration
Add to your CI pipeline:
```yaml
# Example for GitHub Actions
- name: Run tests and SonarQube analysis
  run: |
    mvn clean test jacoco:report sonar:sonar \
      -Dsonar.login=${{ secrets.SONAR_TOKEN }} \
      -Dsonar.host.url=http://localhost:9000
```

## 6. Quality Gates
- Set up quality gates in SonarQube UI
- Configure rules for:
  - Minimum test coverage (e.g., 80%)
  - Maximum number of bugs (e.g., 0)
  - Maximum technical debt

## Configuration Files
- `pom.xml`: Maven configuration with SonarQube and JaCoCo plugins
- `sonar-project.properties`: SonarQube project configuration

## Useful Commands
```bash
# Run only tests
.\mvnw.cmd test

# Generate coverage report
.\mvnw.cmd jacoco:report

# Run SonarQube analysis
.\mvnw.cmd sonar:sonar

# Complete analysis pipeline
.\mvnw.cmd clean test jacoco:report sonar:sonar
```

## Troubleshooting
- Ensure SonarQube server is running on port 9000
- Check that your token has the correct permissions
- Verify Java 17 is being used
- Make sure all tests pass before running analysis