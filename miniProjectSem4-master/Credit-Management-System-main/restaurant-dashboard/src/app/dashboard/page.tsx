"use client"

import React, { useEffect, useState } from "react"
import { motion } from "framer-motion"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { RiMoneyDollarCircleLine, RiShoppingBag3Line, RiGroupLine, RiUserAddLine, RiRestaurantLine } from "react-icons/ri"
import { StatisticCard } from "@/components/statistic-card"
import { RecentOrders } from "@/components/recent-orders"
import { RecentCustomers } from "@/components/recent-customers"  // Add this import
import { PopularItems } from "@/components/popular-items"
import { useRouter } from "next/navigation"

// Update the interface to include recentCustomers
interface DashboardStats {
  revenue: number;
  orders: number;
  customers: number;
  creditBalance: number;
  recentOrders: any[];
  recentCustomers: any[]; // Add this line
}

export default function DashboardPage() {
  const router = useRouter()
  const [stats, setStats] = useState<DashboardStats | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchDashboardStats = async () => {
      try {
        const adminData = localStorage.getItem('adminData');
        if (!adminData) return;
        
        const { id } = JSON.parse(adminData);
        const response = await fetch(`http://localhost:8080/api/dashboard/${id}`, {
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`
          }
        });
        
        if (response.ok) {
          const data = await response.json();
          setStats(data);
        }
      } catch (error) {
        console.error('Failed to fetch dashboard stats:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchDashboardStats();
  }, []);

  const statisticsCards = [
    {
      title: "Today's Revenue",
      value: stats ? `$${stats.revenue.toFixed(2)}` : "$0.00",
      description: "Total revenue today",
      icon: <RiMoneyDollarCircleLine className="h-5 w-5" />,
      trend: "up"
    },
    {
      title: "Orders",
      value: stats?.orders.toString() || "0",
      description: "Total orders",
      icon: <RiShoppingBag3Line className="h-5 w-5" />,
      trend: "up"
    },
    {
      title: "Customers",
      value: stats?.customers.toString() || "0",
      description: "Total customers",
      icon: <RiGroupLine className="h-5 w-5" />,
      trend: "up"
    },
    {
      title: "Credit Balance",
      value: stats ? `$${stats.creditBalance.toFixed(2)}` : "$0.00",
      description: "Available credit",
      icon: <RiMoneyDollarCircleLine className="h-5 w-5" />,
      trend: "neutral"
    }
  ];

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-2">
        <motion.h1
          className="text-3xl font-semibold tracking-tight"
          initial={{ opacity: 0, y: -20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5 }}
        >
          Dashboard
        </motion.h1>
        <motion.p
          className="text-muted-foreground"
          initial={{ opacity: 0, y: -20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5, delay: 0.1 }}
        >
          Welcome back! Here's an overview of your restaurant.
        </motion.p>
      </div>

      {/* Statistics Row */}
      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
        {statisticsCards.map((card, index) => (
          <motion.div
            key={card.title}
            className={`slide-in animate-delay-${(index + 1) * 100}`}
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5, delay: 0.2 + (index * 0.1) }}
          >
            <StatisticCard
              title={card.title}
              value={card.value}
              description={card.description}
              icon={card.icon}
              trend={card.trend}
            />
          </motion.div>
        ))}
      </div>

      {/* Middle Row */}
      <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
        <motion.div
          className="lg:col-span-2"
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5, delay: 0.6 }}
        >
          <Card className="glass-card">
            <CardHeader className="flex flex-row items-center justify-between">
              <CardTitle>Recent Orders</CardTitle>
            </CardHeader>
            <CardContent>
              <RecentOrders orders={stats?.recentOrders || []} />
            </CardContent>
          </Card>
        </motion.div>

        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5, delay: 0.7 }}
        >
          <Card className="glass-card">
            <CardHeader className="flex flex-row items-center justify-between">
              <CardTitle>Recent Customers</CardTitle>
            </CardHeader>
            <CardContent>
              <RecentCustomers customers={stats?.recentCustomers || []} />
            </CardContent>
          </Card>
        </motion.div>
      </div>

      {/* Bottom Row */}
      <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-4">
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5, delay: 0.8 }}
        >
          <Card className="glass-card">
            <CardHeader className="flex flex-row items-center justify-between">
              <CardTitle>Popular Items</CardTitle>
            </CardHeader>
            <CardContent>
              <PopularItems />
            </CardContent>
          </Card>
        </motion.div>

        {/* Quick Action Cards */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5, delay: 0.9 }}
        >
          <Card 
            className="glass-card h-full cursor-pointer hover:shadow-xl transition-shadow"
            onClick={() => router.push('/customers/add')}
          >
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <RiUserAddLine className="h-6 w-6 text-primary" />
                Add Customer
              </CardTitle>
            </CardHeader>
            <CardContent>
              <motion.div
                className="flex flex-col items-center justify-center h-full gap-4 py-8"
                whileHover={{ scale: 1.02 }}
                whileTap={{ scale: 0.98 }}
              >
                <div className="p-4 rounded-full bg-primary/10">
                  <RiUserAddLine className="h-8 w-8 text-primary" />
                </div>
                <div className="text-center">
                  <h3 className="font-semibold text-lg mb-1">New Customer</h3>
                  <p className="text-muted-foreground">
                    Create a new customer profile
                  </p>
                </div>
              </motion.div>
            </CardContent>
          </Card>
        </motion.div>

        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5, delay: 1.0 }}
        >
          <Card 
            className="glass-card h-full cursor-pointer hover:shadow-xl transition-shadow"
            onClick={() => router.push('/orders/add')}
          >
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <RiShoppingBag3Line className="h-6 w-6 text-primary" />
                Add Order
              </CardTitle>
            </CardHeader>
            <CardContent>
              <motion.div
                className="flex flex-col items-center justify-center h-full gap-4 py-8"
                whileHover={{ scale: 1.02 }}
                whileTap={{ scale: 0.98 }}
              >
                <div className="p-4 rounded-full bg-primary/10">
                  <RiShoppingBag3Line className="h-8 w-8 text-primary" />
                </div>
                <div className="text-center">
                  <h3 className="font-semibold text-lg mb-1">New Order</h3>
                  <p className="text-muted-foreground">
                    Create a new order entry
                  </p>
                </div>
              </motion.div>
            </CardContent>
          </Card>
        </motion.div>

        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5, delay: 1.1 }}
        >
          <Card 
            className="glass-card h-full cursor-pointer hover:shadow-xl transition-shadow"
            onClick={() => router.push('/menu/add')}
          >
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <RiRestaurantLine className="h-6 w-6 text-primary" />
                Add Menu Item
              </CardTitle>
            </CardHeader>
            <CardContent>
              <motion.div
                className="flex flex-col items-center justify-center h-full gap-4 py-8"
                whileHover={{ scale: 1.02 }}
                whileTap={{ scale: 0.98 }}
              >
                <div className="p-4 rounded-full bg-primary/10">
                  <RiRestaurantLine className="h-8 w-8 text-primary" />
                </div>
                <div className="text-center">
                  <h3 className="font-semibold text-lg mb-1">New Menu Item</h3>
                  <p className="text-muted-foreground">
                    Add a new dish to your menu
                  </p>
                </div>
              </motion.div>
            </CardContent>
          </Card>
        </motion.div>
      </div>
    </div>
  )
}
