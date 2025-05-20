# 🏠 Smart Home Monitoring System

This project is a Smart Home Monitoring System that integrates **Java**, **Arduino**, and **MySQL** to collect and manage environmental sensor data.

## 📋 Features

- Real-time data collection from sensors (Temperature, Humidity, Light)
- Arduino circuits upload data to a MySQL database
- Java desktop application to:
  - View and manage sensor data
  - Manage users and homes
  - Display alerts (e.g. high temperature or low light)
  - Visualize data in charts or tables
- Role-based system:
  - Primary users collect data from sensors
  - Secondary users monitor or interact with homes

## 🧪 Test Data

The database includes:
- Multiple users (9 primary + 5 for each secondary role)
- User relationships (each primary linked to 2 secondary users)
- At least 3 sensor records per user
- Alerts and optional messages
- One rich profile with extensive sensor history

## 🗃️ Included in this Repository

- Java project source code
- Arduino source code
- `.sql` database backup file
- Sample data for full testing
