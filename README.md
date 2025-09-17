# TourMateBD

TourMateBD is a Java-based hotel booking application that enables users to register, log in, and book hotels at various tour places. Administrators can manage tour places, hotels, rooms, users, and bookings through a Swing-based graphical interface. The application uses MySQL for data persistence and Apache PDFBox for generating PDF reports.

## Table of Contents
- [Features](#features)
- [Technologies](#technologies)
- [Project Structure](#project-structure)
- [Setup Instructions](#setup-instructions)
- [Usage](#usage)
- [Database Schema](#database-schema)
- [Contributing](#contributing)
- [License](#license)

## Features

### User Management:
- User registration with name, email, and password.
- Separate login interfaces for regular users and admins.
- Admin panel to view and manage all users.

### Tour Place Management:
- Add tour places with details (name, description, address, coordinates).
- Associate photos with tour places.

### Hotel and Room Management:
- Add hotels linked to tour places.
- Add rooms with type, price, and capacity.

### Booking Management:
- Users can create bookings by selecting tour places, hotels, rooms, and dates.
- Admins can view all bookings, filter them, and update statuses (Pending, Confirmed, Canceled).
- Search bookings by user or criteria.
- Export bookings to PDF reports.

### Swing-based UI:
- Homepage with options for user login, admin login, and registration.
- User dashboard for viewing tour places and managing bookings.
- Admin panel with forms for managing data and viewing reports.

## Technologies

- Java: Core programming language.
- Swing: For the graphical user interface.
- MySQL: Database for storing users, tour places, hotels, rooms, bookings, and photos.
- JDBC: For database connectivity.
- Apache PDFBox: For generating PDF reports.

### Maven Dependencies
- MySQL Connector/J for JDBC.
- Apache PDFBox for PDF generation.

## Project Structure

TourMateBD/<br>
├── src/<br>
│ ├── controller/<br>
│ │ └── UserController.java # Handles user registration and login logic<br>
│ ├── dao/<br>
│ │ ├── BookingDAO.java # Manages booking-related database operations<br>
│ │ ├── DBConnection.java # Manages MySQL database connections<br>
│ │ ├── HotelDAO.java # Manages hotel-related database operations<br>
│ │ ├── RoomDAO.java # Manages room-related database operations<br>
│ │ ├── TourPlaceDAO.java # Manages tour place and photo operations<br>
│ │ └── UserDAO.java # Manages user-related database operations<br>
│ ├── model/<br>
│ │ └── User.java # User entity model<br>
│ ├── ui/<br>
│ │ ├── AdminPanelUI.java # Admin panel UI for managing data<br>
│ │ ├── HomePageUI.java # Homepage UI for login/registration<br>
│ │ ├── LoginUI.java # Login UI for users and admins<br>
│ │ ├── MainDashboardUI.java # User dashboard UI for bookings and tour places<br>
│ │ └── RegistrationUI.java # User registration UI<br>
├── README.md # Project documentation<br>
├── Database.md<br>


## Setup Instructions

### Prerequisites:
- Java Development Kit (JDK) 8 or higher.
- MySQL Server (version 8.0 or compatible).
- Maven for dependency management.
- MySQL Connector/J and Apache PDFBox libraries (add to `pom.xml`).

### Database Setup:

1. Create a MySQL database named `TourMateBD`.
2. Execute the SQL script in `database.md` to create required tables.

### Configure Database Connection:

- Update the `DBConnection.java` file with your MySQL credentials (replace default username and password).

## Usage

### Homepage

- Launch the application to see the homepage (`HomePageUI`).
- Choose from the following options:
  - **User Login**: Access the user dashboard.
  - **Admin Login**: Access the admin panel.
  - **User Registration**: Register a new user account.

### User Registration

- Enter name, email, and password.
- Basic email validation checks for the presence of `@` and `.`.
- On successful registration, the user is redirected to the homepage.
- Errors such as duplicate email or invalid input will be displayed.

### Login

- Enter your email and password.
- Choose whether to log in as a user or admin.
- A successful login redirects to the appropriate dashboard:
  - Users → `MainDashboardUI`
  - Admins → `AdminPanelUI`

### User Dashboard (`MainDashboardUI`)

- View a list of available tour places.
- Book rooms by selecting:
  - Tour place
  - Hotel
  - Room
  - Date (format: `YYYY-MM-DD`)
- View and manage existing bookings (status starts as **Pending**).

### Admin Panel (`AdminPanelUI`)

- Add and manage:
  - Tour places
  - Hotels
  - Rooms
  - Users
- View all bookings and update their statuses:
  - **Pending**
  - **Confirmed**
  - **Canceled**
- Export booking data to PDF reports.
  - Each export action is logged in the `pdf_logs` table.

---

## Database Schema

The application uses a MySQL database named `TourMateBD`.

- The database includes the following tables:
  - `users`
  - `tour_places`
  - `photos`
  - `hotels`
  - `rooms`
  - `bookings`
  - `pdf_logs`

> 📄 For detailed schema information and SQL table creation statements, see [`Database.md`](Database.md) in the project root.

---

## Contributing

This project was developed as a **1st Year 2nd Semester Java Project** by the following team members:

- **Ekramul Hoque**
  - 📧 Email: bsse1628@iit.du.ac.bd

- **Rokib Ikbal**
  - 📧 Email: bsse1605@iit.du.ac.bd

> For any inquiries related to this project, feel free to reach out to the contributors via email.

---

## License

This project is open-source and available under the [MIT License](LICENSE).
