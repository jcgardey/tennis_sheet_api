# Tennis Sheet

## Description

This repo contains the backend of Tennis Sheet, a web application designed to manage tennis court reservations for a tennis club. It allows users to view available courts, book reservations, and manage their schedules. Administrators can oversee bookings, manage court availability, and handle user accounts.

## Planned Features

- **User Registration and Authentication**: Secure login and registration for club members.
- **Court Booking**: Easy-to-use interface for reserving tennis courts with date and time selection.
- **Schedule Management**: View and manage personal and public booking schedules.
- **Admin Panel**: Tools for administrators to add/edit courts, manage reservations, and user permissions.


## Technologies Used

- **Backend**: Java, Spring Boot
- **Database**: H2 (or check application.properties for actual DB)
- **Build Tool**: Maven
- **Containerization**: Docker (via docker-compose.yml)
- **Other**: Spring Security for authentication

## Prerequisites

- Java 11 or higher
- Maven 3.6+
- Docker and Docker Compose (for containerized deployment)

## Installation

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/yourusername/tennis-sheet.git
   cd tennis-sheet
   ```

2. **Build the Application**:
   ```bash
   mvn clean install
   ```

3. **Run with Maven**:
   ```bash
   mvn spring-boot:run
   ```
   The application will start on `http://localhost:8080`.

4. **Run with Docker**:
   ```bash
   docker-compose up
   ```

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contact

For questions or support, please contact [your-email@example.com].