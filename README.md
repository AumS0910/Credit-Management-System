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
- **TypeScript 5.0** - Type-safe JavaScript
- **Tailwind CSS 3.4.1** - Utility-first CSS framework
- **Shadcn UI** - Modern component library
- **Framer Motion 12.6.5** - Animation library
- **React Icons 5.5.0** - Icon library
- **Chart.js 4.4.8 & Recharts 2.15.2** - Data visualization
- **React Hook Form 7.55.0** - Form handling
- **Zod 3.24.2** - Schema validation

### Backend
- **Spring Boot 2.7.0** - Java framework for REST API
- **Java 11** - Programming language
- **MongoDB Atlas** - Cloud NoSQL database
- **Spring Data MongoDB** - MongoDB integration
- **Spring Security** - Authentication and authorization
- **BCrypt** - Password hashing
- **Lombok 1.18.30** - Code generation

## Prerequisites

- **Node.js** (v18 or higher) - For running the frontend
- **Bun** (optional alternative to npm) - Fast JavaScript runtime and package manager
- **Java JDK 11** - For running the backend
- **MongoDB** - NoSQL database (local or cloud)
- **Maven** - Build tool for Java projects
- **Git** - Version control

## Installation

### 1. Clone the Repository

```bash
git clone https://github.com/AumS0910/Credit-Management-System.git
cd miniProjectSem4-master
```

### 2. Backend Setup

#### Database Configuration

**Option A: Local MongoDB**
1. Install MongoDB locally or use Docker
2. Database will be created automatically

**Option B: MongoDB Atlas (Cloud)**

#### Step-by-Step MongoDB Atlas Setup:

1. **Create MongoDB Atlas Account**
   - Go to [MongoDB Atlas](https://www.mongodb.com/atlas)
   - Click "Try Free" → Sign up with email
   - Verify your email

2. **Create a New Project**
   - Click "Create a New Project"
   - Name: `restaurant-credit-management`
   - Click "Next" → "Create Project"

3. **Build a Database**
   - Click "Build a Database"
   - Choose "M0 Cluster" (Free tier)
   - Provider: AWS/Azure/GCP (any)
   - Region: Select closest to you
   - Cluster Name: `Cluster0` (default)
   - Click "Create Cluster"

4. **Set Up Database Access**
   - Go to "Database Access" (left sidebar)
   - Click "Add New Database User"
   - Authentication Method: "Password"
   - Username: `restaurantuser`
   - Password: Create a strong password (save it!)
   - Click "Add User"

5. **Configure Network Access**
   - Go to "Network Access" (left sidebar)
   - Click "Add IP Address"
   - Choose "Allow Access from Anywhere" (0.0.0.0/0)
   - Click "Confirm"

6. **Get Connection String**
   - Go to "Clusters" → Click "Connect"
   - Choose "Connect your application"
   - Driver: "Java"
   - Version: "4.3 or later"
   - Copy the connection string

7. **Update Application Configuration**
   - Replace `<password>` and `<database>` in the connection string
   - Update `src/main/resources/application.properties`:
   ```properties
   spring.data.mongodb.uri=mongodb+srv://restaurantuser:YOUR_PASSWORD@cluster0.xxxxx.mongodb.net/restaurant_db?retryWrites=true&w=majority
   ```

8. **Test Connection**
   - Run your application: `./mvnw spring-boot:run`
   - Check logs for successful MongoDB connection

#### Pexels API Setup (Optional)
Get an API key from [Pexels](https://www.pexels.com/api/) and add it to `application.properties`:
```properties
pexels.api.key=your_pexels_api_key
```

#### Build and Run Backend
```bash
# Navigate to backend directory (root)
mvn spring-boot:run
```

The backend will start on `http://localhost:8080`

**Note**: The `application.properties` file is gitignored for security. You'll need to create it manually with your MongoDB configuration as shown above.

**CORS Configuration**: The backend is configured to accept requests from `http://localhost:3000` (development) and `https://credit-management-system.vercel.app` (production).

### 3. Frontend Setup

```bash
# Navigate to frontend directory
cd restaurant-dashboard

# Install dependencies (choose one)
npm install
# or (faster alternative)
bun install

# Copy environment variables
# On Windows:
copy .env.example .env.local
# On Linux/Mac:
cp .env.example .env.local

# Update API base URL if needed (default: http://localhost:8080)
# The .env.local file should contain:
# NEXT_PUBLIC_API_BASE_URL=http://localhost:8080

# Run development server (choose one)
npm run dev
# or (faster alternative)
bun dev
```

The frontend will start on `http://localhost:3000`

## Usage

### First Time Setup
The application automatically creates a default admin user on startup:
- **Username**: admin
- **Password**: admin123

### Accessing the Dashboard
1. Login using the API endpoint: `POST /api/admin/login` or use the frontend at `http://localhost:3000`
2. Access the modern dashboard at `http://localhost:3000`

### API Endpoints

**Note**: Most API endpoints require an `Admin-ID` header for authentication.

#### Authentication
- `POST /api/admin/register` - Register new admin
- `POST /api/admin/login` - Admin login
- `POST /api/admin/logout` - Admin logout

#### Customers
- `GET /api/customers` - List all customers (requires Admin-ID header)
- `POST /api/customers/add` - Add new customer (requires Admin-ID header)
- `GET /api/customers/{id}` - Get customer details (requires Admin-ID header)
- `PUT /api/customers/{id}/update` - Update customer (requires Admin-ID header)
- `DELETE /api/customers/{id}/delete` - Delete customer
- `POST /api/customers/{id}/settle` - Settle customer balance (requires Admin-ID header)
- `GET /api/customers/search` - Search customers (requires Admin-ID header)

#### Menu Items
- `GET /api/menu-items` - List menu items (requires Admin-ID header)
- `POST /api/menu-items/add` - Add menu item (requires Admin-ID header)
- `GET /api/menu-items/{id}` - Get menu item (requires Admin-ID header)
- `PUT /api/menu-items/{id}` - Update menu item (requires Admin-ID header)
- `DELETE /api/menu-items/{id}` - Delete menu item

#### Orders
- `POST /api/orders` - Create new order (requires Admin-ID header)
- `GET /api/orders` - List orders (requires Admin-ID header)
- `GET /api/orders/{id}` - Get order details (requires Admin-ID header)
- `PUT /api/orders/{id}` - Update order (requires Admin-ID header)
- `DELETE /api/orders/{id}` - Delete order

#### Dashboard & Reports
- `GET /api/dashboard/{adminId}` - Get dashboard statistics
- `GET /api/reports/detailed` - Get detailed analytics (requires Admin-ID header)

## Project Structure

```
miniProjectSem4-master/
├── src/main/java/com/restaurant/creditmanagement/  # Backend source
│   ├── controller/                                 # REST controllers
│   ├── model/                                      # MongoDB documents
│   ├── repository/                                 # MongoDB repositories
│   ├── service/                                    # Business logic
│   ├── security/                                   # Security configuration
│   └── config/                                     # App configuration
├── src/main/resources/                             # Resources
│   ├── templates/                                  # Thymeleaf templates
│   ├── static/                                     # Static assets
│   ├── db/migration/                               # Database migrations
│   └── application.properties                      # App configuration
├── restaurant-dashboard/                           # Frontend application
│   ├── src/
│   │   ├── app/                                    # Next.js app router
│   │   ├── components/                             # React components
│   │   ├── hooks/                                  # Custom hooks
│   │   ├── lib/                                    # Utilities
│   │   └── types/                                  # TypeScript definitions
│   └── public/                                     # Static assets
├── .gitignore                                      # Git ignore rules
├── pom.xml                                         # Maven configuration
└── README.md                                        # This file
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
# or
bun run build
```

## Deployment

### Local Development Setup

#### Prerequisites
- Java 11
- Maven
- MongoDB (local or Atlas)

#### Run Locally

```bash
# 1. Set up MongoDB (local or Atlas connection)
# 2. Backend
./mvnw spring-boot:run

# 3. Frontend (in another terminal)
cd restaurant-dashboard
npm run dev
# or
bun dev
```

### Production Deployment

#### Cloud Deployment (Render/Heroku/etc.)

For cloud platforms, set these environment variables:

```bash
MONGODB_URI=mongodb+srv://restaurantuser:YOUR_PASSWORD@cluster0.xxxxx.mongodb.net/restaurant_db?retryWrites=true&w=majority
SERVER_PORT=8080
PEXELS_API_KEY=[your-pexels-api-key]
```

**For Render deployment:**
- Set `MONGODB_URI` in Render environment variables
- The application will connect to MongoDB Atlas automatically
- Default admin user is created automatically on first startup

**Note**: MongoDB creates collections automatically. No manual schema setup required.

### Frontend Deployment

The frontend is configured for Vercel deployment.

#### Setting Environment Variables in Vercel:

1. Go to your Vercel dashboard
2. Select your project (`credit-management-system`)
3. Go to **Settings** → **Environment Variables**
4. Add the following variable:
   - **Name**: `NEXT_PUBLIC_API_BASE_URL`
   - **Value**: `https://credit-management-system-40i5.onrender.com`
   - **Environment**: `Production`, `Preview`, `Development`

5. **Redeploy** the frontend to pick up the new environment variable

**Note**: `.env.local` files are not used in Vercel deployments. Environment variables must be set in the Vercel dashboard.

## Live Demo

- **Frontend**: https://credit-management-system.vercel.app
- **Backend API**: https://credit-management-system-40i5.onrender.com

### Default Admin Credentials
- **Username**: admin
- **Password**: admin123

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