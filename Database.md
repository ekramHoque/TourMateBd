
## database.md

```markdown
# TourMateBD Database Schema

The following MySQL tables are used by the TourMateBD application:

### 1. `users`

Stores user account information.

CREATE TABLE users (
id INT AUTO_INCREMENT PRIMARY KEY,
name VARCHAR(255) NOT NULL,
email VARCHAR(255) UNIQUE NOT NULL,
password VARCHAR(255) NOT NULL,
is_admin BOOLEAN DEFAULT FALSE,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


### 2. `tour_places`

Stores information about tour places.

CREATE TABLE tour_places (
id INT AUTO_INCREMENT PRIMARY KEY,
name VARCHAR(255) NOT NULL,
description TEXT,
address VARCHAR(255),
latitude VARCHAR(50),
longitude VARCHAR(50),
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


### 3. `photos`

Stores photo URLs associated with tour places.

CREATE TABLE photos (
id INT AUTO_INCREMENT PRIMARY KEY,
tour_place_id INT,
photo_url VARCHAR(255),
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
FOREIGN KEY (tour_place_id) REFERENCES tour_places(id)
);


### 4. `hotels`

Stores hotels linked to tour places.

CREATE TABLE hotels (
id INT AUTO_INCREMENT PRIMARY KEY,
tour_place_id INT,
name VARCHAR(255) NOT NULL,
location VARCHAR(255),
description TEXT,
contact VARCHAR(255),
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
FOREIGN KEY (tour_place_id) REFERENCES tour_places(id)
);


### 5. `rooms`

Stores rooms belonging to hotels.

CREATE TABLE rooms (
id INT AUTO_INCREMENT PRIMARY KEY,
hotel_id INT,
room_type VARCHAR(100),
price VARCHAR(50),
capacity VARCHAR(50),
availability BOOLEAN DEFAULT TRUE,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
FOREIGN KEY (hotel_id) REFERENCES hotels(id)
);


### 6. `bookings`

Stores bookings made by users.

CREATE TABLE bookings (
id INT AUTO_INCREMENT PRIMARY KEY,
user_id INT,
tour_place_id INT,
hotel_id INT,
room_id INT,
status VARCHAR(50), -- e.g. Pending, Confirmed, Canceled
booking_date TIMESTAMP,
FOREIGN KEY (user_id) REFERENCES users(id),
FOREIGN KEY (tour_place_id) REFERENCES tour_places(id),
FOREIGN KEY (hotel_id) REFERENCES hotels(id),
FOREIGN KEY (room_id) REFERENCES rooms(id)
);


### 7. `pdf_logs`

Logs generated PDF reports for tracking.

CREATE TABLE pdf_logs (
id INT AUTO_INCREMENT PRIMARY KEY,
user_id INT,
file_name VARCHAR(255),
generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
FOREIGN KEY (user_id) REFERENCES users(id)
);


---

**Note:** Import these tables into your MySQL `TourMateBD` database before running the application.
Make sure foreign key constraints are supported and properly configured for your MySQL version.
