"use client"

import React, { useState, useEffect } from "react"
import { motion } from "framer-motion"
import { useRouter } from "next/navigation"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { RiShoppingBag3Line, RiAddLine } from "react-icons/ri"

interface Order {
  id: number;
  customer: {
    id: number;
    name: string;
    creditBalance: number;
  };
  menuItems: Array<{
    id: number;
    name: string;
    price: number;
    quantity: number;
  }>;
  orderDate: string;
  totalAmount: number;
  status: string;
  paymentMethod: string;
  tax: number;
  notes?: string;
}

export default function OrdersPage() {
  const router = useRouter()
  const [orders, setOrders] = useState<Order[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState("")

  useEffect(() => {
    fetchOrders()
  }, [])

  const fetchOrders = async () => {
    try {
      const adminData = localStorage.getItem('adminData')
      if (!adminData) {
        router.push('/login')
        return
      }
      const { id } = JSON.parse(adminData)

      const response = await fetch(`http://localhost:8080/api/orders`, {
        headers: {
          "Content-Type": "application/json",
          "Admin-ID": id.toString()
        }
      })

      if (response.ok) {
        const data = await response.json()
        setOrders(data)
      } else {
        setError("Failed to fetch orders")
      }
    } catch (error) {
      setError("An error occurred while fetching orders")
    } finally {
      setLoading(false)
    }
  }

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    })
  }

  return (
    <div className="min-h-screen p-8 bg-background">
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
        className="max-w-6xl mx-auto space-y-8"
      >
        <div className="flex justify-between items-center">
          <div>
            <motion.h1
              className="text-3xl font-semibold tracking-tight"
              initial={{ opacity: 0, x: -20 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ duration: 0.5, delay: 0.2 }}
            >
              Orders
            </motion.h1>
            <motion.p
              className="text-muted-foreground"
              initial={{ opacity: 0, x: -20 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ duration: 0.5, delay: 0.3 }}
            >
              View and manage your restaurant orders
            </motion.p>
          </div>
          <motion.div
            initial={{ opacity: 0, x: 20 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ duration: 0.5, delay: 0.4 }}
          >
            <Button
              onClick={() => router.push('/orders/add')}
              className="flex items-center gap-2"
            >
              <RiAddLine className="h-5 w-5" />
              New Order
            </Button>
          </motion.div>
        </div>

        {error && (
          <div className="text-red-500 text-center">{error}</div>
        )}

        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5, delay: 0.5 }}
        >
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <RiShoppingBag3Line className="h-6 w-6 text-primary" />
                Recent Orders
              </CardTitle>
            </CardHeader>
            <CardContent>
              {loading ? (
                <div className="text-center py-4">Loading orders...</div>
              ) : orders.length === 0 ? (
                <div className="text-center py-4 text-muted-foreground">
                  No orders found
                </div>
              ) : (
                <div className="space-y-4">
                  {orders.map((order) => (
                    <motion.div
                      key={order.id}
                      className="flex items-center justify-between p-4 border rounded-lg hover:bg-gray-50"
                      whileHover={{ scale: 1.01 }}
                    >
                      <div>
                        <h3 className="font-medium">Order #{order.id}</h3>
                        <p className="text-sm text-muted-foreground">
                          {order.customer.name} • {formatDate(order.orderDate)}
                        </p>
                        <div className="flex gap-2 mt-1">
                          <span className={`text-xs px-2 py-1 rounded-full ${
                            order.status === 'COMPLETED' ? 'bg-green-100 text-green-800' :
                            order.status === 'PENDING' ? 'bg-yellow-100 text-yellow-800' :
                            'bg-gray-100 text-gray-800'
                          }`}>
                            {order.status}
                          </span>
                          <span className="text-xs px-2 py-1 rounded-full bg-blue-100 text-blue-800">
                            {order.paymentMethod}
                          </span>
                        </div>
                      </div>
                      <div className="text-right">
                        <p className="font-medium">${order.totalAmount.toFixed(2)}</p>
                      </div>
                    </motion.div>
                  ))}
                </div>
              )}
            </CardContent>
          </Card>
        </motion.div>
      </motion.div>
    </div>
  )
}