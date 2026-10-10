# 💊 Web-Based Pharmacy Portal (CarePlus)

![Java](https://img.shields.io/badge/Java-Spring%20Boot-green)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-Templates-005F0F)
![SQL Server](https://img.shields.io/badge/Database-MS%20SQL%20Server-red)
![Bootstrap](https://img.shields.io/badge/Bootstrap-5.3-7952B3)
![Course](https://img.shields.io/badge/SLIIT-SE2030-purple)

**Sri Lanka Institute of Information Technology (SLIIT)**
B.Sc. (Hons) in Information Technology — *Software Engineering (SE2030)*
**Group:** 2025-Y2S1-MLB-B3G1-10

---

## 📌 Overview

The Pharmacy Portal is a web-based platform that streamlines pharmacy operations in Sri Lanka. Many pharmacies still rely on phone orders and paper records, which cause delays and errors. This system puts customers, pharmacists, stock managers, accountants, delivery staff, and administrators on one platform.

Customers can register, browse medicines, upload prescriptions, chat with an AI assistant, place orders, pay online, and track deliveries. Staff can verify prescriptions, manage stock, monitor payments, and update delivery status.

## 🎯 Objectives

- Let customers order medicines online from anywhere.
- Provide AI chatbot support for basic medical guidance.
- Let customers track orders through a delivery timeline.
- Help stock managers compare best-selling medicines for better inventory decisions.
- Reduce delays, errors, and manual work in pharmacy operations.

## 👥 User Roles

| Role | What they can do |
|---|---|
| **Customer** | Register/login, manage profile, browse medicines, upload prescriptions, chat with the AI assistant, place and cancel orders, pay, track deliveries |
| **Pharmacist** | Verify (approve/reject) prescriptions, add/edit/delete medicines, mark medicines as prescription-only |
| **Admin** | Manage staff accounts and roles, view system overview, oversee all modules |
| **Accountant** | View, update, and delete payment records; generate financial reports |
| **Stock Manager** | Manage stock levels, view most-selling medicines report, move expired medicines |
| **Delivery Personnel** | Create and update deliveries and statuses; customers are notified by email |

## ✨ Features

### 🔐 User Management
- Registration and login with input validation (e.g. valid email, minimum 8-character password)
- Encrypted passwords and role-based access control (Spring Security)
- Profile editing, password change, and health information
- Upload, download, and delete prescriptions

### 🛒 Customer Experience
- Browse and search medicines by name or category (customers only see non-prescription medicines)
- Shopping cart and checkout
- Current-orders dashboard with Pending / Approved / Processing / Completed filters
- AI health assistant chatbot for basic guidance (not a medical diagnosis tool)

### 💊 Medicine Management
- Add, edit, delete, and search medicines (name, price, category, manufacturer, status)
- Prescription-required flag per medicine
- Pharmacist dashboard to approve or reject uploaded prescriptions and select medicines for the order

### 💳 Payment Management
- Checkout with card payment or cash on delivery
- **Strategy Pattern** for payment methods
- Accountant dashboard to view, update, and delete payment records
- Financial reports by period (daily / weekly / monthly / annually)

### 📦 Stock Management
- Stock dashboard with totals for medicines, expired, low stock, and out of stock
- Add, update, and delete medicines with brand, quantity, and expiry date
- Most Selling Medicines report with a time-period filter
- Expired medicines list with a recorded reason
- **Observer Pattern** keeps a `MedicineHistory` table updated on add/update
- **Factory Pattern** converts `MedicineDTO` objects into `Medicine` entities

### 🚚 Delivery Management
- Delivery dashboard with statuses: `PENDING`, `IN_TRANSIT`, `OUT_FOR_DELIVERY`, `DELIVERED`, `FAILED`, `CANCELLED`
- Create, view, and edit deliveries
- Delivery timeline (status history) for customers
- Automatic **email notification** when a delivery is created or its status changes

### 🛠️ Admin Management
- Create, update, and delete staff accounts and assign roles
- Overview dashboard: customers, active orders, revenue, medicine stock, recent activity

## 🧩 Design Patterns Used

| Pattern | Where | Purpose |
|---|---|---|
| **Strategy** | Payment component (`PaymentStrategy`, `PaymentContext`) | Support multiple payment methods and pick one at runtime |
| **Observer** | Medicine component (event listener) | Auto-update `MedicineHistory` on add/update events |
| **Factory** | Medicine management | Structured creation of `Medicine` entities from DTOs |

## 🗄️ Database

- Relational schema mapped from the EER diagram and normalized to **3NF**
- Staff roles modeled with an ISA hierarchy (Pharmacist, Stock Manager, Accountant, Delivery Personnel, Admin) using separate subclass tables
- Main entities: Customer, Order, Prescription, Payment, Delivery, Medicine, Expired Medicine, Staff and role tables
- SQL Server stored procedures and triggers for totals and automatic order status updates

## 🛠️ Tech Stack

| Category | Technologies |
|---|---|
| Language | Java, HTML5, CSS3, JavaScript |
| Backend | Spring Boot, Spring Data JPA, Spring Security |
| Frontend | Thymeleaf, Bootstrap 5.3, Font Awesome 6.4, Google Fonts (Inter) |
| Database | Microsoft SQL Server |
| Build | Maven |
| Server | Apache Tomcat (embedded in Spring Boot) |
| Tools | IntelliJ IDEA / VS Code, SSMS, Git & GitHub |

## 🚀 Getting Started

### Prerequisites

- JDK 17+ (use the version your project is configured for)
- Maven 3.8+
- Microsoft SQL Server and SQL Server Management Studio (SSMS)
- An SMTP account (e.g. Gmail app password) for email notifications

### Setup

```bash
# 1. Clone the repository
git clone https://github.com/<your-username>/<your-repo-name>.git
cd <your-repo-name>

# 2. Create the database in SQL Server, then run the DDL / sample data scripts
#    (e.g. database/schema.sql, database/sample-data.sql)

# 3. Configure src/main/resources/application.properties (see below)

# 4. Build and run
mvn clean install
mvn spring-boot:run
```

The application starts at **http://localhost:8080**.

### Configuration

Edit `src/main/resources/application.properties`. Replace the placeholders and **never commit real credentials**.

```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=<your_db>;encrypt=true;trustServerCertificate=true
spring.datasource.username=<db_user>
spring.datasource.password=<db_password>
spring.jpa.hibernate.ddl-auto=update

spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=<your_email>
spring.mail.password=<app_password>
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

## 🗂️ Project Structure

> Adjust to match your repository.

```
├── src/
│   ├── main/
│   │   ├── java/             # Controllers, services, repositories, entities, patterns
│   │   └── resources/
│   │       ├── templates/    # Thymeleaf pages
│   │       ├── static/       # CSS, JS, images
│   │       └── application.properties
│   └── test/                 # Unit and integration tests
├── database/                 # SQL schema, triggers, procedures, sample data
├── docs/                     # Final report, diagrams
├── pom.xml
└── README.md
```

## 🏃 Development Process

Built using **Agile (Scrum)** over four 2-week sprints:

| Sprint | Focus |
|---|---|
| 1 (Weeks 1–2) | User and role management |
| 2 (Weeks 3–4) | Order and prescription management |
| 3 (Weeks 5–6) | Inventory and delivery coordination |
| 4 (Weeks 7–8) | Accounting, integration, and deployment |

## ⚠️ Limitations

- Does not provide medical diagnosis and does not replace a doctor's consultation.
- Browser-based only; no mobile app in this version.
- Payments are simulated; no real payment gateway is integrated.
- Requires internet access and a modern browser.

## 🔮 Future Work

- Integrate real APIs (e.g. Stripe for payments, Google Maps for delivery tracking)
- AI-based medication recommendations from prescriptions
- React.js front end and a mobile app
- Two-factor authentication and other advanced security features

## 👨‍💻 Team

| Student ID | Name | Responsibility |
|---|---|---|
| IT24100284 | Jayaratne M. Y. De S. | User/customer management, prescription upload, cart, AI assistant, testing |
| IT24100244 | Nakkavita N. K. P. P. | Payment management, Strategy Pattern, relational schema (EER mapping) |
| IT24100225 | Dissanayaka A. D. M. N. K. | Medicine management, use cases and sequence diagrams |
| IT24100363 | Faazil M. M. M. | Delivery management, email notifications, delivery timeline, SQL triggers/procedures |
| IT24100316 | Weerasingha P. M. P. L. | Stock management, most-selling and expired reports, Observer and Factory patterns |
| IT24100374 | Vinranga M. W. S. | Admin management, staff and roles, system overview |

## 📚 References

- [Spring Boot Reference Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Bootstrap 5.3 Documentation](https://getbootstrap.com/docs/5.3/getting-started/introduction/)
- [Thymeleaf Documentation](https://www.thymeleaf.org/documentation.html)

## 📄 License

Developed for coursework at SLIIT. Add a license of your choice (e.g. MIT) if you plan to make the repository open source.
