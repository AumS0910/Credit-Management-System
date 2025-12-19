# Restaurant Dashboard

This is the frontend dashboard for the Restaurant Credit Management System, built with Next.js.

## Overview

The dashboard provides a modern, responsive interface for managing restaurant operations including:
- Customer management
- Order processing
- Menu management
- Analytics and reporting
- Credit balance tracking

## Tech Stack

- Next.js 15.4.0
- TypeScript
- Tailwind CSS
- Shadcn UI
- Framer Motion

## Getting Started

### Prerequisites
- Node.js v18 or higher
- Backend API running on port 8080

### Installation

```bash
npm install
# or
bun install
```

### Environment Setup

```bash
cp .env.example .env.local
# Edit .env.local with your API base URL
```

### Development

```bash
npm run dev
# or
bun dev
```

Open [http://localhost:3000](http://localhost:3000) to view the dashboard.

### Build

```bash
npm run build
npm start
```

## Project Structure

```
src/
├── app/                    # Next.js app router pages
├── components/             # Reusable React components
├── hooks/                  # Custom React hooks
├── lib/                    # Utilities and configurations
└── types/                  # TypeScript type definitions
```

## Features

- **Dashboard Overview**: Real-time statistics and quick actions
- **Customer Management**: Add, edit, and view customer information
- **Order Management**: Create and manage orders
- **Menu Management**: Add and edit menu items
- **Analytics**: Charts and reports

For more information about the full system, see the main [README](../README.md) in the root directory.
