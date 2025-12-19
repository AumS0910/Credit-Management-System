# Restaurant Credit Management System

A comprehensive restaurant management system with credit tracking capabilities, featuring both a modern web dashboard and a RESTful API backend. This system allows restaurant owners to manage customers, menu items, orders, and track credit balances efficiently.

## Features

- 🔐 **Secure Authentication System** - Admin registration and login with session management
- 👥 **Customer Management** - Add, edit, delete, and search customers with credit balance tracking
- 🍽️ **Menu Management** - Create and manage menu items with image integration via Pexels API
- 📊 **Credit Tracking** - Monitor customer credit balances and settlement history
- 📈 **Analytics Dashboard** - Real-time statistics on revenue, orders, and customers
- 🛍️ **Order Management** - Create, update, and track orders with status management
- 💳 **Payment Processing** - Handle credit settlements and transaction records
- 📋 **Reports** - Detailed analytics and reporting capabilities
- 🎨 **Modern UI** - Responsive dashboard with glassmorphism design

## Tech Stack

### Frontend
- **Next.js 15.4.0** - React framework for the dashboard
- **TypeScript** - Type-safe JavaScript
- **Tailwind CSS** - Utility-first CSS framework
- **Shadcn UI** - Modern component library
- **Framer Motion** - Animation library
- **React Icons** - Icon library
- **Chart.js & Recharts** - Data visualization

### Backend
- **Spring Boot 2.7.0** - Java framework for REST API
- **Java 11** - Programming language
- **PostgreSQL** - Database
- **Spring Data JPA** - ORM for database operations
- **Spring Security** - Authentication and authorization
- **Thymeleaf** - Server-side templating (for admin web interface)
- **Flyway** - Database migration tool

## Prerequisites

- **Node.js** (v18 or higher) - For running the frontend
- **Java JDK 11** - For running the backend
- **PostgreSQL** - Database server
- **Maven** - Build tool for Java projects
- **Git** - Version control

## Installation

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/restaurant-credit-management.git
cd restaurant-credit-management
```

### 2. Backend Setup

#### Database Configuration
1. Install PostgreSQL and create a database named `restaurant_db`
2. Update database credentials in `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/restaurant_db
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

#### Pexels API Setup (Optional)
Get an API key from [Pexels](https://www.pexels.com/api/) and add it to `application.properties`:
```properties
pexels.api.key=your_pexels_api_key
```

#### Build and Run Backend
```bash
# Navigate to backend directory (root)
mvn clean install
mvn spring-boot:run
```

The backend will start on `http://localhost:8080`

### 3. Frontend Setup

```bash
# Navigate to frontend directory
cd restaurant-dashboard

# Install dependencies
npm install
# or
bun install

# Copy environment variables
cp .env.example .env.local

# Update API base URL if needed (default: http://localhost:8080)
echo "NEXT_PUBLIC_API_BASE_URL=http://localhost:8080" > .env.local

# Run development server
npm run dev
# or
bun dev
```

The frontend will start on `http://localhost:3000`

## Usage

### Admin Registration
1. Start the backend server
2. Visit `http://localhost:8080/register` to create an admin account
3. Or use the API endpoint: `POST /api/admin/register`

### Accessing the Dashboard
1. Login at `http://localhost:8080/login` (Thymeleaf interface) or use the API
2. Access the modern dashboard at `http://localhost:3000`

### API Endpoints

#### Authentication
- `POST /api/admin/register` - Register new admin
- `POST /api/admin/login` - Admin login
- `POST /api/admin/logout` - Admin logout

#### Customers
- `GET /api/customers` - List all customers
- `POST /api/customers/add` - Add new customer
- `GET /api/customers/{id}` - Get customer details
- `PUT /api/customers/{id}/update` - Update customer
- `DELETE /api/customers/{id}/delete` - Delete customer
- `POST /api/customers/{id}/settle` - Settle customer balance

#### Menu Items
- `GET /api/menu-items` - List menu items
- `POST /api/menu-items/add` - Add menu item
- `GET /api/menu-items/{id}` - Get menu item
- `PUT /api/menu-items/{id}` - Update menu item
- `DELETE /api/menu-items/{id}` - Delete menu item

#### Orders
- `POST /api/orders` - Create new order
- `GET /api/orders` - List orders
- `GET /api/orders/{id}` - Get order details
- `PUT /api/orders/{id}` - Update order
- `DELETE /api/orders/{id}` - Delete order

#### Dashboard & Reports
- `GET /api/dashboard/{adminId}` - Get dashboard statistics
- `GET /api/reports/detailed` - Get detailed analytics

## Project Structure

```
restaurant-credit-management/
├── src/main/java/com/restaurant/creditmanagement/  # Backend source
│   ├── controller/                                 # REST controllers
│   ├── model/                                      # JPA entities
│   ├── repository/                                 # Data repositories
│   ├── service/                                    # Business logic
│   ├── security/                                   # Security configuration
│   └── config/                                     # App configuration
├── src/main/resources/                             # Resources
│   ├── templates/                                  # Thymeleaf templates
│   ├── static/                                     # Static assets
│   ├── db/migration/                               # Flyway migrations
│   └── application.properties                      # App configuration
├── restaurant-dashboard/                           # Frontend application
│   ├── src/
│   │   ├── app/                                    # Next.js app router
│   │   ├── components/                             # React components
│   │   └── lib/                                    # Utilities
│   └── public/                                     # Static assets
└── pom.xml                                         # Maven configuration
```

## Development

### Running Tests
```bash
# Backend tests
mvn test

# Frontend tests (if implemented)
npm test
```

### Building for Production
```bash
# Backend
mvn clean package

# Frontend
npm run build
```

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

For support, email support@restaurantcredit.com or create an issue in this repository.