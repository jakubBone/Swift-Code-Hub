# 🌐 Remitly Swift Hub

![](src/main/resources/logo.png)

Remitly Swift Hub is a containerized Java application that parses SWIFT code data from an .xlsx file, 
stores the data in a PostgreSQL database, and exposes a REST API to manage SWIFT records.
The application was developed to make SWIFT (Bank Identifier) data easily accessible to other systems.

## 🎯 Key Features

- **SWIFT Data Parsing**: Reads and processes an .xlsx file containing SWIFT code information

- **Database Storage**: Uses PostgreSQL to persist parsed SWIFT data for low-latency querying

- **REST API Endpoints**: Manages SWIFT code records by retrieving detailed information

- **Validation & Error Handling**: Validates input data and provides meaningful error responses

- **Containerization**: Easily deployable using Docker containers for both the application and database


## 🚀 Technologies Used

- **Java 21** as a core programming language

- **PostgreSQL** as a backend database 

- **JOOQ** for type-safe SQL querying

- **Jetty** as an embedded HTTP server

- **Apache POI** for file processing

- **JUnit** for unit testing

- **Rest Assured** for integration testing

- **Log4j2** for logging

- **Gradle** to build and dependency management

- **Docker** for containerization


## 📂 Project Structure

```
├── src/
│   ├── main/
│   │   ├── java/com/jakub/bone/api            # REST API endpoints 
│   │   ├── java/com/jakub/bone/database       # Database connection and schema management
│   │   ├── java/com/jakub/bone/domain         # Domain model
│   │   ├── java/com/jakub/bone/repository     # Data persistence (SwiftCodeRepository, DatabaseSchema)
│   │   ├── java/com/jakub/bone/server         # Server management
│   │   ├── java/com/jakub/bone/service        # Business logic for SWIFT operations
│   │   └── java/com/jakub/bone/utills         # Utility classes 
│   └── test/                                  # Unit and integration tests
├── Dockerfile                                 # Builds the RemitlySwiftHub JAR into a container
├── docker-compose.yml                         # Container orchestration for API and PostgreSQL DB
├── build.gradle                               # Build configuration
└── README.md                                  # Project documentation
``` 


## 🚀 Getting Started

Follow these steps to set up and run the project:

### Prerequisites

Before running the project, make sure you have installed:
- **Java Development Kit (JDK)** 21 or higher
- **Gradle 8.5** for dependency management
- **PostgreSQL** database
- **Docker and Docker Compose** for containerization

### Building the Project

1. **Clone the Repository**  
   Download the project files to your local machine:
   ```bash
   git clone https://github.com/jakubBone/Remitly-Swift-Hub.git
   cd Remitly-Swift-Hub
   ```

2. **Configure the Database**  
    Set up a PostgreSQL database:
   - Create new data base 
   - Update gradle JOOQ configuration with your database credentials 
   - Update `config.properties` file with your database credentials

3. **Build the Project**   
   Use Gradle to build the project and the shadow JAR.
   The JAR will be located under build/libs/RemitlySwiftHub.jar:
   ```bash
   ./gradlew build
   ./gradlew shadowJar
   ```
   
### Containerized Deployment

The application is fully containerized using Docker.
A sample docker-compose.yml is provided to orchestrate both the API and the PostgreSQL database

1. **Configure the Environment**   
   Edit the `config.properties` if needed to update database credentials and file paths for the SWIFT .xlsx file

2. **Docker Desktop**     
	Ensure that Docker Desktop is running before building and running the containers.

3. **Run Docker Compose**   
   From the project root, run:
   ```bash
   docker-compose up --build
   ```
   This command will:
   - Start a PostgreSQL container (using the postgres:15 image) with pre-configured credentials.
   - Build and run the API container using the provided Dockerfile.
   The REST API will be accessible at http://localhost:8080.
   
4. **Running Tests**  
   Integration tests are written using JUnit and Rest Assured. To run tests, execute:
   ```bash
   ./gradlew test
   ```
   
## 🌐 API Overview

The system provides a REST API. Available endpoints

- `GET /v1/swift-codes/{swift-code}` – Retrieve SWIFT Code Details
- `GET /v1/swift-codes/country/{countryISO2}` – Retrieve SWIFT Codes by CountryISO2 code
- `POST /v1/swift-codes` – Add a New SWIFT Code Record
- `DELETE /v1/swift-codes/{swift-code}` – Delete a SWIFT Code Record

### `GET /v1/swift-codes/{swift-code}`  
📌 **Description:** Retrieve SWIFT Code Details

🔹 **Response Example (Headquarter SWIFT Code):** 
```json
{
  "address": "Bank HQ address",
  "bankName": "Bank Name",
  "countryISO2": "PL",
  "countryName": "POLAND",
  "isHeadquarter": true,
  "swiftCode": "ABCDEFGHXXX",
  "branches": [
    {
      "address": "Branch address",
      "bankName": "Bank Name",
      "countryISO2": "PL",
      "isHeadquarter": false,
      "swiftCode": "ABCDEFGH123"
    }
  ]
}
```
<br>

🔹 **Response Example (Branch SWIFT Code):** 
```json
{
   "address": "Branch address",
   "bankName": "Bank Name",
   "countryISO2": "PL",
   "countryName": "POLAND",
   "isHeadquarter": "false",
   "swiftCode": "ABCDEFGH123"
}
```
<br>

### `GET /v1/swift-codes/country/{countryISO2}`  
📌 **Description:** Retrieve SWIFT Codes by CountryISO2

🔹 **Response Example:** 
```json
{
  "countryISO2": "PL",
  "countryName": "POLAND",
  "swiftCodes": [
    {
      "address": "Address1",
      "bankName": "Bank1",
      "countryISO2": "PL",
      "isHeadquarter": true,
      "swiftCode": "ABCDEFGHXXX"
    },
    {
      "address": "Address2",
      "bankName": "Bank1",
      "countryISO2": "PL",
      "isHeadquarter": false,
      "swiftCode": "ABCDEFGH123"
    }
  ]
}
```
<br>

### `POST /v1/swift-codes`  
📌 **Description:** Add a New SWIFT Code Record

🔹 **Request body example:** 
```json
{
  "address": "New Address",
  "bankName": "New Bank",
  "countryISO2": "PL",
  "countryName": "POLAND",
  "isHeadquarter": false,
  "swiftCode": "NEWCODE123"
}
```
<br>

🔹 **Response example:** 
```json
{
  "message": "SWIFT Record added successfully"
}
```
<br>

### `DELETE /v1/swift-codes/{swift-code}`  
📌 **Description:** Delete a SWIFT Code Record

🔹 **Response example:** 
```json
{
  "message": "SWIFT Record deleted successfully"
}
```
<br>


## 🎯 Purpose of the Project

This application was developed with the following goals:
  
- **SWIFT Code Parsing**: Process a provided Excel file and correctly identify SWIFT codes
  
- **Efficient Data Storage**: Store SWIFT data in a database optimized for fast retrieval

- **RESTful API**: Provide endpoints for querying, adding, and deleting SWIFT records

- **Containerization:**: Deploy the application and database using Docker to ensure a consistent runtime environment

- **Testing:**: Detailed unit and integration tests with edge cases covering


## 📧 Contact

If you have any questions, feedback, or suggestions, feel free to reach out to me:

- **Email**: [jakub.bone1990@gmail.com](mailto:jakub.bone1990@gmail,com)
- **Blog**: [javamPokaze.pl](https://javampokaze.pl)  
- **LinkedIn**: [Jakub Bone](https://www.linkedin.com/in/jakub-bone)  

Let's connect and discuss this project further! 🚀
